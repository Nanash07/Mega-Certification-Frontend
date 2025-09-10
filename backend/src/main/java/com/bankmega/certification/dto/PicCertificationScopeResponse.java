package com.bankmega.certification.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PicCertificationScopeResponse {
    private Long userId;
    private String username;
    private String email;  
    private Instant createdAt;
    private Instant updatedAt;
    private List<ScopeDto> certifications;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScopeDto {
        private Long certificationId;
        private String certificationCode;
    }
}