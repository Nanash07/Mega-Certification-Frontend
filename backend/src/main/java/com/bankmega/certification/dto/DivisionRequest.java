package com.bankmega.certification.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DivisionRequest {
    private Long regionalId;
    private String name;
}