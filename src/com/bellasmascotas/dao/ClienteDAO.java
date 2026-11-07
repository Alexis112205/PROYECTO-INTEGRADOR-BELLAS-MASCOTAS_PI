package com.bellasmascotas.dao;

import com.bellasmascotas.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla clientes (RF03, RF04, FC03). */
public class ClienteDAO {

    // CREATE: Registrar un nuevo cliente
    public boolean RegistrarCliente(Cliente c) {
        String sql = "INSERT INTO clientes (cedula, nombres, apellidos, telefono, direccion, fecha_registro) VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCedula());
            ps.setString(2, c.getNombres());
            ps.setString(3, c.getApellidos());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getFechaRegistro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
            return false;
        }
    }

    // READ: Buscar por cédula o nombre (para el buscador)
    public List<Cliente> buscarPorCedulaONombre(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE cedula LIKE ? OR nombres LIKE ? OR apellidos LIKE ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setIdCliente(rs.getInt("id_cliente"));
                cli.setCedula(rs.getString("cedula"));
                cli.setNombres(rs.getString("nombres"));
                cli.setApellidos(rs.getString("apellidos"));
                cli.setTelefono(rs.getString("telefono"));
                cli.setDireccion(rs.getString("direccion"));
                cli.setFechaRegistro(rs.getString("fecha_registro"));
                cli.setPuntosAcumulados(rs.getInt("puntos_acumulados"));
                cli.setFechaUltimaVisita(rs.getString("fecha_ultima_visita"));
                lista.add(cli);
            }
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
        }
        return lista;
    }

    // READ: Buscar todos los clientes
    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombres ASC";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setIdCliente(rs.getInt("id_cliente"));
                cli.setCedula(rs.getString("cedula"));
                cli.setNombres(rs.getString("nombres"));
                cli.setApellidos(rs.getString("apellidos"));
                cli.setTelefono(rs.getString("telefono"));
                cli.setDireccion(rs.getString("direccion"));
                cli.setFechaRegistro(rs.getString("fecha_registro"));
                cli.setPuntosAcumulados(rs.getInt("puntos_acumulados"));
                cli.setFechaUltimaVisita(rs.getString("fecha_ultima_visita"));
                lista.add(cli);
            }
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
        }
        return lista;
    }

    // READ: Filtrar clientes por rango de fechas de registro
    public List<Cliente> buscarPorFechas(String fechaInicio, String fechaFin) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE fecha_registro BETWEEN ? AND ? ORDER BY fecha_registro ASC";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cli = new Cliente();
                cli.setIdCliente(rs.getInt("id_cliente"));
                cli.setCedula(rs.getString("cedula"));
                cli.setNombres(rs.getString("nombres"));
                cli.setApellidos(rs.getString("apellidos"));
                cli.setTelefono(rs.getString("telefono"));
                cli.setDireccion(rs.getString("direccion"));
                cli.setFechaRegistro(rs.getString("fecha_registro"));
                cli.setPuntosAcumulados(rs.getInt("puntos_acumulados"));
                cli.setFechaUltimaVisita(rs.getString("fecha_ultima_visita"));
                lista.add(cli);
            }
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
        }
        return lista;
    }

    // UPDATE: Modificar datos de un cliente
    public boolean modificar(Cliente c) {
        String sql = "UPDATE clientes SET nombres=?, apellidos=?, telefono=?, direccion=? WHERE id_cliente=?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombres());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.setInt(5, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Eliminar un cliente
    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en clientes: " + e.getMessage());
            return false;
        }
    }

    /** READ: true si ya existe un cliente con esa cédula (RF03: la cédula es única). */
    public boolean existeCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cedula = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar cédula: " + e.getMessage());
            return false;
        }
    }
}
