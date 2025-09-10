package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CertificationLevelResponse {
    private Long id;
    private Integer level;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}