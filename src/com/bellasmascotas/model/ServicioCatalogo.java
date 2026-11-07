package com.bellasmascotas.model;

/** Servicio que ofrece la estética (tabla servicios_catalogo). */
public class ServicioCatalogo {

    private int idCatalogo;
    private String tipo;
    private double precioVenta;
    private String descripcion;

    public ServicioCatalogo() { }

    public int getIdCatalogo() { return idCatalogo; }
    public void setIdCatalogo(int idCatalogo) { this.idCatalogo = idCatalogo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** Texto que se muestra en las listas desplegables (JComboBox). */
    @Override
    public String toString() { return tipo; }

}
