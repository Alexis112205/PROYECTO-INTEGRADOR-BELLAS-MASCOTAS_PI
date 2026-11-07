-- =====================================================================
--  SISTEMA DE GESTIÓN "BELLAS MASCOTAS" - Datos de prueba
--  Ejecutar DESPUÉS de tablas_iniciales.sql (la base debe estar vacía).
--  Contraseñas de prueba: cbenitez/admin123 (Administrador),
--  jmontano/logica123 (Estilista), jvelasco/swing456 (Recepción).
-- =====================================================================

SET NAMES utf8mb4;
USE bellasmascotas_db;

-- ---------------------------------------------------------------------
-- 2. DATOS DE PRUEBA
-- ---------------------------------------------------------------------

-- Roles: el orden importa (1 Administrador, 2 Estilista, 3 Recepción), así lo usa la clase Permisos
INSERT INTO roles (nombre, descripcion) VALUES
('Administrador', 'Acceso total al sistema'),
('Estilista', 'Agenda, fichas de mascotas y registro de servicios'),
('Recepcion', 'Atención de clientes, citas y ventas');

INSERT INTO usuarios (nombre, username, password, id_rol) VALUES
('Carlos Benitez', 'cbenitez', SHA2('admin123', 256), 1),
('Joimar Montaño', 'jmontano', SHA2('logica123', 256), 2),
('Jackeline Velasco', 'jvelasco', SHA2('swing456', 256), 3),
('Ana Rios', 'arios', SHA2('pass1', 256), 2),
('Luis Perez', 'lperez', SHA2('pass2', 256), 2),
('Marta Gomez', 'mgomez', SHA2('pass3', 256), 3),
('Pedro Ruiz', 'pruiz', SHA2('pass4', 256), 2),
('Sofia Lara', 'slara', SHA2('pass5', 256), 3);

INSERT INTO clientes (cedula, nombres, apellidos, telefono, direccion, fecha_registro, puntos_acumulados, fecha_ultima_visita) VALUES
('1107045321', 'Juan', 'Castro', '3001452210', 'Calle 5 # 38-21', CURDATE() - INTERVAL 250 DAY, 30, CURDATE() - INTERVAL 3 DAY),
('1144098120', 'María', 'López', '3152201934', 'Carrera 66 # 9-40', CURDATE() - INTERVAL 230 DAY, 20, CURDATE() - INTERVAL 5 DAY),
('94512003', 'Andrés', 'Gil', '3189004471', 'Avenida Roosevelt # 30-12', CURDATE() - INTERVAL 210 DAY, 20, CURDATE() - INTERVAL 8 DAY),
('31998456', 'Luz', 'Prada', '3015558890', 'Calle 13 # 100-05', CURDATE() - INTERVAL 200 DAY, 10, CURDATE() - INTERVAL 12 DAY),
('1006187334', 'José', 'Ortiz', '3204107765', 'Carrera 1 # 56-77', CURDATE() - INTERVAL 160 DAY, 10, CURDATE() - INTERVAL 15 DAY),
('66843210', 'Dora', 'Luz', '3127783301', 'Calle 70 # 2-15', CURDATE() - INTERVAL 140 DAY, 10, CURDATE() - INTERVAL 18 DAY),
('1130654987', 'Beto', 'Polo', '3112369045', 'Carrera 44 # 18-60', CURDATE() - INTERVAL 120 DAY, 10, CURDATE() - INTERVAL 20 DAY),
('1143876540', 'Gaby', 'Ruiz', '3164452378', 'Calle 9 # 23-10', CURDATE() - INTERVAL 100 DAY, 10, CURDATE() - INTERVAL 22 DAY),
('29876123', 'Lola', 'Rey', '3008891122', 'Carrera 80 # 12-34', CURDATE() - INTERVAL 90 DAY, 10, CURDATE() - INTERVAL 24 DAY),
('1112334455', 'Paco', 'San', '3176654433', 'Calle 34 # 5-67', CURDATE() - INTERVAL 80 DAY, 10, CURDATE() - INTERVAL 26 DAY),
('1061223344', 'Katy', 'Mar', '3145567788', 'Carrera 15 # 40-20', CURDATE() - INTERVAL 60 DAY, 0, NULL),
('16789012', 'Leo', 'Sol', '3109988776', 'Calle 44 # 7-89', CURDATE() - INTERVAL 45 DAY, 0, NULL),
('1151998877', 'Sara', 'Mir', '3183344556', 'Carrera 24 # 3-14', CURDATE() - INTERVAL 30 DAY, 0, NULL),
('1107556677', 'Nico', 'Pan', '3016677889', 'Calle 52 # 28-30', CURDATE() - INTERVAL 10 DAY, 0, NULL),
('1144332211', 'Tato', 'Mor', '3122233445', 'Carrera 39 # 9-11', CURDATE(), 0, NULL);

