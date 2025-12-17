package com.example.scheduler.api;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/interviewers/{interviewerId}/slots")
public class SlotController {
    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public int generate(@PathVariable Long interviewerId,
                        @Valid @RequestBody(required = false) DTOs.GenerateSlotsRequest req) {
        Integer days = req == null ? null : req.days();
        return slotService.generateSlots(interviewerId, days);
    }

    @GetMapping
    public DTOs.PageResponse<DTOs.SlotResponse> list(@PathVariable Long interviewerId,
                                                     @RequestParam(required = false) String cursor,
                                                     @RequestParam(defaultValue = "20") int limit,
                                                     @RequestParam(required = false) Instant from,
                                                     @RequestParam(required = false) Instant to) {
        return slotService.listOpenSlots(interviewerId, cursor, limit, from, to);
    }
}
