package com.example.scheduler.service;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.core.BusinessException;
import com.example.scheduler.core.ConflictException;
import com.example.scheduler.core.NotFoundException;
import com.example.scheduler.domain.*;
import com.example.scheduler.repository.*;
import com.example.scheduler.util.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepo;
    private final CandidateRepository candidateRepo;
    private final InterviewerRepository interviewerRepo;
    private final GeneratedSlotRepository slotRepo;
    private final WeeklyCapRepository capRepo;
    private final WeeklyCounterRepository counterRepo;

    public BookingService(BookingRepository bookingRepo,
                          CandidateRepository candidateRepo,
                          InterviewerRepository interviewerRepo,
                          GeneratedSlotRepository slotRepo,
                          WeeklyCapRepository capRepo,
                          WeeklyCounterRepository counterRepo) {
        this.bookingRepo = bookingRepo;
        this.candidateRepo = candidateRepo;
        this.interviewerRepo = interviewerRepo;
        this.slotRepo = slotRepo;
        this.capRepo = capRepo;
        this.counterRepo = counterRepo;
    }

    @Transactional
    public DTOs.BookingResponse create(DTOs.BookingRequest req) {
        var candidate = candidateRepo.findById(req.candidateId())
                .orElseThrow(() -> new NotFoundException("candidate not found"));
        var interviewer = interviewerRepo.findById(req.interviewerId())
                .orElseThrow(() -> new NotFoundException("interviewer not found"));
        var slot = slotRepo.findById(req.slotId())
                .orElseThrow(() -> new NotFoundException("slot not found"));
        if (!slot.getInterviewer().getId().equals(interviewer.getId())) {
            throw new BusinessException("slot does not belong to interviewer");
        }
        if (slot.getStatus() != SlotStatus.OPEN) {
            throw new ConflictException("slot not open");
        }
        if (slot.getStartAt().isBefore(Instant.now())) {
            throw new BusinessException("slot is in the past");
        }

        LocalDate weekStart = TimeUtils.weekStartUtc(slot.getStartAt().atOffset(java.time.ZoneOffset.UTC).toLocalDate());
        WeeklyCap cap = capRepo.findByInterviewerIdAndWeekStartDate(interviewer.getId(), weekStart)
                .orElseThrow(() -> new NotFoundException("weekly cap not set"));
        WeeklyCounter counter = counterRepo.findByInterviewerIdAndWeekStartDate(interviewer.getId(), weekStart)
                .orElseGet(() -> {
                    WeeklyCounter c = new WeeklyCounter();
                    c.setInterviewer(interviewer);
                    c.setWeekStartDate(weekStart);
                    c.setConfirmedCount(0);
                    return counterRepo.save(c);
                });

        // Close slot first to prevent double booking (optimistic lock via @Version on slot)
        slot.setStatus(SlotStatus.CLOSED);
        // Persist slot change; optimistic lock will throw if concurrent update
        slotRepo.saveAndFlush(slot);

        if (counter.getConfirmedCount() >= cap.getMaxInterviews()) {
            throw new ConflictException("weekly cap exceeded");
        }
        counter.setConfirmedCount(counter.getConfirmedCount() + 1);
        counterRepo.save(counter);

        Booking booking = new Booking();
        booking.setCandidate(candidate);
        booking.setInterviewer(interviewer);
        booking.setSlot(slot);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(Instant.now());
        bookingRepo.save(booking);

        return new DTOs.BookingResponse(booking.getId(), slot.getId(), booking.getStatus().name(), "booking confirmed");
    }

    @Transactional
    public DTOs.BookingResponse reschedule(Long bookingId, DTOs.RescheduleRequest req) {
        Booking existing = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        if (existing.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("booking not active");
        }

        var newSlot = slotRepo.findById(req.newSlotId())
                .orElseThrow(() -> new NotFoundException("slot not found"));
        if (!newSlot.getInterviewer().getId().equals(existing.getInterviewer().getId())) {
            throw new BusinessException("slot interviewer mismatch");
        }
        if (newSlot.getStatus() != SlotStatus.OPEN) {
            throw new ConflictException("slot not open");
        }
        if (newSlot.getStartAt().isBefore(Instant.now())) {
            throw new BusinessException("slot is in the past");
        }

        // attempt booking of new slot (close it)
        newSlot.setStatus(SlotStatus.CLOSED);
        slotRepo.saveAndFlush(newSlot);

        // cancel old booking and reopen old slot (if still future)
        existing.setStatus(BookingStatus.CANCELED);
        existing.setUpdatedAt(Instant.now());
        bookingRepo.save(existing);
        GeneratedSlot oldSlot = existing.getSlot();
        if (oldSlot.getStartAt().isAfter(Instant.now())) {
            oldSlot.setStatus(SlotStatus.OPEN);
            slotRepo.save(oldSlot);
        }

        // create new booking
        Booking newBooking = new Booking();
        newBooking.setCandidate(existing.getCandidate());
        newBooking.setInterviewer(existing.getInterviewer());
        newBooking.setSlot(newSlot);
        newBooking.setStatus(BookingStatus.CONFIRMED);
        newBooking.setCreatedAt(Instant.now());
        bookingRepo.save(newBooking);

        return new DTOs.BookingResponse(newBooking.getId(), newSlot.getId(), newBooking.getStatus().name(), "booking rescheduled");
    }

    @Transactional
    public void cancel(Long bookingId) {
        Booking existing = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        if (existing.getStatus() == BookingStatus.CANCELED) return;
        existing.setStatus(BookingStatus.CANCELED);
        existing.setUpdatedAt(Instant.now());
        bookingRepo.save(existing);
        GeneratedSlot slot = existing.getSlot();
        if (slot.getStartAt().isAfter(Instant.now())) {
            slot.setStatus(SlotStatus.OPEN);
            slotRepo.save(slot);
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> listByCandidate(Long candidateId) {
        return bookingRepo.findByCandidateId(candidateId);
    }
}
