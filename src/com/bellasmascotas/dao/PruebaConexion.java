package com.bellasmascotas.dao;

import java.sql.Connection;

/** Programa de apoyo: verifica que config.properties permite conectarse a MySQL. */
public class PruebaConexion {
    public static void main(String[] args) {
        try (Connection con = Conexion.obtenerConexion()) {
            System.out.println(con != null && !con.isClosed()
                    ? "Conexión exitosa con bellasmascotas_db."
                    : "No se pudo conectar. Revise config.properties.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
