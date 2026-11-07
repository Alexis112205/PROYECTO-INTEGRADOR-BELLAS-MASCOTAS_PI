package com.bellasmascotas.dao;

import com.bellasmascotas.model.Insumo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla insumos (RF08, RF09). */
public class InsumoDAO {

    /** CREATE: registra un insumo nuevo. */
    public boolean insertarInsumo(String nombre, int cantidad, double precio) {
        String sql = "INSERT INTO insumos (nombre, cantidad_stock, precio_costo) VALUES (?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, cantidad);
            ps.setDouble(3, precio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar insumo: " + e.getMessage());
            return false;
        }
    }

    /** READ: lista todos los insumos ordenados por nombre. */
    public List<Insumo> listarTodo() {
        return consultar("SELECT * FROM insumos ORDER BY nombre", null);
    }

    /** READ: insumos con stock igual o menor al umbral (alertas de reposición). */
    public List<Insumo> listarStockBajo(int umbral) {
        return consultar("SELECT * FROM insumos WHERE cantidad_stock <= ? ORDER BY cantidad_stock, nombre", umbral);
    }

    /** UPDATE: resta stock de un insumo (descuento manual por consumo). */
    public boolean actualizarStock(int idInsumo, int cantidadUsada) {
        String sql = "UPDATE insumos SET cantidad_stock = cantidad_stock - ? WHERE id_insumo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidadUsada);
            ps.setInt(2, idInsumo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    /** UPDATE: modifica nombre, existencias y precio de costo. */
    public boolean modificar(Insumo i) {
        String sql = "UPDATE insumos SET nombre = ?, cantidad_stock = ?, precio_costo = ? WHERE id_insumo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, i.getNombre());
            ps.setInt(2, i.getCantidadStock());
            ps.setDouble(3, i.getPrecioCosto());
            ps.setInt(4, i.getIdInsumo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar insumo: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: elimina un insumo. Falla si está en la receta de algún servicio. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM insumos WHERE id_insumo = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar insumo: " + e.getMessage());
            return false;
        }
    }

    private List<Insumo> consultar(String sql, Integer parametro) {
        List<Insumo> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Insumo i = new Insumo();
                    i.setIdInsumo(rs.getInt("id_insumo"));
                    i.setNombre(rs.getString("nombre"));
                    i.setCantidadStock(rs.getInt("cantidad_stock"));
                    i.setPrecioCosto(rs.getDouble("precio_costo"));
                    lista.add(i);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar insumos: " + e.getMessage());
        }
        return lista;
    }
}
