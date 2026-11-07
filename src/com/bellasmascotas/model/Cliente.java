package com.bellasmascotas.model;

/** Propietario de las mascotas (tabla clientes). */
public class Cliente {

    private int idCliente;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String fechaRegistro;
    private int puntosAcumulados;
    private String fechaUltimaVisita;

    public Cliente() { }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }

    public String getFechaUltimaVisita() { return fechaUltimaVisita; }
    public void setFechaUltimaVisita(String fechaUltimaVisita) { this.fechaUltimaVisita = fechaUltimaVisita; }

    /** Nombre completo para mostrar en tablas y listas. */
    public String getNombreCompleto() { return nombres + " " + apellidos; }

    @Override
    public String toString() { return cedula + " · " + getNombreCompleto(); }

}
