package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExceptionImportLogResponse {
    private Long id;
    private String username;
    private String fileName;
    private int totalProcessed;
    private int totalCreated;
    private int totalUpdated;
    private int totalDeactivated;
    private int totalErrors;
    private boolean dryRun;
    private LocalDateTime createdAt;
}