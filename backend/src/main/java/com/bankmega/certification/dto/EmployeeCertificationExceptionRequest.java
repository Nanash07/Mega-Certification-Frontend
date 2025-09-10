package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class EmployeeCertificationExceptionRequest {
    private Long employeeId;
    private Long certificationRuleId;
    private String reason;
}
