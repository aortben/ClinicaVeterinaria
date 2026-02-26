INSERT INTO cliente (nombre, apellidos, dni, telefono, email, direccion) VALUES
('Vicente', 'Ruinez', '47344525C', '+34 655 88 77 11', 'vicenteruinez@tia.com', 'La tia'),
('Esther', 'Díaz Navarro', '29876543Z', '+34 687 12 34 56', 'esther.diaz.navarro@hotmail.com', 'Avda. Libertad 45'),
('Alvaro', 'Ortega Benitez', '47344555K', '+34 637 18 51 35', 'alvaroortegabenitez03@gmail.com', 'Calle 3 de abril 24'),
('Ricardo', 'Soto Gil', '71009876Q', '+34 722 33 44 55', 'r.soto.gil@outlook.es', 'Plaza Mayor 1, Bajo'),
('Silvia', 'Gómez Martín', '50123987F', '+34 633 99 00 11', 'silvia.gomez@gmail.com', 'Paseo de Gracia 101'),
('Javier', 'Hernández Paz', '45678123G', '+34 699 88 77 66', 'javi.hpaz@hotmail.com', 'Calle Alameda 5, 2º');

INSERT INTO veterinario (nombre, apellidos, numero_colegiado, email, especialidad) VALUES
('Laura', 'Martínez', 'VET-001', 'laura.martinez.vet@clinica.com', 'Cirugía'),
('Carlos', 'Ruiz', 'VET-002', 'carlos.ruiz.vet@clinica.com', 'Medicina Interna'),
('Sofía', 'Alvarez', 'VET-003', 'sofia.alvarez.derm@clinica.com', 'Dermatología'),
('Pablo', 'Sánchez', 'VET-004', 'pablo.sanchez.exo@clinica.com', 'Exóticos'),
('Ana', 'Vázquez', 'VET-005', 'ana.vazquez.oftal@clinica.com', 'Oftalmología');

INSERT INTO mascota (nombre, especie, raza, fecha_nacimiento, peso, cliente_id) VALUES
('Mortadelo', 'Perro', 'Caniche Toy', '2023-01-20', 5.5, 1),
('Filemón', 'Perro', 'Caniche Toy', '2023-01-20', 6.0, 1),
('Tambor', 'Conejo', 'Belier', '2024-06-01', 1.8, 2),
('Bimba', 'Perro', 'Breton', '2020-08-15', 10.3, 3),
('Arwen', 'Perro', 'Labrador', '2017-11-05', 36.2, 3),
('Wiwi', 'Gato', 'Siamés', '2018-03-10', 4.5, 4),
('Nermal', 'Gato', 'Persa', '2020-09-12', 5.3, 5);

INSERT INTO cita_veterinaria (fecha_hora, motivo, diagnostico, estado, mascota_id, veterinario_id) VALUES
('2025-01-10 10:00:00', 'Vacunación anual', 'Paciente sano. Se aplica DHPPi.', 'Realizada', 1, 2),
('2025-01-15 11:30:00', 'Revisión por picazón', 'Dermatitis atópica leve.', 'Realizada', 4, 3),
('2025-01-25 16:00:00', 'Traumatismo ocular', 'Úlcera corneal leve.', 'Realizada', 5, 5),
('2025-02-01 09:30:00', 'Crecimiento dental', 'Recorte de incisivos.', 'Realizada', 3, 4),
('2025-12-12 17:00:00', 'Revisión post-operatoria', NULL, 'Pendiente', 2, 1),
('2025-12-15 12:00:00', 'Vacuna Leishmania', NULL, 'Pendiente', 7, 2),
('2025-12-20 16:30:00', 'Revisión piel anual', NULL, 'Pendiente', 6, 3),
('2025-01-05 18:00:00', 'Limpieza dental', NULL, 'Cancelada', 4, 1);

INSERT INTO tratamiento (descripcion, medicamento, precio, observaciones, cita_id) VALUES
('Vacuna DHPPi', 'Canigen', 35.00, 'Lote 45B. Próximo recordatorio en 1 año.', 1),
('Revisión General', NULL, 20.00, 'Constantes vitales normales. Peso estable.', 1),
('Consulta Dermatología', NULL, 50.00, 'Se tomaron muestras para citología.', 2),
('Champú terapéutico', 'Clorexiderm', 18.50, 'Uso 2 veces por semana.', 2),
('Consulta Oftalmología', NULL, 65.00, 'Se aplicó fluoresceína.', 3),
('Pomada Tópica Ocular', 'Terramicina', 14.00, 'Aplicar 3 veces al día.', 3),
('Recorte de Incisivos', NULL, 25.00, 'Se pautó revisión cada 4 meses.', 4),
('Sutura Menor', NULL, 40.00, 'Retirar puntos en 7 días.', 5);

INSERT INTO usuario (email, password, rol, cliente_id, veterinario_id) VALUES
('vet@test.com', '$2y$10$wO3tnt2j8BntEhm6Y.HwHOHn8HXYs0/i1FwN.UjG/UvB4Z8mZ284q', 'VETERINARIO', NULL, 1),
('cliente@test.com', '$2y$10$wO3tnt2j8BntEhm6Y.HwHOHn8HXYs0/i1FwN.UjG/UvB4Z8mZ284q', 'CLIENTE', 1, NULL);
