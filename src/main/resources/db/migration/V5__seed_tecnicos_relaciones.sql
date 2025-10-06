-- Nuevos técnicos con asignación de categorías y servicios

-- Plomeros
INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Ana Plomera', 'plomero1@servitec.local', '5551110001', 'Av. Agua 123, CDMX', '/uploads/efirma/plomero1.png', '/uploads/certificacion/plomero1.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'plomero1@servitec.local');

INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Carlos Tuberías', 'plomero2@servitec.local', '5551110002', 'Calle Fuga 456, CDMX', '/uploads/efirma/plomero2.png', '/uploads/certificacion/plomero2.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'plomero2@servitec.local');

-- Electricistas
INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Lucía Voltios', 'electricista1@servitec.local', '5552220001', 'Av. Voltios 789, CDMX', '/uploads/efirma/electricista1.png', '/uploads/certificacion/electricista1.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'electricista1@servitec.local');

INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Miguel Corto', 'electricista2@servitec.local', '5552220002', 'Calle Cable 321, CDMX', '/uploads/efirma/electricista2.png', '/uploads/certificacion/electricista2.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'electricista2@servitec.local');

-- Electrodomésticos
INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Sara Electro', 'electro1@servitec.local', '5553330001', 'Av. Electrodomésticos 654, CDMX', '/uploads/efirma/electro1.png', '/uploads/certificacion/electro1.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'electro1@servitec.local');

INSERT INTO Tecnicos (nombre, correo, telefono, direccion, efirma, certificacion, contrasena, estado)
SELECT 'Diego Reparador', 'electro2@servitec.local', '5553330002', 'Calle Microondas 987, CDMX', '/uploads/efirma/electro2.png', '/uploads/certificacion/electro2.png', '{noop}password', 'DISPONIBLE'
WHERE NOT EXISTS (SELECT 1 FROM Tecnicos WHERE correo = 'electro2@servitec.local');

-- Relaciones con categorías
INSERT INTO Tecnico_Categoria (id_tecnico, id_categoria)
SELECT t.id_tecnico, c.id_categoria
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Plomería'
WHERE t.correo IN ('plomero1@servitec.local', 'plomero2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Categoria tc
    WHERE tc.id_tecnico = t.id_tecnico AND tc.id_categoria = c.id_categoria
  );

INSERT INTO Tecnico_Categoria (id_tecnico, id_categoria)
SELECT t.id_tecnico, c.id_categoria
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Electricidad'
WHERE t.correo IN ('electricista1@servitec.local', 'electricista2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Categoria tc
    WHERE tc.id_tecnico = t.id_tecnico AND tc.id_categoria = c.id_categoria
  );

INSERT INTO Tecnico_Categoria (id_tecnico, id_categoria)
SELECT t.id_tecnico, c.id_categoria
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Reparación de Electrodomésticos'
WHERE t.correo IN ('electro1@servitec.local', 'electro2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Categoria tc
    WHERE tc.id_tecnico = t.id_tecnico AND tc.id_categoria = c.id_categoria
  );

-- Relaciones con servicios (asignar cada técnico a los servicios de su categoría)
INSERT INTO Tecnico_Servicio (id_tecnico, id_servicio)
SELECT t.id_tecnico, s.id_servicio
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Plomería'
JOIN Servicios s ON s.id_categoria = c.id_categoria
WHERE t.correo IN ('plomero1@servitec.local', 'plomero2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Servicio ts
    WHERE ts.id_tecnico = t.id_tecnico AND ts.id_servicio = s.id_servicio
  );

INSERT INTO Tecnico_Servicio (id_tecnico, id_servicio)
SELECT t.id_tecnico, s.id_servicio
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Electricidad'
JOIN Servicios s ON s.id_categoria = c.id_categoria
WHERE t.correo IN ('electricista1@servitec.local', 'electricista2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Servicio ts
    WHERE ts.id_tecnico = t.id_tecnico AND ts.id_servicio = s.id_servicio
  );

INSERT INTO Tecnico_Servicio (id_tecnico, id_servicio)
SELECT t.id_tecnico, s.id_servicio
FROM Tecnicos t
JOIN Categorias c ON c.nombre_categoria = 'Reparación de Electrodomésticos'
JOIN Servicios s ON s.id_categoria = c.id_categoria
WHERE t.correo IN ('electro1@servitec.local', 'electro2@servitec.local')
  AND NOT EXISTS (
    SELECT 1 FROM Tecnico_Servicio ts
    WHERE ts.id_tecnico = t.id_tecnico AND ts.id_servicio = s.id_servicio
  );
