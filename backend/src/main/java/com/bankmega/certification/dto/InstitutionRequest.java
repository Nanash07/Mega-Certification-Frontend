package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class InstitutionRequest {
    private String name;
    private String type; // Internal / External
    private String address;
    private String contactPerson;
}