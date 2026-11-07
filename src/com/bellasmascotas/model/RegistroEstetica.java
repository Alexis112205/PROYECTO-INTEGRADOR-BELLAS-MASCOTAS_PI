package com.bellasmascotas.model;

/** Servicio realizado o venta (tabla registros_estetica). */
public class RegistroEstetica {

    private int idRegistro;
    private String fecha;
    private double total;
    private int idMascota;
    private int idUsuario;
    private int idCatalogo;

    public RegistroEstetica() { }

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdCatalogo() { return idCatalogo; }
    public void setIdCatalogo(int idCatalogo) { this.idCatalogo = idCatalogo; }

}
