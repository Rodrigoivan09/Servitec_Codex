-- Flyway migration: esquema inicial + seeds
-- Nota: No crear/eliminar base de datos aquí; docker-compose crea ServitecDB.

-- Tabla Usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL,
    direccion TEXT NOT NULL,
    contrasena VARCHAR(255) NOT NULL
);

-- Tabla Técnicos
CREATE TABLE IF NOT EXISTS Tecnicos (
    id_tecnico INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL UNIQUE,
    direccion TEXT NOT NULL,
    efirma VARCHAR(255) NOT NULL,
    certificacion VARCHAR(255) NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
);

-- Tabla Categorías
CREATE TABLE IF NOT EXISTS Categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla Servicios
CREATE TABLE IF NOT EXISTS Servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT NOT NULL,
    id_categoria INT NOT NULL,
    CONSTRAINT fk_servicio_categoria FOREIGN KEY (id_categoria) REFERENCES Categorias(id_categoria)
);

-- Tabla Tarifas
CREATE TABLE IF NOT EXISTS Tarifas (
    id_tarifa INT AUTO_INCREMENT PRIMARY KEY,
    id_servicio INT NOT NULL UNIQUE,
    tarifa_base DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_tarifa_servicio FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);

-- Tabla Horarios de Técnicos
CREATE TABLE IF NOT EXISTS HorariosTecnicos (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_tecnico INT NOT NULL,
    dia VARCHAR(10) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    UNIQUE (id_tecnico, dia, hora_inicio),
    CONSTRAINT fk_horario_tecnico FOREIGN KEY (id_tecnico) REFERENCES Tecnicos(id_tecnico)
);

-- Tabla Solicitudes
CREATE TABLE IF NOT EXISTS Solicitudes (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tecnico INT NOT NULL,
    id_servicio INT NOT NULL,
    fecha DATE NOT NULL,
    fecha_solicitud DATE NOT NULL,
    hora_llegada TIME NOT NULL DEFAULT '00:00:00',
    estado VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    direccion VARCHAR(255) NOT NULL,
    CONSTRAINT fk_solicitud_usuario FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    CONSTRAINT fk_solicitud_tecnico FOREIGN KEY (id_tecnico) REFERENCES Tecnicos(id_tecnico),
    CONSTRAINT fk_solicitud_servicio FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);

-- Tabla Pagos Simulados
CREATE TABLE IF NOT EXISTS Pagos_Simulados (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL UNIQUE,
    numero_tarjeta VARCHAR(16) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    cvv INT NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_pago_solicitud FOREIGN KEY (id_solicitud) REFERENCES Solicitudes(id_solicitud)
);

-- Tabla Evaluaciones
CREATE TABLE IF NOT EXISTS Evaluaciones (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL UNIQUE,
    id_tecnico INT NOT NULL,
    id_usuario INT NOT NULL,
    id_servicio INT,
    calificacion INT CHECK (calificacion BETWEEN 1 AND 5),
    comentarios TEXT,
    fecha_evaluacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_solicitud FOREIGN KEY (id_solicitud) REFERENCES Solicitudes(id_solicitud),
    CONSTRAINT fk_eval_tecnico FOREIGN KEY (id_tecnico) REFERENCES Tecnicos(id_tecnico),
    CONSTRAINT fk_eval_usuario FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    CONSTRAINT fk_eval_servicio FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);

-- Tabla Categorías-Servicios (Muchos a Muchos)
CREATE TABLE IF NOT EXISTS CategoriaServicio (
    id_categoria INT NOT NULL,
    id_servicio INT NOT NULL,
    PRIMARY KEY (id_categoria, id_servicio),
    CONSTRAINT fk_cs_categoria FOREIGN KEY (id_categoria) REFERENCES Categorias(id_categoria),
    CONSTRAINT fk_cs_servicio FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);

-- Relación Muchos a Muchos: Técnicos - Servicios
CREATE TABLE IF NOT EXISTS Tecnico_Servicio (
    id_tecnico INT NOT NULL,
    id_servicio INT NOT NULL,
    PRIMARY KEY (id_tecnico, id_servicio),
    CONSTRAINT fk_ts_tecnico FOREIGN KEY (id_tecnico) REFERENCES Tecnicos(id_tecnico),
    CONSTRAINT fk_ts_servicio FOREIGN KEY (id_servicio) REFERENCES Servicios(id_servicio)
);

-- Relación Muchos a Muchos: Técnicos - Categorías (duplicado en SQL original; se conserva el válido)
CREATE TABLE IF NOT EXISTS Tecnico_Categoria (
    id_tecnico INT NOT NULL,
    id_categoria INT NOT NULL,
    PRIMARY KEY (id_tecnico, id_categoria),
    CONSTRAINT fk_tc_tecnico FOREIGN KEY (id_tecnico) REFERENCES Tecnicos(id_tecnico),
    CONSTRAINT fk_tc_categoria FOREIGN KEY (id_categoria) REFERENCES Categorias(id_categoria)
);

-- Administradores
CREATE TABLE IF NOT EXISTS Administradores (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL,
    contrasena VARCHAR(255) NOT NULL
);

-- Seeds
INSERT INTO Administradores (nombre, correo, telefono, contrasena)
VALUES ('admin', 'admin', '9999999999', 'password')
ON DUPLICATE KEY UPDATE telefono=VALUES(telefono);

INSERT INTO Usuarios (nombre, correo, telefono, direccion, contrasena)
VALUES ('user', 'user', '5551234567', 'Calle Falsa 123, CDMX', 'password')
ON DUPLICATE KEY UPDATE telefono=VALUES(telefono);

INSERT INTO Categorias (nombre_categoria) VALUES
('Plomería'),
('Electricidad'),
('Reparación de Electrodomésticos')
ON DUPLICATE KEY UPDATE nombre_categoria=VALUES(nombre_categoria);

-- Servicios de Plomería (id_categoria = 1)
INSERT INTO Servicios (nombre_servicio, descripcion, id_categoria) VALUES
('Reparación de fugas', 'Detectar y reparar fugas de agua.', 1),
('Instalación de tuberías', 'Instalación de tuberías en baños y cocinas.', 1),
('Desazolve', 'Desazolve de drenajes y tuberías obstruidas.', 1)
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

-- Servicios de Electricidad (id_categoria = 2)
INSERT INTO Servicios (nombre_servicio, descripcion, id_categoria) VALUES
('Instalación eléctrica', 'Montaje de cableado eléctrico.', 2),
('Cambio de apagadores', 'Sustitución de apagadores dañados.', 2),
('Revisión de cortos', 'Detección de fallas eléctricas.', 2)
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

-- Servicios de Electrodomésticos (id_categoria = 3)
INSERT INTO Servicios (nombre_servicio, descripcion, id_categoria) VALUES
('Reparación de lavadora', 'Diagnóstico y reparación de lavadoras.', 3),
('Reparación de refrigerador', 'Mantenimiento de refrigeradores.', 3),
('Reparación de microondas', 'Revisión de microondas que no encienden.', 3)
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

-- Tarifas base
INSERT INTO Tarifas (id_servicio, tarifa_base) VALUES
(1, 500.00), (2, 700.00), (3, 600.00),
(4, 800.00), (5, 300.00), (6, 400.00),
(7, 750.00), (8, 850.00), (9, 500.00)
ON DUPLICATE KEY UPDATE tarifa_base=VALUES(tarifa_base);

