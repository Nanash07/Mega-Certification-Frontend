package com.bankmega.certification.controller;

import com.bankmega.certification.dto.CertificationRuleHistoryResponse;
import com.bankmega.certification.service.CertificationRuleHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certification-rule-histories")
@RequiredArgsConstructor
public class CertificationRuleHistoryController {

    private final CertificationRuleHistoryService historyService;

    @GetMapping
    public Page<CertificationRuleHistoryResponse> getHistories(
            @RequestParam(required = false) Long ruleId,
            @RequestParam(defaultValue = "all") String actionType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return historyService.getPagedHistory(ruleId, actionType, search, pageable);
    }
}
