package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.controller.Permisos;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.CatalogoDAO;
import com.bellasmascotas.dao.CitaDAO;
import com.bellasmascotas.dao.Conexion;
import com.bellasmascotas.dao.ServicioDAO;
import com.bellasmascotas.model.*;
import com.bellasmascotas.view.Datos;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Registrar servicio realizado (venta). Para el Estilista se muestra como "Mi agenda":
 * sus citas del día y el registro del servicio (CU09, CU10, CU11; FC07, RF07, RF08, FC12).
 */
public class PnlVentas extends JPanel implements Refrescable {

    private final boolean esEstilista = Sesion.esRol(Permisos.ESTILISTA);
    private final CitaDAO citaDAO = new CitaDAO();
    private Datos datos;
    private Cita citaActual;          // null = cliente sin cita
    private Map<Integer, List<RecetaServicio>> recetas = new HashMap<>();

    private final DefaultListModel<Cita> modeloCitas = new DefaultListModel<>();
    public JList<Cita> listaCitas = new JList<>(modeloCitas);
    public JButton btnSinCita, btnIniciar, btnRegistrar;
    public JComboBox<Mascota> cbMascota = Estilo.combo();
    public JComboBox<Usuario> cbEstilista = Estilo.combo();
    private final JPanel pnlServicios = new JPanel(new GridLayout(0, 2, 8, 8));
    private final Map<ServicioCatalogo, JCheckBox> checks = new LinkedHashMap<>();
    private final DefaultTableModel modeloInsumos = Estilo.modelo("Insumo", "Cantidad", "Stock actual");
    public JTextArea txtObs = new JTextArea(3, 20);
    private final JLabel lblTotal = new JLabel("$ 0");
    private final JLabel lblMascota = new JLabel();
    private final JLabel lblModo = Estilo.etiqueta("");
    private final JLabel lblAlerta = Estilo.alerta("", false);
    private final JLabel lblResumen = new JLabel();
    private final JLabel lblTituloLista = Estilo.negrita("Por atender", 15);

