package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobCertImportLogResponse {
    private Long id;
    private String username;
    private String fileName;
    private int totalProcessed;
    private int totalInserted;
    private int totalReactivated;
    private int totalSkipped;
    private int totalErrors;
    private boolean dryRun;
    private LocalDateTime createdAt;
}
