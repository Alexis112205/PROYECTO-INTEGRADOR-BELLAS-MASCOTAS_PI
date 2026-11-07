package com.bellasmascotas.view;

import com.bellasmascotas.dao.*;
import com.bellasmascotas.model.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga en memoria las listas de referencia (clientes, mascotas, usuarios, servicios e insumos)
 * para mostrar nombres en lugar de IDs en tablas y listas desplegables.
 * Se vuelve a crear cada vez que un panel se refresca, así los datos siempre están al día.
 */
public class Datos {

    public final Map<Integer, Cliente> clientes = new LinkedHashMap<>();
    public final Map<Integer, Mascota> mascotas = new LinkedHashMap<>();
    public final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    public final Map<Integer, ServicioCatalogo> servicios = new LinkedHashMap<>();
    public final Map<Integer, Insumo> insumos = new LinkedHashMap<>();

    public Datos() {
        for (Cliente c : new ClienteDAO().obtenerTodos()) clientes.put(c.getIdCliente(), c);
        for (Mascota m : new MascotaDAO().listarTodo()) mascotas.put(m.getIdMascota(), m);
        for (Usuario u : new UsuarioDAO().listarTodo()) usuarios.put(u.getIdUsuario(), u);
        for (ServicioCatalogo s : new CatalogoDAO().listarTodo()) servicios.put(s.getIdCatalogo(), s);
        for (Insumo i : new InsumoDAO().listarTodo()) insumos.put(i.getIdInsumo(), i);
    }

    public String nombreCliente(int id) {
        Cliente c = clientes.get(id);
        return c == null ? "Sin propietario" : c.getNombreCompleto();
    }

    public String nombreMascota(int id) {
        Mascota m = mascotas.get(id);
        return m == null ? "—" : m.getNombre();
    }

    public String nombreUsuario(int id) {
        Usuario u = usuarios.get(id);
        return u == null ? "—" : u.getNombre();
    }

    public String nombreServicio(int id) {
        ServicioCatalogo s = servicios.get(id);
        return s == null ? "—" : s.getTipo();
    }

    /** Propietario de una mascota (o null). */
    public Cliente propietario(Mascota m) {
        return m == null ? null : clientes.get(m.getIdCliente());
    }

    /** Usuarios con rol Estilista (para asignar citas y servicios). */
    public List<Usuario> estilistas() {
        return usuarios.values().stream().filter(u -> u.getIdRol() == 2).toList();
    }
}
