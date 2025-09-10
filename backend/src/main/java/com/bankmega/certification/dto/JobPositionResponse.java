package com.bankmega.certification.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPositionResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}