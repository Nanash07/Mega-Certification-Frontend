package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class EmployeeEligibilityExceptionRequest {
    private Long employeeId;
    private Long certificationRuleId; // ✅ konsisten
    private String notes;
}