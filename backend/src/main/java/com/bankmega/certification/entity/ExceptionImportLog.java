package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "exception_import_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fileName;

    private int totalProcessed;
    private int totalCreated;
    private int totalUpdated;
    private int totalDeactivated;
    private int totalErrors;

    private boolean dryRun;

    @Builder.Default
    @Column(updatable = false, nullable = false)
    private Instant createdAt = Instant.now();
}