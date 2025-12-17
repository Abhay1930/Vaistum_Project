package com.example.scheduler.util;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    @Test
    void weekStartUtc_isMonday() {
        LocalDate any = LocalDate.of(2025, 1, 1); // Wed
        LocalDate monday = TimeUtils.weekStartUtc(any);
        assertEquals(DayOfWeek.MONDAY, monday.getDayOfWeek());
        assertTrue(!monday.isAfter(any));
    }
}
