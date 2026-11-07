package com.bellasmascotas.dao;

import com.bellasmascotas.model.RecetaServicio;
import com.bellasmascotas.model.ServicioCatalogo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a las tablas servicios_catalogo y receta_servicio (FC08). */
public class CatalogoDAO {

    /** CREATE: registra un servicio del catálogo. */
    public boolean insertar(ServicioCatalogo s) {
        String sql = "INSERT INTO servicios_catalogo (tipo, precio_venta, descripcion) VALUES (?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getTipo());
            ps.setDouble(2, s.getPrecioVenta());
            ps.setString(3, s.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar servicio: " + e.getMessage());
            return false;
        }
    }

    /** READ: lista los servicios ordenados por nombre. */
    public List<ServicioCatalogo> listarTodo() {
        List<ServicioCatalogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios_catalogo ORDER BY tipo";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServicioCatalogo s = new ServicioCatalogo();
                s.setIdCatalogo(rs.getInt("id_catalogo"));
                s.setTipo(rs.getString("tipo"));
                s.setPrecioVenta(rs.getDouble("precio_venta"));
                s.setDescripcion(rs.getString("descripcion"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios: " + e.getMessage());
        }
        return lista;
    }

    /** UPDATE: modifica nombre, precio y descripción de un servicio. */
    public boolean modificar(ServicioCatalogo s) {
        String sql = "UPDATE servicios_catalogo SET tipo = ?, precio_venta = ?, descripcion = ? WHERE id_catalogo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getTipo());
            ps.setDouble(2, s.getPrecioVenta());
            ps.setString(3, s.getDescripcion());
            ps.setInt(4, s.getIdCatalogo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar servicio: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: elimina un servicio. Falla si tiene citas, recetas o registros históricos. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM servicios_catalogo WHERE id_catalogo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }

    /** READ: insumos que consume un servicio (su receta). */
    public List<RecetaServicio> listarReceta(int idCatalogo) {
        List<RecetaServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta_servicio WHERE id_catalogo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCatalogo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecetaServicio r = new RecetaServicio();
                    r.setIdReceta(rs.getInt("id_receta"));
                    r.setIdCatalogo(rs.getInt("id_catalogo"));
                    r.setIdInsumo(rs.getInt("id_insumo"));
                    r.setCantidadUsada(rs.getInt("cantidad_usada"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar receta: " + e.getMessage());
        }
        return lista;
    }

    /** CREATE: agrega un insumo a la receta de un servicio. */
    public boolean agregarInsumoReceta(RecetaServicio r) {
        String sql = "INSERT INTO receta_servicio (id_catalogo, id_insumo, cantidad_usada) VALUES (?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getIdCatalogo());
            ps.setInt(2, r.getIdInsumo());
            ps.setInt(3, r.getCantidadUsada());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar insumo a la receta: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: quita un renglón de la receta. */
    public boolean quitarInsumoReceta(int idReceta) {
        String sql = "DELETE FROM receta_servicio WHERE id_receta = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al quitar insumo de la receta: " + e.getMessage());
            return false;
        }
    }
}
