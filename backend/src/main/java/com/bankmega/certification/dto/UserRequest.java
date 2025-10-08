package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private Long roleId;
    private Long employeeId; // optional
    private Boolean isActive;
}