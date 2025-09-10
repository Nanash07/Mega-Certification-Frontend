package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JobCertificationMappingResponse {
    private Long id;

    private Long jobPositionId;
    private String jobName;

    private Long certificationRuleId;
    
    private String certificationName;
    private String certificationCode;

    private String certificationLevelName;
    private Integer certificationLevelLevel;

    private String subFieldName;
    private String subFieldCode;

    private Boolean isActive;

    private String ruleLabel;

    private Instant updatedAt;
    private Instant createdAt;
}
