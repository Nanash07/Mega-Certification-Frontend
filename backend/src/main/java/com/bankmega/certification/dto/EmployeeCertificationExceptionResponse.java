package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EmployeeCertificationExceptionResponse {
    private Long id;
    private Long employeeId;
    private String nip;
    private String employeeName;
    private String jobPositionTitle; // dari employee.jobPosition

    private Long certificationRuleId;
    private String certificationCode;
    private String certificationLevel;
    private String subFieldCode;

    private String reason;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}