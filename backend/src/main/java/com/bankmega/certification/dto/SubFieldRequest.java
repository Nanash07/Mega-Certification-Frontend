package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class SubFieldRequest {
    private Long certificationId;
    private String name;
    private String code;
}