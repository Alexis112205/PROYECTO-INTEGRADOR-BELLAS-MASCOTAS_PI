# Guía del código – Bellas Mascotas

Esta guía explica qué hace cada clase y dónde se cumple cada requerimiento, para preparar la evaluación oral.
Los requerimientos (RF y RNF), las funciones complementarias (FC), las características de calidad complementarias (CC) y los casos de uso (CU) son los del documento final.

## 1. Cómo funciona una acción de principio a fin (ejemplo: registrar un cliente)
1. **Vista:** en `PnlClientes` el usuario pulsa "+ Nuevo" y se abre `DlgNuevoCliente`.
2. **Validación (RNF03):** `DlgNuevoCliente.validar()` revisa campos vacíos, cédula numérica, nombres sin números y teléfono de 7 a 15 dígitos.
3. **Regla de negocio:** `ClienteDAO.existeCedula()` verifica que la cédula no exista (RF03).
4. **Modelo:** se crea un objeto `Cliente` con los datos.
5. **Acceso a datos:** `ClienteDAO.RegistrarCliente()` abre la conexión con `Conexion.abrir()` y ejecuta un `INSERT` con `PreparedStatement` (RNF02).
6. **Respuesta:** el DAO devuelve `true` o `false` y la vista muestra el mensaje y recarga la tabla.

La vista nunca escribe SQL: siempre llama a un DAO. Esa es la separación del patrón MVC (RNF06).

## 2. Paquete `model` (entidades)
Cada clase representa una tabla. Tiene atributos privados, constructor vacío y métodos get/set.

| Clase | Tabla | Detalle |
|---|---|---|
| `Rol` | roles | 1 Administrador, 2 Estilista, 3 Recepción. |
| `Usuario` | usuarios | `password` guarda el hash SHA-256. |
| `Cliente` | clientes | `getNombreCompleto()`; puntos y última visita (FC12). |
| `Mascota` | mascotas | `tieneAlertas()` y `textoAlertas()` resumen alergias, agresividad y bozal (FC05). |
| `ControlVacuna` | control_vacunas | Vacunas de una mascota (FC06). |
| `Cita` | citas | Estados: Programada, En curso, Finalizada, Cancelada. |
| `ServicioCatalogo` | servicios_catalogo | Servicio y precio de venta. |
| `RecetaServicio` | receta_servicio | Insumo y cantidad que consume un servicio. |
| `Insumo` | insumos | Existencias y precio de costo. |
| `RegistroEstetica` | registros_estetica | Servicio realizado (venta). |
| `GastoOperativo` | gastos_operativos | Gasto y usuario que lo registró. |

## 3. Paquete `dao` (acceso a datos)
- **`Conexion`:** lee `config.properties` (RNF07) y abre la conexión JDBC a MySQL 8 local. `abrir()` lanza la excepción para que el DAO la maneje.
- **`Seguridad`:** `sha256(texto)` calcula el hash de la contraseña, igual que `SHA2(texto, 256)` de MySQL (RNF01).
- **Patrón de todos los DAO:** `try (Connection con = Conexion.abrir(); PreparedStatement ps = ...)`. El *try-with-resources* cierra la conexión automáticamente aunque ocurra un error.

| DAO | Métodos principales | RF |
|---|---|---|
| `UsuarioDAO` | validarLogin, insertar, listarTodo, listarPorRol, modificar, eliminar, listarRoles | RF01, FC02 |
| `ClienteDAO` | RegistrarCliente, obtenerTodos, buscarPorCedulaONombre, buscarPorFechas, modificar, eliminar, existeCedula | FC01, FC02, FC03 |
| `MascotaDAO` | registrarMascota, listarTodo, listarPorCliente, modificar, eliminar | RF05 |
| `VacunaDAO` | registrar, listarPorMascota, eliminar | FC06 |
| `CitaDAO` | horarioDisponible, registrar, listarPorFecha, listarPorEstilista, actualizarEstado, eliminar | RF06, FC07 |
| `CatalogoDAO` | insertar, listarTodo, modificar, eliminar, listarReceta, agregarInsumoReceta, quitarInsumoReceta | FC08 |
| `ServicioDAO` | **registrarServicios (transacción)**, ingresosPorRango, ingresosPorServicio, listarPorFecha | RF07, RF08, RF10, FC12 |
| `InsumoDAO` | insertarInsumo, listarTodo, listarStockBajo, actualizarStock, modificar, eliminar | RF09 |
| `GastoDAO` | registrar, listarPorRango, totalPorRango, eliminar | FC10, RF10 |
| `DashboardDAO` | consultas COUNT y SUM del panel | FC11 |

### La transacción de `ServicioDAO.registrarServicios` (pregunta segura en la sustentación)
1. `con.setAutoCommit(false)`: desde aquí nada se guarda hasta el `commit()`.
2. Por cada servicio marcado, en orden: verifica que haya stock para su receta; si no, hace `rollback()` y devuelve el mensaje.
3. Inserta el registro en `registros_estetica` con el precio del catálogo (`INSERT ... SELECT`).
4. Descuenta los insumos de su receta (`UPDATE insumos JOIN receta_servicio`). Como todo va en la misma transacción, la verificación del siguiente servicio ya ve el stock descontado: dos servicios que usan el mismo insumo nunca lo dejan en negativo. Además, la tabla `insumos` tiene `CHECK (cantidad_stock >= 0)`.
5. Finaliza la cita (o crea una cita finalizada si el cliente llegó sin cita) y guarda las observaciones.
6. Suma los puntos al cliente y actualiza la fecha de última visita.
7. `con.commit()`. Si algo falla, el `catch` hace `rollback()`: no queda nada a medias (RNF05).

