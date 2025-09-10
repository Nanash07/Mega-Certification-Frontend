package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CertificationResponse {
    private Long id;
    private String code;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
}