    public PnlVentas() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        lblResumen.setFont(Estilo.fuente(Font.PLAIN, 12));
        String nombre = Sesion.getUsuario() == null ? "" : Sesion.getUsuario().getNombre().split(" ")[0];
        c.add(Estilo.encabezado(esEstilista ? "Mi agenda y servicios" : "Registrar servicio",
                esEstilista ? "Hola, " + nombre + ". Estas son tus citas de hoy y el registro de cada servicio."
                            : "Atienda las citas del día o registre el servicio de un cliente sin cita.",
                lblResumen), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1;

        // ── Lista de citas ──
        Tarjeta izq = new Tarjeta(new BorderLayout(0, 10));
        JPanel cabIzq = new JPanel(new BorderLayout());
        cabIzq.setOpaque(false);
        cabIzq.add(lblTituloLista, BorderLayout.WEST);
        cabIzq.add(Estilo.etiqueta("Hoy, " + Estilo.hoy()), BorderLayout.EAST);
        izq.add(cabIzq, BorderLayout.NORTH);
        listaCitas.setCellRenderer(new RenderCita());
        listaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCitas.setBackground(Color.WHITE);
        listaCitas.setFixedCellHeight(-1);
        JScrollPane sl = new JScrollPane(listaCitas);
        sl.setBorder(null);
        izq.add(sl, BorderLayout.CENTER);
        btnSinCita = Estilo.botonSecundario("+ Cliente sin cita");
        btnIniciar = Estilo.botonSecundario("Marcar en curso");
        JPanel bi = new JPanel(new GridLayout(1, 2, 8, 0));
        bi.setOpaque(false);
        bi.add(btnIniciar); bi.add(btnSinCita);
        izq.add(bi, BorderLayout.SOUTH);
        g.gridx = 0; g.weightx = 1; g.insets = new Insets(0, 0, 0, 14);
        izq.setPreferredSize(new Dimension(10, 10));
        grid.add(izq, g);

        // ── Registro ──
        Tarjeta der = new Tarjeta(new BorderLayout());
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.add(Estilo.negrita("Registrar servicio realizado", 15), BorderLayout.WEST);
        cab.add(lblModo, BorderLayout.EAST);
        cab.setBorder(new EmptyBorder(0, 0, 8, 0));
        der.add(cab, BorderLayout.NORTH);
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        Tarjeta caja = new Tarjeta(new BorderLayout(), new Color(250, 252, 252), Estilo.BORDE);
        caja.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblMascota.setFont(Estilo.fuente(Font.PLAIN, 13));
        caja.add(lblMascota);
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        f.add(caja);
        JPanel fila = new JPanel(new GridLayout(1, 2, 8, 0));
        fila.setOpaque(false);
        JPanel p1 = new JPanel(); p1.setOpaque(false); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        Estilo.fila(p1, "Mascota", cbMascota);
        JPanel p2 = new JPanel(); p2.setOpaque(false); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        Estilo.fila(p2, "Estilista", cbEstilista);
        fila.add(p1); fila.add(p2);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(Estilo.fijarAlto(fila));
        lblAlerta.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAlerta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        f.add(Box.createVerticalStrut(8));
        f.add(lblAlerta);
        JLabel ls = Estilo.etiqueta("Servicios del catálogo realizados");
        ls.setBorder(new EmptyBorder(8, 0, 6, 0));
        ls.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(ls);
        pnlServicios.setOpaque(false);
        pnlServicios.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(pnlServicios);
        pnlServicios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        JLabel li = Estilo.etiqueta("Insumos que se descontarán (receta del servicio)");
        li.setBorder(new EmptyBorder(10, 0, 6, 0));
        li.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(li);
        JTable tIns = Estilo.tabla(modeloInsumos);
        tIns.setRowHeight(28);
        JScrollPane si = Estilo.scroll(tIns);
        si.setPreferredSize(new Dimension(200, 120));
        si.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(si);
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setFont(Estilo.fuente(Font.PLAIN, 13));
        JScrollPane so = new JScrollPane(txtObs);
        so.setPreferredSize(new Dimension(200, 56));
        Estilo.fila(f, "Observaciones del servicio", so);
        der.add(f, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(12, 0, 0, 0));
        JPanel tot = new JPanel();
        tot.setOpaque(false);
        tot.setLayout(new BoxLayout(tot, BoxLayout.Y_AXIS));
        tot.add(Estilo.etiqueta("Total a cobrar"));
        lblTotal.setFont(Estilo.fuente(Font.BOLD, 22));
        tot.add(lblTotal);
        pie.add(tot, BorderLayout.WEST);
        btnRegistrar = Estilo.botonPrimario("Registrar servicio");
        JPanel pb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        pb.setOpaque(false);
        pb.add(btnRegistrar);
        pie.add(pb, BorderLayout.EAST);
        der.add(pie, BorderLayout.SOUTH);
        g.gridx = 1; g.weightx = 1.35; g.insets = new Insets(0, 0, 0, 0);
        der.setPreferredSize(new Dimension(10, 10));
        grid.add(der, g);
        c.add(grid, BorderLayout.CENTER);

        // ── Eventos ──
        listaCitas.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && listaCitas.getSelectedValue() != null) seleccionarCita(listaCitas.getSelectedValue()); });
        btnSinCita.addActionListener(e -> modoSinCita());
        btnIniciar.addActionListener(e -> iniciarCita());
        cbMascota.addActionListener(e -> mostrarMascota());
        btnRegistrar.addActionListener(e -> registrar());
    }

    @Override
    public void refrescar() {
        datos = new Datos();
        recetas = new HashMap<>();
        CatalogoDAO cat = new CatalogoDAO();
        for (ServicioCatalogo s : datos.servicios.values()) recetas.put(s.getIdCatalogo(), cat.listarReceta(s.getIdCatalogo()));

        // Servicios como casillas
        pnlServicios.removeAll();
        checks.clear();
        for (ServicioCatalogo s : datos.servicios.values()) {
            JCheckBox cb = new JCheckBox(s.getTipo() + " · " + Estilo.pesos(s.getPrecioVenta()));
            cb.setFont(Estilo.fuente(Font.PLAIN, 12));
            cb.setOpaque(false);
            cb.addActionListener(e -> recalcular());
            checks.put(s, cb);
            pnlServicios.add(cb);
        }
        pnlServicios.revalidate();

        cbEstilista.removeAllItems();
        if (esEstilista) cbEstilista.addItem(Sesion.getUsuario());
        else for (Usuario u : datos.estilistas()) cbEstilista.addItem(u);
        cbEstilista.setEnabled(!esEstilista);

        cbMascota.removeAllItems();
        for (Mascota m : datos.mascotas.values()) cbMascota.addItem(m);

        // Citas del día (las del estilista si es Estilista)
        modeloCitas.clear();
        List<Cita> citas = esEstilista ? citaDAO.listarPorEstilista(Sesion.getUsuario().getIdUsuario(), Estilo.hoy())
                                       : citaDAO.listarPorFecha(Estilo.hoy());
        List<Cita> orden = new ArrayList<>();
        citas.stream().filter(c -> "En curso".equals(c.getEstado())).forEach(orden::add);
        citas.stream().filter(c -> "Programada".equals(c.getEstado())).forEach(orden::add);
        citas.stream().filter(c -> "Finalizada".equals(c.getEstado())).forEach(orden::add);
        orden.forEach(modeloCitas::addElement);
        long fin = citas.stream().filter(c -> "Finalizada".equals(c.getEstado())).count();
        long activas = citas.stream().filter(c -> !"Cancelada".equals(c.getEstado())).count();
        lblResumen.setText(activas + " citas hoy · " + fin + " finalizadas");
        lblTituloLista.setText("Por atender (" + (activas - fin) + ")");

        if (!orden.isEmpty() && !"Finalizada".equals(orden.get(0).getEstado())) listaCitas.setSelectedIndex(0);
        else modoSinCita();
    }

    private void seleccionarCita(Cita c) {
        citaActual = c;
        lblModo.setText("Cita de las " + c.getFechaHoraCita().substring(11, 16));
        for (int i = 0; i < cbMascota.getItemCount(); i++)
            if (cbMascota.getItemAt(i).getIdMascota() == c.getIdMascota()) cbMascota.setSelectedIndex(i);
        cbMascota.setEnabled(false);
        if (!esEstilista) {
            for (int i = 0; i < cbEstilista.getItemCount(); i++)
                if (cbEstilista.getItemAt(i).getIdUsuario() == c.getIdUsuario()) cbEstilista.setSelectedIndex(i);
        }
        checks.forEach((s, cb) -> cb.setSelected(s.getIdCatalogo() == c.getIdCatalogo()));
        txtObs.setText(c.getObservaciones() == null ? "" : c.getObservaciones());
        boolean finalizada = "Finalizada".equals(c.getEstado());
        btnRegistrar.setEnabled(!finalizada);
        btnIniciar.setEnabled("Programada".equals(c.getEstado()));
        if (finalizada) lblModo.setText("Cita finalizada");
        recalcular();
        mostrarMascota();
    }

    /** Cliente que llega sin cita: se elige la mascota directamente (curso alterno 1a de CU10). */
    private void modoSinCita() {
        citaActual = null;
        listaCitas.clearSelection();
        lblModo.setText("Cliente sin cita");
        cbMascota.setEnabled(true);
        checks.values().forEach(cb -> cb.setSelected(false));
        txtObs.setText("");
        btnRegistrar.setEnabled(true);
        btnIniciar.setEnabled(false);
        recalcular();
        mostrarMascota();
    }

    private void iniciarCita() {
        if (citaActual == null) return;
        if (citaDAO.actualizarEstado(citaActual.getIdCita(), "En curso")) {
            int id = citaActual.getIdCita();
            refrescar();
            for (int i = 0; i < modeloCitas.size(); i++) if (modeloCitas.get(i).getIdCita() == id) listaCitas.setSelectedIndex(i);
        }
    }

    private void mostrarMascota() {
        Mascota m = (Mascota) cbMascota.getSelectedItem();
        if (m == null || datos == null) { lblMascota.setText("Seleccione una mascota."); lblAlerta.setVisible(false); return; }
        Cliente p = datos.propietario(m);
        lblMascota.setText("<html><b>" + m.getNombre() + " · " + m.getRaza() + "</b><br><span style='color:#6B7C85;font-size:9px'>"
                + m.getEspecie() + " · " + m.getEdad() + " años · " + (p == null ? "Sin propietario" : p.getNombreCompleto() + " · " + p.getTelefono())
                + "</span></html>");
        if (m.tieneAlertas()) {
            lblAlerta.setText("<html>⚠ " + m.textoAlertas() + "</html>");
            lblAlerta.setVisible(true);
        } else lblAlerta.setVisible(false);
    }

    /** Suma las recetas de los servicios marcados y calcula el total con el precio del catálogo. */
    private void recalcular() {
        double total = 0;
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (Map.Entry<ServicioCatalogo, JCheckBox> e : checks.entrySet()) {
            if (!e.getValue().isSelected()) continue;
            total += e.getKey().getPrecioVenta();
            for (RecetaServicio r : recetas.getOrDefault(e.getKey().getIdCatalogo(), List.of()))
                cantidades.merge(r.getIdInsumo(), r.getCantidadUsada(), Integer::sum);
        }
        modeloInsumos.setRowCount(0);
        for (Map.Entry<Integer, Integer> e : cantidades.entrySet()) {
            Insumo i = datos.insumos.get(e.getKey());
            if (i == null) continue;
            modeloInsumos.addRow(new Object[]{i.getNombre(), e.getValue(), i.getCantidadStock()});
        }
        lblTotal.setText(Estilo.pesos(total));
    }

    /** Registra los servicios marcados en una sola transacción (ServicioDAO.registrarServicios). */
    private void registrar() {
        Mascota m = (Mascota) cbMascota.getSelectedItem();
        Usuario u = (Usuario) cbEstilista.getSelectedItem();
        List<ServicioCatalogo> elegidos = new ArrayList<>();
        checks.forEach((s, cb) -> { if (cb.isSelected()) elegidos.add(s); });
        if (m == null) { Estilo.aviso(this, "Seleccione la mascota."); return; }
        if (u == null) { Estilo.aviso(this, "Seleccione el estilista que realizó el servicio."); return; }
        if (elegidos.isEmpty()) { Estilo.aviso(this, "Marque al menos un servicio realizado."); return; }
        if (m.tieneAlertas() && !Estilo.confirmar(this, "Recuerde: " + m.textoAlertas() + "\n¿Confirmar el registro del servicio?")) return;
        int puntos = Conexion.propiedadEntera("negocio.puntos_por_servicio", 10);
        String obs = txtObs.getText().trim().isEmpty() ? null : txtObs.getText().trim();
        String error = new ServicioDAO().registrarServicios(citaActual == null ? 0 : citaActual.getIdCita(),
                m.getIdMascota(), u.getIdUsuario(), elegidos, obs, puntos);
        if (error != null) { Estilo.error(this, error); return; }
        Estilo.info(this, "Servicio registrado para " + m.getNombre() + ". Total: " + lblTotal.getText()
                + "\nSe descontaron los insumos y se sumaron " + (puntos * elegidos.size()) + " puntos al cliente.");
        refrescar();
    }

    /** Dibuja cada cita de la lista como una tarjeta. */
    private class RenderCita implements ListCellRenderer<Cita> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Cita> list, Cita c, int index, boolean sel, boolean foc) {
            JPanel envoltura = new JPanel(new BorderLayout());
            envoltura.setBackground(Color.WHITE);
            envoltura.setBorder(new EmptyBorder(0, 0, 8, 0));
            boolean fin = "Finalizada".equals(c.getEstado());
            Tarjeta t = new Tarjeta(new BorderLayout(6, 2), sel ? new Color(242, 250, 249) : Color.WHITE, sel ? Estilo.PRIMARIO : Estilo.BORDE);
            t.setBorder(new EmptyBorder(10, 12, 10, 12));
            Mascota m = datos == null ? null : datos.mascotas.get(c.getIdMascota());
            JLabel arriba = new JLabel(c.getFechaHoraCita().substring(11, 16) + " · " + (m == null ? "" : m.getNombre()));
            arriba.setFont(Estilo.fuente(Font.BOLD, 13));
            arriba.setForeground(fin ? Estilo.TEXTO_SUAVE : Estilo.TEXTO);
            String det = (m == null ? "" : m.getRaza() + " · " + datos.nombreCliente(m.getIdCliente()) + " · ")
                    + (datos == null ? "" : datos.nombreServicio(c.getIdCatalogo()))
                    + (esEstilista || datos == null ? "" : " · " + datos.nombreUsuario(c.getIdUsuario()));
            JLabel abajo = Estilo.etiqueta(det);
            abajo.setFont(Estilo.fuente(Font.PLAIN, 11));
            JPanel textos = new JPanel(new GridLayout(2, 1));
            textos.setOpaque(false);
            textos.add(arriba); textos.add(abajo);
            t.add(textos, BorderLayout.CENTER);
            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            derecha.setOpaque(false);
            if (m != null && m.isRequiereBozal()) derecha.add(new Estilo.Pill("Bozal"));
            derecha.add(new Estilo.Pill(c.getEstado()));
            t.add(derecha, BorderLayout.EAST);
            envoltura.add(t);
            return envoltura;
        }
    }
}
