package com.example.scheduler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "idempotency_key", uniqueConstraints = @UniqueConstraint(name = "uq_idem", columnNames = {"scope", "key_hash"}))
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String scope;

    @Column(name = "key_hash", nullable = false, length = 128)
    private String keyHash;

    @Column(name = "response_hash", length = 256)
    private String responseHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
