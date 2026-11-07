package com.bellasmascotas.dao;

import com.bellasmascotas.model.GastoOperativo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla gastos_operativos (FC10, RF10). */
public class GastoDAO {

    /** CREATE: registra un gasto asociado al usuario que lo registra. */
    public boolean registrar(GastoOperativo g) {
        String sql = "INSERT INTO gastos_operativos (concepto, monto, fecha_gasto, id_usuario) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, g.getConcepto());
            ps.setDouble(2, g.getMonto());
            ps.setString(3, g.getFechaGasto());
            ps.setInt(4, g.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar gasto: " + e.getMessage());
            return false;
        }
    }

    /** READ: gastos entre dos fechas (AAAA-MM-DD, ambas incluidas). */
    public List<GastoOperativo> listarPorRango(String ini, String fin) {
        List<GastoOperativo> lista = new ArrayList<>();
        String sql = "SELECT * FROM gastos_operativos WHERE fecha_gasto BETWEEN ? AND ? ORDER BY fecha_gasto";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ini);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GastoOperativo g = new GastoOperativo();
                    g.setIdGasto(rs.getInt("id_gasto"));
                    g.setConcepto(rs.getString("concepto"));
                    g.setMonto(rs.getDouble("monto"));
                    g.setFechaGasto(rs.getString("fecha_gasto"));
                    g.setIdUsuario(rs.getInt("id_usuario"));
                    lista.add(g);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar gastos: " + e.getMessage());
        }
        return lista;
    }

    /** READ: total de gastos entre dos fechas. */
    public double totalPorRango(String ini, String fin) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM gastos_operativos WHERE fecha_gasto BETWEEN ? AND ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ini);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al totalizar gastos: " + e.getMessage());
        }
        return 0.0;
    }

    /** DELETE: elimina un gasto registrado por error. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM gastos_operativos WHERE id_gasto = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar gasto: " + e.getMessage());
            return false;
        }
    }
}
