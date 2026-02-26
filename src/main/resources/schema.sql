DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS tratamiento;
DROP TABLE IF EXISTS cita_veterinaria;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS cliente;


-- 1. Tabla CLIENTE
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(9) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(255)
);

-- 2. Tabla VETERINARIO
CREATE TABLE veterinario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    numero_colegiado VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100)
);

-- 3. Tabla MASCOTA
CREATE TABLE mascota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raza VARCHAR(100),
    fecha_nacimiento DATE,
    peso DOUBLE,
    imagen_url VARCHAR(255),
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

-- 4. Tabla CITA_VETERINARIA
CREATE TABLE cita_veterinaria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(255),
    diagnostico VARCHAR(255),
    estado VARCHAR(50),
    mascota_id BIGINT,
    veterinario_id BIGINT,
    CONSTRAINT fk_cita_mascota FOREIGN KEY (mascota_id) REFERENCES mascota(id) ON DELETE CASCADE,
    CONSTRAINT fk_cita_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario(id) ON DELETE SET NULL
);

-- 5. Tabla TRATAMIENTO
CREATE TABLE tratamiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    medicamento VARCHAR(100),
    precio DOUBLE,
    observaciones TEXT,
    cita_id BIGINT,
    CONSTRAINT fk_tratamiento_cita FOREIGN KEY (cita_id) REFERENCES cita_veterinaria(id) ON DELETE CASCADE
);

-- 6. Tabla USUARIO
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    cliente_id BIGINT,
    veterinario_id BIGINT,
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinario(id) ON DELETE CASCADE
);