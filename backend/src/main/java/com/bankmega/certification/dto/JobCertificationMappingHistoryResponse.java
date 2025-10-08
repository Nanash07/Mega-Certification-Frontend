package com.bankmega.certification.dto;

import com.bankmega.certification.entity.JobCertificationMappingHistory;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCertificationMappingHistoryResponse {
    private Long id;

    private String jobName;
    private String certificationCode;
    private Integer certificationLevel;
    private String subFieldCode;
    private Boolean isActive;

    private JobCertificationMappingHistory.ActionType actionType;
    private Instant actionAt;
}
