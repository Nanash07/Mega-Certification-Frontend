package com.bankmega.certification.dto;

import com.bankmega.certification.entity.EmployeeCertificationHistory;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertificationHistoryResponse {
    private Long id;
    private Long certificationId;
    private String snapshot; // JSON snapshot
    private EmployeeCertificationHistory.ActionType actionType;
    private Instant actionAt;
    private String actionBy;
}