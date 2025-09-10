package com.bankmega.certification.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping
    public Map<String, Object> getDashboard(Authentication authentication) {
        // Ambil semua role user
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        System.out.println("AUTHORITY FROM CONTROLLER: " + roles);

        // Bikin response dinamis
        if (roles.contains("ROLE_SUPERADMIN")) {
            return Map.of(
                "role", "SUPERADMIN",
                "message", "Welcome Superadmin!",
                "menu", new String[]{"Kelola User", "Kelola Pegawai", "Kelola Sertifikasi", "Laporan All"}
            );
        } else if (roles.contains("ROLE_PIC")) {
            return Map.of(
                "role", "PIC",
                "message", "Welcome PIC!",
                "menu", new String[]{"Verifikasi Sertifikat", "Monitoring Pegawai", "Laporan PIC"}
            );
        } else if (roles.contains("ROLE_PEGAWAI")) {
            return Map.of(
                "role", "PEGAWAI",
                "message", "Welcome Pegawai!",
                "menu", new String[]{"Data Pribadi", "Status Sertifikat", "Ajukan Sertifikat"}
            );
        } else {
            // Kalau role gak dikenali
            return Map.of(
                "role", "UNKNOWN",
                "message", "Role tidak dikenal, akses terbatas.",
                "menu", new String[]{}
            );
        }
    }
}
