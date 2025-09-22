package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeImportLogResponse;
import com.bankmega.certification.dto.EmployeeImportResponse;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeImportService {

    private final RegionalRepository regionalRepo;
    private final DivisionRepository divisionRepo;
    private final UnitRepository unitRepo;
    private final JobPositionRepository jobPositionRepo;
    private final EmployeeRepository employeeRepo;
    private final EmployeeHistoryRepository historyRepo;
    private final EmployeeImportLogRepository logRepo;

    // ==== Cache Master Data ====
    private final Map<String, Regional> regionalCache = new HashMap<>();
    private final Map<String, Division> divisionCache = new HashMap<>();
    private final Map<String, Unit> unitCache = new HashMap<>();
    private final Map<String, JobPosition> jobCache = new HashMap<>();

    // ===================== DRYRUN & CONFIRM =====================
    public EmployeeImportResponse dryRun(MultipartFile file, User user) throws Exception {
        return process(file, true, user);
    }

    @Transactional
    public EmployeeImportResponse confirm(MultipartFile file, User user) throws Exception {
        EmployeeImportResponse response = process(file, false, user);
        response.setMessage("Import pegawai berhasil ✅ oleh " + user.getUsername());
        return response;
    }

    // ===================== LOG =====================
    public List<EmployeeImportLogResponse> getAllLogs() {
        return logRepo.findAll().stream().map(this::toResponse).toList();
    }

    public List<EmployeeImportLogResponse> getLogsByUser(Long userId) {
        return logRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    private EmployeeImportLogResponse toResponse(EmployeeImportLog log) {
        return EmployeeImportLogResponse.builder()
                .id(log.getId())
                .username(log.getUser().getUsername())
                .fileName(log.getFileName())
                .totalProcessed(log.getTotalProcessed())
                .totalCreated(log.getTotalCreated())
                .totalUpdated(log.getTotalUpdated())
                .totalMutated(log.getTotalMutated())
                .totalResigned(log.getTotalResigned())
                .totalErrors(log.getTotalErrors())
                .dryRun(log.isDryRun())
                .createdAt(log.getCreatedAt())
                .build();
    }

    // ===================== CORE IMPORT =====================
    private EmployeeImportResponse process(MultipartFile file, boolean dryRun, User user) throws Exception {
        int processed = 0, created = 0, updated = 0, mutated = 0, resigned = 0, errors = 0;
        List<String> errorDetails = new ArrayList<>();

        // Ambil semua NIP existing (lebih efisien pakai projection)
        Set<String> existingNips = employeeRepo.findAllBy()
                .stream()
                .map(EmployeeRepository.NipOnly::getNip)
                .collect(Collectors.toSet());

        Set<String> importedNips = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                processed++;

                try {
                    String regionalName = getCellValue(row.getCell(0));
                    String divisionName = getCellValue(row.getCell(1));
                    String unitName = getCellValue(row.getCell(2));
                    String jobName = getCellValue(row.getCell(3));
                    String nip = getCellValue(row.getCell(4));
                    String name = getCellValue(row.getCell(5));
                    String gender = getCellValue(row.getCell(6));
                    String email = getCellValue(row.getCell(7));
                    String skEffectiveStr = getCellValue(row.getCell(8));

                    if (nip == null || nip.isBlank()) continue;
                    importedNips.add(nip);

                    LocalDate skEffective = parseDateSafe(row.getCell(8), skEffectiveStr);

                    Regional regional = resolveRegional(regionalName, dryRun);
                    Division division = resolveDivision(divisionName, dryRun);
                    Unit unit = resolveUnit(unitName, dryRun);
                    JobPosition jobPos = resolveJob(jobName, dryRun);
                    if (jobPos == null) continue;

                    Employee emp = employeeRepo.findByNip(nip).orElse(null);

                    if (emp == null) {
                        created++;
                        if (!dryRun) {
                            emp = Employee.builder()
                                    .nip(nip)
                                    .name(name)
                                    .gender(gender)
                                    .email(email)
                                    .regional(regional)
                                    .division(division)
                                    .unit(unit)
                                    .jobPosition(jobPos)
                                    .status("ACTIVE")
                                    .joinDate(skEffective)
                                    .build();
                            employeeRepo.save(emp);

                            historyRepo.save(EmployeeHistory.builder()
                                    .employee(emp)
                                    .newJobPosition(jobPos)
                                    .effectiveDate(skEffective)
                                    .actionType("CREATED")
                                    .build());
                        }
                    } else {
                        boolean isMutasi = emp.getJobPosition() != null &&
                                !emp.getJobPosition().getId().equals(jobPos.getId());

                        boolean dataChanged = hasChanged(emp, name, email, gender, regional, division, unit);

                        if (isMutasi) {
                            mutated++;
                            if (!dryRun) {
                                JobPosition oldJob = emp.getJobPosition();
                                emp.setJobPosition(jobPos);
                                if (skEffective != null) emp.setJoinDate(skEffective);
                                employeeRepo.save(emp);

                                historyRepo.save(EmployeeHistory.builder()
                                        .employee(emp)
                                        .oldJobPosition(oldJob)
                                        .newJobPosition(jobPos)
                                        .effectiveDate(skEffective)
                                        .actionType("MUTASI")
                                        .build());
                            }
                        } else if (dataChanged) {
                            updated++;
                            if (!dryRun) {
                                emp.setName(name);
                                emp.setEmail(email);
                                emp.setGender(gender);
                                emp.setRegional(regional);
                                emp.setDivision(division);
                                emp.setUnit(unit);
                                if (skEffective != null) emp.setJoinDate(skEffective);
                                employeeRepo.save(emp);

                                historyRepo.save(EmployeeHistory.builder()
                                        .employee(emp)
                                        .newJobPosition(jobPos)
                                        .effectiveDate(skEffective)
                                        .actionType("UPDATED")
                                        .build());
                            }
                        }
                    }
                } catch (Exception e) {
                    errors++;
                    errorDetails.add("Row " + i + ": " + e.getMessage());
                }
            }
        }

        // 🔹 Deteksi Resign (lebih efisien dengan batch)
        Set<String> resignedNips = new HashSet<>(existingNips);
        resignedNips.removeAll(importedNips);
        resigned = resignedNips.size();

        if (!dryRun && !resignedNips.isEmpty()) {
            List<Employee> resignedEmployees = employeeRepo.findByNipIn(resignedNips);

            resignedEmployees.forEach(emp -> emp.setStatus("RESIGN"));
            employeeRepo.saveAll(resignedEmployees);

            List<EmployeeHistory> histories = resignedEmployees.stream()
                    .map(emp -> EmployeeHistory.builder()
                            .employee(emp)
                            .oldJobPosition(emp.getJobPosition())
                            .effectiveDate(LocalDate.now())
                            .actionType("RESIGN")
                            .build())
                    .toList();

            historyRepo.saveAll(histories);
        }

        if (!dryRun) {
            logRepo.save(EmployeeImportLog.builder()
                    .user(user)
                    .fileName(file.getOriginalFilename())
                    .totalProcessed(processed)
                    .totalCreated(created)
                    .totalUpdated(updated)
                    .totalMutated(mutated)
                    .totalResigned(resigned)
                    .totalErrors(errors)
                    .dryRun(false)
                    .build());
        }

        return EmployeeImportResponse.builder()
                .fileName(file.getOriginalFilename())
                .dryRun(dryRun)
                .processed(processed)
                .created(created)
                .updated(updated)
                .mutated(mutated)
                .resigned(resigned)
                .errors(errors)
                .errorDetails(errorDetails)
                .message(dryRun
                        ? "Dry run selesai ✅. Pegawai baru: " + created + ", resign: " + resigned
                        : "Import selesai ✅ oleh " + user.getUsername() + ". Pegawai baru: " + created + ", resign: " + resigned)
                .build();
    }

    // ===================== TEMPLATE =====================
    public ResponseEntity<byte[]> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");
            Row header = sheet.createRow(0);
            String[] cols = {
                    "Regional", "Division", "Unit", "JobTitle",
                    "NIP", "Name", "Gender", "Email", "SKEffective (yyyy-MM-dd)"
            };
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_template.xlsx")
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

    private LocalDate parseDateSafe(Cell cell, String value) {
        try {
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
            if (value == null || value.isBlank()) return null;
            if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private boolean hasChanged(Employee emp, String name, String email, String gender,
                               Regional regional, Division division, Unit unit) {
        return !Objects.equals(emp.getName(), name) ||
               !Objects.equals(emp.getEmail(), email) ||
               !Objects.equals(emp.getGender(), gender) ||
               !Objects.equals(emp.getRegional(), regional) ||
               !Objects.equals(emp.getDivision(), division) ||
               !Objects.equals(emp.getUnit(), unit);
    }

    private Regional resolveRegional(String name, boolean dryRun) {
        if (name == null || name.isBlank()) return null;
        return regionalCache.computeIfAbsent(name.toLowerCase(), key ->
            regionalRepo.findByNameIgnoreCase(name).orElseGet(() ->
                dryRun ? Regional.builder().id(-1L).name(name).build()
                       : regionalRepo.save(Regional.builder().name(name).build())
            )
        );
    }

    private Division resolveDivision(String name, boolean dryRun) {
        if (name == null || name.isBlank()) return null;
        return divisionCache.computeIfAbsent(name.toLowerCase(), key ->
            divisionRepo.findByNameIgnoreCase(name).orElseGet(() ->
                dryRun ? Division.builder().id(-1L).name(name).build()
                       : divisionRepo.save(Division.builder().name(name).build())
            )
        );
    }

    private Unit resolveUnit(String name, boolean dryRun) {
        if (name == null || name.isBlank()) return null;
        return unitCache.computeIfAbsent(name.toLowerCase(), key ->
            unitRepo.findByNameIgnoreCase(name).orElseGet(() ->
                dryRun ? Unit.builder().id(-1L).name(name).build()
                       : unitRepo.save(Unit.builder().name(name).build())
            )
        );
    }

    private JobPosition resolveJob(String name, boolean dryRun) {
        if (name == null || name.isBlank()) return null;
        return jobCache.computeIfAbsent(name.toLowerCase(), key ->
            jobPositionRepo.findByNameIgnoreCase(name).orElseGet(() ->
                dryRun ? JobPosition.builder().id(-1L).name(name).build()
                       : jobPositionRepo.save(JobPosition.builder().name(name).build())
            )
        );
    }
}