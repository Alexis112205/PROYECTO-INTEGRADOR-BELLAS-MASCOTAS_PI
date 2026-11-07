package com.bellasmascotas.dao;

import com.bellasmascotas.model.Rol;
import com.bellasmascotas.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla usuarios (RF01, FC02). Las contraseñas se guardan como hash SHA-256. */
public class UsuarioDAO {

    /** READ: valida credenciales comparando el hash SHA-256 (RF01). Devuelve null si no coinciden. */
    public Usuario validarLogin(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, Seguridad.sha256(pass));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    /** CREATE: registra un usuario; la contraseña se guarda como hash (FC02). */
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, username, password, id_rol) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setString(3, Seguridad.sha256(u.getPassword()));
            ps.setInt(4, u.getIdRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /** READ: lista todos los usuarios ordenados por nombre (FC02). */
    public List<Usuario> listarTodo() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    /** READ: usuarios de un rol, por ejemplo los estilistas para asignar citas. */
    public List<Usuario> listarPorRol(int idRol) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE id_rol = ? ORDER BY nombre";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios por rol: " + e.getMessage());
        }
        return lista;
    }

    /**
     * UPDATE: modifica nombre, usuario y rol. Si getPassword() trae texto,
     * también cambia la contraseña (se guarda el hash) (FC02).
     */
    public boolean modificar(Usuario u) {
        boolean cambiaClave = u.getPassword() != null && !u.getPassword().isEmpty();
        String sql = cambiaClave
                ? "UPDATE usuarios SET nombre = ?, username = ?, id_rol = ?, password = ? WHERE id_usuario = ?"
                : "UPDATE usuarios SET nombre = ?, username = ?, id_rol = ? WHERE id_usuario = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setInt(3, u.getIdRol());
            if (cambiaClave) {
                ps.setString(4, Seguridad.sha256(u.getPassword()));
                ps.setInt(5, u.getIdUsuario());
            } else {
                ps.setInt(4, u.getIdUsuario());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
            return false;
        }
    }

    /** DELETE: elimina un usuario. Falla si tiene citas, servicios o gastos asociados. */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    /** READ: roles disponibles para la lista desplegable del formulario de usuarios. */
    public List<Rol> listarRoles() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id_rol";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Rol(rs.getInt("id_rol"), rs.getString("nombre"), rs.getString("descripcion")));
        } catch (SQLException e) {
            System.err.println("Error al listar roles: " + e.getMessage());
        }
        return lista;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"),
                rs.getString("username"), rs.getString("password"), rs.getInt("id_rol"));
    }
}
