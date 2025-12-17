package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_counter", uniqueConstraints = @UniqueConstraint(name = "uq_weekly_counter", columnNames = {"interviewer_id", "week_start_date"}))
@Getter
@Setter
@NoArgsConstructor
public class WeeklyCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @Column(name = "confirmed_count", nullable = false)
    private int confirmedCount = 0;

    @Version
    private Long version;
}
