package com.bellasmascotas.dao;

import com.bellasmascotas.model.ControlVacuna;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla control_vacunas (FC06). */
public class VacunaDAO {

    /** CREATE: registra una vacuna aplicada a una mascota. */
    public boolean registrar(ControlVacuna v) {
        String sql = "INSERT INTO control_vacunas (nombre_vacuna, fecha_aplicacion, proxima_dosis, id_mascota) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getNombreVacuna());
            ps.setString(2, v.getFechaAplicacion());
            if (v.getProximaDosis() == null || v.getProximaDosis().isEmpty()) ps.setNull(3, Types.DATE);
            else ps.setString(3, v.getProximaDosis());
            ps.setInt(4, v.getIdMascota());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar vacuna: " + e.getMessage());
            return false;
        }
    }

    /** READ: historial de vacunas de una mascota, de la más reciente a la más antigua. */
    public List<ControlVacuna> listarPorMascota(int idMascota) {
        List<ControlVacuna> lista = new ArrayList<>();
        String sql = "SELECT * FROM control_vacunas WHERE id_mascota = ? ORDER BY fecha_aplicacion DESC";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ControlVacuna v = new ControlVacuna();
                    v.setIdVacuna(rs.getInt("id_vacuna"));
                    v.setNombreVacuna(rs.getString("nombre_vacuna"));
                    v.setFechaAplicacion(rs.getString("fecha_aplicacion"));
                    v.setProximaDosis(rs.getString("proxima_dosis"));
                    v.setIdMascota(rs.getInt("id_mascota"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar vacunas: " + e.getMessage());
        }
        return lista;
    }

    /** DELETE: elimina un registro de vacuna. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM control_vacunas WHERE id_vacuna = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar vacuna: " + e.getMessage());
            return false;
        }
    }
}
