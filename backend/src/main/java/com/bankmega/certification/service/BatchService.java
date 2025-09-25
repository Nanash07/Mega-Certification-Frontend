package com.bankmega.certification.service;

import com.bankmega.certification.dto.BatchRequest;
import com.bankmega.certification.dto.BatchResponse;
import com.bankmega.certification.entity.Batch;
import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.Institution;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.BatchRepository;
import com.bankmega.certification.repository.CertificationRuleRepository;
import com.bankmega.certification.repository.InstitutionRepository;
import com.bankmega.certification.specification.BatchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final CertificationRuleRepository certificationRuleRepository;
    private final InstitutionRepository institutionRepository;

    // 🔹 Create
    public BatchResponse create(BatchRequest request) {
        Batch batch = fromRequest(request);
        return toResponse(batchRepository.save(batch));
    }

    // 🔹 Update
    public BatchResponse update(Long id, BatchRequest request) {
        Batch existing = batchRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Batch not found with id " + id));

        Batch updated = fromRequest(request);
        updated.setId(existing.getId()); // preserve ID

        return toResponse(batchRepository.save(updated));
    }

    // 🔹 Get by ID
    public BatchResponse getByIdResponse(Long id) {
        return toResponse(batchRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Batch not found with id " + id)));
    }

    // 🔹 Soft Delete
    public void delete(Long id) {
        Batch existing = batchRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Batch not found with id " + id));
        existing.setDeletedAt(Instant.now());
        batchRepository.save(existing);
    }

    // 🔹 Search + Filter + Paging
    public Page<BatchResponse> search(
            String search,
            Batch.Status status,
            Long certificationRuleId,
            Long institutionId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Batch> spec = BatchSpecification.notDeleted()
                .and(BatchSpecification.bySearch(search))
                .and(BatchSpecification.byStatus(status))
                .and(BatchSpecification.byCertificationRule(certificationRuleId))
                .and(BatchSpecification.byInstitution(institutionId))
                .and(BatchSpecification.byDateRange(startDate, endDate));

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Order.asc("startDate"))
            );
        }

        return batchRepository.findAll(spec, pageable).map(this::toResponse);
    }

    // =======================
    // 🔹 Mapping Helpers
    // =======================

    private Batch fromRequest(BatchRequest request) {
        CertificationRule rule = certificationRuleRepository.findById(request.getCertificationRuleId())
                .orElseThrow(() -> new NotFoundException("CertificationRule not found"));

        Institution institution = null;
        if (request.getInstitutionId() != null) {
            institution = institutionRepository.findById(request.getInstitutionId())
                    .orElseThrow(() -> new NotFoundException("Institution not found"));
        }

        return Batch.builder()
                .batchName(request.getBatchName())
                .certificationRule(rule)
                .institution(institution)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .quota(request.getQuota())
                .status(request.getStatus())
                .notes(request.getNotes())
                .build();
    }

    private BatchResponse toResponse(Batch b) {
        CertificationRule rule = b.getCertificationRule();

        return BatchResponse.builder()
                .id(b.getId())
                .batchName(b.getBatchName())

                // CertificationRule
                .certificationRuleId(rule != null ? rule.getId() : null)
                .certificationId(rule != null && rule.getCertification() != null ? rule.getCertification().getId() : null)
                .certificationName(rule != null && rule.getCertification() != null ? rule.getCertification().getName() : null)
                .certificationCode(rule != null && rule.getCertification() != null ? rule.getCertification().getCode() : null)

                // Level
                .certificationLevelId(rule != null && rule.getCertificationLevel() != null ? rule.getCertificationLevel().getId() : null)
                .certificationLevelName(rule != null && rule.getCertificationLevel() != null ? rule.getCertificationLevel().getName() : null)
                .certificationLevelLevel(rule != null && rule.getCertificationLevel() != null ? rule.getCertificationLevel().getLevel() : null)

                // Subfield
                .subFieldId(rule != null && rule.getSubField() != null ? rule.getSubField().getId() : null)
                .subFieldName(rule != null && rule.getSubField() != null ? rule.getSubField().getName() : null)
                .subFieldCode(rule != null && rule.getSubField() != null ? rule.getSubField().getCode() : null)

                // Rule metadata
                .validityMonths(rule != null ? rule.getValidityMonths() : null)
                .reminderMonths(rule != null ? rule.getReminderMonths() : null)
                .refreshmentTypeId(rule != null && rule.getRefreshmentType() != null ? rule.getRefreshmentType().getId() : null)
                .refreshmentTypeName(rule != null && rule.getRefreshmentType() != null ? rule.getRefreshmentType().getName() : null)
                .wajibSetelahMasuk(rule != null ? rule.getWajibSetelahMasuk() : null)
                .isActiveRule(rule != null ? rule.getIsActive() : null)

                // Institution
                .institutionId(b.getInstitution() != null ? b.getInstitution().getId() : null)
                .institutionName(b.getInstitution() != null ? b.getInstitution().getName() : null)

                // Batch data
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .quota(b.getQuota())
                .status(b.getStatus())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}