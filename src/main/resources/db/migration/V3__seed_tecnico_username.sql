-- Seed técnico con username 'tecnico' para login por correo simple

INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
VALUES (
  'Tecnico Username',
  'tecnico',
  '7777777777',
  'Calle Taller 456, CDMX',
  '/uploads/efirma/tecnico.png',
  '/uploads/certificacion/tecnico.png',
  '{noop}password',
  'DISPONIBLE'
)
ON DUPLICATE KEY UPDATE telefono = VALUES(telefono);
