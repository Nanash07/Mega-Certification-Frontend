package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_import_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // asumsi User entity sudah ada

    private String fileName;

    private int totalProcessed;
    private int totalCreated;
    private int totalUpdated;
    private int totalMutated;
    private int totalResigned;
    private int totalErrors;

    private boolean dryRun;

    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}