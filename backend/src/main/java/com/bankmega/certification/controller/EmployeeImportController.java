package com.bankmega.certification.controller;

import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.service.EmployeeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeImportController {

    private final EmployeeImportService importService;

    // ✅ Import Excel
    @PostMapping("/import")
    public ResponseEntity<List<Employee>> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Employee> result = importService.importExcel(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Download Template
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        return importService.downloadTemplate();
    }
}