package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.GastoDAO;
import com.bellasmascotas.dao.ServicioDAO;
import com.bellasmascotas.model.GastoOperativo;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/** Módulo Reportes financieros y gastos operativos (CU14, CU15; FC10, RF10). */
public class PnlReportes extends JPanel implements Refrescable {

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final GastoDAO gastoDAO = new GastoDAO();
    private List<GastoOperativo> gastos = List.of();

    public JComboBox<String> cbTipo = Estilo.combo();
    public JTextField txtInicio = Estilo.campo("AAAA-MM-DD");
    public JTextField txtFin = Estilo.campo("AAAA-MM-DD");
    public JButton btnGenerar, btnNuevoGasto, btnEliminarGasto;
    private final JLabel valIngresos = new JLabel(), detIngresos = new JLabel("SUM(total) registros_estetica");
    private final JLabel valGastos = new JLabel(), detGastos = new JLabel("SUM(monto) gastos_operativos");
    private final JLabel valUtilidad = new JLabel(), detUtilidad = new JLabel("Ingresos − gastos");
    private final JLabel valServicios = new JLabel(), detServicios = new JLabel();
    public DefaultTableModel modeloServicios = Estilo.modelo("Servicio", "Cantidad", "Precio", "Ingresos", "Participación");
    public DefaultTableModel modeloGastos = Estilo.modelo("ID", "Fecha", "Concepto", "Monto");
    private final JTable tablaGastos = Estilo.tabla(modeloGastos);

