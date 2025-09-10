package com.bankmega.certification.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}