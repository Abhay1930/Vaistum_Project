package com.example.scheduler.util;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public final class TimeUtils {
    private TimeUtils() {}

    public static LocalDate weekStartUtc(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static Instant toInstantUtc(LocalDate date, LocalTime time) {
        return ZonedDateTime.of(date, time, ZoneOffset.UTC).toInstant();
    }
}
