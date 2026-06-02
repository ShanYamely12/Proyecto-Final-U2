CREATE TABLE IF NOT EXISTS ciudadano (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    genero VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    credenciales VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tipo_denuncia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS denuncia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ubicacion VARCHAR(200) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    ciudadano_id BIGINT NOT NULL,
    tipo_id BIGINT NOT NULL,
    funcionario_id BIGINT,
    FOREIGN KEY (ciudadano_id) REFERENCES ciudadano(id),
    FOREIGN KEY (tipo_id) REFERENCES tipo_denuncia(id),
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

-- Datos semilla para Login
MERGE INTO funcionario (id, nombre, cargo, credenciales)
KEY(id) VALUES (1, 'admin', 'ADMINISTRADOR', 'admin');

MERGE INTO tipo_denuncia (id, nombre)
KEY(id) VALUES 
(1, 'Robo / Asalto'),
(2, 'Accidente de Tránsito'),
(3, 'Vandalismo'),
(4, 'Violencia Familiar'),
(5, 'Fraude / Estafa');