INSERT INTO mascotas (nombre, especie, raza, edad, observaciones, id_cliente, agresivo, requiere_bozal, alergias) VALUES
('Max', 'Perro', 'Poodle', 3, 'Prefiere corte corto en patas', 1, 0, 0, 'Shampoo de coco'),
('Luna', 'Perro', 'Golden', 5, 'Muy nerviosa con el secador', 2, 0, 0, NULL),
('Rocky', 'Perro', 'Beagle', 2, 'Piel sensible', 3, 0, 0, 'Jabón antipulgas'),
('Bella', 'Gato', 'Persa', 4, 'Corte tipo león', 4, 0, 0, NULL),
('Coco', 'Perro', 'Bulldog', 1, 'Sin observaciones', 5, 0, 0, NULL),
('Daisy', 'Perro', 'Yorkie', 6, 'Usa moños rosas', 6, 0, 0, NULL),
('Toby', 'Perro', 'Labrador', 4, 'Revisar oídos', 7, 0, 0, 'Jabón antipulgas'),
('Milo', 'Gato', 'Siamés', 3, 'Solo baño seco', 8, 0, 0, NULL),
('Simba', 'Perro', 'Chihuahua', 2, 'Tiembla mucho', 9, 0, 0, NULL),
('Lucky', 'Perro', 'Shih Tzu', 7, 'Corte corto', 10, 0, 0, NULL),
('Nala', 'Perro', 'Husky', 5, 'Mucha muda de pelo', 11, 0, 0, NULL),
('Boby', 'Perro', 'Criollo', 8, 'Sin observaciones', 12, 0, 0, NULL),
('Kira', 'Gato', 'Común', 2, 'Uñas largas', 13, 0, 0, NULL),
('Zeus', 'Perro', 'Bóxer', 3, 'Juguetón pero muerde al cortar uñas', 14, 1, 1, NULL),
('Sasha', 'Perro', 'Pastor Alemán', 6, 'Pelo enredado', 15, 1, 0, NULL);

INSERT INTO control_vacunas (nombre_vacuna, fecha_aplicacion, proxima_dosis, id_mascota) VALUES
('Rabia', CURDATE() - INTERVAL 230 DAY, CURDATE() + INTERVAL 135 DAY, 1),
('Parvovirus', CURDATE() - INTERVAL 350 DAY, CURDATE() + INTERVAL 15 DAY, 1),
('Rabia', CURDATE() - INTERVAL 100 DAY, CURDATE() + INTERVAL 265 DAY, 2),
('Triple felina', CURDATE() - INTERVAL 300 DAY, CURDATE() + INTERVAL 65 DAY, 4),
('Moquillo', CURDATE() - INTERVAL 370 DAY, CURDATE() - INTERVAL 5 DAY, 7),
('Rabia', CURDATE() - INTERVAL 60 DAY, CURDATE() + INTERVAL 305 DAY, 14);

INSERT INTO servicios_catalogo (tipo, precio_venta, descripcion) VALUES
('Corte Basico', 35000, 'Corte higiénico y de raza'),
('Baño Normal', 25000, 'Baño, secado y cepillado'),
('Baño Medicado', 40000, 'Baño con shampoo medicado'),
('Corte de Uñas', 10000, 'Corte y limado de uñas'),
('Limpieza Oidos', 8000, 'Limpieza externa de oídos'),
('Full Combo', 55000, 'Baño, corte, uñas y oídos');

INSERT INTO insumos (nombre, cantidad_stock, precio_costo) VALUES
('Shampoo Coco', 20, 15000), ('Shampoo Medicado', 10, 25000),
('Acondicionador', 15, 12000), ('Cuchillas A5', 5, 45000),
('Cuchillas A7', 5, 48000), ('Loción Talco', 12, 8000),
('Moños Pack', 100, 200), ('Pañoletas', 50, 500),
('Jabón Antipulgas', 30, 7000), ('Cepillo Carda', 8, 18000),
('Tijeras Rectas', 3, 60000), ('Corta Uñas', 4, 15000),
('Limpiador Oidos', 20, 9000), ('Toallas Desechables', 200, 100),
('Algodon Pack', 10, 3000);

