package com.bellasmascotas.model;

/** Rol del usuario (tabla roles). */
public class Rol {

    private int idRol;
    private String nombre;
    private String descripcion;

    public Rol() { }

    public Rol(int idRol, String nombre, String descripcion) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** Texto que se muestra en las listas desplegables (JComboBox). */
    @Override
    public String toString() { return nombre; }

}
