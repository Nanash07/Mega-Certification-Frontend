package com.bankmega.certification.dto;

import com.bankmega.certification.entity.EmployeeCertification;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertificationRequest {

    private Long employeeId;
    private Long certificationRuleId;
    private Long institutionId;

    private String certNumber;
    private LocalDate certDate;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String fileUrl;

    private EmployeeCertification.ProcessType processType;

    private String notes;
}