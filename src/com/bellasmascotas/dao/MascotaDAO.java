package com.bellasmascotas.dao;

import com.bellasmascotas.model.Mascota;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla mascotas (RF05, FC05). */
public class MascotaDAO {

    /** CREATE: registra una mascota vinculada a su propietario (id_cliente). */
    public boolean registrarMascota(Mascota m) {
        String sql = "INSERT INTO mascotas (nombre, especie, raza, edad, observaciones, id_cliente, "
                   + "agresivo, requiere_bozal, ruta_imagen, alergias) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, m);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar mascota: " + e.getMessage());
            return false;
        }
    }

    /** READ: lista todas las mascotas ordenadas por nombre. */
    public List<Mascota> listarTodo() {
        return consultar("SELECT * FROM mascotas ORDER BY nombre", null);
    }

    /** READ: mascotas de un propietario (para agendar citas y registrar servicios). */
    public List<Mascota> listarPorCliente(int idCliente) {
        return consultar("SELECT * FROM mascotas WHERE id_cliente = ? ORDER BY nombre", idCliente);
    }

    /** UPDATE: modifica todos los datos de una mascota existente. */
    public boolean modificar(Mascota m) {
        String sql = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, edad = ?, observaciones = ?, "
                   + "id_cliente = ?, agresivo = ?, requiere_bozal = ?, ruta_imagen = ?, alergias = ? "
                   + "WHERE id_mascota = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, m);
            ps.setInt(11, m.getIdMascota());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar mascota: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: elimina una mascota. Sus vacunas se borran en cascada; falla si tiene citas o servicios. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM mascotas WHERE id_mascota = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar mascota: " + e.getMessage());
            return false;
        }
    }

    private void llenar(PreparedStatement ps, Mascota m) throws SQLException {
        ps.setString(1, m.getNombre());
        ps.setString(2, m.getEspecie());
        ps.setString(3, m.getRaza());
        ps.setInt(4, m.getEdad());
        ps.setString(5, m.getObservaciones());
        if (m.getIdCliente() > 0) ps.setInt(6, m.getIdCliente()); else ps.setNull(6, Types.INTEGER);
        ps.setBoolean(7, m.isAgresivo());
        ps.setBoolean(8, m.isRequiereBozal());
        ps.setString(9, m.getRutaImagen());
        ps.setString(10, m.getAlergias());
    }

    private List<Mascota> consultar(String sql, Integer parametro) {
        List<Mascota> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mascota m = new Mascota(rs.getInt("id_mascota"), rs.getString("nombre"), rs.getString("especie"),
                            rs.getString("raza"), rs.getInt("edad"), rs.getString("observaciones"),
                            rs.getInt("id_cliente"), rs.getString("ruta_imagen"));
                    m.setAgresivo(rs.getBoolean("agresivo"));
                    m.setRequiereBozal(rs.getBoolean("requiere_bozal"));
                    m.setAlergias(rs.getString("alergias"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar mascotas: " + e.getMessage());
        }
        return lista;
    }
}