-- Receta: insumos que consume cada servicio (id_catalogo, id_insumo, cantidad)
INSERT INTO receta_servicio (id_catalogo, id_insumo, cantidad_usada) VALUES
(1, 14, 1), (1, 8, 1),                         -- Corte Basico: toalla y pañoleta
(2, 1, 1), (2, 3, 1), (2, 14, 2),              -- Baño Normal: shampoo coco, acondicionador, toallas
(3, 2, 1), (3, 14, 2),                         -- Baño Medicado: shampoo medicado, toallas
(4, 14, 1),                                    -- Corte de Uñas: toalla
(5, 13, 1), (5, 15, 1),                        -- Limpieza Oidos: limpiador y algodón
(6, 1, 1), (6, 3, 1), (6, 14, 2), (6, 7, 1);   -- Full Combo: shampoo, acondicionador, toallas, moño

-- Citas: hoy y mañana (se calculan con CURDATE() para que siempre haya agenda)
INSERT INTO citas (fecha_hora_cita, estado, observaciones, id_mascota, id_usuario, id_catalogo) VALUES
(CURDATE() + INTERVAL 9 HOUR,  'Finalizada', NULL, 1, 2, 1),
(CURDATE() + INTERVAL 10 HOUR, 'En curso',   'Secado a baja temperatura', 2, 4, 2),
(CURDATE() + INTERVAL 11 HOUR, 'Programada', NULL, 3, 5, 3),
(CURDATE() + INTERVAL 14 HOUR, 'Programada', 'Corte tipo león', 4, 2, 6),
(CURDATE() + INTERVAL 15 HOUR + INTERVAL 30 MINUTE, 'Cancelada', NULL, 7, 7, 4),
(CURDATE() + INTERVAL 16 HOUR, 'Programada', 'Usar bozal', 14, 2, 2),
(CURDATE() + INTERVAL 1 DAY + INTERVAL 9 HOUR,  'Programada', NULL, 5, 4, 6),
(CURDATE() + INTERVAL 1 DAY + INTERVAL 11 HOUR, 'Programada', NULL, 9, 2, 1);

-- Servicios realizados durante el último mes (para reportes e indicadores)
INSERT INTO registros_estetica (fecha, total, id_mascota, id_usuario, id_catalogo) VALUES
(CURDATE() + INTERVAL 9 HOUR + INTERVAL 50 MINUTE, 35000, 1, 2, 1),
(CURDATE() - INTERVAL 1 DAY, 25000, 2, 4, 2),
(CURDATE() - INTERVAL 2 DAY, 40000, 3, 5, 3),
(CURDATE() - INTERVAL 3 DAY, 55000, 1, 2, 6),
(CURDATE() - INTERVAL 4 DAY, 10000, 4, 7, 4),
(CURDATE() - INTERVAL 5 DAY, 55000, 5, 2, 6),
(CURDATE() - INTERVAL 6 DAY, 35000, 6, 4, 1),
(CURDATE() - INTERVAL 8 DAY, 25000, 7, 5, 2),
(CURDATE() - INTERVAL 9 DAY, 8000, 8, 7, 5),
(CURDATE() - INTERVAL 11 DAY, 40000, 9, 2, 3),
(CURDATE() - INTERVAL 13 DAY, 55000, 10, 4, 6),
(CURDATE() - INTERVAL 15 DAY, 35000, 11, 5, 1),
(CURDATE() - INTERVAL 18 DAY, 25000, 12, 7, 2),
(CURDATE() - INTERVAL 21 DAY, 10000, 13, 2, 4),
(CURDATE() - INTERVAL 24 DAY, 35000, 14, 4, 1),
(CURDATE() - INTERVAL 27 DAY, 55000, 15, 5, 6);

INSERT INTO gastos_operativos (concepto, monto, fecha_gasto, id_usuario) VALUES
('Arriendo del local', 1200000, CURDATE() - INTERVAL 25 DAY, 1),
('Servicios públicos', 320000, CURDATE() - INTERVAL 20 DAY, 1),
('Compra de shampoo y toallas', 260000, CURDATE() - INTERVAL 12 DAY, 1),
('Mantenimiento del secador', 140000, CURDATE() - INTERVAL 6 DAY, 1);

-- ---------------------------------------------------------------------
-- 3. VERIFICACIÓN: cantidad de registros por tabla
-- ---------------------------------------------------------------------
SELECT 'roles' AS tabla, COUNT(*) AS registros FROM roles
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'mascotas', COUNT(*) FROM mascotas
UNION ALL SELECT 'control_vacunas', COUNT(*) FROM control_vacunas
UNION ALL SELECT 'servicios_catalogo', COUNT(*) FROM servicios_catalogo
UNION ALL SELECT 'insumos', COUNT(*) FROM insumos
UNION ALL SELECT 'receta_servicio', COUNT(*) FROM receta_servicio
UNION ALL SELECT 'citas', COUNT(*) FROM citas
UNION ALL SELECT 'registros_estetica', COUNT(*) FROM registros_estetica
UNION ALL SELECT 'gastos_operativos', COUNT(*) FROM gastos_operativos;
