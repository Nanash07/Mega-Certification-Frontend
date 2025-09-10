package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubFieldResponse {
    private Long id;
    private String code;
    private String name;
    private Long certificationId;
    private String certificationName;
    private String certificationCode;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}