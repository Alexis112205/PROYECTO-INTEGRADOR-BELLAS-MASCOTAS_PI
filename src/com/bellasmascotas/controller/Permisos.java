package com.bellasmascotas.controller;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Matriz de permisos por rol (RF02, CC01).
 * Un único lugar define qué módulos puede usar cada rol.
 * Si el rol no existe en la matriz, no tiene acceso a nada (denegar por defecto).
 */
public final class Permisos {

    /** Módulos del sistema (coinciden con las opciones del menú lateral). */
    public enum Modulo { DASHBOARD, AGENDA, CLIENTES, MASCOTAS, VENTAS, INVENTARIO, REPORTES, USUARIOS, NOTIFICACIONES }

    // Deben coincidir con roles.id_rol en la base de datos
    public static final int ADMINISTRADOR = 1;
    public static final int ESTILISTA = 2;
    public static final int RECEPCION = 3;

    private static final Map<Integer, Set<Modulo>> MATRIZ = new HashMap<>();

    static {
        // Administrador: acceso total al sistema
        MATRIZ.put(ADMINISTRADOR, EnumSet.allOf(Modulo.class));

        // Estilista: su agenda con el registro de servicios ("Mi agenda") y las fichas de las mascotas
        MATRIZ.put(ESTILISTA, EnumSet.of(
                Modulo.DASHBOARD, Modulo.MASCOTAS, Modulo.VENTAS, Modulo.NOTIFICACIONES));

        // Recepción: atención de clientes, agenda de citas, mascotas y registro de ventas
        MATRIZ.put(RECEPCION, EnumSet.of(
                Modulo.DASHBOARD, Modulo.AGENDA, Modulo.CLIENTES, Modulo.MASCOTAS, Modulo.VENTAS, Modulo.NOTIFICACIONES));
    }

    private Permisos() { }

    public static boolean puede(int idRol, Modulo modulo) {
        Set<Modulo> permitidos = MATRIZ.get(idRol);
        return permitidos != null && permitidos.contains(modulo);
    }

    /** Nombre legible del rol según roles.id_rol. */
    public static String nombreRol(int idRol) {
        switch (idRol) {
            case ADMINISTRADOR: return "Administrador";
            case ESTILISTA:     return "Estilista";
            case RECEPCION:     return "Recepción";
            default:            return "Sin rol asignado";
        }
    }
}
