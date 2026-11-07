package com.bellasmascotas.model;

/** Vacuna aplicada a una mascota (tabla control_vacunas). */
public class ControlVacuna {

    private int idVacuna;
    private String nombreVacuna;
    private String fechaAplicacion;
    private String proximaDosis;
    private int idMascota;

    public ControlVacuna() { }

    public int getIdVacuna() { return idVacuna; }
    public void setIdVacuna(int idVacuna) { this.idVacuna = idVacuna; }

    public String getNombreVacuna() { return nombreVacuna; }
    public void setNombreVacuna(String nombreVacuna) { this.nombreVacuna = nombreVacuna; }

    public String getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(String fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public String getProximaDosis() { return proximaDosis; }
    public void setProximaDosis(String proximaDosis) { this.proximaDosis = proximaDosis; }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

}
