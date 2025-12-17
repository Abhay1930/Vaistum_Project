package com.example.scheduler.api.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class DTOs {
    private DTOs() {}

    public record CreateAvailabilityRuleRequest(
            @NotNull Integer dayOfWeek,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            @NotNull @Min(5) @Max(240) Integer slotMinutes,
            Boolean active
    ) {}

    public record AvailabilityRuleResponse(
            Long id,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Integer slotMinutes,
            Boolean active
    ) {}

    public record WeeklyCapRequest(
            @NotNull LocalDate weekStartDate,
            @NotNull @Min(1) Integer maxInterviews
    ) {}

    public record WeeklyCapResponse(
            LocalDate weekStartDate,
            Integer maxInterviews
    ) {}

    public record GenerateSlotsRequest(
            @Min(1) @Max(31) Integer days
    ) {}

    public record SlotResponse(
            Long id,
            Instant startAt,
            Instant endAt,
            String status
    ) {}

    public record PageResponse<T>(
            List<T> items,
            String nextCursor
    ) {}

    public record BookingRequest(
            @NotNull Long candidateId,
            @NotNull Long interviewerId,
            @NotNull Long slotId,
            @Size(max = 128) String idempotencyKey
    ) {}

    public record RescheduleRequest(
            @NotNull Long newSlotId,
            @Size(max = 128) String idempotencyKey
    ) {}

    public record BookingResponse(
            Long bookingId,
            Long slotId,
            String status,
            String message
    ) {}
}
