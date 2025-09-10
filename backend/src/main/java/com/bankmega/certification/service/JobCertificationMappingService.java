package com.bankmega.certification.service;

import com.bankmega.certification.dto.JobCertificationMappingRequest;
import com.bankmega.certification.dto.JobCertificationMappingResponse;
import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.JobCertificationMapping;
import com.bankmega.certification.entity.JobPosition;
import com.bankmega.certification.repository.CertificationRuleRepository;
import com.bankmega.certification.repository.JobCertificationMappingRepository;
import com.bankmega.certification.repository.JobPositionRepository;
import com.bankmega.certification.specification.JobCertificationMappingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobCertificationMappingService {

    private final JobCertificationMappingRepository mappingRepo;
    private final JobPositionRepository jobPositionRepo;
    private final CertificationRuleRepository ruleRepo;

    // 🔹 Convert entity → DTO Response
    private JobCertificationMappingResponse toResponse(JobCertificationMapping m) {
        CertificationRule rule = m.getCertificationRule();

        return JobCertificationMappingResponse.builder()
                .id(m.getId())
                .jobPositionId(m.getJobPosition().getId())
                .jobName(m.getJobPosition().getName())

                .certificationRuleId(rule.getId())
                .certificationName(rule.getCertification() != null ? rule.getCertification().getName() : null)
                .certificationCode(rule.getCertification() != null ? rule.getCertification().getCode() : null)
                .certificationLevelName(rule.getCertificationLevel() != null ? rule.getCertificationLevel().getName() : null)
                .certificationLevelLevel(rule.getCertificationLevel() != null ? rule.getCertificationLevel().getLevel() : null)
                .subFieldName(rule.getSubField() != null ? rule.getSubField().getName() : null)
                .subFieldCode(rule.getSubField() != null ? rule.getSubField().getCode() : null)

                .isActive(m.getIsActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    // 🔹 Paging + Filter + Search (support multi-select filter)
    @Transactional(readOnly = true)
    public Page<JobCertificationMappingResponse> getPagedFiltered(
            List<Long> jobIds,
            List<String> certCodes,
            List<Integer> levels,
            List<String> subCodes,
            String status,
            String search,
            Pageable pageable
    ) {
        Specification<JobCertificationMapping> spec = JobCertificationMappingSpecification.notDeleted()
                .and(JobCertificationMappingSpecification.byJobIds(jobIds))
                .and(JobCertificationMappingSpecification.byCertCodes(certCodes))
                .and(JobCertificationMappingSpecification.byLevels(levels))
                .and(JobCertificationMappingSpecification.bySubCodes(subCodes))
                .and(JobCertificationMappingSpecification.byStatus(status))
                .and(JobCertificationMappingSpecification.bySearch(search));

        return mappingRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // 🔹 Create baru
    @Transactional
    public JobCertificationMappingResponse create(JobCertificationMappingRequest req) {
        // Cek duplikat (job + rule) yang belum soft-delete
        if (mappingRepo.existsByJobPosition_IdAndCertificationRule_IdAndDeletedAtIsNull(
                req.getJobPositionId(), req.getCertificationRuleId())) {
            throw new IllegalArgumentException("Mapping sudah ada untuk kombinasi ini");
        }

        JobPosition job = jobPositionRepo.findById(req.getJobPositionId())
                .orElseThrow(() -> new IllegalArgumentException("Job Position tidak ditemukan"));

        CertificationRule rule = ruleRepo.findById(req.getCertificationRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Certification Rule tidak ditemukan"));

        JobCertificationMapping mapping = JobCertificationMapping.builder()
                .jobPosition(job)
                .certificationRule(rule)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return toResponse(mappingRepo.save(mapping));
    }

    // 🔹 Update mapping
    @Transactional
    public JobCertificationMappingResponse update(Long id, JobCertificationMappingRequest req) {
        JobCertificationMapping mapping = mappingRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Mapping tidak ditemukan"));

        if (req.getJobPositionId() != null) {
            mapping.setJobPosition(jobPositionRepo.findById(req.getJobPositionId())
                    .orElseThrow(() -> new IllegalArgumentException("Job Position tidak ditemukan")));
        }

        if (req.getCertificationRuleId() != null) {
            mapping.setCertificationRule(ruleRepo.findById(req.getCertificationRuleId())
                    .orElseThrow(() -> new IllegalArgumentException("Certification Rule tidak ditemukan")));
        }

        if (req.getIsActive() != null) {
            mapping.setIsActive(req.getIsActive());
        }

        mapping.setUpdatedAt(Instant.now());
        return toResponse(mappingRepo.save(mapping));
    }

    // 🔹 Toggle aktif/nonaktif
    @Transactional
    public JobCertificationMappingResponse toggle(Long id) {
        JobCertificationMapping mapping = mappingRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Mapping tidak ditemukan"));

        mapping.setIsActive(!mapping.getIsActive());
        mapping.setUpdatedAt(Instant.now());

        return toResponse(mappingRepo.save(mapping));
    }

    // 🔹 Soft delete mapping
    @Transactional
    public void delete(Long id) {
        JobCertificationMapping mapping = mappingRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Mapping tidak ditemukan"));

        mapping.setIsActive(false);
        mapping.setDeletedAt(Instant.now());
        mapping.setUpdatedAt(Instant.now());

        mappingRepo.save(mapping);
    }

    // 🔹 Ambil semua mapping aktif untuk 1 jabatan
    @Transactional(readOnly = true)
    public List<JobCertificationMapping> getActiveMappingsByJobPosition(Long jobPositionId) {
        return mappingRepo.findAll(
                JobCertificationMappingSpecification.notDeleted()
                        .and(JobCertificationMappingSpecification.byJobIds(List.of(jobPositionId)))
                        .and(JobCertificationMappingSpecification.byStatus("active"))
        );
    }
}
