-- =====================================================================
--  SISTEMA DE GESTIÓN "BELLAS MASCOTAS" - Creación de la base de datos (tablas)
--  MySQL 8 local (puerto 3306). Crea la base bellasmascotas_db y sus 11 tablas.
--  Después ejecute datos_a_todas_las_tablas.sql para cargar los datos de prueba.
--
--  ATENCIÓN: si la base bellasmascotas_db ya existe, se BORRA y se crea de nuevo.
--  Contraseñas de prueba (se guardan como hash SHA-256):
--    cbenitez / admin123  (Administrador)
--    jmontano / logica123 (Estilista)
--    jvelasco / swing456  (Recepción)
-- =====================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS bellasmascotas_db;
CREATE DATABASE bellasmascotas_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE bellasmascotas_db;

-- ---------------------------------------------------------------------
-- 1. TABLAS (en orden: primero las que no dependen de otras)
-- ---------------------------------------------------------------------

CREATE TABLE roles (
  id_rol       INT NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(50) NOT NULL,
  descripcion  VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (id_rol)
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id_usuario  INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(100) NOT NULL,
  username    VARCHAR(50) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  id_rol      INT DEFAULT NULL,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY username (username),
  KEY id_rol (id_rol),
  CONSTRAINT usuarios_ibfk_1 FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
) ENGINE=InnoDB;

CREATE TABLE clientes (
  id_cliente           INT NOT NULL AUTO_INCREMENT,
  cedula               VARCHAR(20) NOT NULL,
  nombres              VARCHAR(100) NOT NULL,
  apellidos            VARCHAR(100) NOT NULL,
  telefono             VARCHAR(20) DEFAULT NULL,
  direccion            VARCHAR(200) DEFAULT NULL,
  fecha_registro       DATE DEFAULT NULL,
  puntos_acumulados    INT DEFAULT 0,
  fecha_ultima_visita  DATE DEFAULT NULL,
  PRIMARY KEY (id_cliente),
  UNIQUE KEY cedula (cedula)
) ENGINE=InnoDB;

CREATE TABLE mascotas (
  id_mascota      INT NOT NULL AUTO_INCREMENT,
  nombre          VARCHAR(50) NOT NULL,
  especie         VARCHAR(30) DEFAULT NULL,
  raza            VARCHAR(50) DEFAULT NULL,
  edad            INT DEFAULT NULL,
  observaciones   TEXT,
  id_cliente      INT DEFAULT NULL,
  agresivo        TINYINT(1) DEFAULT 0,
  requiere_bozal  TINYINT(1) DEFAULT 0,
  ruta_imagen     VARCHAR(255) DEFAULT NULL,
  alergias        TEXT,
  PRIMARY KEY (id_mascota),
  KEY id_cliente (id_cliente),
  CONSTRAINT mascotas_ibfk_1 FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente)
) ENGINE=InnoDB;

CREATE TABLE control_vacunas (
  id_vacuna         INT NOT NULL AUTO_INCREMENT,
  nombre_vacuna     VARCHAR(100) NOT NULL,
  fecha_aplicacion  DATE NOT NULL,
  proxima_dosis     DATE DEFAULT NULL,
  id_mascota        INT NOT NULL,
  PRIMARY KEY (id_vacuna),
  KEY id_mascota (id_mascota),
  CONSTRAINT control_vacunas_ibfk_1 FOREIGN KEY (id_mascota) REFERENCES mascotas (id_mascota) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE servicios_catalogo (
  id_catalogo   INT NOT NULL AUTO_INCREMENT,
  tipo          VARCHAR(50) NOT NULL,
  precio_venta  DECIMAL(10,2) NOT NULL,
  descripcion   VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (id_catalogo)
) ENGINE=InnoDB;

CREATE TABLE insumos (
  id_insumo       INT NOT NULL AUTO_INCREMENT,
  nombre          VARCHAR(100) NOT NULL,
  cantidad_stock  INT DEFAULT 0,
  precio_costo    DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_insumo),
  CONSTRAINT chk_stock_no_negativo CHECK (cantidad_stock >= 0)
) ENGINE=InnoDB;

CREATE TABLE receta_servicio (
  id_receta       INT NOT NULL AUTO_INCREMENT,
  id_catalogo     INT NOT NULL,
  id_insumo       INT NOT NULL,
  cantidad_usada  INT NOT NULL,
  PRIMARY KEY (id_receta),
  KEY id_catalogo (id_catalogo),
  KEY id_insumo (id_insumo),
  CONSTRAINT receta_servicio_ibfk_1 FOREIGN KEY (id_catalogo) REFERENCES servicios_catalogo (id_catalogo),
  CONSTRAINT receta_servicio_ibfk_2 FOREIGN KEY (id_insumo) REFERENCES insumos (id_insumo)
) ENGINE=InnoDB;

CREATE TABLE citas (
  id_cita          INT NOT NULL AUTO_INCREMENT,
  fecha_hora_cita  DATETIME NOT NULL,
  estado           VARCHAR(30) DEFAULT 'Programada',
  observaciones    TEXT,
  id_mascota       INT NOT NULL,
  id_usuario       INT NOT NULL,
  id_catalogo      INT NOT NULL,
  PRIMARY KEY (id_cita),
  KEY id_mascota (id_mascota),
  KEY id_usuario (id_usuario),
  KEY id_catalogo (id_catalogo),
  CONSTRAINT citas_ibfk_1 FOREIGN KEY (id_mascota) REFERENCES mascotas (id_mascota),
  CONSTRAINT citas_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
  CONSTRAINT citas_ibfk_3 FOREIGN KEY (id_catalogo) REFERENCES servicios_catalogo (id_catalogo)
) ENGINE=InnoDB;

CREATE TABLE registros_estetica (
  id_registro  INT NOT NULL AUTO_INCREMENT,
  fecha        DATETIME DEFAULT CURRENT_TIMESTAMP,
  total        DECIMAL(10,2) DEFAULT NULL,
  id_mascota   INT DEFAULT NULL,
  id_usuario   INT DEFAULT NULL,
  id_catalogo  INT DEFAULT NULL,
  PRIMARY KEY (id_registro),
  KEY id_mascota (id_mascota),
  KEY id_usuario (id_usuario),
  KEY id_catalogo (id_catalogo),
  CONSTRAINT registros_estetica_ibfk_1 FOREIGN KEY (id_mascota) REFERENCES mascotas (id_mascota),
  CONSTRAINT registros_estetica_ibfk_2 FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
  CONSTRAINT registros_estetica_ibfk_3 FOREIGN KEY (id_catalogo) REFERENCES servicios_catalogo (id_catalogo)
) ENGINE=InnoDB;

CREATE TABLE gastos_operativos (
  id_gasto     INT NOT NULL AUTO_INCREMENT,
  concepto     VARCHAR(150) NOT NULL,
  monto        DECIMAL(10,2) NOT NULL,
  fecha_gasto  DATE NOT NULL,
  id_usuario   INT NOT NULL,
  PRIMARY KEY (id_gasto),
  KEY id_usuario (id_usuario),
  CONSTRAINT gastos_operativos_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
) ENGINE=InnoDB;

