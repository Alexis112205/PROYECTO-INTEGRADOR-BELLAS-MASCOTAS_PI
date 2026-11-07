package com.bellasmascotas.model;

/** Mascota atendida (tabla mascotas). */
public class Mascota {

    private int idMascota;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String observaciones;
    private int idCliente;
    private boolean agresivo;
    private boolean requiereBozal;
    private String rutaImagen;
    private String alergias;

    public Mascota() { }

    public Mascota(int idMascota, String nombre, String especie, String raza, int edad,
                   String observaciones, int idCliente, String rutaImagen) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.observaciones = observaciones;
        this.idCliente = idCliente;
        this.rutaImagen = rutaImagen;
    }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public boolean isAgresivo() { return agresivo; }
    public void setAgresivo(boolean agresivo) { this.agresivo = agresivo; }

    public boolean isRequiereBozal() { return requiereBozal; }
    public void setRequiereBozal(boolean requiereBozal) { this.requiereBozal = requiereBozal; }

    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    /** true si la mascota requiere cuidado especial: alergias, agresividad o bozal (FC05). */
    public boolean tieneAlertas() {
        return agresivo || requiereBozal || (alergias != null && !alergias.trim().isEmpty());
    }

    /** Texto corto con las alertas de cuidado de la mascota (FC05). */
    public String textoAlertas() {
        StringBuilder sb = new StringBuilder();
        if (alergias != null && !alergias.trim().isEmpty()) sb.append("Alergias: ").append(alergias.trim()).append(". ");
        if (agresivo) sb.append("Agresivo. ");
        if (requiereBozal) sb.append("Requiere bozal.");
        return sb.toString().trim();
    }

    @Override
    public String toString() { return nombre + (raza != null && !raza.isEmpty() ? " (" + raza + ")" : ""); }
}
