package com.bankmega.certification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Long roleId;
    private String roleName;
    private Long employeeId;
    private Boolean isActive;
    private Boolean isFirstLogin;
}