package com.bellasmascotas.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String username;
    private String password;
    private int idRol; // Referencia a la tabla roles

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String username, String password, int idRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.idRol = idRol;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    /** Texto que se muestra en las listas desplegables (JComboBox). */
    @Override
    public String toString() { return nombre; }
}
