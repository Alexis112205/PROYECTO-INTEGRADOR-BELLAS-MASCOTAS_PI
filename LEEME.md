# Sistema de Gestión "Bellas Mascotas"

Aplicación de escritorio en Java Swing (patrón MVC) con base de datos MySQL 8 local.
Proyecto Integrador 411/412/413 – Entrega Final – UNIAJC, 2026-2.

## Requisitos
- JDK 17 o superior (recomendado Eclipse Temurin 21).
- MySQL Server 8 instalado localmente en el puerto 3306 (y MySQL Workbench). No use XAMPP: trae MariaDB, que no reconoce la intercalación `utf8mb4_0900_ai_ci` del script.
- Visual Studio Code con la extensión **Extension Pack for Java**.

## Instalación
1. **Base de datos.** En MySQL Workbench ejecute, en este orden:
   1. `sql/tablas_iniciales.sql` (crea la base `bellasmascotas_db` y sus 11 tablas).
   2. `sql/datos_a_todas_las_tablas.sql` (datos de prueba).

   También puede ejecutar solo `sql/bellasmascotas_completo.sql`, que hace los dos pasos.
   Atención: los scripts borran la base `bellasmascotas_db` si ya existe.
2. **Conexión.** Copie `config.ejemplo.properties` con el nombre `config.properties` y escriba en `db.password` la contraseña de su usuario `root` de MySQL. Este archivo no se sube a GitHub.
3. **VS Code.** Abra la carpeta del proyecto (la que contiene `src`, `lib` y `sql`).
   La carpeta `.vscode` ya trae la configuración de las librerías.
4. **Prueba.** Ejecute *Prueba de conexión* (panel Run and Debug) o `PruebaConexion.java`.
5. **Ejecutar.** Ejecute *Bellas Mascotas* o `src/com/bellasmascotas/Main.java`.

## Usuarios de prueba
| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | cbenitez | admin123 |
| Estilista | jmontano | logica123 |
| Recepción | jvelasco | swing456 |

## Estructura de carpetas
- `src/com/bellasmascotas/model` – entidades (POJO).
- `src/com/bellasmascotas/dao` – conexión, seguridad y acceso a datos (una clase DAO por entidad).
- `src/com/bellasmascotas/controller` – navegación, sesión y permisos por rol.
- `src/com/bellasmascotas/view` – ventanas y paneles Swing.
- `src/resources/images` – logotipo.
- `lib` – FlatLaf 3.4.1 y MySQL Connector/J 9.6.0.
- `sql` – scripts de la base de datos.
- `config.ejemplo.properties` – plantilla de `config.properties` (conexión y reglas del negocio: puntos por servicio y umbral de stock bajo).
- `pruebas` – pruebas unitarias de los DAO y prueba de rendimiento.
- `GUIA_DEL_CODIGO.md` – explicación de cada clase para la sustentación.

## Pruebas
La carpeta `pruebas` contiene dos programas de apoyo que se ejecutan contra una base recién cargada con `sql/bellasmascotas_completo.sql`:
- `PruebasUnitarias.java`: 24 pruebas de los métodos DAO (imprime "Aprobada" o "FALLA" por prueba).
- `Rendimiento.java`: mide el tiempo de las consultas principales.

Para ejecutarlas, compile `src` y `pruebas` juntos con las librerías de `lib` y ejecute la clase deseada desde la carpeta del proyecto.

## Respaldo
- Respaldo: `mysqldump -u root -p bellasmascotas_db > respaldo_AAAA-MM-DD.sql`
- Restauración: `mysql -u root -p bellasmascotas_db < respaldo_AAAA-MM-DD.sql`
