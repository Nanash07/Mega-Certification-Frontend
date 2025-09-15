package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class EmployeeExceptionResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String nip;
    private String jobPositionTitle;

    private Long certificationRuleId;
    private String certificationCode;
    private String certificationName;
    private String certificationLevelName;
    private Integer certificationLevelLevel;
    private String subFieldName;
    private String subFieldCode;

    private Boolean isActive;
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}