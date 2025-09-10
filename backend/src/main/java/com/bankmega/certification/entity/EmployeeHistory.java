package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK ke employee utama
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // FK posisi lama & baru
    @ManyToOne
    @JoinColumn(name = "old_job_position_id")
    private JobPosition oldJobPosition;

    @ManyToOne
    @JoinColumn(name = "new_job_position_id")
    private JobPosition newJobPosition;

    // Snapshot lama
    private String oldJobTitle;
    private String oldUnitName;
    private String oldDivisionName;
    private String oldRegionalName;

    // Snapshot baru
    private String newJobTitle;
    private String newUnitName;
    private String newDivisionName;
    private String newRegionalName;

    private LocalDate effectiveDate;   // tanggal SK efektif
    private String actionType;         // CREATED / MUTASI / UPDATED / TERMINATED
    private String notes;

    private String actionBy;           // siapa yg trigger (System Import / Admin)
    private Instant actionAt;          // kapan dibuat
}