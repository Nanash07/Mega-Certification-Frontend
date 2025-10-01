package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeeCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relasi ke Employee
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 🔹 Snapshot nama jabatan saat sertifikat dibuat
    @Column(name = "job_position_title", length = 200)
    private String jobPositionTitle;

    // 🔹 Relasi ke CertificationRule
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_rule_id", nullable = false)
    private CertificationRule certificationRule;

    // 🔹 Lembaga penyelenggara (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @Column(name = "cert_number", length = 100)
    private String certNumber;

    @Column(name = "cert_date")
    private LocalDate certDate;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "reminder_date")
    private LocalDate reminderDate;

    @Column(name = "file_url", length = 500) 
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", length = 30)
    private ProcessType processType;

    // 🔹 Audit fields
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public enum Status {
        NOT_YET_CERTIFIED,
        PENDING,
        ACTIVE,
        DUE,
        EXPIRED,
        INVALID
    }

    public enum ProcessType {
        SERTIFIKASI,
        REFRESHMENT,
        TRAINING
    }
}