package com.example.scheduler.service;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.core.NotFoundException;
import com.example.scheduler.domain.AvailabilityRule;
import com.example.scheduler.domain.Interviewer;
import com.example.scheduler.repository.AvailabilityRuleRepository;
import com.example.scheduler.repository.InterviewerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.scheduler.core.BusinessException;

@Service
public class AvailabilityService {
    private final AvailabilityRuleRepository ruleRepo;
    private final InterviewerRepository interviewerRepo;

    public AvailabilityService(AvailabilityRuleRepository ruleRepo, InterviewerRepository interviewerRepo) {
        this.ruleRepo = ruleRepo;
        this.interviewerRepo = interviewerRepo;
    }

    @Transactional
    public DTOs.AvailabilityRuleResponse createRule(Long interviewerId, DTOs.CreateAvailabilityRuleRequest req) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new NotFoundException("interviewer not found"));
        AvailabilityRule rule = new AvailabilityRule();
        rule.setInterviewer(interviewer);
        rule.setDayOfWeek(req.dayOfWeek());
        rule.setStartTime(req.startTime());
        rule.setEndTime(req.endTime());
        rule.setSlotMinutes(req.slotMinutes());
        rule.setActive(req.active() == null || req.active());
        rule = ruleRepo.save(rule);
        return new DTOs.AvailabilityRuleResponse(rule.getId(), rule.getDayOfWeek(), rule.getStartTime(), rule.getEndTime(), rule.getSlotMinutes(), rule.isActive());
    }

    @Transactional(readOnly = true)
    public List<DTOs.AvailabilityRuleResponse> listRules(Long interviewerId) {
        // verify interviewer exists
        interviewerRepo.findById(interviewerId).orElseThrow(() -> new NotFoundException("interviewer not found"));
        return ruleRepo.findByInterviewerIdAndActiveTrue(interviewerId).stream()
                .map(r -> new DTOs.AvailabilityRuleResponse(r.getId(), r.getDayOfWeek(), r.getStartTime(), r.getEndTime(), r.getSlotMinutes(), r.isActive()))
                .toList();
    }
}
