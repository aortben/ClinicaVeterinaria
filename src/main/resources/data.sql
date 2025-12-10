-- data.sql ACTUALIZADO

-- 1. CLIENTES (DNI únicos obligatorios)
INSERT INTO cliente (nombre, apellidos, dni, telefono, email, direccion) VALUES
('Juan', 'García Pérez', '11111111A', '600111222', 'juan.garcia@email.com', 'Calle Mayor 1'),
('Ana', 'López Díaz', '22222222B', '600333444', 'ana.lopez@email.com', 'Av. Constitución 23'),
('Pedro', 'Ramírez Soler', '33333333C', '700555666', 'pedro.ramirez@email.com', 'Plaza España 5'),
('María', 'Fernández Gil', '44444444D', '611223344', 'maria.fer@email.com', 'C/ Pez 8'),
('Luis', 'Gómez Arribas', '55555555E', '622998877', 'luis.gomez@email.com', 'Paseo del Prado 10'),
('Elena', 'Vázquez Rojo', '66666666F', '633445566', 'elena.vaz@email.com', 'Av. América 100');

-- 2. VETERINARIOS (Nº Colegiado único)
INSERT INTO veterinario (nombre, apellidos, numero_colegiado, email, especialidad) VALUES
('Laura', 'Martínez', 'VET-001', 'laura.vet@clinica.com', 'Cirugía'),
('Carlos', 'Ruiz', 'VET-002', 'carlos.vet@clinica.com', 'Medicina Interna'),
('Sofía', 'Alvarez', 'VET-003', 'sofia.vet@clinica.com', 'Dermatología'),
('Pablo', 'Sánchez', 'VET-004', 'pablo.vet@clinica.com', 'Exóticos');

-- 3. MASCOTAS
-- ID 1-3 (Juan), 4 (Ana), 5-6 (Pedro), 7 (María), 8 (Luis)
INSERT INTO mascota (nombre, especie, raza, fecha_nacimiento, peso, cliente_id) VALUES
('Bobby', 'Perro', 'Golden Retriever', '2020-05-20', 30.5, 1),
('Thor', 'Perro', 'Bulldog Francés', '2021-01-10', 12.0, 1),
('Luna', 'Gato', 'Común Europeo', '2019-08-15', 4.0, 1),
('Michi', 'Gato', 'Siamés', '2018-03-10', 4.5, 2),
('Lola', 'Ave', 'Periquito', '2022-03-01', 0.1, 3),
('Rocky', 'Perro', 'Pastor Alemán', '2017-11-05', 35.0, 3),
('Tambor', 'Conejo', 'Belier', '2021-06-20', 2.3, 4),
('Simba', 'Gato', 'Persa', '2020-09-12', 5.1, 5);

-- 4. CITAS
-- Pasadas (Realizadas)
INSERT INTO cita_veterinaria (fecha_hora, motivo, diagnostico, estado, mascota_id, veterinario_id) VALUES
('2023-10-01 10:00:00', 'Vacunación Rabia', 'Paciente sano. Se aplica vacuna.', 'Realizada', 1, 2),
('2023-10-05 11:30:00', 'Revisión anual', 'Todo correcto.', 'Realizada', 4, 2),
('2023-10-10 16:00:00', 'Cojera pata trasera', 'Pequeña torcedura. Reposo.', 'Realizada', 2, 1),
('2023-10-12 09:30:00', 'Pico largo', 'Se realiza limado.', 'Realizada', 5, 4);

-- Recientes / Pendientes
INSERT INTO cita_veterinaria (fecha_hora, motivo, diagnostico, estado, mascota_id, veterinario_id) VALUES
('2023-11-20 17:00:00', 'Vómitos', 'Gastroenteritis leve.', 'Realizada', 8, 2),
('2025-01-15 10:00:00', 'Revisión post-operatoria', NULL, 'Pendiente', 2, 1),
('2025-02-01 12:00:00', 'Vacuna Leishmania', NULL, 'Pendiente', 6, 2),
('2025-02-02 16:30:00', 'Revisión piel', NULL, 'Pendiente', 3, 3);

-- Canceladas
INSERT INTO cita_veterinaria (fecha_hora, motivo, diagnostico, estado, mascota_id, veterinario_id) VALUES
('2023-09-15 18:00:00', 'Limpieza dental', NULL, 'Cancelada', 1, 1),
('2023-11-01 11:00:00', 'Urgencia', NULL, 'Cancelada', 7, 4);

-- 5. TRATAMIENTOS (Asociados a las citas realizadas)
-- Cita 1 (Vacuna Bobby)
INSERT INTO tratamiento (descripcion, medicamento, precio, observaciones, cita_id) VALUES
('Vacuna Rabia', 'RabiesVac 2ml', 45.00, 'Sin reacciones adversas', 1),
('Revisión General', NULL, 20.00, 'Constantes normales', 1);

-- Cita 3 (Cojera Thor)
INSERT INTO tratamiento (descripcion, medicamento, precio, observaciones, cita_id) VALUES
('Consulta Traumatología', NULL, 50.00, 'Exploración física', 3),
('Antiinflamatorio Inyectable', 'Meloxicam', 15.50, 'Dosis inicial', 3),
('Vendaje', NULL, 10.00, 'Vendaje blando', 3);

-- Cita 4 (Pico Lola)
INSERT INTO tratamiento (descripcion, medicamento, precio, observaciones, cita_id) VALUES
('Limado de pico', NULL, 25.00, 'Con micromotor', 4);

-- Cita 5 (Vómitos Simba)
INSERT INTO tratamiento (descripcion, medicamento, precio, observaciones, cita_id) VALUES
('Consulta Urgencia', NULL, 60.00, 'Horario tarde', 5),
('Suero Subcutáneo', 'Ringer Lactato', 12.00, 'Hidratación', 5),
('Antiemético', 'Cerenia', 22.00, 'Inyectable', 5);