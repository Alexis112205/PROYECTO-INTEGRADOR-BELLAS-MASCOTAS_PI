package com.bellasmascotas.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centraliza la conexión a MySQL 8 local (puerto 3306, base bellasmascotas_db).
 * Los parámetros se leen del archivo config.properties (RNF07), así que cambiar
 * de equipo no exige recompilar. Cada DAO abre la conexión con try-with-resources.
 */
public class Conexion {

    private static final String ARCHIVO = "config.properties";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Properties props;

    /** Carga config.properties una sola vez (carpeta del proyecto o classpath). */
    private static synchronized Properties config() {
        if (props != null) return props;
        props = new Properties();
        try (InputStream in = new FileInputStream(ARCHIVO)) {
            props.load(in);
        } catch (IOException e) {
            try (InputStream in = Conexion.class.getResourceAsStream("/" + ARCHIVO)) {
                if (in != null) props.load(in);
                else System.err.println("No se encontró " + ARCHIVO + "; se usan valores por defecto.");
            } catch (IOException ex) {
                System.err.println("No se pudo leer " + ARCHIVO + ": " + ex.getMessage());
            }
        }
        return props;
    }

    /** Devuelve una propiedad de config.properties o el valor por defecto. */
    public static String propiedad(String clave, String porDefecto) {
        return config().getProperty(clave, porDefecto).trim();
    }

    /** Devuelve una propiedad numérica de config.properties o el valor por defecto. */
    public static int propiedadEntera(String clave, int porDefecto) {
        try {
            return Integer.parseInt(propiedad(clave, String.valueOf(porDefecto)));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    /** Abre una conexión nueva. Devuelve null si no fue posible conectarse. */
    public static Connection obtenerConexion() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(
                    propiedad("db.url", "jdbc:mysql://localhost:3306/bellasmascotas_db"),
                    propiedad("db.usuario", "root"),
                    propiedad("db.password", ""));
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("ERROR de conexión: " + e.getMessage());
            return null;
        }
    }

    /** Variante que lanza la excepción; la usan los DAO para que el error llegue a la vista. */
    public static Connection abrir() throws SQLException {
        Connection c = obtenerConexion();
        if (c == null) throw new SQLException("No hay conexión con MySQL. Revise config.properties y que el servidor esté encendido.");
        return c;
    }
}
