package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "generated_slot",
        uniqueConstraints = @UniqueConstraint(name = "uq_slot_start", columnNames = {"interviewer_id", "start_at"}))
@Getter
@Setter
@NoArgsConstructor
public class GeneratedSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interviewer_id")
    private Interviewer interviewer;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SlotStatus status = SlotStatus.OPEN;

    @Version
    private Long version;
}
