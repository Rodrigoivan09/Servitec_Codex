-- Crea o actualiza la cuenta QA utilizada por el pipeline de seguridad

INSERT INTO Administradores (nombre, correo, telefono, contrasena)
VALUES ('QA Seguridad', 'qa.seguridad@servitec.local', '5555555555', 'Contraseña1#')
ON DUPLICATE KEY UPDATE
    telefono = VALUES(telefono),
    contrasena = VALUES(contrasena);
