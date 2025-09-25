package com.bankmega.certification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBatchRequest {
    private Long employeeId;
    private Long batchId;
}