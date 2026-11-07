package com.bellasmascotas.dao;

import com.bellasmascotas.model.RegistroEstetica;
import com.bellasmascotas.model.ServicioCatalogo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla registros_estetica: servicios realizados y reportes de ingresos (RF07, RF08, RF10, FC12). */
public class ServicioDAO {

    /** CREATE: registra un servicio realizado (registro simple, sin descontar inventario). */
    public boolean registrarServicio(int idMascota, int idUsuario, int idCat, double total) {
        String sql = "INSERT INTO registros_estetica (id_mascota, id_usuario, id_catalogo, total) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idCat);
            ps.setDouble(4, total);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar servicio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra uno o varios servicios prestados a una mascota en UNA SOLA TRANSACCIÓN (CU10 y CU11):
     * 1) verifica que haya stock para la receta de cada servicio,
     * 2) inserta un registro por servicio con el precio del catálogo,
     * 3) descuenta los insumos de la receta,
     * 4) finaliza la cita (o crea una cita finalizada si el cliente llegó sin cita) y guarda las observaciones,
     * 5) suma puntos al cliente y actualiza su fecha de última visita.
     * Si algo falla se hace ROLLBACK y no queda nada a medias.
     *
     * @param idCita      cita que se atiende, o 0 si el cliente llegó sin cita
     * @return null si todo salió bien; el mensaje del error si no se pudo registrar
     */
    public String registrarServicios(int idCita, int idMascota, int idUsuario, List<ServicioCatalogo> servicios,
                                     String observaciones, int puntos) {
        if (servicios == null || servicios.isEmpty()) return "Seleccione al menos un servicio.";
        Connection con = null;
        try {
            con = Conexion.abrir();
            con.setAutoCommit(false);

            // Por cada servicio: 1) verificar el stock, 2) registrar el servicio y 3) descontar su receta.
            // Como todo ocurre en la misma transacción, la verificación del segundo servicio ya ve
            // el stock descontado por el primero; así dos servicios que usan el mismo insumo
            // no pueden dejarlo en negativo.
            String sqlStock = "SELECT i.nombre, i.cantidad_stock, r.cantidad_usada FROM receta_servicio r "
                            + "JOIN insumos i ON i.id_insumo = r.id_insumo "
                            + "WHERE r.id_catalogo = ? AND i.cantidad_stock < r.cantidad_usada";
            String sqlRegistro = "INSERT INTO registros_estetica (id_mascota, id_usuario, id_catalogo, total) "
                               + "SELECT ?, ?, id_catalogo, precio_venta FROM servicios_catalogo WHERE id_catalogo = ?";
            String sqlDescuento = "UPDATE insumos i JOIN receta_servicio r ON r.id_insumo = i.id_insumo "
                                + "SET i.cantidad_stock = i.cantidad_stock - r.cantidad_usada WHERE r.id_catalogo = ?";
            for (ServicioCatalogo s : servicios) {
                // 1. Verificar stock suficiente para la receta de este servicio
                try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
                    ps.setInt(1, s.getIdCatalogo());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            con.rollback();
                            return "No hay stock suficiente de \"" + rs.getString("nombre") + "\" (quedan "
                                    + rs.getInt("cantidad_stock") + ", se necesitan " + rs.getInt("cantidad_usada")
                                    + ") para el servicio " + s.getTipo() + ".";
                        }
                    }
                }
                // 2. Registrar el servicio con el precio del catálogo
                try (PreparedStatement ps = con.prepareStatement(sqlRegistro)) {
                    ps.setInt(1, idMascota);
                    ps.setInt(2, idUsuario);
                    ps.setInt(3, s.getIdCatalogo());
                    ps.executeUpdate();
                }
                // 3. Descontar los insumos de la receta
                try (PreparedStatement ps = con.prepareStatement(sqlDescuento)) {
                    ps.setInt(1, s.getIdCatalogo());
                    ps.executeUpdate();
                }
            }

            // 4. Finalizar la cita o crear una cita finalizada (cliente sin cita)
            if (idCita > 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE citas SET estado = 'Finalizada', observaciones = ? WHERE id_cita = ?")) {
                    ps.setString(1, observaciones);
                    ps.setInt(2, idCita);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO citas (fecha_hora_cita, estado, observaciones, id_mascota, id_usuario, id_catalogo) "
                      + "VALUES (NOW(), 'Finalizada', ?, ?, ?, ?)")) {
                    ps.setString(1, observaciones);
                    ps.setInt(2, idMascota);
                    ps.setInt(3, idUsuario);
                    ps.setInt(4, servicios.get(0).getIdCatalogo());
                    ps.executeUpdate();
                }
            }

            // 5. Sumar puntos al cliente y actualizar la fecha de última visita (FC12)
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE clientes c JOIN mascotas m ON m.id_cliente = c.id_cliente "
                  + "SET c.puntos_acumulados = c.puntos_acumulados + ?, c.fecha_ultima_visita = CURDATE() "
                  + "WHERE m.id_mascota = ?")) {
                ps.setInt(1, puntos * servicios.size());
                ps.setInt(2, idMascota);
                ps.executeUpdate();
            }

            con.commit();
            return null;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ignored) { }
            return "No se pudo registrar el servicio: " + e.getMessage();
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (SQLException ignored) { }
        }
    }

    /** READ: suma de todos los ingresos registrados (RF10). */
    public double obtenerVentasTotales() {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total_ingresos FROM registros_estetica";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total_ingresos");
        } catch (SQLException e) {
            System.err.println("Error en reporte: " + e.getMessage());
        }
        return 0.0;
    }

    /** READ: ingresos entre dos fechas (AAAA-MM-DD, ambas incluidas) (RF10). */
    public double ingresosPorRango(String ini, String fin) {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM registros_estetica WHERE DATE(fecha) BETWEEN ? AND ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ini);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en ingresos por rango: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * READ: cantidad e ingresos por tipo de servicio en un rango (JOIN y GROUP BY) (RF10).
     * Cada fila: {tipo (String), precio (Double), cantidad (Integer), ingresos (Double)}.
     */
    public List<Object[]> ingresosPorServicio(String ini, String fin) {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT s.tipo, s.precio_venta, COUNT(*) AS cantidad, SUM(r.total) AS ingresos "
                   + "FROM registros_estetica r JOIN servicios_catalogo s ON s.id_catalogo = r.id_catalogo "
                   + "WHERE DATE(r.fecha) BETWEEN ? AND ? "
                   + "GROUP BY s.id_catalogo, s.tipo, s.precio_venta ORDER BY ingresos DESC";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ini);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getString("tipo"), rs.getDouble("precio_venta"),
                            rs.getInt("cantidad"), rs.getDouble("ingresos")});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en ingresos por servicio: " + e.getMessage());
        }
        return filas;
    }

    /** READ: servicios realizados en un día (para el panel y "Mi agenda"). */
    public List<RegistroEstetica> listarPorFecha(String fecha) {
        List<RegistroEstetica> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros_estetica WHERE DATE(fecha) = ? ORDER BY fecha DESC";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroEstetica r = new RegistroEstetica();
                    r.setIdRegistro(rs.getInt("id_registro"));
                    Timestamp ts = rs.getTimestamp("fecha");
                    r.setFecha(ts == null ? "" : ts.toLocalDateTime().toString().replace('T', ' '));
                    r.setTotal(rs.getDouble("total"));
                    r.setIdMascota(rs.getInt("id_mascota"));
                    r.setIdUsuario(rs.getInt("id_usuario"));
                    r.setIdCatalogo(rs.getInt("id_catalogo"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios del día: " + e.getMessage());
        }
        return lista;
    }
}
