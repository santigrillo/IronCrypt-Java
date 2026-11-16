-- ==============================================================================
-- IronCrypt Database Schema (PostgreSQL)
-- Compatible con Jakarta EE 10 / JPA / GlassFish 7
-- ==============================================================================

-- 1. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Equipos
CREATE TABLE IF NOT EXISTS equipos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    creado_por BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla de Miembros del Equipo
CREATE TABLE IF NOT EXISTS miembros_equipo (
    id BIGSERIAL PRIMARY KEY,
    equipo_id BIGINT NOT NULL REFERENCES equipos(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    fecha_union TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_equipo_usuario UNIQUE (equipo_id, usuario_id)
);

-- 4. Tabla de Bóvedas (Personales si equipo_id IS NULL, o de Equipo si equipo_id IS NOT NULL)
CREATE TABLE IF NOT EXISTS bovedas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    equipo_id BIGINT REFERENCES equipos(id) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Tabla de Credenciales Cifradas (Ítems de Bóveda)
CREATE TABLE IF NOT EXISTS items_boveda (
    id BIGSERIAL PRIMARY KEY,
    boveda_id BIGINT NOT NULL REFERENCES bovedas(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    website_url VARCHAR(500),
    username VARCHAR(255),
    password_cifrada TEXT NOT NULL,
    iv_nonce VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    notas TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices recomendados para optimización de consultas
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_bovedas_usuario ON bovedas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_bovedas_equipo ON bovedas(equipo_id);
CREATE INDEX IF NOT EXISTS idx_items_boveda ON items_boveda(boveda_id);
CREATE INDEX IF NOT EXISTS idx_miembros_usuario ON miembros_equipo(usuario_id);
CREATE INDEX IF NOT EXISTS idx_miembros_equipo ON miembros_equipo(equipo_id);
