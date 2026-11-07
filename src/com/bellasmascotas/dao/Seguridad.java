package com.bellasmascotas.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Seguridad {

    // Devuelve el hash SHA-256 en hexadecimal minúscula (64 caracteres),
    // igual al resultado de SHA2(texto, 256) en MySQL.
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}