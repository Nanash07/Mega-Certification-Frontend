package com.bankmega.certification.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBatchRequest {
    private List<Long> employeeIds;
    private Long employeeId;
    private Long batchId;
}