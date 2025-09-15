package com.bankmega.certification.service;

import com.bankmega.certification.dto.ExceptionImportLogResponse;
import com.bankmega.certification.dto.ExceptionImportResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeExceptionImportService {

    private final EmployeeRepository employeeRepo;
    private final CertificationRuleRepository ruleRepo;
    private final EmployeeCertificationExceptionRepository exceptionRepo;
    private final ExceptionImportLogRepository logRepo;

    // ===================== DRYRUN & CONFIRM =====================
    public ExceptionImportResponse dryRun(MultipartFile file, User user) throws Exception {
        return process(file, true, user);
    }

    @Transactional
    public ExceptionImportResponse confirm(MultipartFile file, User user) throws Exception {
        ExceptionImportResponse response = process(file, false, user);
        response.setMessage("Import exception berhasil ✅ oleh " + user.getUsername());
        return response;
    }

    // ===================== LOG =====================
    public List<ExceptionImportLogResponse> getAllLogs() {
        return logRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<ExceptionImportLogResponse> getLogsByUser(Long userId) {
        return logRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    private ExceptionImportLogResponse toResponse(ExceptionImportLog log) {
        return ExceptionImportLogResponse.builder()
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

    // ===================== CORE IMPORT =====================
    private ExceptionImportResponse process(MultipartFile file, boolean dryRun, User user) throws Exception {
        int processed = 0, created = 0, updated = 0, deactivated = 0, errors = 0;
        List<String> errorDetails = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                processed++;

                try {
                    String nip = getCellValue(row.getCell(0));
                    String certCode = getCellValue(row.getCell(1));
                    String levelStr = getCellValue(row.getCell(2));
                    String subCode = getCellValue(row.getCell(3));
                    String notes = getCellValue(row.getCell(4));
                    String activeFlag = getCellValue(row.getCell(5));

                    if (nip == null || nip.isBlank() || certCode == null || certCode.isBlank()) {
                        errorDetails.add("Row " + i + ": NIP / CertificationCode kosong");
                        errors++;
                        continue;
                    }

                    Employee emp = employeeRepo.findByNipAndDeletedAtIsNull(nip)
                            .orElseThrow(() -> new RuntimeException("Employee not found: " + nip));

                    Integer level = (levelStr != null && !levelStr.isBlank())
                            ? Integer.parseInt(levelStr)
                            : null;

                    CertificationRule rule = ruleRepo
                            .findByCertification_CodeIgnoreCaseAndCertificationLevel_LevelAndSubField_CodeIgnoreCaseAndDeletedAtIsNull(
                                    certCode, level, subCode
                            ).orElseThrow(() -> new RuntimeException("CertificationRule not found: " + certCode));

                    EmployeeCertificationException exception = exceptionRepo
                            .findByEmployeeAndCertificationRule(emp, rule)
                            .orElse(null);

                    boolean shouldActive = !"N".equalsIgnoreCase(activeFlag);

                    if (exception == null) {
                        created++;
                        if (!dryRun) {
                            exception = EmployeeCertificationException.builder()
                                    .employee(emp)
                                    .certificationRule(rule)
                                    .isActive(shouldActive)
                                    .notes(notes)
                                    .build();
                            exceptionRepo.save(exception);
                        }
                    } else {
                        if (!Objects.equals(exception.getNotes(), notes) ||
                                !Objects.equals(exception.getIsActive(), shouldActive)) {
                            updated++;
                            if (!dryRun) {
                                exception.setNotes(notes);
                                exception.setIsActive(shouldActive);
                                exceptionRepo.save(exception);
                            }
                        } else if (!shouldActive && Boolean.TRUE.equals(exception.getIsActive())) {
                            deactivated++;
                            if (!dryRun) {
                                exception.setIsActive(false);
                                exception.setDeletedAt(Instant.now());
                                exceptionRepo.save(exception);
                            }
                        }
                    }
                } catch (Exception e) {
                    errors++;
                    errorDetails.add("Row " + i + ": " + e.getMessage());
                }
            }
        }

        // Save log kalau confirm
        if (!dryRun) {
            logRepo.save(ExceptionImportLog.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .totalProcessed(processed)
                    .totalCreated(created)
                    .totalUpdated(updated)
                    .totalDeactivated(deactivated)
                    .totalErrors(errors)
                    .dryRun(false)
                    .build());
        }

        return ExceptionImportResponse.builder()
                .fileName(file.getOriginalFilename())
                .dryRun(dryRun)
                .processed(processed)
                .created(created)
                .updated(updated)
                .deactivated(deactivated)
                .errors(errors)
                .errorDetails(errorDetails)
                .message(dryRun
                        ? "Dry run selesai ✅. Baru: " + created + ", update: " + updated + ", nonaktif: " + deactivated
                        : "Import selesai ✅ oleh " + user.getUsername())
                .build();
    }

    // ===================== TEMPLATE =====================
    public ResponseEntity<byte[]> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Exceptions");
            Row header = sheet.createRow(0);
            String[] cols = {
                    "NIP", "CertCode", "Level", "SubCode", "Notes", "ActiveFlag (Y/N)"
            };
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exception_template.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat template: " + e.getMessage(), e);
        }
    }

    // ===================== UTIL =====================
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}