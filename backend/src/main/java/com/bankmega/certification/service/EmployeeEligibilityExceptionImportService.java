package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeEligibilityExceptionImportLogResponse;
import com.bankmega.certification.dto.EmployeeEligibilityExceptionImportResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeEligibilityExceptionImportService {

    private final EmployeeRepository employeeRepo;
    private final CertificationRuleRepository ruleRepo;
    private final EmployeeEligibilityExceptionRepository exceptionRepo;
    private final EligibilityExceptionImportLogRepository logRepo;

    // ===================== DRYRUN & CONFIRM =====================
    public EmployeeEligibilityExceptionImportResponse dryRun(MultipartFile file, User user) throws Exception {
        return process(file, true, user);
    }

    @Transactional
    public EmployeeEligibilityExceptionImportResponse confirm(MultipartFile file, User user) throws Exception {
        EmployeeEligibilityExceptionImportResponse response = process(file, false, user);
        response.setMessage("Import exception berhasil oleh " + user.getUsername());
        return response;
    }

    // ===================== GET LOGS =====================
    public List<EmployeeEligibilityExceptionImportLogResponse> getAllLogsDto() {
        return logRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<EmployeeEligibilityExceptionImportLogResponse> getLogsByUserDto(Long userId) {
        return logRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private EmployeeEligibilityExceptionImportLogResponse toResponse(EmployeeEligibilityExceptionImportLog log) {
        return EmployeeEligibilityExceptionImportLogResponse.builder()
                .id(log.getId())
                .username(log.getUser().getUsername())
                .fileName(log.getFileName())
                .totalProcessed(log.getTotalProcessed())
                .totalCreated(log.getTotalCreated())
                .totalUpdated(log.getTotalUpdated())
                .totalDeactivated(log.getTotalDeactivated())
                .totalErrors(log.getTotalErrors())
                .dryRun(log.isDryRun())
                .createdAt(log.getCreatedAt())
                .build();
    }

    // ===================== CORE PROCESS =====================
    private EmployeeEligibilityExceptionImportResponse process(MultipartFile file, boolean dryRun, User user) throws Exception {
        List<String> errorDetails = new ArrayList<>();
        int processed = 0, created = 0, reactivated = 0, updated = 0, deactivated = 0, skipped = 0, errors = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header
                processed++;

                try {
                    String nip = getCellValue(row.getCell(0));
                    String name = getCellValue(row.getCell(1));
                    String certCode = getCellValue(row.getCell(2));
                    String levelStr = getCellValue(row.getCell(3));
                    String subCode = getCellValue(row.getCell(4));
                    String notes = getCellValue(row.getCell(5));
                    String activeFlag = getCellValue(row.getCell(6));

                    if (nip.isBlank() || certCode.isBlank()) {
                        throw new IllegalArgumentException("NIP & CertificationCode wajib diisi");
                    }

                    // 🔹 Cari employee
                    Employee emp = employeeRepo.findByNipAndDeletedAtIsNull(nip)
                            .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + nip));

                    // 🔹 Validasi nama opsional
                    if (name != null && !name.isBlank() &&
                            !emp.getName().equalsIgnoreCase(name.trim())) {
                        throw new IllegalArgumentException("Nama tidak sesuai dengan NIP (" + nip + ")");
                    }

                    // 🔹 Parse level
                    Integer level = null;
                    if (levelStr != null && !levelStr.isBlank()) {
                        try {
                            level = Integer.parseInt(levelStr.trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Level harus berupa angka, tapi dapat: " + levelStr);
                        }
                    }

                    // 🔹 Cari rule
                    CertificationRule rule = findRuleUnique(certCode, level, subCode);

                    // 🔹 Ambil exception (aktif / soft delete)
                    EmployeeEligibilityException anyException = exceptionRepo
                            .findFirstByEmployeeIdAndCertificationRuleId(emp.getId(), rule.getId())
                            .orElse(null);

                    boolean shouldActive = !"N".equalsIgnoreCase(activeFlag);

                    if (anyException == null) {
                        // ✅ CREATE baru
                        created++;
                        if (!dryRun) {
                            EmployeeEligibilityException ex = EmployeeEligibilityException.builder()
                                    .employee(emp)
                                    .certificationRule(rule)
                                    .isActive(shouldActive)
                                    .notes(notes)
                                    .createdAt(Instant.now())
                                    .build();
                            exceptionRepo.save(ex);
                        }

                    } else if (anyException.getDeletedAt() != null) {
                        // ✅ REACTIVATE
                        reactivated++;
                        if (!dryRun) {
                            anyException.setDeletedAt(null);
                            anyException.setIsActive(shouldActive);
                            anyException.setNotes(notes);
                            anyException.setUpdatedAt(Instant.now());
                            exceptionRepo.save(anyException);
                        }

                    } else if (!shouldActive && Boolean.TRUE.equals(anyException.getIsActive())) {
                        // ✅ DEACTIVATE
                        deactivated++;
                        if (!dryRun) {
                            anyException.setIsActive(false);
                            anyException.setDeletedAt(Instant.now());
                            exceptionRepo.save(anyException);
                        }

                    } else if (!Objects.equals(anyException.getNotes(), notes) ||
                               !Objects.equals(anyException.getIsActive(), shouldActive)) {
                        // ✅ UPDATE
                        updated++;
                        if (!dryRun) {
                            anyException.setNotes(notes);
                            anyException.setIsActive(shouldActive);
                            anyException.setUpdatedAt(Instant.now());
                            exceptionRepo.save(anyException);
                        }

                    } else {
                        // ✅ SKIP
                        skipped++;
                    }

                } catch (Exception e) {
                    errors++;
                    errorDetails.add("Row " + (row.getRowNum() + 1) + ": ERROR → " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid file format", e);
        }

        // 🔹 Save log
        if (!dryRun) {
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User tidak boleh null saat simpan import log");
            }
            EmployeeEligibilityExceptionImportLog log = EmployeeEligibilityExceptionImportLog.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .totalProcessed(processed)
                    .totalCreated(created)
                    .totalUpdated(updated + reactivated)
                    .totalDeactivated(deactivated)
                    .totalErrors(errors)
                    .dryRun(false)
                    .createdAt(Instant.now())
                    .build();
            logRepo.save(log);
        }

        return EmployeeEligibilityExceptionImportResponse.builder()
                .fileName(file.getOriginalFilename())
                .dryRun(dryRun)
                .processed(processed)
                .created(created)
                .updated(updated + reactivated)
                .deactivated(deactivated)
                .errors(errors)
                .errorDetails(errorDetails)
                .message(dryRun
                        ? "Dry run selesai ✅. Baru: " + created + ", reactivate: " + reactivated + ", update: " + updated + ", nonaktif: " + deactivated + ", skip: " + skipped
                        : "Import selesai ✅ oleh " + user.getUsername())
                .build();
    }

    // ===================== HELPER =====================
    private CertificationRule findRuleUnique(String certCode, Integer level, String subFieldCode) {
        String subCode = (subFieldCode == null || subFieldCode.isBlank()) ? null : subFieldCode.trim();

        List<CertificationRule> candidates = ruleRepo.findByCertification_CodeIgnoreCaseAndDeletedAtIsNull(certCode.trim());

        List<CertificationRule> filtered = candidates.stream()
                .filter(rule ->
                        (level == null || (rule.getCertificationLevel() != null &&
                                Objects.equals(rule.getCertificationLevel().getLevel(), level))) &&
                        (subCode == null || (rule.getSubField() != null &&
                                subCode.equalsIgnoreCase(rule.getSubField().getCode()))))
                .toList();

        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("Certification Rule tidak ditemukan untuk code=" + certCode
                    + ", level=" + level + ", subField=" + subCode);
        }
        if (filtered.size() > 1) {
            throw new IllegalArgumentException("Certification Rule ambigu (lebih dari satu) untuk code=" + certCode
                    + ", level=" + level + ", subField=" + subCode);
        }

        return filtered.get(0);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    // ===================== TEMPLATE =====================
    @Transactional(readOnly = true)
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Exceptions");

            Row header = sheet.createRow(0);
            String[] columns = {"NIP", "Nama", "CertCode", "Level", "SubCode", "Notes", "ActiveFlag (Y/N)"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Contoh baris
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue("23101918");
            example.createCell(1).setCellValue("SITI RAHAYU");
            example.createCell(2).setCellValue("SMR");
            example.createCell(3).setCellValue("5");
            example.createCell(4).setCellValue("");
            example.createCell(5).setCellValue("Keterangan opsional");
            example.createCell(6).setCellValue("Y");

            byte[] bytes;
            try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
                workbook.write(out);
                bytes = out.toByteArray();
            }

            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exception_template.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(bytes.length)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat template Excel", e);
        }
    }
}