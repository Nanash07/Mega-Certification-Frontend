package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "employee_eligibilities",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "certification_rule_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeeEligibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Relasi ke Employee
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 🔗 Relasi ke CertificationRule
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_rule_id", nullable = false)
    private CertificationRule certificationRule;

    // 🔹 Status eligibility
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EligibilityStatus status;

    // 🔹 Asal kewajiban
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EligibilitySource source;

    // 🔹 Batas waktu sertifikasi
    @Column(name = "due_date")
    private LocalDate dueDate;

    // 🔹 Snapshot aturan dari CertificationRule
    @Column(name = "validity_months")
    private Integer validityMonths;

    @Column(name = "reminder_months")
    private Integer reminderMonths;

    @Column(name = "wajib_setelah_masuk")
    private Integer wajibSetelahMasuk;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    // 🔹 Audit
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}