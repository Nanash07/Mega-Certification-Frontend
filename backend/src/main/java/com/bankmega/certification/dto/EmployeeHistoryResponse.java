package com.bankmega.certification.dto;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

import com.bankmega.certification.entity.EmployeeHistory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeHistoryResponse {
    private Long id;
    private Long employeeId;
    private String employeeNip;
    private String employeeName;

    private Long oldJobPositionId;
    private String oldJobTitle;
    private String oldUnitName;
    private String oldDivisionName;
    private String oldRegionalName;

    private Long newJobPositionId;
    private String newJobTitle;
    private String newUnitName;
    private String newDivisionName;
    private String newRegionalName;

    private LocalDate effectiveDate;
    private EmployeeHistory.EmployeeActionType actionType;
    private Instant actionAt;
}