package com.example.scheduler.api;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.domain.Booking;
import com.example.scheduler.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOs.BookingResponse create(@Valid @RequestBody DTOs.BookingRequest req) {
        return bookingService.create(req);
    }

    @PatchMapping("/bookings/{bookingId}/reschedule")
    public DTOs.BookingResponse reschedule(@PathVariable Long bookingId,
                                           @Valid @RequestBody DTOs.RescheduleRequest req) {
        return bookingService.reschedule(bookingId, req);
    }

    @DeleteMapping("/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long bookingId) {
        bookingService.cancel(bookingId);
    }

    @GetMapping("/candidates/{candidateId}/bookings")
    public List<Booking> list(@PathVariable Long candidateId) {
        return bookingService.listByCandidate(candidateId);
    }
}
