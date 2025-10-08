package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeHistoryResponse;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeHistory;
import com.bankmega.certification.entity.JobPosition;
import com.bankmega.certification.repository.EmployeeHistoryRepository;
import com.bankmega.certification.specification.EmployeeHistorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeHistoryService {

        private final EmployeeHistoryRepository historyRepo;

        // Buffer untuk batch insert
        private final List<EmployeeHistory> batchBuffer = new ArrayList<>();
        private static final int BATCH_SIZE = 200;

        // ===================== SNAPSHOT DENGAN BATCH =====================
        @Transactional
        public void snapshot(Employee emp,
                        JobPosition oldJob,
                        JobPosition newJob,
                        LocalDate effective,
                        EmployeeHistory.EmployeeActionType actionType) {
                if (emp == null)
                        return;

                // Skip kalau gak ada perubahan signifikan
                if ((actionType == EmployeeHistory.EmployeeActionType.UPDATED ||
                                actionType == EmployeeHistory.EmployeeActionType.MUTASI)
                                && !hasChanged(emp)) {
                        return;
                }

                EmployeeHistory history = EmployeeHistory.builder()
                                .employee(emp)
                                .employeeNip(emp.getNip())
                                .employeeName(emp.getName())
                                .oldJobPosition(oldJob)
                                .oldJobTitle(oldJob != null ? oldJob.getName() : null)
                                .oldUnitName(emp.getUnit() != null ? emp.getUnit().getName() : null)
                                .oldDivisionName(emp.getDivision() != null ? emp.getDivision().getName() : null)
                                .oldRegionalName(emp.getRegional() != null ? emp.getRegional().getName() : null)
                                .newJobPosition(newJob)
                                .newJobTitle(newJob != null ? newJob.getName() : null)
                                .newUnitName(emp.getUnit() != null ? emp.getUnit().getName() : null)
                                .newDivisionName(emp.getDivision() != null ? emp.getDivision().getName() : null)
                                .newRegionalName(emp.getRegional() != null ? emp.getRegional().getName() : null)
                                .effectiveDate(effective != null ? effective : emp.getEffectiveDate())
                                .actionType(actionType)
                                .actionAt(Instant.now())
                                .build();

                batchBuffer.add(history);

                if (batchBuffer.size() >= BATCH_SIZE) {
                        flushBatch();
                }
        }

        @Transactional
        public void flushBatch() {
                if (batchBuffer.isEmpty())
                        return;
                try {
                        historyRepo.saveAll(batchBuffer);
                        historyRepo.flush();
                        batchBuffer.clear();
                } catch (Exception e) {
                        log.error("Gagal batch insert history: {}", e.getMessage());
                }
        }

        public void snapshot(Employee emp, EmployeeHistory.EmployeeActionType actionType, LocalDate effectiveDate) {
                snapshot(emp, emp.getJobPosition(), emp.getJobPosition(), effectiveDate, actionType);
        }

        public void snapshot(Employee emp, EmployeeHistory.EmployeeActionType actionType) {
                snapshot(emp, emp.getJobPosition(), emp.getJobPosition(),
                                emp.getEffectiveDate(), actionType);
        }

        // ===================== CEK PERUBAHAN =====================
        private boolean hasChanged(Employee emp) {
                if (emp == null)
                        return false;
                EmployeeHistory last = historyRepo.findTopByEmployee_IdOrderByActionAtDesc(emp.getId()).orElse(null);
                if (last == null)
                        return true;

                return !Objects.equals(last.getEmployeeName(), emp.getName())
                                || !Objects.equals(last.getEmployeeNip(), emp.getNip())
                                || !Objects.equals(last.getNewJobTitle(),
                                                emp.getJobPosition() != null ? emp.getJobPosition().getName() : null)
                                || !Objects.equals(last.getNewUnitName(),
                                                emp.getUnit() != null ? emp.getUnit().getName() : null)
                                || !Objects.equals(last.getNewDivisionName(),
                                                emp.getDivision() != null ? emp.getDivision().getName() : null)
                                || !Objects.equals(last.getNewRegionalName(),
                                                emp.getRegional() != null ? emp.getRegional().getName() : null)
                                || !Objects.equals(last.getEffectiveDate(), emp.getEffectiveDate());
        }

        // ===================== PAGINATION =====================

        /**
         * Versi lama (tanpa filter tanggal) — biar backward compatible.
         */
        @Transactional(readOnly = true)
        public Page<EmployeeHistoryResponse> getPagedHistory(
                        Long employeeId, String actionType, String search, Pageable pageable) {

                return getPagedHistory(employeeId, actionType, search, null, null, pageable);
        }

        /**
         * Versi baru (support filter tanggal)
         */
        @Transactional(readOnly = true)
        public Page<EmployeeHistoryResponse> getPagedHistory(
                        Long employeeId,
                        String actionType,
                        String search,
                        LocalDate startDate,
                        LocalDate endDate,
                        Pageable pageable) {

                Specification<EmployeeHistory> spec = EmployeeHistorySpecification.byEmployeeId(employeeId)
                                .and(EmployeeHistorySpecification.byActionType(actionType))
                                .and(EmployeeHistorySpecification.bySearch(search))
                                .and(EmployeeHistorySpecification.byDateRange(startDate, endDate));

                Pageable sorted = PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                Sort.by(Sort.Direction.DESC, "actionAt"));

                return historyRepo.findAll(spec, sorted).map(this::toResponse);
        }

        // ===================== MAPPING ENTITY -> DTO =====================
        private EmployeeHistoryResponse toResponse(EmployeeHistory h) {
                return EmployeeHistoryResponse.builder()
                                .id(h.getId())
                                .employeeId(h.getEmployee() != null ? h.getEmployee().getId() : null)
                                .employeeNip(h.getEmployeeNip())
                                .employeeName(h.getEmployeeName())
                                .oldJobTitle(h.getOldJobTitle())
                                .newJobTitle(h.getNewJobTitle())
                                .newUnitName(h.getNewUnitName())
                                .newDivisionName(h.getNewDivisionName())
                                .newRegionalName(h.getNewRegionalName())
                                .effectiveDate(h.getEffectiveDate())
                                .actionType(h.getActionType())
                                .actionAt(h.getActionAt())
                                .build();
        }
}
