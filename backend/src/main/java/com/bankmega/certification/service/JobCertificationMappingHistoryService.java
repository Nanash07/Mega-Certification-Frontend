package com.bankmega.certification.service;

import com.bankmega.certification.dto.JobCertificationMappingHistoryResponse;
import com.bankmega.certification.entity.JobCertificationMapping;
import com.bankmega.certification.entity.JobCertificationMappingHistory;
import com.bankmega.certification.repository.JobCertificationMappingHistoryRepository;
import com.bankmega.certification.specification.JobCertificationMappingHistorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobCertificationMappingHistoryService {

    private final JobCertificationMappingHistoryRepository historyRepo;

    // ============================================================
    // 🔹 Snapshot otomatis saat ada aksi CREATE / UPDATE / TOGGLE / DELETE
    // ============================================================
    public void snapshot(JobCertificationMapping mapping, JobCertificationMappingHistory.ActionType action) {
        if (mapping == null)
            return;

        try {
            JobCertificationMappingHistory h = JobCertificationMappingHistory.builder()
                    .mapping(mapping)
                    .jobName(mapping.getJobPosition() != null ? mapping.getJobPosition().getName() : null)
                    .certificationCode(mapping.getCertificationRule() != null
                            && mapping.getCertificationRule().getCertification() != null
                                    ? mapping.getCertificationRule().getCertification().getCode()
                                    : null)
                    .certificationLevel(mapping.getCertificationRule() != null
                            && mapping.getCertificationRule().getCertificationLevel() != null
                                    ? mapping.getCertificationRule().getCertificationLevel().getLevel()
                                    : null)
                    .subFieldCode(mapping.getCertificationRule() != null
                            && mapping.getCertificationRule().getSubField() != null
                                    ? mapping.getCertificationRule().getSubField().getCode()
                                    : null)
                    .isActive(mapping.getIsActive())
                    .actionType(action)
                    .actionAt(Instant.now())
                    .build();

            historyRepo.save(h);
            log.info("✅ History mapping tersimpan: [{} - {}] action={}",
                    h.getJobName(), h.getCertificationCode(), action);
        } catch (Exception e) {
            log.error("❌ Gagal menyimpan history mapping: {}", e.getMessage());
        }
    }

    // ============================================================
    // 🔹 Ambil data history dengan filter (paging + spec)
    // ============================================================
    @Transactional(readOnly = true)
    public Page<JobCertificationMappingHistoryResponse> getPagedHistory(
            String jobName,
            String certCode,
            String subField,
            String actionType,
            String search,
            Instant start,
            Instant end,
            Pageable pageable) {
        Specification<JobCertificationMappingHistory> spec = JobCertificationMappingHistorySpecification
                .byJobName(jobName)
                .and(JobCertificationMappingHistorySpecification.byCertCode(certCode))
                .and(JobCertificationMappingHistorySpecification.bySubField(subField))
                .and(JobCertificationMappingHistorySpecification.byActionType(actionType))
                .and(JobCertificationMappingHistorySpecification.byDateRange(start, end))
                .and(JobCertificationMappingHistorySpecification.bySearch(search));

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "actionAt"));

        return historyRepo.findAll(spec, sorted).map(this::toResponse);
    }

    // ============================================================
    // 🔹 Mapping Entity → DTO
    // ============================================================
    private JobCertificationMappingHistoryResponse toResponse(JobCertificationMappingHistory h) {
        if (h == null)
            return null;

        return JobCertificationMappingHistoryResponse.builder()
                .id(h.getId())
                .jobName(h.getJobName())
                .certificationCode(h.getCertificationCode())
                .certificationLevel(h.getCertificationLevel())
                .subFieldCode(h.getSubFieldCode())
                .isActive(h.getIsActive())
                .actionType(h.getActionType())
                .actionAt(h.getActionAt())
                .build();
    }
}
