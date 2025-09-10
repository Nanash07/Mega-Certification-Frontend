package com.bankmega.certification.service;

import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeImportService {

    private final RegionalRepository regionalRepo;
    private final DivisionRepository divisionRepo;
    private final UnitRepository unitRepo;
    private final JobPositionRepository jobPositionRepo;
    private final EmployeeRepository employeeRepo;
    private final EmployeeHistoryRepository historyRepo;

    // ================== IMPORT ==================
    public List<Employee> importExcel(MultipartFile file) throws Exception {
        List<Employee> result = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String regionalName   = getCellValue(row.getCell(0));
                    String divisionName   = getCellValue(row.getCell(1));
                    String unitName       = getCellValue(row.getCell(2));
                    String jobName       = getCellValue(row.getCell(3));
                    String nip            = getCellValue(row.getCell(4));
                    String name           = getCellValue(row.getCell(5));
                    String gender         = getCellValue(row.getCell(6));
                    String email          = getCellValue(row.getCell(7));
                    String skEffectiveStr = getCellValue(row.getCell(8));

                    if (nip == null || nip.isBlank()) continue;

                    LocalDate skEffective = parseDateSafe(row.getCell(8), skEffectiveStr);

                    // 🔹 Auto-create master data (flat structure)
                    Regional regional = (regionalName == null || regionalName.isBlank()) ? null :
                            regionalRepo.findByNameIgnoreCase(regionalName)
                                    .orElseGet(() -> regionalRepo.save(Regional.builder().name(regionalName).build()));

                    Division division = (divisionName == null || divisionName.isBlank()) ? null :
                            divisionRepo.findByNameIgnoreCase(divisionName)
                                    .orElseGet(() -> divisionRepo.save(Division.builder().name(divisionName).build()));

                    Unit unit = (unitName == null || unitName.isBlank()) ? null :
                            unitRepo.findByNameIgnoreCase(unitName)
                                    .orElseGet(() -> unitRepo.save(Unit.builder().name(unitName).build()));

                    JobPosition jobPos = (jobName == null || jobName.isBlank()) ? null :
                            jobPositionRepo.findByNameIgnoreCase(jobName)
                                    .orElseGet(() -> jobPositionRepo.save(JobPosition.builder().name(jobName).build()));

                    if (jobPos == null) continue;

                    // 🔹 Employee (upsert by NIP)
                    Employee emp = employeeRepo.findByNip(nip)
                            .orElse(Employee.builder().nip(nip).build());
                    emp.setDeletedAt(null);

                    boolean isNew = emp.getId() == null;
                    boolean isMutasi = !isNew && emp.getJobPosition() != null &&
                            !emp.getJobPosition().getId().equals(jobPos.getId());

                    boolean dataChanged = !isNew && (
                            !safeEquals(emp.getName(), name) ||
                            !safeEquals(emp.getEmail(), email) ||
                            !safeEquals(emp.getGender(), gender) ||
                            !safeEquals(emp.getRegional(), regional) ||
                            !safeEquals(emp.getDivision(), division) ||
                            !safeEquals(emp.getUnit(), unit)
                    );

                    JobPosition oldJob = emp.getJobPosition();

                    // 🔹 Update data pegawai
                    emp.setName(name);
                    emp.setGender(gender);
                    emp.setEmail(email);
                    emp.setRegional(regional);
                    emp.setDivision(division);
                    emp.setUnit(unit);
                    emp.setJobPosition(jobPos);
                    emp.setStatus("ACTIVE");
                    if (skEffective != null) {
                        emp.setJoinDate(skEffective); // selalu update joinDate
                    }

                    Employee saved = employeeRepo.save(emp);
                    result.add(saved);

                    // 🔹 Save history
                    if (isNew) {
                        historyRepo.save(EmployeeHistory.builder()
                                .employee(saved)
                                .newJobPosition(jobPos)
                                .effectiveDate(skEffective)
                                .actionType("CREATED")
                                .build());
                    } else if (isMutasi) {
                        historyRepo.save(EmployeeHistory.builder()
                                .employee(saved)
                                .oldJobPosition(oldJob)
                                .newJobPosition(jobPos)
                                .effectiveDate(skEffective)
                                .actionType("MUTASI")
                                .build());
                    } else if (dataChanged) {
                        historyRepo.save(EmployeeHistory.builder()
                                .employee(saved)
                                .newJobPosition(jobPos)
                                .effectiveDate(skEffective)
                                .actionType("UPDATED")
                                .build());
                    }

                } catch (Exception exRow) {
                    System.out.println("⚠️ Error di row " + i + ": " + exRow.getMessage());
                    exRow.printStackTrace();
                }
            }
        }
        return result;
    }

    // ================== TEMPLATE ==================
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

    // ================== UTIL ==================
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
            } else if (value.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (Exception e) {
            System.out.println("⚠️ Gagal parse tanggal: " + value);
        }
        return null;
    }

    private boolean safeEquals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}