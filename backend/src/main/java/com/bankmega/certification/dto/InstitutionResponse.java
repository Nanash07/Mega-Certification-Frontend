package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InstitutionResponse {
    private Long id;
    private String name;
    private String type;
    private String address;
    private String contactPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}