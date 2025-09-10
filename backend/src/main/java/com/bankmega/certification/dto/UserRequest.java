package com.bankmega.certification.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private Long roleId;
    private Long employeeId;   // optional
    private Boolean isActive = true;
}