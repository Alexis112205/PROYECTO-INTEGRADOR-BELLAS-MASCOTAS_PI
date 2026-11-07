-- Vacía todas las tablas de bellasmascotas_db y reinicia los ID.
-- Úselo antes de volver a cargar datos_a_todas_las_tablas.sql.
USE bellasmascotas_db;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE registros_estetica;
TRUNCATE TABLE citas;
TRUNCATE TABLE receta_servicio;
TRUNCATE TABLE control_vacunas;
TRUNCATE TABLE gastos_operativos;
TRUNCATE TABLE mascotas;
TRUNCATE TABLE clientes;
TRUNCATE TABLE insumos;
TRUNCATE TABLE servicios_catalogo;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE roles;
SET FOREIGN_KEY_CHECKS = 1;
