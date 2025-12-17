package com.example.scheduler.api;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviewers/{interviewerId}/availability-rules")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTOs.AvailabilityRuleResponse create(@PathVariable Long interviewerId,
                                                @Valid @RequestBody DTOs.CreateAvailabilityRuleRequest req) {
        return availabilityService.createRule(interviewerId, req);
    }

    @GetMapping
    public List<DTOs.AvailabilityRuleResponse> list(@PathVariable Long interviewerId) {
        return availabilityService.listRules(interviewerId);
    }
}
