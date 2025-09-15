package com.bankmega.certification.service;

import com.bankmega.certification.dto.CertificationRuleRequest;
import com.bankmega.certification.dto.CertificationRuleResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.CertificationRuleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificationRuleService {

    private final CertificationRuleRepository ruleRepo;
    private final CertificationRepository certificationRepo;
    private final CertificationLevelRepository levelRepo;
    private final SubFieldRepository subFieldRepo;
    private final RefreshmentTypeRepository refreshmentRepo;

    // 🔹 Mapper entity -> DTO
    private CertificationRuleResponse toResponse(CertificationRule entity) {
        return CertificationRuleResponse.builder()
                .id(entity.getId())
                .certificationId(entity.getCertification().getId())
                .certificationName(entity.getCertification().getName())
                .certificationCode(entity.getCertification().getCode())
                .certificationLevelId(entity.getCertificationLevel() != null ? entity.getCertificationLevel().getId() : null)
                .certificationLevelName(entity.getCertificationLevel() != null ? entity.getCertificationLevel().getName() : null)
                .certificationLevelLevel(entity.getCertificationLevel() != null ? entity.getCertificationLevel().getLevel() : null)
                .subFieldId(entity.getSubField() != null ? entity.getSubField().getId() : null)
                .subFieldName(entity.getSubField() != null ? entity.getSubField().getName() : null)
                .subFieldCode(entity.getSubField() != null ? entity.getSubField().getCode() : null)
                .validityMonths(entity.getValidityMonths())
                .reminderMonths(entity.getReminderMonths())
                .refreshmentTypeId(entity.getRefreshmentType() != null ? entity.getRefreshmentType().getId() : null)
                .refreshmentTypeName(entity.getRefreshmentType() != null ? entity.getRefreshmentType().getName() : null)
                .wajibSetelahMasuk(entity.getWajibSetelahMasuk())
                .isActive(entity.getIsActive())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // 🔹 Paging + Filter + Search
    @Transactional(readOnly = true)
    public Page<CertificationRuleResponse> getPagedFiltered(
            List<Long> certIds,
            List<Long> levelIds,
            List<Long> subIds,
            String status,
            String search,
            Pageable pageable
    ) {
        Specification<CertificationRule> spec = CertificationRuleSpecification.notDeleted()
                .and(CertificationRuleSpecification.byCertIds(certIds))
                .and(CertificationRuleSpecification.byLevelIds(levelIds))
                .and(CertificationRuleSpecification.bySubIds(subIds))
                .and(CertificationRuleSpecification.byStatus(status))
                .and(CertificationRuleSpecification.bySearch(search));

        if (pageable.getSort().isUnsorted()) {
            Sort defaultSort = Sort.by("certification.code")
                    .ascending()
                    .and(Sort.by("certificationLevel.level").ascending())
                    .and(Sort.by("subField.code").ascending());

            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        return ruleRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // 🔹 All active rules
    @Transactional(readOnly = true)
    public List<CertificationRuleResponse> getAllActive() {
        return ruleRepo.findByIsActiveTrueAndDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 🔹 All non-deleted rules
    @Transactional(readOnly = true)
    public List<CertificationRuleResponse> getAll() {
        return ruleRepo.findByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 🔹 Create
    @Transactional
    public CertificationRuleResponse create(CertificationRuleRequest req) {
        Certification cert = certificationRepo.findById(req.getCertificationId())
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        CertificationLevel level = req.getCertificationLevelId() != null
                ? levelRepo.findById(req.getCertificationLevelId()).orElse(null)
                : null;

        SubField subField = req.getSubFieldId() != null
                ? subFieldRepo.findById(req.getSubFieldId()).orElse(null)
                : null;

        RefreshmentType refreshmentType = req.getRefreshmentTypeId() != null
                ? refreshmentRepo.findById(req.getRefreshmentTypeId()).orElse(null)
                : null;

        Optional<CertificationRule> existingOpt = ruleRepo
                .findByCertification_IdAndCertificationLevel_IdAndSubField_Id(
                        req.getCertificationId(),
                        req.getCertificationLevelId(),
                        req.getSubFieldId()
                );

        if (existingOpt.isPresent()) {
            CertificationRule existing = existingOpt.get();
            if (existing.getDeletedAt() != null) {
                existing.setDeletedAt(null);
                existing.setIsActive(true);
            } else {
                throw new RuntimeException("Certification Rule sudah ada untuk kombinasi ini!");
            }
            existing.setCertification(cert);
            existing.setCertificationLevel(level);
            existing.setSubField(subField);
            existing.setValidityMonths(req.getValidityMonths());
            existing.setReminderMonths(req.getReminderMonths());
            existing.setRefreshmentType(refreshmentType);
            existing.setWajibSetelahMasuk(req.getWajibSetelahMasuk());
            existing.setUpdatedAt(Instant.now());
            return toResponse(ruleRepo.save(existing));
        }

        CertificationRule entity = CertificationRule.builder()
                .certification(cert)
                .certificationLevel(level)
                .subField(subField)
                .validityMonths(req.getValidityMonths())
                .reminderMonths(req.getReminderMonths())
                .refreshmentType(refreshmentType)
                .wajibSetelahMasuk(req.getWajibSetelahMasuk())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return toResponse(ruleRepo.save(entity));
    }

    // 🔹 Update
    @Transactional
    public CertificationRuleResponse update(Long id, CertificationRuleRequest req) {
        CertificationRule existing = ruleRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification Rule not found"));

        if (req.getCertificationLevelId() != null) {
            existing.setCertificationLevel(
                    levelRepo.findById(req.getCertificationLevelId())
                            .orElseThrow(() -> new RuntimeException("Certification Level not found"))
            );
        }
        if (req.getSubFieldId() != null) {
            existing.setSubField(
                    subFieldRepo.findById(req.getSubFieldId())
                            .orElseThrow(() -> new RuntimeException("SubField not found"))
            );
        }
        if (req.getRefreshmentTypeId() != null) {
            existing.setRefreshmentType(
                    refreshmentRepo.findById(req.getRefreshmentTypeId())
                            .orElseThrow(() -> new RuntimeException("Refreshment Type not found"))
            );
        }

        existing.setValidityMonths(req.getValidityMonths());
        existing.setReminderMonths(req.getReminderMonths());
        existing.setWajibSetelahMasuk(req.getWajibSetelahMasuk());
        if (req.getIsActive() != null) {
            existing.setIsActive(req.getIsActive());
        }
        existing.setUpdatedAt(Instant.now());

        return toResponse(ruleRepo.save(existing));
    }

    // 🔹 Toggle status
    @Transactional
    public CertificationRuleResponse toggleStatus(Long id) {
        CertificationRule rule = ruleRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification Rule not found"));
        rule.setIsActive(rule.getIsActive() == null ? true : !rule.getIsActive());
        rule.setUpdatedAt(Instant.now());
        return toResponse(ruleRepo.save(rule));
    }

    // 🔹 Soft Delete
    @Transactional
    public void softDelete(Long id) {
        CertificationRule existing = ruleRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification Rule not found"));
        existing.setIsActive(false);
        existing.setDeletedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());
        ruleRepo.save(existing);
    }
}