package com.bellasmascotas.model;

/** Insumo del inventario (tabla insumos). */
public class Insumo {

    private int idInsumo;
    private String nombre;
    private int cantidadStock;
    private double precioCosto;

    public Insumo() { }

    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(int cantidadStock) { this.cantidadStock = cantidadStock; }

    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double precioCosto) { this.precioCosto = precioCosto; }

    /** Texto que se muestra en las listas desplegables (JComboBox). */
    @Override
    public String toString() { return nombre; }

}
