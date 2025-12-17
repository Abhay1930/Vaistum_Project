package com.example.scheduler.api;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.service.WeeklyCapService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/interviewers/{interviewerId}/weekly-cap")
public class CapController {
    private final WeeklyCapService capService;

    public CapController(WeeklyCapService capService) {
        this.capService = capService;
    }

    @PutMapping
    public DTOs.WeeklyCapResponse setCap(@PathVariable Long interviewerId,
                                         @Valid @RequestBody DTOs.WeeklyCapRequest req) {
        return capService.setCap(interviewerId, req);
    }

    @GetMapping
    public DTOs.WeeklyCapResponse getCap(@PathVariable Long interviewerId,
                                         @RequestParam LocalDate weekStartDate) {
        return capService.getCap(interviewerId, weekStartDate);
    }
}
