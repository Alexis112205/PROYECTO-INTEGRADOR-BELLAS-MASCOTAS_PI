package com.bellasmascotas.model;

/** Cita programada (tabla citas). Estados: Programada, En curso, Finalizada, Cancelada. */
public class Cita {

    private int idCita;
    private String fechaHoraCita;
    private String estado;
    private String observaciones;
    private int idMascota;
    private int idUsuario;
    private int idCatalogo;

    public Cita() { }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public String getFechaHoraCita() { return fechaHoraCita; }
    public void setFechaHoraCita(String fechaHoraCita) { this.fechaHoraCita = fechaHoraCita; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdCatalogo() { return idCatalogo; }
    public void setIdCatalogo(int idCatalogo) { this.idCatalogo = idCatalogo; }

}
