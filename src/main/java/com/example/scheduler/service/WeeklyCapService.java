package com.example.scheduler.service;

import com.example.scheduler.api.dto.DTOs;
import com.example.scheduler.core.NotFoundException;
import com.example.scheduler.domain.Interviewer;
import com.example.scheduler.domain.WeeklyCap;
import com.example.scheduler.repository.InterviewerRepository;
import com.example.scheduler.repository.WeeklyCapRepository;
import com.example.scheduler.util.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class WeeklyCapService {
    private final WeeklyCapRepository capRepo;
    private final InterviewerRepository interviewerRepo;

    public WeeklyCapService(WeeklyCapRepository capRepo, InterviewerRepository interviewerRepo) {
        this.capRepo = capRepo;
        this.interviewerRepo = interviewerRepo;
    }

    @Transactional
    public DTOs.WeeklyCapResponse setCap(Long interviewerId, DTOs.WeeklyCapRequest req) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new NotFoundException("interviewer not found"));
        LocalDate weekStart = TimeUtils.weekStartUtc(req.weekStartDate());
        WeeklyCap cap = capRepo.findByInterviewerIdAndWeekStartDate(interviewerId, weekStart)
                .orElseGet(() -> {
                    WeeklyCap c = new WeeklyCap();
                    c.setInterviewer(interviewer);
                    c.setWeekStartDate(weekStart);
                    return c;
                });
        cap.setMaxInterviews(req.maxInterviews());
        capRepo.save(cap);
        return new DTOs.WeeklyCapResponse(cap.getWeekStartDate(), cap.getMaxInterviews());
    }

    @Transactional(readOnly = true)
    public DTOs.WeeklyCapResponse getCap(Long interviewerId, LocalDate weekStartDate) {
        LocalDate weekStart = TimeUtils.weekStartUtc(weekStartDate);
        WeeklyCap cap = capRepo.findByInterviewerIdAndWeekStartDate(interviewerId, weekStart)
                .orElseThrow(() -> new NotFoundException("weekly cap not set"));
        return new DTOs.WeeklyCapResponse(cap.getWeekStartDate(), cap.getMaxInterviews());
    }
}
