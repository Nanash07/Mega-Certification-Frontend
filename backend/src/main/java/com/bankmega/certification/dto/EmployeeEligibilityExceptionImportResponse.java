package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeEligibilityExceptionImportResponse {
    private String fileName;
    private boolean dryRun;
    private int processed;
    private int created;
    private int updated;
    private int deactivated;
    private int errors;
    private List<String> errorDetails;
    private String message;
}