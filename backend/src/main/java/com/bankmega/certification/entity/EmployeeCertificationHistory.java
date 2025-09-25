package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_certification_id", nullable = false)
    private EmployeeCertification employeeCertification;

    // 🔹 Snapshot sertifikat saat itu (JSON string)
    @Lob
    @Column(name = "snapshot", columnDefinition = "TEXT", nullable = false)
    private String snapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 20, nullable = false)
    private ActionType actionType;

    @Column(name = "action_at", nullable = false)
    private Instant actionAt;

    @Column(name = "action_by")
    private Long actionBy;

    public enum ActionType {
        CREATED,
        UPDATED,
        DELETED
    }
}