    public PnlReportes() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filtros.setOpaque(false);
        cbTipo.addItem("Mensual"); cbTipo.addItem("Semanal"); cbTipo.addItem("Personalizado");
        txtInicio.setPreferredSize(new Dimension(120, 34));
        txtFin.setPreferredSize(new Dimension(120, 34));
        btnGenerar = Estilo.botonPrimario("Generar");
        filtros.add(cbTipo); filtros.add(txtInicio); filtros.add(txtFin); filtros.add(btnGenerar);
        c.add(Estilo.encabezado("Reportes financieros", "Ingresos por servicios, gastos operativos y utilidad del período.", filtros), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 14));
        cuerpo.setOpaque(false);
        JPanel kpis = new JPanel(new GridLayout(1, 4, 14, 0));
        kpis.setOpaque(false);
        kpis.add(Tarjeta.indicador("Ingresos del período", valIngresos, detIngresos));
        kpis.add(Tarjeta.indicador("Gastos operativos", valGastos, detGastos));
        kpis.add(Tarjeta.indicador("Utilidad", valUtilidad, detUtilidad));
        kpis.add(Tarjeta.indicador("Servicios realizados", valServicios, detServicios));
        cuerpo.add(kpis, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1;
        Tarjeta porServicio = new Tarjeta(new BorderLayout());
        porServicio.add(Estilo.encabezadoTarjeta("Ingresos por tipo de servicio", Estilo.etiqueta("JOIN y GROUP BY por servicio")), BorderLayout.NORTH);
        JTable ts = Estilo.tabla(modeloServicios);
        ts.getColumnModel().getColumn(4).setCellRenderer(new BarraRenderer());
        ts.getColumnModel().getColumn(4).setPreferredWidth(140);
        ts.getColumnModel().getColumn(0).setPreferredWidth(150);
        ts.getColumnModel().getColumn(1).setPreferredWidth(70);
        porServicio.add(Estilo.scroll(ts), BorderLayout.CENTER);
        g.gridx = 0; g.weightx = 1.3; g.insets = new Insets(0, 0, 0, 14);
        porServicio.setPreferredSize(new Dimension(10, 10));
        grid.add(porServicio, g);

        Tarjeta pGastos = new Tarjeta(new BorderLayout());
        JPanel bg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        bg.setOpaque(false);
        btnNuevoGasto = Estilo.botonSecundario("+ Registrar gasto");
        btnEliminarGasto = Estilo.botonPeligro("Eliminar");
        bg.add(btnNuevoGasto); bg.add(btnEliminarGasto);
        pGastos.add(Estilo.encabezadoTarjeta("Gastos operativos", bg), BorderLayout.NORTH);
        Estilo.ocultarColumna(tablaGastos, 0);
        tablaGastos.getColumnModel().getColumn(2).setPreferredWidth(200);
        pGastos.add(Estilo.scroll(tablaGastos), BorderLayout.CENTER);
        JLabel h = Estilo.etiqueta("Cada gasto queda asociado al usuario que lo registró (id_usuario).");
        h.setBorder(new EmptyBorder(8, 0, 0, 0));
        pGastos.add(h, BorderLayout.SOUTH);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0);
        pGastos.setPreferredSize(new Dimension(10, 10));
        grid.add(pGastos, g);
        cuerpo.add(grid, BorderLayout.CENTER);
        c.add(cuerpo, BorderLayout.CENTER);

        cbTipo.addActionListener(e -> aplicarTipo());
        btnGenerar.addActionListener(e -> generar());
        btnNuevoGasto.addActionListener(e -> registrarGasto());
        btnEliminarGasto.addActionListener(e -> eliminarGasto());
        aplicarTipo();
    }

    /** Llena las fechas según el tipo: mes actual, semana actual (lunes a domingo) o personalizado. */
    private void aplicarTipo() {
        LocalDate hoy = LocalDate.now();
        String tipo = (String) cbTipo.getSelectedItem();
        boolean editable = "Personalizado".equals(tipo);
        if ("Mensual".equals(tipo)) {
            txtInicio.setText(hoy.withDayOfMonth(1).toString());
            txtFin.setText(hoy.withDayOfMonth(hoy.lengthOfMonth()).toString());
        } else if ("Semanal".equals(tipo)) {
            LocalDate lunes = hoy.with(DayOfWeek.MONDAY);
            txtInicio.setText(lunes.toString());
            txtFin.setText(lunes.plusDays(6).toString());
        }
        txtInicio.setEditable(editable);
        txtFin.setEditable(editable);
    }

    @Override
    public void refrescar() { generar(); }

    /** Calcula ingresos, gastos, utilidad e ingresos por servicio del período (RF10). */
    private void generar() {
        String ini = txtInicio.getText().trim(), fin = txtFin.getText().trim();
        if (!Estilo.esFechaValida(ini) || !Estilo.esFechaValida(fin)) { Estilo.aviso(this, "Formato de fecha incorrecto (AAAA-MM-DD)."); return; }
        if (ini.compareTo(fin) > 0) { Estilo.aviso(this, "La fecha inicial no puede ser mayor que la final."); return; }
        double ingresos = servicioDAO.ingresosPorRango(ini, fin);
        double totalGastos = gastoDAO.totalPorRango(ini, fin);
        List<Object[]> filas = servicioDAO.ingresosPorServicio(ini, fin);
        int cantidad = filas.stream().mapToInt(f -> (int) f[2]).sum();
        valIngresos.setText(Estilo.pesos(ingresos));
        valGastos.setText(Estilo.pesos(totalGastos));
        valUtilidad.setText(Estilo.pesos(ingresos - totalGastos));
        valUtilidad.setForeground(ingresos - totalGastos >= 0 ? new Color(46, 125, 79) : new Color(179, 54, 47));
        valServicios.setText(String.valueOf(cantidad));
        detServicios.setText(cantidad == 0 ? "Sin servicios en el período" : "Promedio " + Estilo.pesos(ingresos / cantidad) + " por servicio");

        double max = filas.stream().mapToDouble(f -> (double) f[3]).max().orElse(1);
        modeloServicios.setRowCount(0);
        for (Object[] f : filas) {
            modeloServicios.addRow(new Object[]{f[0], f[2], Estilo.pesos((double) f[1]), Estilo.pesos((double) f[3]),
                    (double) f[3] / max});
        }
        gastos = gastoDAO.listarPorRango(ini, fin);
        modeloGastos.setRowCount(0);
        for (GastoOperativo gst : gastos)
            modeloGastos.addRow(new Object[]{gst.getIdGasto(), gst.getFechaGasto(), gst.getConcepto(), Estilo.pesos(gst.getMonto())});
    }

    /** Registra un gasto operativo (CU14, FC10). */
    private void registrarGasto() {
        JTextField concepto = Estilo.campo("Ej.: Arriendo del local");
        JTextField monto = Estilo.campo("Pesos, sin puntos");
        JTextField fecha = Estilo.campo("AAAA-MM-DD");
        fecha.setText(Estilo.hoy());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        Estilo.fila(p, "Concepto", concepto);
        Estilo.fila(p, "Monto", monto);
        Estilo.fila(p, "Fecha del gasto", fecha);
        p.setPreferredSize(new Dimension(320, 170));
        if (JOptionPane.showConfirmDialog(this, p, "Registrar gasto operativo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        if (concepto.getText().isBlank()) { Estilo.aviso(this, "Escriba el concepto del gasto."); return; }
        double valor;
        try {
            valor = Double.parseDouble(monto.getText().trim().replace(".", "").replace(",", "."));
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { Estilo.aviso(this, "El monto debe ser un número mayor que 0."); return; }
        if (!Estilo.esFechaValida(fecha.getText().trim())) { Estilo.aviso(this, "La fecha debe tener el formato AAAA-MM-DD."); return; }
        GastoOperativo g = new GastoOperativo();
        g.setConcepto(concepto.getText().trim());
        g.setMonto(valor);
        g.setFechaGasto(fecha.getText().trim());
        g.setIdUsuario(Sesion.getUsuario().getIdUsuario());
        if (gastoDAO.registrar(g)) generar();
        else Estilo.error(this, "No se pudo registrar el gasto.");
    }

    private void eliminarGasto() {
        int fila = tablaGastos.getSelectedRow();
        if (fila < 0) { Estilo.aviso(this, "Selecciona un gasto de la tabla."); return; }
        int id = (int) modeloGastos.getValueAt(fila, 0);
        if (Estilo.confirmar(this, "¿Eliminar este gasto?") && gastoDAO.eliminar(id)) generar();
    }

    /** Dibuja la participación de cada servicio como una barra horizontal. */
    private static class BarraRenderer extends DefaultTableCellRenderer {
        private double valor;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, "", sel, false, r, c);
            valor = v instanceof Double ? (Double) v : 0;
            return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int ancho = (int) ((getWidth() - 24) * valor);
            g2.setColor(new Color(91, 184, 180));
            g2.fillRoundRect(10, getHeight() / 2 - 4, Math.max(ancho, 4), 8, 8, 8);
            g2.dispose();
        }
    }
}
