package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeImportLogResponse {
    private Long id;
    private String username;
    private String fileName;
    private int totalProcessed;
    private int totalCreated;
    private int totalUpdated;
    private int totalMutated;
    private int totalResigned;
    private int totalErrors;
    private boolean dryRun;
    private LocalDateTime createdAt;
}