## 4. Paquete `controller`
- **`Permisos`:** matriz rol → módulos (enum `Modulo`). Si un rol no está en la matriz no tiene acceso (denegar por defecto, CC01).
- **`Sesion`:** guarda el usuario que inició sesión; `puede(modulo)` y `esRol(idRol)`.
- **`MenuController`:** asigna los clics del menú, **verifica el permiso antes de mostrar** cada módulo, cambia el panel del `CardLayout`, recarga sus datos (`Refrescable`), maneja la búsqueda global, las notificaciones y el cierre de sesión (RF02, FC01).

## 5. Paquete `view`
| Clase | Qué hace | RF / CU |
|---|---|---|
| `FrmLogin` | Ingreso; valida campos y conexión, abre el menú según el rol. | RF01 / CU01 |
| `FrmMenuPrincipal` | Menú lateral, barra superior y `CardLayout`. Solo crea los módulos que el rol puede usar. | RF02 |
| `PnlDashboard` | Indicadores del día, agenda de hoy, insumos por reponer o vacunas por vencer. | FC11 / CU16 |
| `PnlAgenda` | Agenda por día y estilista; nueva cita con validación de horario ocupado; cambiar estado. | RF06, FC07 / CU08, CU09 |
| `PnlClientes`, `DlgNuevoCliente`, `DlgEditarCliente` | Directorio, búsqueda, filtro por fechas, registro y edición. Eliminar solo para Administrador. | FC01, FC02, FC03 / CU04, CU05 |
| `PnlMascotas`, `DlgMascota`, `PnlFotoMascota` | Fichas con foto, alertas de cuidado y control de vacunas. | RF03, FC04, FC05, FC06 / CU06, CU07 |
| `PnlVentas` | "Registrar servicio" (Recepción y Administrador) o "Mi agenda" (Estilista). | FC07, RF07, RF08, FC12 / CU10, CU11 |
| `PnlInventario` | CRUD de insumos con estado Disponible, Stock bajo o Agotado. | RF09 / CU12 |
| `PnlReportes` | Reporte semanal, mensual o personalizado; gastos operativos. | FC10, RF10 / CU14, CU15 |
| `PnlUsuarios` | Usuarios con rol y contraseña; catálogo de servicios y su receta. | FC02, FC08 / CU03, CU13 |
| `Estilo`, `Tarjeta` | Colores, botones, tablas y tarjetas del mockup (CC02). | — |
| `Datos` | Carga clientes, mascotas, usuarios, servicios e insumos para mostrar nombres en lugar de ID. | — |
| `Refrescable` | Interfaz que implementan los paneles que se recargan al abrirse. | — |

**Herencia:** las ventanas heredan de `JFrame` (`FrmLogin`, `FrmMenuPrincipal`), los paneles de `JPanel` y los diálogos de `JDialog`. `DlgEditarCliente` hereda de `DlgNuevoCliente` y reutiliza su formulario y su validación.

**Componentes que pide la rúbrica (SCE2.9):** tablas (`JTable`) en todos los módulos y listas desplegables (`JComboBox`) para especie, propietario, mascota, servicio, estilista, hora, rol, insumo y tipo de reporte.

## 6. Requerimientos no funcionales en el código
| RNF | Dónde se cumple |
|---|---|
| RNF01 Contraseñas SHA-256 | `Seguridad.sha256`, `UsuarioDAO` |
| RNF02 Sin inyección SQL | `PreparedStatement` en todos los DAO |
| RNF03 Prevención de errores | Validaciones en los formularios y mensajes en español |
| RNF04 Desempeño (< 2 s) | Consultas filtradas en SQL e índices en llaves; clase `pruebas/Rendimiento` |
| RNF05 Transacciones | `ServicioDAO.registrarServicios` con `commit` y `rollback` |
| RNF06 Mantenibilidad | Paquetes model, view, controller y dao |
| RNF07 Configuración | `config.properties` con MySQL local en el puerto 3306 |

Características complementarias: CC01 autorización (`Permisos`, `Sesion.puede`), CC02 facilidad de uso (FlatLaf, `Estilo`, `CardLayout`), CC03 integridad referencial (llaves foráneas, UNIQUE, CHECK de stock no negativo), CC04 utf8mb4, CC05 Ley 1581 (Clientes solo para Recepción y Administrador) y CC06 respaldo con mysqldump.

## 7. Reparto sugerido para la sustentación
- **Login, permisos y menú:** `FrmLogin`, `Permisos`, `Sesion`, `MenuController`.
- **Clientes y mascotas:** `PnlClientes`, `PnlMascotas`, `ClienteDAO`, `MascotaDAO`, `VacunaDAO`.
- **Agenda y registro de servicio (transacción):** `PnlAgenda`, `PnlVentas`, `CitaDAO`, `ServicioDAO`.
- **Base de datos, inventario, reportes y usuarios:** scripts SQL, `InsumoDAO`, `GastoDAO`, `PnlReportes`, `PnlUsuarios`.
