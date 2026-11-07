import com.bellasmascotas.dao.*;
import java.util.*;
import java.util.function.Supplier;
public class Rendimiento {
    static void medir(String nombre, Supplier<Object> f) {
        f.get(); long[] t = new long[5];
        for (int i = 0; i < 5; i++) { long a = System.nanoTime(); Object r = f.get(); t[i] = (System.nanoTime() - a) / 1_000_000; }
        Arrays.sort(t); Object r = f.get();
        String n = r instanceof List ? ((List<?>) r).size() + " filas" : String.valueOf(r);
        System.out.println(nombre + "|" + n + "|" + t[2] + "|" + t[4]);
    }
    public static void main(String[] a) {
        String hoy = java.time.LocalDate.now().toString();
        medir("ClienteDAO.obtenerTodos()", () -> new ClienteDAO().obtenerTodos());
        medir("ClienteDAO.buscarPorCedulaONombre(\"Perez\")", () -> new ClienteDAO().buscarPorCedulaONombre("Perez"));
        medir("ClienteDAO.buscarPorFechas(año completo)", () -> new ClienteDAO().buscarPorFechas("2026-01-01", "2026-12-31"));
        medir("ServicioDAO.ingresosPorServicio(año)", () -> new ServicioDAO().ingresosPorServicio("2026-01-01", "2026-12-31"));
        medir("ServicioDAO.ingresosPorRango(año)", () -> new ServicioDAO().ingresosPorRango("2026-01-01", "2026-12-31"));
        medir("DashboardDAO.getIngresosDelMes()", () -> new DashboardDAO().getIngresosDelMes());
        medir("MascotaDAO.listarTodo()", () -> new MascotaDAO().listarTodo());
    }
}
