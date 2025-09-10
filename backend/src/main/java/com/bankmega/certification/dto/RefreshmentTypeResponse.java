package com.bankmega.certification.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshmentTypeResponse {
    private Long id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}