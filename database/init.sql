-- =========================
-- EXTENSIONS
-- =========================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- ENUMS
-- =========================
CREATE TYPE user_role_enum AS ENUM ('ADMIN', 'OPERADOR', 'SUPER_ADMIN');
CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE');

-- =========================
-- TABLE: roles
-- =========================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name user_role_enum UNIQUE NOT NULL
);

-- =========================
-- TABLE: users
-- =========================
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    rol VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    update_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- =========================
-- TABLE: projects
-- =========================
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    board_id BIGINT NOT NULL, -- monday board id
    status status_enum DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_projects_board_id ON projects(board_id);

-- =========================
-- TABLE: monday_columns
-- =========================
CREATE TABLE monday_columns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    column_id VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- TABLE: elements
-- =========================
CREATE TABLE elements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status status_enum DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- TABLE: equipments
-- =========================
CREATE TABLE equipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status status_enum DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- TABLE: equipment_elements (N:M)
-- =========================
CREATE TABLE equipment_elements (
    equipment_id UUID REFERENCES equipments(id) ON DELETE CASCADE,
    element_id UUID REFERENCES elements(id) ON DELETE CASCADE,
    PRIMARY KEY (equipment_id, element_id)
);

-- =========================
-- TABLE: project_equipments
-- =========================
CREATE TABLE project_equipments (
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    equipment_id UUID REFERENCES equipments(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, equipment_id)
);

-- =========================
-- TABLE: user_projects
-- =========================
CREATE TABLE user_projects (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, project_id)
);

-- =========================
-- TRIGGER updated_at
-- =========================
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_projects_updated
BEFORE UPDATE ON projects
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_elements_updated
BEFORE UPDATE ON elements
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_equipments_updated
BEFORE UPDATE ON equipments
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

-- =========================
-- SEED DATA
-- =========================
INSERT INTO roles (name) VALUES ('ADMIN'), ('OPERADOR');

-- password: admin123 (encriptar en backend en producción)
INSERT INTO users (name, email, password, role_id)
VALUES (
    'Admin',
    'admin@mss.com',
    'admin123',
    1
);