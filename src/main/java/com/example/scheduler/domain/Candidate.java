package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 320, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
