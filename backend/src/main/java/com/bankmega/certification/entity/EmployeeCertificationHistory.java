package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "employee_certification_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmployeeCertificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relasi ke sertifikat pegawai
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_certification_id", nullable = false)
    private EmployeeCertification employeeCertification;

    // 🔹 Snapshot sertifikat saat itu (JSON string)
    @Column(name = "snapshot", columnDefinition = "TEXT", nullable = false)
    private String snapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 20, nullable = false)
    private ActionType actionType;

    @CreatedDate
    @Column(name = "action_at", nullable = false, updatable = false)
    private Instant actionAt;

    @Column(name = "action_by", length = 100)
    private String actionBy; // username / email PIC yang trigger

    public enum ActionType {
        CREATED,
        UPDATED,
        DELETED
    }
}
