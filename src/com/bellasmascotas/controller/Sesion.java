package com.bellasmascotas.controller;

import com.bellasmascotas.model.Usuario;

/** Guarda el usuario que inició sesión mientras la aplicación está abierta. */
public final class Sesion {

    private static Usuario usuario;

    private Sesion() { }

    public static void iniciar(Usuario u) { usuario = u; }

    public static void cerrar() { usuario = null; }

    public static Usuario getUsuario() { return usuario; }

    /** true solo si hay sesión activa y el rol del usuario tiene permiso sobre el módulo. */
    public static boolean puede(Permisos.Modulo modulo) {
        return usuario != null && Permisos.puede(usuario.getIdRol(), modulo);
    }

    /** true si el usuario en sesión tiene el rol indicado. */
    public static boolean esRol(int idRol) {
        return usuario != null && usuario.getIdRol() == idRol;
    }
}
