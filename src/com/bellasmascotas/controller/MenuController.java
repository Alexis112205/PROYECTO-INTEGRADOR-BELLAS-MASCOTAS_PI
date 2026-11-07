package com.bellasmascotas.controller;

import com.bellasmascotas.dao.Conexion;
import com.bellasmascotas.dao.DashboardDAO;
import com.bellasmascotas.dao.InsumoDAO;
import com.bellasmascotas.model.Insumo;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.FrmLogin;
import com.bellasmascotas.view.FrmMenuPrincipal;
import com.bellasmascotas.view.FrmMenuPrincipal.BtnMenu;
import com.bellasmascotas.view.Refrescable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Controlador del menú principal (MVC): asigna los eventos del menú, verifica los
 * permisos antes de mostrar cada módulo, cambia de panel en el CardLayout,
 * recarga los datos del módulo y gestiona búsqueda, notificaciones y cierre de sesión.
 */
public class MenuController {

    private final FrmMenuPrincipal vistaMenu;
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private BtnMenu btnActivo;

    public MenuController(FrmMenuPrincipal vistaMenu) {
        this.vistaMenu = vistaMenu;
        configurarNavegacion();
        configurarCabecera();
        // Primer módulo visible: Panel de inicio
        if (!vistaMenu.tarjetas.isEmpty()) mostrar(vistaMenu.tarjetas.keySet().iterator().next());
        actualizarContador();
    }

    private void configurarNavegacion() {
        for (Map.Entry<BtnMenu, String> e : vistaMenu.tarjetas.entrySet()) {
            BtnMenu btn = e.getKey();
            agregarListener(btn, () -> mostrar(btn));
        }
    }

    /** Muestra el módulo si el rol tiene permiso (se verifica aunque el botón esté visible). */
    private void mostrar(BtnMenu btn) {
        if (btn.modulo != null && !Sesion.puede(btn.modulo)) {
            JOptionPane.showMessageDialog(vistaMenu, "No tienes permiso para acceder a este módulo.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (btnActivo != null) btnActivo.setActivo(false);
        btn.setActivo(true);
        btnActivo = btn;
        String nombre = vistaMenu.tarjetas.get(btn);
        vistaMenu.navegador.show(vistaMenu.pnlVistaDinamica, nombre);
        for (Component c : vistaMenu.pnlVistaDinamica.getComponents()) {
            if (c.isVisible() && c instanceof Refrescable) {
                cargando(true);
                try { ((Refrescable) c).refrescar(); } finally { cargando(false); }
            }
        }
        actualizarContador();
    }

    private void cargando(boolean si) {
        vistaMenu.setCursor(si ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void configurarCabecera() {
        // Búsqueda global: abre Clientes con el texto buscado
        vistaMenu.txtBuscar.addActionListener(e -> {
            String texto = vistaMenu.txtBuscar.getText().trim();
            if (vistaMenu.pnlClientes == null) return;
            mostrar(vistaMenu.btnClientes);
            vistaMenu.pnlClientes.buscar(texto);
            vistaMenu.txtBuscar.setText("");
        });

        // Notificaciones
        MouseAdapter campana = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mostrarNotificaciones(); }
        };
        vistaMenu.lblCampanita.addMouseListener(campana);
        vistaMenu.lblContador.addMouseListener(campana);

        // Cerrar sesión (FC01)
        vistaMenu.btnSalir.addActionListener(e -> {
            if (Estilo.confirmar(vistaMenu, "¿Deseas cerrar la sesión?")) {
                Sesion.cerrar();
                vistaMenu.dispose();
                new FrmLogin().setVisible(true);
            }
        });
    }

    /** Cuenta las alertas pendientes: citas de hoy, vacunas por vencer e insumos por reponer. */
    private void actualizarContador() {
        int total = dashboardDAO.getVacunasPorVencer();
        if (Sesion.puede(Permisos.Modulo.INVENTARIO)) {
            total += dashboardDAO.getInsumosPorReponer(Conexion.propiedadEntera("negocio.umbral_stock_bajo", 5));
        }
        vistaMenu.lblContador.setText(String.valueOf(total));
        vistaMenu.lblContador.setVisible(total > 0);
    }

    private void mostrarNotificaciones() {
        StringBuilder sb = new StringBuilder("<html><b>Notificaciones</b><br><br>");
        sb.append("• Citas activas para hoy: ").append(dashboardDAO.getCitasHoy()).append("<br>");
        sb.append("• Vacunas vencidas o por vencer (30 días): ").append(dashboardDAO.getVacunasPorVencer()).append("<br>");
        if (Sesion.puede(Permisos.Modulo.INVENTARIO)) {
            int umbral = Conexion.propiedadEntera("negocio.umbral_stock_bajo", 5);
            List<Insumo> bajos = new InsumoDAO().listarStockBajo(umbral);
            sb.append("• Insumos por reponer: ").append(bajos.size()).append("<br>");
            for (Insumo i : bajos) {
                sb.append("&nbsp;&nbsp;&nbsp;– ").append(i.getNombre()).append(": ").append(i.getCantidadStock()).append(" uds.<br>");
            }
        }
        sb.append("</html>");
        JOptionPane.showMessageDialog(vistaMenu, sb.toString(), "Notificaciones", JOptionPane.INFORMATION_MESSAGE);
        actualizarContador();
    }

    private void agregarListener(BtnMenu btn, Runnable accion) {
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { accion.run(); }
        };
        btn.addMouseListener(ma);
        btn.label.addMouseListener(ma);
    }

    /** Permite a otros módulos abrir un módulo (por ejemplo, desde el panel de inicio). */
    public void irA(BtnMenu btn) { mostrar(btn); }
}
