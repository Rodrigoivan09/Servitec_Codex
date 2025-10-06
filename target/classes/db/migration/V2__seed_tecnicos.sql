-- Seed técnicos de ejemplo para login de rol TECNICO

INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
VALUES (
  'Tecnico Demo',
  'tecnico@servitec.local',
  '6666666666',
  'Calle Taller 123, CDMX',
  '/uploads/efirma/demo.png',
  '/uploads/certificacion/demo.png',
  '{noop}password',
  'DISPONIBLE'
)
ON DUPLICATE KEY UPDATE telefono = VALUES(telefono);
