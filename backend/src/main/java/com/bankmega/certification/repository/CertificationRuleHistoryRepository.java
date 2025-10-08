package com.bankmega.certification.repository;

import com.bankmega.certification.entity.CertificationRuleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CertificationRuleHistoryRepository extends
        JpaRepository<CertificationRuleHistory, Long>,
        JpaSpecificationExecutor<CertificationRuleHistory> {

    // 🔹 Ambil semua history by ruleId, urut paling baru
    List<CertificationRuleHistory> findByCertificationRuleIdOrderByActionAtDesc(Long ruleId);

    // 🔹 Ambil history terakhir
    Optional<CertificationRuleHistory> findTopByCertificationRuleIdOrderByActionAtDesc(Long ruleId);
}
