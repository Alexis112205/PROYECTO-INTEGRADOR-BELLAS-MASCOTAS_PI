package com.bellasmascotas.model;

/** Gasto del negocio (tabla gastos_operativos). */
public class GastoOperativo {

    private int idGasto;
    private String concepto;
    private double monto;
    private String fechaGasto;
    private int idUsuario;

    public GastoOperativo() { }

    public int getIdGasto() { return idGasto; }
    public void setIdGasto(int idGasto) { this.idGasto = idGasto; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(String fechaGasto) { this.fechaGasto = fechaGasto; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

}
