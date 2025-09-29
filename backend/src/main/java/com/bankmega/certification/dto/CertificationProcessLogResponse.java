package com.bankmega.certification.dto;

import com.bankmega.certification.entity.CertificationProcessLog;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationProcessLogResponse {
    private Long id;
    private Long employeeCertificationId;
    private CertificationProcessLog.ProcessType processType;
    private LocalDate processDate;
    private String fileUrl;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}