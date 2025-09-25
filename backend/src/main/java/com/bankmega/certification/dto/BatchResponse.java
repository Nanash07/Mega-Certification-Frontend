// src/main/java/com/bankmega/certification/dto/BatchResponse.java

package com.bankmega.certification.dto;

import com.bankmega.certification.entity.Batch;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchResponse {
    private Long id;
    private String batchName;

    // 🔹 Certification
    private Long certificationRuleId;
    private Long certificationId;
    private String certificationName;
    private String certificationCode;

    // 🔹 Level
    private Long certificationLevelId;
    private String certificationLevelName;
    private Integer certificationLevelLevel;

    // 🔹 Subfield
    private Long subFieldId;
    private String subFieldName;
    private String subFieldCode;

    // 🔹 Rule Metadata
    private Integer validityMonths;
    private Integer reminderMonths;
    private Long refreshmentTypeId;
    private String refreshmentTypeName;
    private Integer wajibSetelahMasuk;
    private Boolean isActiveRule;

    // 🔹 Institution
    private Long institutionId;
    private String institutionName;

    // 🔹 Batch
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer quota;
    private Batch.Status status;
    private String notes;

    // 🔹 Audit
    private Instant createdAt;
    private Instant updatedAt;
}