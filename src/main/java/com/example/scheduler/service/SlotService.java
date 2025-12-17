package com.example.scheduler.service;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.core.NotFoundException;
import com.example.scheduler.domain.*;
import com.example.scheduler.repository.AvailabilityRuleRepository;
import com.example.scheduler.repository.GeneratedSlotRepository;
import com.example.scheduler.repository.InterviewerRepository;
import com.example.scheduler.util.CursorUtil;
import com.example.scheduler.util.TimeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {
    private final AvailabilityRuleRepository ruleRepo;
    private final GeneratedSlotRepository slotRepo;
    private final InterviewerRepository interviewerRepo;

    public SlotService(AvailabilityRuleRepository ruleRepo, GeneratedSlotRepository slotRepo, InterviewerRepository interviewerRepo) {
        this.ruleRepo = ruleRepo;
        this.slotRepo = slotRepo;
        this.interviewerRepo = interviewerRepo;
    }

    @Transactional
    public int generateSlots(Long interviewerId, Integer days) {
        int horizon = (days == null ? 14 : Math.min(Math.max(days, 1), 31));
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new NotFoundException("interviewer not found"));
        List<AvailabilityRule> rules = ruleRepo.findByInterviewerIdAndActiveTrue(interviewerId);
        if (rules.isEmpty()) return 0;

        LocalDate start = LocalDate.now(ZoneOffset.UTC);
        LocalDate end = start.plusDays(horizon);
        int createdOrUpserted = 0;

        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            int dow = d.getDayOfWeek().getValue(); // 1..7
            for (AvailabilityRule r : rules) {
                if (r.getDayOfWeek() != dow) continue;
                LocalTime cur = r.getStartTime();
                while (!cur.isAfter(r.getEndTime().minusMinutes(r.getSlotMinutes()))) {
                    Instant startAt = TimeUtils.toInstantUtc(d, cur);
                    Instant endAt = startAt.plusSeconds(r.getSlotMinutes() * 60L);
                    GeneratedSlot slot = slotRepo.findByInterviewerIdAndStartAt(interviewerId, startAt).orElse(null);
                    if (slot == null) {
                        slot = new GeneratedSlot();
                        slot.setInterviewer(interviewer);
                        slot.setStartAt(startAt);
                        slot.setEndAt(endAt);
                        slot.setStatus(SlotStatus.OPEN);
                        slotRepo.save(slot);
                        createdOrUpserted++;
                    } else {
                        // if exists but past, close it; else keep as-is
                        if (slot.getStartAt().isBefore(Instant.now())) {
                            slot.setStatus(SlotStatus.CLOSED);
                        }
                    }
                    cur = cur.plusMinutes(r.getSlotMinutes());
                }
            }
        }
        return createdOrUpserted;
    }

    @Transactional(readOnly = true)
    public DTOs.PageResponse<DTOs.SlotResponse> listOpenSlots(Long interviewerId, String cursor, int limit, Instant from, Instant to) {
        interviewerRepo.findById(interviewerId).orElseThrow(() -> new NotFoundException("interviewer not found"));
        int pageSize = Math.max(1, Math.min(limit, 50));
        Instant rangeFrom = (from == null ? Instant.now() : from);
        Instant rangeTo = (to == null ? rangeFrom.plus(Duration.ofDays(30)) : to);
        List<GeneratedSlot> slots;
        long[] decoded = CursorUtil.decode(cursor);
        if (decoded == null) {
            slots = slotRepo.findOpenSlotsInRange(interviewerId, SlotStatus.OPEN, rangeFrom, rangeTo, PageRequest.of(0, pageSize));
        } else {
            Instant startAt = Instant.ofEpochMilli(decoded[0]);
            long lastId = decoded[1];
            slots = slotRepo.findAfterCursor(interviewerId, SlotStatus.OPEN, startAt, lastId, rangeTo, PageRequest.of(0, pageSize));
        }
        List<DTOs.SlotResponse> items = slots.stream()
                .map(s -> new DTOs.SlotResponse(s.getId(), s.getStartAt(), s.getEndAt(), s.getStatus().name()))
                .toList();
        String nextCursor = null;
        if (!items.isEmpty()) {
            var last = slots.get(slots.size() - 1);
            nextCursor = CursorUtil.encode(last.getStartAt().toEpochMilli(), last.getId());
        }
        return new DTOs.PageResponse<>(items, nextCursor);
    }
}
