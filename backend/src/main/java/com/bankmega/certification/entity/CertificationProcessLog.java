package com.bankmega.certification.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "certification_process_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CertificationProcessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relasi ke sertifikat pegawai
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_certification_id", nullable = false)
    private EmployeeCertification employeeCertification;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", length = 30, nullable = false)
    private ProcessType processType;

    @Column(name = "process_date")
    private LocalDate processDate;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum ProcessType {
        REGISTERED,           // daftar batch
        ATTENDED,             // hadir ujian
        PASSED,               // dinyatakan lulus
        FAILED,               // dinyatakan gagal
        UPLOAD_CERTIFICATE,   // upload sertifikat resmi
        REUPLOAD_CERTIFICATE, // upload ulang sertifikat
        DELETE_CERTIFICATE,   // hapus sertifikat
        REFRESHMENT           // ikut refreshment
    }

}