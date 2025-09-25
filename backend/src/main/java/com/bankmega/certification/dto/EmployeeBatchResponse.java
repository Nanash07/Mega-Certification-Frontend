package com.bankmega.certification.dto;

import com.bankmega.certification.entity.EmployeeBatch.Status;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBatchResponse {

    private Long id;

    private Long employeeId;
    private String employeeNip;
    private String employeeName;

    private Long batchId;
    private String batchName;

    private Status status;  // ✅ Enum pakai EmployeeBatch.Status

    private Instant createdAt;
    private Instant updatedAt;
}