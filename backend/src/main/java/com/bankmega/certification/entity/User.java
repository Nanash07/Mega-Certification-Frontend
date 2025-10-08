package com.bankmega.certification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "username" }),
        @UniqueConstraint(columnNames = { "email" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    // ===================== PRIMARY KEY =====================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================== CREDENTIAL INFO =====================
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    /**
     * Email wajib diisi — digunakan untuk login, reset password, dan notifikasi.
     * Untuk user pegawai, email biasanya disamakan dengan employee.email.
     */
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    // ===================== STATUS =====================
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_first_login", nullable = false)
    private Boolean isFirstLogin = true;

    @Column(name = "last_login")
    private Instant lastLogin;

    // ===================== AUDIT =====================

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // ===================== RELATIONSHIPS =====================

    /**
     * User dapat terhubung ke satu pegawai (Employee) — nullable karena
     * superadmin/PIC
     * mungkin bukan bagian dari entitas pegawai.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PicCertificationScope> picCertificationScopes = new HashSet<>();
}
