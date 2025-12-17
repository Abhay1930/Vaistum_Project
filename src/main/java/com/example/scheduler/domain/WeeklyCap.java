package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_cap", uniqueConstraints = @UniqueConstraint(name = "uq_weekly_cap", columnNames = {"interviewer_id", "week_start_date"}))
@Getter
@Setter
@NoArgsConstructor
public class WeeklyCap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate; // ISO week start (Monday)

    @Column(name = "max_interviews", nullable = false)
    private int maxInterviews;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
