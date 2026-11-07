package com.bellasmascotas.model;

/** Insumo y cantidad que consume un servicio (tabla receta_servicio). */
public class RecetaServicio {

    private int idReceta;
    private int idCatalogo;
    private int idInsumo;
    private int cantidadUsada;

    public RecetaServicio() { }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public int getIdCatalogo() { return idCatalogo; }
    public void setIdCatalogo(int idCatalogo) { this.idCatalogo = idCatalogo; }

    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }

    public int getCantidadUsada() { return cantidadUsada; }
    public void setCantidadUsada(int cantidadUsada) { this.cantidadUsada = cantidadUsada; }

}
