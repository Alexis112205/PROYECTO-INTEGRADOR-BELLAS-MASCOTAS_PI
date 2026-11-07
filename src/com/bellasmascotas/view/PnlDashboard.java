package com.bellasmascotas.view;

import com.bellasmascotas.controller.Permisos;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.CitaDAO;
import com.bellasmascotas.dao.Conexion;
import com.bellasmascotas.dao.DashboardDAO;
import com.bellasmascotas.dao.InsumoDAO;
import com.bellasmascotas.dao.ServicioDAO;
import com.bellasmascotas.model.Cita;
import com.bellasmascotas.model.Insumo;
import com.bellasmascotas.model.Mascota;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/** Panel de inicio con los indicadores del día (CU16, FC11). */
public class PnlDashboard extends JPanel implements Refrescable {

    private final DashboardDAO dao = new DashboardDAO();
    private final boolean verFinanzas = Sesion.puede(Permisos.Modulo.REPORTES);

    private final JLabel valCitas = new JLabel("0"), detCitas = new JLabel(" ");
    private final JLabel valFinalizados = new JLabel("0"), detFinalizados = new JLabel(" ");
    private final JLabel valTercero = new JLabel("0"), detTercero = new JLabel(" ");
    private final JLabel valCuarto = new JLabel("0"), detCuarto = new JLabel(" ");
    private final JLabel lblFecha = new JLabel();

    private final DefaultTableModel modeloAgenda = Estilo.modelo("Hora", "Mascota / propietario", "Servicio", "Estilista", "Estado");
    private final DefaultTableModel modeloLateral;
    private final JLabel lblResumen = new JLabel();

