import com.bellasmascotas.dao.*;
import com.bellasmascotas.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Pruebas unitarias de la capa DAO sobre la base de prueba cargada con datos_a_todas_las_tablas.sql. */
public class PruebasUnitarias {
    static int ok = 0, fail = 0;
    static void prueba(String id, String metodo, String caso, boolean cumple, String obtenido) {
        System.out.println(id + "|" + metodo + "|" + caso + "|" + (cumple ? "Aprobada" : "FALLA") + "|" + obtenido);
        if (cumple) ok++; else fail++;
    }
    static int unaCifra(String sql) throws SQLException {
        try (Connection c = Conexion.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) { r.next(); return r.getInt(1); }
    }
    public static void main(String[] a) throws Exception {
        String hoy = LocalDate.now().toString();
        UsuarioDAO udao = new UsuarioDAO(); ClienteDAO cdao = new ClienteDAO(); MascotaDAO mdao = new MascotaDAO();
        CitaDAO citaDao = new CitaDAO(); InsumoDAO idao = new InsumoDAO(); ServicioDAO sdao = new ServicioDAO();
        CatalogoDAO catDao = new CatalogoDAO(); GastoDAO gdao = new GastoDAO(); VacunaDAO vdao = new VacunaDAO();

        Usuario u = udao.validarLogin("cbenitez", "admin123");
        prueba("PU01", "UsuarioDAO.validarLogin", "Credenciales válidas", u != null && u.getIdRol() == 1, u == null ? "null" : "Usuario " + u.getUsername() + ", rol " + u.getIdRol());
        prueba("PU02", "UsuarioDAO.validarLogin", "Contraseña incorrecta", udao.validarLogin("cbenitez", "clave") == null, "null");
        prueba("PU03", "UsuarioDAO.validarLogin", "Inyección SQL ' OR '1'='1", udao.validarLogin("' OR '1'='1", "' OR '1'='1") == null, "null");
        String h = Seguridad.sha256("admin123");
        String hm; try (Connection c = Conexion.abrir(); ResultSet r = c.createStatement().executeQuery("SELECT SHA2('admin123',256)")) { r.next(); hm = r.getString(1); }
        prueba("PU04", "Seguridad.sha256", "Hash igual al de MySQL SHA2()", h.length() == 64 && h.equals(hm), h.length() + " caracteres, coincide con SHA2()");
        Usuario dup = new Usuario(0, "Duplicado", "cbenitez", "123456", 2);
        prueba("PU05", "UsuarioDAO.insertar", "Username repetido (UNIQUE)", !udao.insertar(dup), "false");

        Cliente cl = new Cliente(); cl.setCedula("1090000001"); cl.setNombres("Prueba"); cl.setApellidos("Unitaria");
        cl.setTelefono("3000000000"); cl.setDireccion("Calle 1"); cl.setFechaRegistro(hoy);
        boolean ins = cdao.RegistrarCliente(cl);
        prueba("PU06", "ClienteDAO.RegistrarCliente", "Cliente nuevo", ins && cdao.existeCedula("1090000001"), "true; existeCedula = true");
        prueba("PU07", "ClienteDAO.RegistrarCliente", "Cédula duplicada (UNIQUE)", !cdao.RegistrarCliente(cl), "false");
        List<Cliente> b = cdao.buscarPorCedulaONombre("Castro");
        prueba("PU08", "ClienteDAO.buscarPorCedulaONombre", "Texto \"Castro\"", b.size() == 1, b.size() + " resultado(s)");
        List<Cliente> f = cdao.buscarPorFechas(hoy, hoy);
        prueba("PU09", "ClienteDAO.buscarPorFechas", "Registrados hoy", f.stream().anyMatch(x -> x.getCedula().equals("1090000001")), f.size() + " cliente(s) del día");
        Cliente nuevo = cdao.buscarPorCedulaONombre("1090000001").get(0);
        nuevo.setTelefono("3111111111");
        boolean mod = cdao.modificar(nuevo);
        prueba("PU10", "ClienteDAO.modificar", "Cambiar teléfono", mod && cdao.buscarPorCedulaONombre("1090000001").get(0).getTelefono().equals("3111111111"), "teléfono 3111111111");
        prueba("PU11", "ClienteDAO.eliminar", "Cliente con mascotas (FK)", !cdao.eliminar(1), "false (restricción de llave foránea)");

        Mascota m = new Mascota(); m.setNombre("Firulais"); m.setEspecie("Perro"); m.setRaza("Criollo"); m.setEdad(2);
        m.setIdCliente(nuevo.getIdCliente()); m.setAlergias("Shampoo de coco"); m.setObservaciones("");
        boolean mi = mdao.registrarMascota(m);
        List<Mascota> ms = mdao.listarPorCliente(nuevo.getIdCliente());
        prueba("PU12", "MascotaDAO.registrarMascota", "Mascota con alergia", mi && ms.size() == 1 && ms.get(0).tieneAlertas(), ms.size() + " mascota; tieneAlertas() = " + (ms.isEmpty() ? "-" : ms.get(0).tieneAlertas()));
        Mascota mm = ms.get(0);
        ControlVacuna v = new ControlVacuna(); v.setNombreVacuna("Rabia"); v.setFechaAplicacion(hoy); v.setProximaDosis(LocalDate.now().plusYears(1).toString()); v.setIdMascota(mm.getIdMascota());
        prueba("PU13", "VacunaDAO.registrar / listarPorMascota", "Registrar vacuna", vdao.registrar(v) && vdao.listarPorMascota(mm.getIdMascota()).size() == 1, "1 vacuna en el historial");

        String hora = LocalDate.now().plusDays(1) + " 10:00:00";
        Cita ci = new Cita(); ci.setFechaHoraCita(hora); ci.setObservaciones("prueba"); ci.setIdMascota(mm.getIdMascota()); ci.setIdUsuario(2);
        ServicioCatalogo full = catDao.listarTodo().stream().filter(s -> s.getTipo().equals("Full Combo")).findFirst().get();
        ci.setIdCatalogo(full.getIdCatalogo());
        boolean libre = citaDao.horarioDisponible(2, hora);
        boolean reg = citaDao.registrar(ci);
        prueba("PU14", "CitaDAO.horarioDisponible", "Mismo estilista, fecha y hora", libre && reg && !citaDao.horarioDisponible(2, hora), "antes: true; después de agendar: false");
        Cita guardada = citaDao.listarPorEstilista(2, hora.substring(0, 10)).stream().filter(x -> x.getIdMascota() == mm.getIdMascota()).findFirst().get();
        prueba("PU15", "CitaDAO.actualizarEstado", "Programada → En curso", citaDao.actualizarEstado(guardada.getIdCita(), "En curso"), "true");

        int registrosAntes = unaCifra("SELECT COUNT(*) FROM registros_estetica");
        int puntosAntes = unaCifra("SELECT puntos_acumulados FROM clientes WHERE id_cliente = " + nuevo.getIdCliente());
        Map<Integer, Integer> stockAntes = new HashMap<>();
        for (Insumo i : idao.listarTodo()) stockAntes.put(i.getIdInsumo(), i.getCantidadStock());
        List<RecetaServicio> receta = catDao.listarReceta(full.getIdCatalogo());
        String r1 = sdao.registrarServicios(guardada.getIdCita(), mm.getIdMascota(), 2, List.of(full), "ok", 10);
        boolean stockOk = true;
        for (Insumo i : idao.listarTodo()) {
            int usado = receta.stream().filter(x -> x.getIdInsumo() == i.getIdInsumo()).mapToInt(RecetaServicio::getCantidadUsada).sum();
            if (i.getCantidadStock() != stockAntes.get(i.getIdInsumo()) - usado) stockOk = false;
        }
        int puntosDespues = unaCifra("SELECT puntos_acumulados FROM clientes WHERE id_cliente = " + nuevo.getIdCliente());
        String estado; try (Connection c = Conexion.abrir(); ResultSet r = c.createStatement().executeQuery("SELECT estado FROM citas WHERE id_cita = " + guardada.getIdCita())) { r.next(); estado = r.getString(1); }
        prueba("PU16", "ServicioDAO.registrarServicios", "Servicio con stock suficiente (COMMIT)",
                r1 == null && stockOk && unaCifra("SELECT COUNT(*) FROM registros_estetica") == registrosAntes + 1 && puntosDespues == puntosAntes + 10 && estado.equals("Finalizada"),
                "1 registro, " + receta.size() + " insumos descontados, +" + (puntosDespues - puntosAntes) + " puntos, cita " + estado);

        Insumo primero = idao.listarTodo().stream().filter(i -> i.getIdInsumo() == receta.get(0).getIdInsumo()).findFirst().get();
        int prev = primero.getCantidadStock(); primero.setCantidadStock(0); idao.modificar(primero);
        int regAntes2 = unaCifra("SELECT COUNT(*) FROM registros_estetica");
        Map<Integer, Integer> st2 = new HashMap<>(); for (Insumo i : idao.listarTodo()) st2.put(i.getIdInsumo(), i.getCantidadStock());
        String r2 = sdao.registrarServicios(0, mm.getIdMascota(), 2, List.of(full), "sin stock", 10);
        boolean sinCambios = unaCifra("SELECT COUNT(*) FROM registros_estetica") == regAntes2;
        for (Insumo i : idao.listarTodo()) if (!st2.get(i.getIdInsumo()).equals(i.getCantidadStock())) sinCambios = false;
        prueba("PU17", "ServicioDAO.registrarServicios", "Insumo sin stock (ROLLBACK)", r2 != null && sinCambios, "mensaje de error; 0 registros y stock sin cambios");
        primero.setCantidadStock(prev); idao.modificar(primero);

        List<Insumo> bajos = idao.listarStockBajo(5);
        prueba("PU18", "InsumoDAO.listarStockBajo", "Umbral de 5 unidades", !bajos.isEmpty() && bajos.stream().allMatch(i -> i.getCantidadStock() <= 5), bajos.size() + " insumos con 5 o menos");
        prueba("PU19", "InsumoDAO.eliminar", "Insumo usado en una receta (FK)", !idao.eliminar(primero.getIdInsumo()), "false (restricción de llave foránea)");

        String ini = hoy.substring(0, 8) + "01";
        double g0 = gdao.totalPorRango(ini, hoy);
        GastoOperativo go = new GastoOperativo(); go.setConcepto("Prueba"); go.setMonto(15000); go.setFechaGasto(hoy); go.setIdUsuario(1);
        prueba("PU20", "GastoDAO.registrar / totalPorRango", "Gasto de $15.000", gdao.registrar(go) && gdao.totalPorRango(ini, hoy) == g0 + 15000, "total aumentó en 15.000");
        double tot = sdao.ingresosPorRango(ini, hoy);
        double suma = sdao.ingresosPorServicio(ini, hoy).stream().mapToDouble(o -> ((Number) o[3]).doubleValue()).sum();
        prueba("PU21", "ServicioDAO.ingresosPorRango / ingresosPorServicio", "Total = suma por servicio", Math.abs(tot - suma) < 0.01, String.format("%.0f = %.0f", tot, suma));
        DashboardDAO d = new DashboardDAO();
        prueba("PU22", "DashboardDAO.getTotalMascotas", "Coincide con el listado", d.getTotalMascotas() == mdao.listarTodo().size(), d.getTotalMascotas() + " mascotas");
        // PU23: dos servicios que usan el mismo insumo y solo alcanza para uno
        Insumo acond = idao.listarTodo().stream().filter(i -> i.getNombre().equals("Acondicionador")).findFirst().get();
        int prevAc = acond.getCantidadStock(); acond.setCantidadStock(1); idao.modificar(acond);
        ServicioCatalogo banoNormal = catDao.listarTodo().stream().filter(x -> x.getTipo().equals("Baño Normal")).findFirst().get();
        int regAntes3 = unaCifra("SELECT COUNT(*) FROM registros_estetica");
        String r3 = sdao.registrarServicios(0, mm.getIdMascota(), 2, List.of(banoNormal, full), "dos servicios", 10);
        int stockAc = idao.listarTodo().stream().filter(i -> i.getIdInsumo() == acond.getIdInsumo()).findFirst().get().getCantidadStock();
        prueba("PU23", "ServicioDAO.registrarServicios", "Dos servicios con el mismo insumo y stock para uno (ROLLBACK)",
                r3 != null && stockAc == 1 && unaCifra("SELECT COUNT(*) FROM registros_estetica") == regAntes3,
                "mensaje de error; stock sigue en " + stockAc + " y 0 registros");
        acond.setCantidadStock(prevAc); idao.modificar(acond);
        // PU24: la restricción CHECK impide un stock negativo
        boolean neg = idao.actualizarStock(acond.getIdInsumo(), prevAc + 1);
        int stockDesp = idao.listarTodo().stream().filter(i -> i.getIdInsumo() == acond.getIdInsumo()).findFirst().get().getCantidadStock();
        prueba("PU24", "InsumoDAO.actualizarStock", "Descontar más de lo disponible (CHECK)", !neg && stockDesp == prevAc,
                "false; el stock sigue en " + stockDesp + " (restricción CHECK)");
        System.out.println("RESUMEN|" + ok + "|" + fail);
    }
}
