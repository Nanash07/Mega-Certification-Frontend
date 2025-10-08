package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeCertificationHistoryResponse;
import com.bankmega.certification.service.EmployeeCertificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-certification-histories")
@RequiredArgsConstructor
public class EmployeeCertificationHistoryController {

    private final EmployeeCertificationHistoryService historyService;

    @GetMapping
    public Page<EmployeeCertificationHistoryResponse> getHistories(
            @RequestParam(required = false) Long certificationId,
            @RequestParam(defaultValue = "all") String actionType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return historyService.getPagedHistory(certificationId, actionType, search, pageable);
    }
}
