package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeHistoryResponse;
import com.bankmega.certification.service.EmployeeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employee-histories")
@RequiredArgsConstructor
public class EmployeeHistoryController {

    private final EmployeeHistoryService historyService;

    @GetMapping
    public Page<EmployeeHistoryResponse> getHistories(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(defaultValue = "all") String actionType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return historyService.getPagedHistory(
                employeeId,
                actionType,
                search,
                startDate,
                endDate,
                pageable);
    }
}
