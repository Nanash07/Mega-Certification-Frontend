package com.bankmega.certification.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UnitRequest {
    private Long divisionId;
    private String name;
}