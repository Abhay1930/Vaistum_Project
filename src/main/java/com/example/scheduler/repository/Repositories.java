package com.example.scheduler.repository;

import com.example.scheduler.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {}

public interface CandidateRepository extends JpaRepository<Candidate, Long> {}

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
    List<AvailabilityRule> findByInterviewerIdAndActiveTrue(Long interviewerId);
}

public interface WeeklyCapRepository extends JpaRepository<WeeklyCap, Long> {
    Optional<WeeklyCap> findByInterviewerIdAndWeekStartDate(Long interviewerId, LocalDate weekStartDate);
}

public interface WeeklyCounterRepository extends JpaRepository<WeeklyCounter, Long> {
    Optional<WeeklyCounter> findByInterviewerIdAndWeekStartDate(Long interviewerId, LocalDate weekStartDate);
}

public interface GeneratedSlotRepository extends JpaRepository<GeneratedSlot, Long> {
    Optional<GeneratedSlot> findByInterviewerIdAndStartAt(Long interviewerId, Instant startAt);

    @Query("select s from GeneratedSlot s where s.interviewer.id = :interviewerId and s.status = :status and s.startAt >= :from and s.startAt < :to order by s.startAt asc, s.id asc")
    List<GeneratedSlot> findOpenSlotsInRange(Long interviewerId, SlotStatus status, Instant from, Instant to, Pageable pageable);
}

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCandidateId(Long candidateId);
    Optional<Booking> findBySlotIdAndStatus(Long slotId, BookingStatus status);
}
