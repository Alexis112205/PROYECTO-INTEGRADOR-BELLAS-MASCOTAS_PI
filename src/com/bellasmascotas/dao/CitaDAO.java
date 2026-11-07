package com.bellasmascotas.dao;

import com.bellasmascotas.model.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla citas (RF06, FC07). */
public class CitaDAO {

    /** READ: true si el estilista no tiene otra cita activa a esa misma fecha y hora (RF06). */
    public boolean horarioDisponible(int idUsuario, String fechaHora) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_usuario = ? AND fecha_hora_cita = ? AND estado <> 'Cancelada'";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, fechaHora);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar horario: " + e.getMessage());
            return false;
        }
    }

    /** CREATE: registra la cita con estado inicial 'Programada' (valor por defecto de la tabla). */
    public boolean registrar(Cita c) {
        String sql = "INSERT INTO citas (fecha_hora_cita, observaciones, id_mascota, id_usuario, id_catalogo) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getFechaHoraCita());
            ps.setString(2, c.getObservaciones());
            ps.setInt(3, c.getIdMascota());
            ps.setInt(4, c.getIdUsuario());
            ps.setInt(5, c.getIdCatalogo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar cita: " + e.getMessage());
            return false;
        }
    }

    /** READ: citas de un día (fecha en formato AAAA-MM-DD), ordenadas por hora. */
    public List<Cita> listarPorFecha(String fecha) {
        return consultar("SELECT * FROM citas WHERE DATE(fecha_hora_cita) = ? ORDER BY fecha_hora_cita", fecha, null);
    }

    /** READ: citas de un estilista en un día (su agenda) (FC07). */
    public List<Cita> listarPorEstilista(int idUsuario, String fecha) {
        return consultar("SELECT * FROM citas WHERE DATE(fecha_hora_cita) = ? AND id_usuario = ? ORDER BY fecha_hora_cita",
                fecha, idUsuario);
    }

    /** UPDATE: cambia el estado: Programada, En curso, Finalizada o Cancelada (FC07). */
    public boolean actualizarEstado(int idCita, String estado) {
        String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: elimina una cita registrada por error. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id_cita = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cita: " + e.getMessage());
            return false;
        }
    }

    private List<Cita> consultar(String sql, String fecha, Integer idUsuario) {
        List<Cita> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fecha);
            if (idUsuario != null) ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita c = new Cita();
                    c.setIdCita(rs.getInt("id_cita"));
                    Timestamp ts = rs.getTimestamp("fecha_hora_cita");
                    c.setFechaHoraCita(ts == null ? "" : ts.toLocalDateTime().toString().replace('T', ' '));
                    c.setEstado(rs.getString("estado"));
                    c.setObservaciones(rs.getString("observaciones"));
                    c.setIdMascota(rs.getInt("id_mascota"));
                    c.setIdUsuario(rs.getInt("id_usuario"));
                    c.setIdCatalogo(rs.getInt("id_catalogo"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar citas: " + e.getMessage());
        }
        return lista;
    }
}
