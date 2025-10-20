-- Ajustes para flujo de solicitudes (aceptación/declinación, disponibilidad y adjuntos)

-- Normalizar estado previo (mayúsculas para Enum)
UPDATE Solicitudes SET estado = UPPER(estado);

-- Nuevos campos para seguimiento de solicitudes
ALTER TABLE Solicitudes
    MODIFY estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    ADD COLUMN tipo_atencion VARCHAR(20) NOT NULL DEFAULT 'INMEDIATA',
    ADD COLUMN detalles_adicionales VARCHAR(500) NULL,
    ADD COLUMN requiere_videollamada TINYINT(1) DEFAULT 0,
    ADD COLUMN contacto_confirmado TINYINT(1) DEFAULT 0,
    ADD COLUMN observaciones_tecnico VARCHAR(500) NULL,
    ADD COLUMN motivo_declinar VARCHAR(30) NULL,
    ADD COLUMN motivo_declinar_detalle VARCHAR(500) NULL,
    ADD COLUMN fecha_decision DATETIME NULL,
    ADD COLUMN fecha_limite_respuesta DATETIME NULL;

UPDATE Solicitudes
SET tipo_atencion = 'INMEDIATA'
WHERE tipo_atencion IS NULL;

-- Adjuntos multimedia asociados a la solicitud
CREATE TABLE IF NOT EXISTS SolicitudAdjuntos (
    id_adjunto BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    nombre_archivo VARCHAR(200) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100),
    tamanio_bytes BIGINT,
    CONSTRAINT fk_adjuntos_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES Solicitudes(id_solicitud) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_solicitud_adjuntos_solicitud
    ON SolicitudAdjuntos (id_solicitud);

-- Disponibilidad dinámica del técnico
ALTER TABLE Tecnicos
    ADD COLUMN disponible_ahora TINYINT(1) DEFAULT 0,
    ADD COLUMN notas_disponibilidad VARCHAR(500) NULL;

ALTER TABLE HorariosTecnicos
    ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;

UPDATE HorariosTecnicos
SET dia = UPPER(dia)
WHERE dia IS NOT NULL;
