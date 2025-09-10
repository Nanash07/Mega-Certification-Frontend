package com.bankmega.certification.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCertImportResponse {
    private String fileName;
    private int processed;
    private int inserted;
    private int reactivated;
    private int skipped;
    private int errors;
    private List<String> errorDetails;
    private boolean dryRun;
    private String message;
}
