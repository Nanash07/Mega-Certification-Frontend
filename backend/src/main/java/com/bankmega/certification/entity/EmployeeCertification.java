package com.bankmega.certification.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.*;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 🔹 Relasi ke CertificationRule
    @ManyToOne(optional = false)
    @JoinColumn(name = "certification_rule_id", nullable = false)
    private CertificationRule certificationRule;

    // 🔹 Lembaga penyelenggara (optional)
    @ManyToOne
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

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", length = 30)
    private ProcessType processType;

    // 🔹 Audit fields pakai Instant
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    public enum Status {
        NOT_YET_CERTIFIED,
        ACTIVE,
        DUE,
        EXPIRED,
        REVOKED
    }

    public enum ProcessType {
        SERTIFIKASI,
        REFRESHMENT,
        TRAINING
    }
}