    public PnlDashboard() {
        setLayout(new BorderLayout());
        setBackground(Estilo.FONDO);
        JPanel c = Estilo.contenedor();
        add(c);

        lblFecha.setFont(Estilo.fuente(Font.PLAIN, 12));
        lblFecha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 223, 224), 1, true), new EmptyBorder(6, 12, 6, 12)));
        c.add(Estilo.encabezado("Resumen del día",
                "Citas, servicios" + (verFinanzas ? ", ingresos e insumos que requieren atención." : " y mascotas que requieren atención."),
                lblFecha), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 14));
        cuerpo.setOpaque(false);
        JPanel kpis = new JPanel(new GridLayout(1, 4, 14, 0));
        kpis.setOpaque(false);
        kpis.add(Tarjeta.indicador("Citas programadas hoy", valCitas, detCitas));
        kpis.add(Tarjeta.indicador("Servicios finalizados", valFinalizados, detFinalizados));
        kpis.add(Tarjeta.indicador(verFinanzas ? "Ingresos del día" : "Mascotas registradas", valTercero, detTercero));
        kpis.add(Tarjeta.indicador(verFinanzas ? "Insumos por reponer" : "Vacunas por vencer", valCuarto, detCuarto));
        cuerpo.add(kpis, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;

        Tarjeta agenda = new Tarjeta(new BorderLayout());
        agenda.add(Estilo.encabezadoTarjeta("Agenda de hoy", Estilo.etiqueta("Citas del día")), BorderLayout.NORTH);
        JTable tabla = Estilo.tabla(modeloAgenda);
        tabla.setRowHeight(42);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new Estilo.PillRenderer());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        agenda.add(Estilo.scroll(tabla), BorderLayout.CENTER);
        g.gridx = 0; g.weightx = 2; g.insets = new Insets(0, 0, 0, 14);
        agenda.setPreferredSize(new Dimension(10, 10));
        grid.add(agenda, g);

        Tarjeta lateral = new Tarjeta(new BorderLayout());
        if (verFinanzas) {
            modeloLateral = Estilo.modelo("Insumo", "Stock", "Estado");
            lateral.add(Estilo.encabezadoTarjeta("Atención de inventario", null), BorderLayout.NORTH);
        } else {
            modeloLateral = Estilo.modelo("Mascota", "Vacuna", "Próxima dosis");
            lateral.add(Estilo.encabezadoTarjeta("Vacunas por vencer", null), BorderLayout.NORTH);
        }
        JTable tLat = Estilo.tabla(modeloLateral);
        tLat.setRowHeight(42);
        tLat.getColumnModel().getColumn(2).setCellRenderer(new Estilo.PillRenderer());
        tLat.getColumnModel().getColumn(2).setPreferredWidth(170);
        tLat.getColumnModel().getColumn(0).setPreferredWidth(190);
        tLat.getColumnModel().getColumn(1).setPreferredWidth(80);
        lateral.add(Estilo.scroll(tLat), BorderLayout.CENTER);
        lblResumen.setFont(Estilo.fuente(Font.PLAIN, 12));
        lblResumen.setBorder(new EmptyBorder(12, 0, 0, 0));
        lateral.add(lblResumen, BorderLayout.SOUTH);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0);
        lateral.setPreferredSize(new Dimension(10, 10));
        grid.add(lateral, g);
        cuerpo.add(grid, BorderLayout.CENTER);
        c.add(cuerpo, BorderLayout.CENTER);
    }

    @Override
    public void refrescar() {
        LocalDate hoy = LocalDate.now();
        String dia = hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-CO"));
        String mes = hoy.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO"));
        lblFecha.setText(Character.toUpperCase(dia.charAt(0)) + dia.substring(1) + ", " + hoy.getDayOfMonth() + " " + mes + " " + hoy.getYear());

        Datos datos = new Datos();
        List<Cita> citas = new CitaDAO().listarPorFecha(Estilo.hoy());
        if (Sesion.esRol(Permisos.ESTILISTA)) {
            int yo = Sesion.getUsuario().getIdUsuario();
            citas = citas.stream().filter(x -> x.getIdUsuario() == yo).toList();
        }
        long activas = citas.stream().filter(x -> !"Cancelada".equals(x.getEstado())).count();
        long pendientes = citas.stream().filter(x -> "Programada".equals(x.getEstado())).count();
        long enCurso = citas.stream().filter(x -> "En curso".equals(x.getEstado())).count();
        long finalizadas = citas.stream().filter(x -> "Finalizada".equals(x.getEstado())).count();
        valCitas.setText(String.valueOf(activas));
        detCitas.setText(pendientes + " pendientes · " + enCurso + " en curso");
        valFinalizados.setText(String.valueOf(finalizadas));
        detFinalizados.setText("de " + activas + " citas del día");

        modeloAgenda.setRowCount(0);
        for (Cita ci : citas) {
            Mascota m = datos.mascotas.get(ci.getIdMascota());
            String detalle = (m == null ? "" : m.getRaza() + " · ") + datos.nombreCliente(m == null ? 0 : m.getIdCliente());
            modeloAgenda.addRow(new Object[]{ci.getFechaHoraCita().substring(11, 16),
                    Estilo.dosLineas(datos.nombreMascota(ci.getIdMascota()), detalle),
                    datos.nombreServicio(ci.getIdCatalogo()), datos.nombreUsuario(ci.getIdUsuario()), ci.getEstado()});
        }

        modeloLateral.setRowCount(0);
        if (verFinanzas) {
            int umbral = Conexion.propiedadEntera("negocio.umbral_stock_bajo", 5);
            List<Insumo> bajos = new InsumoDAO().listarStockBajo(umbral);
            valTercero.setText(Estilo.pesos(dao.getVentasDelDia()));
            detTercero.setText("COP · " + new ServicioDAO().listarPorFecha(Estilo.hoy()).size() + " servicios registrados");
            valCuarto.setText(String.valueOf(bajos.size()));
            detCuarto.setText("con " + umbral + " unidades o menos");
            for (Insumo i : bajos) {
                modeloLateral.addRow(new Object[]{Estilo.dosLineas(i.getNombre(), "Costo " + Estilo.pesos(i.getPrecioCosto())),
                        i.getCantidadStock() + " uds.", i.getCantidadStock() == 0 ? "Agotado" : "Stock bajo"});
            }
            double ingresos = dao.getIngresosDelMes(), gastos = dao.getGastosDelMes();
            lblResumen.setText("<html><span style='color:#6B7C85'>Ingresos del mes</span><br><b style='font-size:16px'>"
                    + Estilo.pesos(ingresos) + "</b><br><span style='color:#6B7C85'>Gastos: " + Estilo.pesos(gastos)
                    + " · Utilidad: " + Estilo.pesos(ingresos - gastos) + "</span><br><span style='color:#6B7C85'>Mascotas registradas: "
                    + dao.getTotalMascotas() + " · Clientes nuevos hoy: " + dao.getNuevosClientesHoy() + "</span></html>");
        } else {
            valTercero.setText(String.valueOf(dao.getTotalMascotas()));
            detTercero.setText(dao.getNuevosClientesHoy() + " clientes nuevos hoy");
            valCuarto.setText(String.valueOf(dao.getVacunasPorVencer()));
            detCuarto.setText("vencidas o en los próximos 30 días");
            for (Mascota m : datos.mascotas.values()) {
                for (var v : new com.bellasmascotas.dao.VacunaDAO().listarPorMascota(m.getIdMascota())) {
                    if (v.getProximaDosis() == null) continue;
                    LocalDate p = LocalDate.parse(v.getProximaDosis());
                    if (!p.isAfter(hoy.plusDays(30))) {
                        modeloLateral.addRow(new Object[]{m.getNombre(), v.getNombreVacuna(),
                                (p.isBefore(hoy) ? "Vencida " : "Por vencer ") + v.getProximaDosis()});
                    }
                }
            }
            lblResumen.setText("<html><span style='color:#6B7C85'>Revise la ficha de cada mascota antes de atenderla.</span></html>");
        }
    }
}
