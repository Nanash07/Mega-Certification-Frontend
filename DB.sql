-- 1. Master Pegawai
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    nip VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    job_title VARCHAR(100),
    job_level VARCHAR(50),
    join_date DATE,
    status VARCHAR(20),
    photo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 2. Akun User Aplikasi
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    employee_id BIGINT REFERENCES employees(id),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 3. Master Role
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Mapping User <-> Role
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Master Sertifikasi
CREATE TABLE certifications (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    is_wajib_6bln BOOLEAN DEFAULT FALSE,
    masa_berlaku INT,
    reminder_month INT,
    refreshment_type VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 6. Jenjang Sertifikasi
CREATE TABLE certification_levels (
    id BIGSERIAL PRIMARY KEY,
    level INT NOT NULL,
    name VARCHAR(50),
    certification_id BIGINT NOT NULL REFERENCES certifications(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Sub Bidang
CREATE TABLE sub_fields (
    id BIGSERIAL PRIMARY KEY,
    certification_id BIGINT NOT NULL REFERENCES certifications(id),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Master Lembaga
CREATE TABLE institutions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20),
    address VARCHAR(255),
    contact_person VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Relasi Pegawai <-> Sertifikat
CREATE TABLE employee_certifications (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    certification_id BIGINT NOT NULL REFERENCES certifications(id),
    sub_field_id BIGINT REFERENCES sub_fields(id),
    certification_level_id BIGINT REFERENCES certification_levels(id),
    institution_id BIGINT REFERENCES institutions(id),
    cert_number VARCHAR(100),
    cert_date DATE,
    valid_from DATE,
    valid_until DATE,
    file_url VARCHAR(255),
    status VARCHAR(30),
    process_type VARCHAR(30),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_at TIMESTAMP
);

-- 10. Log/Audit Proses Sertifikasi Pegawai (Histori)
CREATE TABLE certification_process_logs (
    id BIGSERIAL PRIMARY KEY,
    employee_certification_id BIGINT NOT NULL REFERENCES employee_certifications(id) ON DELETE CASCADE,
    process_type VARCHAR(30) NOT NULL,
    institution_id BIGINT REFERENCES institutions(id),
    process_date DATE,
    file_url VARCHAR(255),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Mapping Jabatan <-> Sertifikasi
CREATE TABLE job_certification_mappings (
    id BIGSERIAL PRIMARY KEY,
    job_title VARCHAR(100) NOT NULL,
    job_level VARCHAR(50),
    certification_id BIGINT NOT NULL REFERENCES certifications(id),
    sub_field_id BIGINT REFERENCES sub_fields(id),
    certification_level_id BIGINT REFERENCES certification_levels(id),
    is_wajib_6bln BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. Permissions (opsional, granular RBAC)
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 13. Tabel Histori Sertifikasi (Snapshot/Immutable, tanpa FK ke tabel utama)
CREATE TABLE employee_certification_histories (
    id BIGSERIAL PRIMARY KEY,
    employee_certification_id BIGINT,     -- id dari employee_certifications, tidak FK
    employee_id BIGINT,
    employee_nip VARCHAR(20),
    employee_name VARCHAR(100),
    certification_id BIGINT,
    certification_code VARCHAR(20),
    certification_name VARCHAR(50),
    sub_field_id BIGINT,
    sub_field_name VARCHAR(100),
    certification_level_id BIGINT,
    certification_level INT,
    certification_level_name VARCHAR(50),
    institution_id BIGINT,
    institution_name VARCHAR(100),
    cert_number VARCHAR(100),
    cert_date DATE,
    valid_from DATE,
    valid_until DATE,
    masa_berlaku_bulan INT,
    reminder_month INT,
    file_url VARCHAR(255),
    status VARCHAR(30),
    process_type VARCHAR(30),
    notes TEXT,
    action_type VARCHAR(20),         -- Created/Updated/Deleted
    action_by BIGINT,
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. Tabel Histori Data Pegawai (Snapshot/Immutable, tanpa FK ke tabel utama)
CREATE TABLE employee_histories (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    nip VARCHAR(20),
    name VARCHAR(100),
    email VARCHAR(100),
    job_title VARCHAR(100),
    job_level VARCHAR(50),
    join_date DATE,
    status VARCHAR(20),
    photo_url VARCHAR(255),
    action_type VARCHAR(20),         -- Created/Updated/Deleted/Imported/Mutasi
    action_by BIGINT,
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

