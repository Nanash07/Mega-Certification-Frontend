package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class JobCertificationMappingRequest {
    private Long jobPositionId;
    private Long certificationRuleId;
    private Boolean isActive;
}