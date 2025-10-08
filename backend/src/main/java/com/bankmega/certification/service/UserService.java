package com.bankmega.certification.service;

import com.bankmega.certification.dto.UserRequest;
import com.bankmega.certification.dto.UserResponse;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.Role;
import com.bankmega.certification.entity.User;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.EmployeeRepository;
import com.bankmega.certification.repository.RoleRepository;
import com.bankmega.certification.repository.UserRepository;
import com.bankmega.certification.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EmployeeRepository empRepo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // ===================== MAPPER =====================
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .employeeId(user.getEmployee() != null ? user.getEmployee().getId() : null)
                .employeeNip(user.getEmployee() != null ? user.getEmployee().getNip() : null)
                .employeeName(user.getEmployee() != null ? user.getEmployee().getName() : null)
                .isActive(user.getIsActive())
                .isFirstLogin(user.getIsFirstLogin())
                .createdAt(user.getCreatedAt() != null ? FORMATTER.format(user.getCreatedAt()) : null)
                .updatedAt(user.getUpdatedAt() != null ? FORMATTER.format(user.getUpdatedAt()) : null)
                .build();
    }

    // ===================== PAGINATION + FILTER =====================
    @Transactional(readOnly = true)
    public Page<UserResponse> getPage(Long roleId, Boolean isActive, String q, Pageable pageable) {
        Specification<User> spec = UserSpecification.notDeleted()
                .and(UserSpecification.byRoleId(roleId))
                .and(UserSpecification.byIsActive(isActive))
                .and(UserSpecification.bySearch(q));

        Pageable sorted = pageable.getSort().isUnsorted()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("username")))
                : pageable;

        return userRepo.findAll(spec, sorted).map(this::toResponse);
    }

    // ===================== GET ONE =====================
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userRepo.findByIdAndDeletedAtIsNull(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("User dengan id " + id + " tidak ditemukan"));
    }

    // ===================== CREATE =====================
    @Transactional
    public UserResponse create(UserRequest req) {
        validateUnique(req.getUsername(), req.getEmail());

        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan dengan id " + req.getRoleId()));

        Employee emp = null;
        if (req.getEmployeeId() != null) {
            emp = empRepo.findById(req.getEmployeeId())
                    .orElseThrow(
                            () -> new NotFoundException("Employee tidak ditemukan dengan id " + req.getEmployeeId()));
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()))
                .role(role)
                .employee(emp)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .isFirstLogin(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return toResponse(userRepo.save(user));
    }

    // ===================== UPDATE =====================
    @Transactional
    public UserResponse update(Long id, UserRequest req) {
        User user = userRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan dengan id " + id));

        if (!Objects.equals(user.getUsername(), req.getUsername()) &&
                userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ConflictException("Username sudah digunakan: " + req.getUsername());
        }

        if (!Objects.equals(user.getEmail(), req.getEmail()) &&
                userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ConflictException("Email sudah digunakan: " + req.getEmail());
        }

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
            user.setIsFirstLogin(true);
        }

        if (req.getRoleId() != null) {
            Role role = roleRepo.findById(req.getRoleId())
                    .orElseThrow(() -> new NotFoundException("Role tidak ditemukan dengan id " + req.getRoleId()));
            user.setRole(role);
        }

        if (req.getEmployeeId() != null) {
            Employee emp = empRepo.findById(req.getEmployeeId())
                    .orElseThrow(
                            () -> new NotFoundException("Employee tidak ditemukan dengan id " + req.getEmployeeId()));
            user.setEmployee(emp);
        }

        if (req.getIsActive() != null) {
            user.setIsActive(req.getIsActive());
        }

        user.setUpdatedAt(Instant.now());
        return toResponse(userRepo.save(user));
    }

    // ===================== SOFT DELETE =====================
    @Transactional
    public void softDelete(Long id) {
        User user = userRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan dengan id " + id));

        user.setDeletedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        userRepo.save(user);
    }

    // ===================== TOGGLE STATUS =====================
    @Transactional
    public UserResponse toggleStatus(Long id) {
        User user = userRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan dengan id " + id));

        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(Instant.now());

        return toResponse(userRepo.save(user));
    }

    // ===================== PASSWORD MANAGEMENT =====================
    @Transactional
    public void changePasswordFirstLogin(Long userId, String newPassword) {
        User user = userRepo.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("User tidak ditemukan dengan id " + userId));

        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setIsFirstLogin(false);
        user.setUpdatedAt(Instant.now());
        userRepo.save(user);
    }

    // ===================== AUTO CREATE USER DARI EMPLOYEE =====================
    @Transactional
    public User createUserForEmployee(Employee emp, Role pegawaiRole) {
        return userRepo.findByEmployee(emp).orElseGet(() -> {
            if (emp.getEmail() == null || emp.getEmail().isBlank()) {
                throw new ConflictException("Pegawai " + emp.getName() + " tidak memiliki email.");
            }

            if (userRepo.findByUsername(emp.getNip()).isPresent()) {
                throw new ConflictException("Username (NIP) sudah digunakan: " + emp.getNip());
            }

            User newUser = User.builder()
                    .username(emp.getNip())
                    .email(emp.getEmail())
                    .password(BCrypt.hashpw(emp.getNip(), BCrypt.gensalt()))
                    .role(pegawaiRole)
                    .employee(emp)
                    .isActive(true)
                    .isFirstLogin(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            return userRepo.save(newUser);
        });
    }

    // ===================== ACTIVE USERS =====================
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActive() {
        return userRepo.findByDeletedAtIsNull().stream()
                .filter(User::getIsActive)
                .map(this::toResponse)
                .toList();
    }

    // 🔹 Search user aktif berdasarkan keyword (untuk React Select)
    @Transactional(readOnly = true)
    public List<UserResponse> searchActiveUsers(String q) {
        Specification<User> spec = UserSpecification.notDeleted()
                .and(UserSpecification.byIsActive(true))
                .and(UserSpecification.bySearch(q));

        return userRepo.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    // ===================== UTILITIES =====================
    private void validateUnique(String username, String email) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new ConflictException("Username sudah digunakan: " + username);
        }
        if (email != null && userRepo.findByEmail(email).isPresent()) {
            throw new ConflictException("Email sudah digunakan: " + email);
        }
    }
}
