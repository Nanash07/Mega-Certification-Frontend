package com.bankmega.certification.controller;

import com.bankmega.certification.dto.JobCertificationMappingHistoryResponse;
import com.bankmega.certification.service.JobCertificationMappingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/job-certification-mapping-histories")
@RequiredArgsConstructor
public class JobCertificationMappingHistoryController {

    private final JobCertificationMappingHistoryService historyService;

    // ============================================================
    // 🔹 Get paginated histories
    // ============================================================
    @GetMapping
    public Page<JobCertificationMappingHistoryResponse> getPagedHistories(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String certCode,
            @RequestParam(required = false) String subField,
            @RequestParam(defaultValue = "all") String actionType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return historyService.getPagedHistory(
                jobName, certCode, subField, actionType, search, start, end, PageRequest.of(page, size));
    }
}
