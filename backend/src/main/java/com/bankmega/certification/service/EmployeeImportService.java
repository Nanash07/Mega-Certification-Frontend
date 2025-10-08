package com.bankmega.certification.service;

import com.bankmega.certification.dto.*;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeImportService {

    private final RegionalRepository regionalRepo;
    private final DivisionRepository divisionRepo;
    private final UnitRepository unitRepo;
    private final JobPositionRepository jobRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeImportLogRepository logRepo;
    private final EmployeeHistoryService historyService;
    private final UserService userService;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    // cache master data
    private final Map<String, Regional> regionalCache = new HashMap<>();
    private final Map<String, Division> divisionCache = new HashMap<>();
    private final Map<String, Unit> unitCache = new HashMap<>();
    private final Map<String, JobPosition> jobCache = new HashMap<>();

    // ===================== DRYRUN =====================
    public EmployeeImportResponse dryRun(MultipartFile file, User user) throws Exception {
        return process(file, true, user);
    }

    // ===================== CONFIRM =====================
    @Transactional
    public EmployeeImportResponse confirm(MultipartFile file, User user) throws Exception {
        EmployeeImportResponse res = process(file, false, user);
        res.setMessage("✅ Import pegawai berhasil oleh " + user.getUsername());
        return res;
    }

    // ===================== MAIN IMPORT =====================
    private EmployeeImportResponse process(MultipartFile file, boolean dryRun, User user) throws Exception {
        int processed = 0, created = 0, updated = 0, mutated = 0, resigned = 0, errors = 0;
        List<String> errorDetails = new ArrayList<>();

        Set<String> existingNips = empRepo.findAllBy().stream()
                .map(EmployeeRepository.NipOnly::getNip)
                .collect(Collectors.toSet());
        Set<String> importedNips = new HashSet<>();

        Set<String> existingUsernames = userRepo.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        Role pegawaiRole = roleRepo.findByNameIgnoreCase("Pegawai")
                .orElseGet(() -> roleRepo.save(Role.builder()
                        .name("Pegawai")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()));

        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                processed++;

                try {
                    String regionalName = fmt.formatCellValue(row.getCell(0)).trim();
                    String divisionName = fmt.formatCellValue(row.getCell(1)).trim();
                    String unitName = fmt.formatCellValue(row.getCell(2)).trim();
                    String jobName = fmt.formatCellValue(row.getCell(3)).trim();
                    String nip = fmt.formatCellValue(row.getCell(4)).trim();
                    String name = fmt.formatCellValue(row.getCell(5)).trim();
                    String gender = fmt.formatCellValue(row.getCell(6)).trim();
                    String email = fmt.formatCellValue(row.getCell(7)).trim();
                    String effStr = fmt.formatCellValue(row.getCell(8)).trim();

                    if (nip.isEmpty())
                        continue;
                    importedNips.add(nip);

                    LocalDate effDate = parseDateSafe(row.getCell(8), effStr);
                    Regional regional = resolveRegional(regionalName);
                    Division division = resolveDivision(divisionName);
                    Unit unit = resolveUnit(unitName);
                    JobPosition job = resolveJob(jobName);

                    Employee emp = empRepo.findByNip(nip).orElse(null);

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
                                    .jobPosition(job)
                                    .status("ACTIVE")
                                    .effectiveDate(effDate)
                                    .createdAt(Instant.now())
                                    .updatedAt(Instant.now())
                                    .build();

                            empRepo.save(emp);
                            historyService.snapshot(emp, EmployeeHistory.EmployeeActionType.CREATED, effDate);

                            // auto-create user
                            if (!existingUsernames.contains(nip)) {
                                userService.create(UserRequest.builder()
                                        .username(nip)
                                        .email(email)
                                        .password(nip)
                                        .roleId(pegawaiRole.getId())
                                        .employeeId(emp.getId())
                                        .isActive(true)
                                        .build());
                                existingUsernames.add(nip);
                            }
                        }
                    } else {
                        boolean mutasi = emp.getJobPosition() != null &&
                                !Objects.equals(emp.getJobPosition().getId(), job.getId());
                        boolean changed = hasChanged(emp, name, email, gender, regional, division, unit, job);

                        if (mutasi) {
                            mutated++;
                            if (!dryRun) {
                                JobPosition oldJob = emp.getJobPosition();
                                emp.setJobPosition(job);
                                emp.setUpdatedAt(Instant.now());
                                if (effDate != null)
                                    emp.setEffectiveDate(effDate);
                                empRepo.save(emp);
                                historyService.snapshot(emp, oldJob, job, effDate,
                                        EmployeeHistory.EmployeeActionType.MUTASI);
                            }
                        } else if (changed) {
                            updated++;
                            if (!dryRun) {
                                emp.setName(name);
                                emp.setEmail(email);
                                emp.setGender(gender);
                                emp.setRegional(regional);
                                emp.setDivision(division);
                                emp.setUnit(unit);
                                emp.setUpdatedAt(Instant.now());
                                if (effDate != null)
                                    emp.setEffectiveDate(effDate);
                                empRepo.save(emp);
                                historyService.snapshot(emp, EmployeeHistory.EmployeeActionType.UPDATED, effDate);
                            }
                        }
                    }

                } catch (Exception e) {
                    errors++;
                    errorDetails.add("Row " + i + ": " + e.getMessage());
                }
            }
        }

        // Handle resign
        Set<String> resignedNips = new HashSet<>(existingNips);
        resignedNips.removeAll(importedNips);
        resigned = resignedNips.size();
        if (!dryRun && !resignedNips.isEmpty()) {
            List<Employee> resignedEmployees = empRepo.findByNipInAndDeletedAtIsNull(resignedNips);
            resignedEmployees.forEach(emp -> {
                emp.setStatus("RESIGN");
                emp.setUpdatedAt(Instant.now());
                historyService.snapshot(emp, EmployeeHistory.EmployeeActionType.RESIGN, LocalDate.now());
            });
            empRepo.saveAll(resignedEmployees);
        }

        if (!dryRun) {
            historyService.flushBatch();
            saveImportLog(user, file, processed, created, updated, mutated, resigned, errors);
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
                        ? "✅ Dry run selesai. Pegawai baru: " + created + ", resign: " + resigned
                        : "✅ Import selesai oleh " + user.getUsername())
                .build();
    }

    // ===================== LOG IMPORT =====================
    public List<EmployeeImportLogResponse> getAllLogs() {
        return logRepo.findAll().stream()
                .map(this::toLogResponse)
                .sorted(Comparator.comparing(EmployeeImportLogResponse::getCreatedAt).reversed())
                .toList();
    }

    public List<EmployeeImportLogResponse> getLogsByUser(Long userId) {
        return logRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toLogResponse)
                .toList();
    }

    private EmployeeImportLogResponse toLogResponse(EmployeeImportLog log) {
        return EmployeeImportLogResponse.builder()
                .id(log.getId())
                .username(log.getUser() != null ? log.getUser().getUsername() : "-")
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

    // ===================== DOWNLOAD TEMPLATE =====================
    public ResponseEntity<byte[]> downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");
            Row header = sheet.createRow(0);
            String[] cols = {
                    "Regional", "Division", "Unit", "JobTitle",
                    "NIP", "Name", "Gender", "Email", "EffectiveDate (yyyy-MM-dd)"
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

    // ===================== UTILITIES =====================
    private void saveImportLog(User user, MultipartFile file, int processed,
            int created, int updated, int mutated, int resigned, int errors) {
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
                .createdAt(Instant.now())
                .build());
    }

    private boolean hasChanged(Employee emp, String name, String email, String gender,
            Regional reg, Division div, Unit unit, JobPosition job) {
        return !Objects.equals(emp.getName(), name)
                || !Objects.equals(emp.getEmail(), email)
                || !Objects.equals(emp.getGender(), gender)
                || !sameEntity(emp.getRegional(), reg)
                || !sameEntity(emp.getDivision(), div)
                || !sameEntity(emp.getUnit(), unit)
                || !sameEntity(emp.getJobPosition(), job);
    }

    private boolean sameEntity(Object a, Object b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        try {
            var idA = a.getClass().getMethod("getId").invoke(a);
            var idB = b.getClass().getMethod("getId").invoke(b);
            return Objects.equals(idA, idB);
        } catch (Exception e) {
            return a.equals(b);
        }
    }

    private LocalDate parseDateSafe(Cell cell, String val) {
        try {
            if (cell != null && cell.getCellType() == CellType.NUMERIC)
                return cell.getLocalDateTimeCellValue().toLocalDate();
            if (val.matches("\\d{4}-\\d{2}-\\d{2}"))
                return LocalDate.parse(val, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignored) {
        }
        return null;
    }

    private Regional resolveRegional(String name) {
        return resolveCached(name, regionalCache, regionalRepo::findByNameIgnoreCase,
                n -> regionalRepo.save(Regional.builder().name(n).build()));
    }

    private Division resolveDivision(String name) {
        return resolveCached(name, divisionCache, divisionRepo::findByNameIgnoreCase,
                n -> divisionRepo.save(Division.builder().name(n).build()));
    }

    private Unit resolveUnit(String name) {
        return resolveCached(name, unitCache, unitRepo::findByNameIgnoreCase,
                n -> unitRepo.save(Unit.builder().name(n).build()));
    }

    private JobPosition resolveJob(String name) {
        return resolveCached(name, jobCache, jobRepo::findByNameIgnoreCase,
                n -> jobRepo.save(JobPosition.builder().name(n).build()));
    }

    private <T> T resolveCached(String name, Map<String, T> cache,
            java.util.function.Function<String, Optional<T>> finder,
            java.util.function.Function<String, T> creator) {
        if (name == null || name.isBlank())
            return null;
        return cache.computeIfAbsent(name.toLowerCase(),
                key -> finder.apply(name).orElseGet(() -> creator.apply(name)));
    }
}
