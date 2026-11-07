package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.CitaDAO;
import com.bellasmascotas.dao.ClienteDAO;
import com.bellasmascotas.dao.MascotaDAO;
import com.bellasmascotas.model.*;
import com.bellasmascotas.view.Datos;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Módulo Agenda de citas: consultar por día y estilista, agendar y cambiar estados (CU08, CU09; RF06, FC07). */
public class PnlAgenda extends JPanel implements Refrescable {

    private final CitaDAO dao = new CitaDAO();
    private Datos datos;
    private LocalDate fecha = LocalDate.now();
    private int filtroEstilista = 0;               // 0 = todos
    private List<Cita> citas = new ArrayList<>();

    private final JLabel lblFecha = new JLabel();
    private final JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
    public DefaultTableModel modeloTabla = Estilo.modelo("ID", "Hora", "Mascota", "Servicio", "Estilista", "Estado");
    public JTable tablaCitas = Estilo.tabla(modeloTabla);
    public JButton btnEnCurso, btnCancelar, btnEliminar;

    // Formulario de nueva cita
    public JTextField txtCliente = Estilo.campo("Cédula o nombre del cliente");
    public JComboBox<Mascota> cbMascota = Estilo.combo();
    public JComboBox<ServicioCatalogo> cbServicio = Estilo.combo();
    public JComboBox<Usuario> cbEstilista = Estilo.combo();
    public JTextField txtFecha = Estilo.campo("AAAA-MM-DD");
    public JComboBox<String> cbHora = Estilo.combo();
    public JTextField txtObs = Estilo.campo("Indicaciones del propietario…");
    public JButton btnAgendar, btnLimpiar;
    private final JLabel lblAlertaMascota = Estilo.alerta("", false);
    private final JLabel lblCliente = Estilo.etiqueta(" ");

    public PnlAgenda() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        nav.setOpaque(false);
        JButton ant = Estilo.botonSecundario("‹");
        JButton sig = Estilo.botonSecundario("›");
        JButton hoy = Estilo.botonSecundario("Hoy");
        lblFecha.setFont(Estilo.fuente(Font.BOLD, 12));
        lblFecha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 223, 224), 1, true), new EmptyBorder(7, 12, 7, 12)));
        nav.add(ant); nav.add(lblFecha); nav.add(sig); nav.add(hoy);
        c.add(Estilo.encabezado("Agenda de citas", "Programe citas por estilista, servicio y horario.", nav), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1;

        // ── Agenda ──
        Tarjeta agenda = new Tarjeta(new BorderLayout(0, 10));
        pnlTabs.setOpaque(false);
        agenda.add(pnlTabs, BorderLayout.NORTH);
        tablaCitas.setRowHeight(42);
        Estilo.ocultarColumna(tablaCitas, 0);
        tablaCitas.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaCitas.getColumnModel().getColumn(5).setCellRenderer(new Estilo.PillRenderer());
        agenda.add(Estilo.scroll(tablaCitas), BorderLayout.CENTER);
        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        btnEnCurso = Estilo.botonSecundario("Marcar en curso");
        btnCancelar = Estilo.botonSecundario("Cancelar cita");
        btnEliminar = Estilo.botonPeligro("Eliminar");
        acc.add(Estilo.etiqueta("Cita seleccionada:"));
        acc.add(btnEnCurso); acc.add(btnCancelar); acc.add(btnEliminar);
        agenda.add(acc, BorderLayout.SOUTH);
        g.gridx = 0; g.weightx = 1.5; g.insets = new Insets(0, 0, 0, 14);
        agenda.setPreferredSize(new Dimension(10, 10));
        grid.add(agenda, g);

        // ── Nueva cita ──
        Tarjeta nueva = new Tarjeta(new BorderLayout());
        nueva.add(Estilo.encabezadoTarjeta("Nueva cita", null), BorderLayout.NORTH);
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        JPanel filaCli = new JPanel(new BorderLayout(6, 0));
        filaCli.setOpaque(false);
        filaCli.add(txtCliente, BorderLayout.CENTER);
        JButton btnBuscar = Estilo.botonSecundario("Buscar");
        filaCli.add(btnBuscar, BorderLayout.EAST);
        filaCli.setPreferredSize(new Dimension(200, 34));
        Estilo.fila(f, "Cliente (cédula o nombre)", filaCli);
        lblCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCliente.setBorder(new EmptyBorder(4, 0, 0, 0));
        f.add(lblCliente);
        Estilo.fila(f, "Mascota", cbMascota);
        Estilo.fila(f, "Servicio del catálogo", cbServicio);
        Estilo.fila(f, "Estilista", cbEstilista);
        JPanel fh = new JPanel(new GridLayout(1, 2, 8, 0));
        fh.setOpaque(false);
        JPanel pf = new JPanel(); pf.setOpaque(false); pf.setLayout(new BoxLayout(pf, BoxLayout.Y_AXIS));
        Estilo.fila(pf, "Fecha", txtFecha);
        JPanel ph = new JPanel(); ph.setOpaque(false); ph.setLayout(new BoxLayout(ph, BoxLayout.Y_AXIS));
        Estilo.fila(ph, "Hora", cbHora);
        fh.add(pf); fh.add(ph);
        fh.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(Estilo.fijarAlto(fh));
        Estilo.fila(f, "Observaciones", txtObs);
        lblAlertaMascota.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAlertaMascota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        lblAlertaMascota.setVisible(false);
        f.add(Box.createVerticalStrut(10));
        f.add(lblAlertaMascota);
        JPanel bot = new JPanel(new GridBagLayout());
        bot.setOpaque(false);
        bot.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAgendar = Estilo.botonPrimario("Agendar cita");
        btnLimpiar = Estilo.botonSecundario("Limpiar");
        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.HORIZONTAL; gb.weightx = 1;
        bot.add(btnAgendar, gb);
        gb.weightx = 0; gb.insets = new Insets(0, 8, 0, 0);
        bot.add(btnLimpiar, gb);
        bot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.add(Box.createVerticalStrut(12));
        f.add(bot);
        f.add(Box.createVerticalGlue());
        nueva.add(f, BorderLayout.CENTER);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0);
        nueva.setPreferredSize(new Dimension(10, 10));
        grid.add(nueva, g);
        c.add(grid, BorderLayout.CENTER);

        for (int h = 8; h <= 18; h++) {
            cbHora.addItem(String.format("%02d:00", h));
            if (h < 18) cbHora.addItem(String.format("%02d:30", h));
        }

        // ── Eventos ──
        ant.addActionListener(e -> { fecha = fecha.minusDays(1); cargarCitas(); });
        sig.addActionListener(e -> { fecha = fecha.plusDays(1); cargarCitas(); });
        hoy.addActionListener(e -> { fecha = LocalDate.now(); cargarCitas(); });
        btnBuscar.addActionListener(e -> buscarCliente());
        txtCliente.addActionListener(e -> buscarCliente());
        cbMascota.addActionListener(e -> mostrarAlertaMascota());
        btnAgendar.addActionListener(e -> agendar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnEnCurso.addActionListener(e -> cambiarEstado("En curso"));
        btnCancelar.addActionListener(e -> cambiarEstado("Cancelada"));
        btnEliminar.addActionListener(e -> eliminar());
    }

    @Override
    public void refrescar() {
        datos = new Datos();
        Object servSel = cbServicio.getSelectedItem(), estSel = cbEstilista.getSelectedItem();
        cbServicio.removeAllItems();
        for (ServicioCatalogo s : datos.servicios.values()) cbServicio.addItem(s);
        cbEstilista.removeAllItems();
        for (Usuario u : datos.estilistas()) cbEstilista.addItem(u);
        if (servSel != null) cbServicio.setSelectedItem(servSel);
        if (estSel != null) cbEstilista.setSelectedItem(estSel);
        if (txtFecha.getText().isBlank()) txtFecha.setText(fecha.toString());
        construirTabs();
        cargarCitas();
    }

    /** Pestañas para ver todas las citas o las de un estilista. */
    private void construirTabs() {
        pnlTabs.removeAll();
        agregarTab("Todos los estilistas", 0);
        for (Usuario u : datos.estilistas()) agregarTab(u.getNombre(), u.getIdUsuario());
        pnlTabs.revalidate(); pnlTabs.repaint();
    }

    private void agregarTab(String texto, int id) {
        JLabel l = new JLabel(texto);
        boolean on = filtroEstilista == id;
        l.setFont(Estilo.fuente(on ? Font.BOLD : Font.PLAIN, 12));
        l.setForeground(on ? Estilo.PRIMARIO : Estilo.TEXTO_SUAVE);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, on ? 2 : 0, 0, Estilo.PRIMARIO), new EmptyBorder(0, 0, 4, 0)));
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { filtroEstilista = id; construirTabs(); cargarCitas(); }
        });
        pnlTabs.add(l);
    }

    private void cargarCitas() {
        if (datos == null) return;
        String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-CO"));
        lblFecha.setText(Character.toUpperCase(dia.charAt(0)) + dia.substring(1) + ", " + fecha);
        citas = filtroEstilista == 0 ? dao.listarPorFecha(fecha.toString()) : dao.listarPorEstilista(filtroEstilista, fecha.toString());
        modeloTabla.setRowCount(0);
        for (Cita c : citas) {
            Mascota m = datos.mascotas.get(c.getIdMascota());
            ServicioCatalogo s = datos.servicios.get(c.getIdCatalogo());
            String alerta = m != null && m.tieneAlertas() ? "Cuidado" : null;
            modeloTabla.addRow(new Object[]{c.getIdCita(), c.getFechaHoraCita().substring(11, 16),
                    Estilo.dosLineas(datos.nombreMascota(c.getIdMascota()), datos.nombreCliente(m == null ? 0 : m.getIdCliente()), alerta),
                    Estilo.dosLineas(datos.nombreServicio(c.getIdCatalogo()), s == null ? "" : Estilo.pesos(s.getPrecioVenta())),
                    datos.nombreUsuario(c.getIdUsuario()), c.getEstado()});
        }
    }

    /** Busca el cliente por cédula o nombre y carga sus mascotas (CU05 incluido en CU08). */
    private void buscarCliente() {
        String texto = txtCliente.getText().trim();
        cbMascota.removeAllItems();
        if (texto.isEmpty()) { Estilo.aviso(this, "Escriba la cédula o el nombre del cliente."); return; }
        List<Cliente> encontrados = new ClienteDAO().buscarPorCedulaONombre(texto);
        if (encontrados.isEmpty()) {
            lblCliente.setText("No se encontró el cliente. Regístrelo en el módulo Clientes.");
            return;
        }
        Cliente c = encontrados.get(0);
        if (encontrados.size() > 1) {
            c = (Cliente) JOptionPane.showInputDialog(this, "Se encontraron varios clientes:", "Seleccionar cliente",
                    JOptionPane.QUESTION_MESSAGE, null, encontrados.toArray(), encontrados.get(0));
            if (c == null) return;
        }
        txtCliente.setText(c.getCedula() + " · " + c.getNombreCompleto());
        List<Mascota> ms = new MascotaDAO().listarPorCliente(c.getIdCliente());
        for (Mascota m : ms) cbMascota.addItem(m);
        lblCliente.setText(ms.isEmpty() ? "El cliente no tiene mascotas registradas." : ms.size() + " mascota(s) de " + c.getNombreCompleto());
        mostrarAlertaMascota();
    }

    private void mostrarAlertaMascota() {
        Mascota m = (Mascota) cbMascota.getSelectedItem();
        if (m != null && m.tieneAlertas()) {
            lblAlertaMascota.setText("<html>" + m.getNombre() + ": " + m.textoAlertas() + "</html>");
            lblAlertaMascota.setVisible(true);
        } else lblAlertaMascota.setVisible(false);
    }

    /** Valida los datos y registra la cita con estado "Programada" (RF06). */
    private void agendar() {
        Mascota m = (Mascota) cbMascota.getSelectedItem();
        ServicioCatalogo s = (ServicioCatalogo) cbServicio.getSelectedItem();
        Usuario u = (Usuario) cbEstilista.getSelectedItem();
        String f = txtFecha.getText().trim();
        if (m == null) { Estilo.aviso(this, "Busque el cliente y seleccione la mascota."); return; }
        if (s == null || u == null) { Estilo.aviso(this, "Seleccione el servicio y el estilista."); return; }
        if (!Estilo.esFechaValida(f)) { Estilo.aviso(this, "La fecha debe tener el formato AAAA-MM-DD."); return; }
        if (LocalDate.parse(f).isBefore(LocalDate.now())) { Estilo.aviso(this, "No se pueden agendar citas en fechas pasadas."); return; }
        String fechaHora = f + " " + cbHora.getSelectedItem() + ":00";
        if (!dao.horarioDisponible(u.getIdUsuario(), fechaHora)) {
            Estilo.aviso(this, "Horario ocupado: " + u.getNombre() + " ya tiene una cita el " + f + " a las " + cbHora.getSelectedItem() + ".");
            return;
        }
        Cita c = new Cita();
        c.setFechaHoraCita(fechaHora);
        c.setObservaciones(txtObs.getText().trim().isEmpty() ? null : txtObs.getText().trim());
        c.setIdMascota(m.getIdMascota());
        c.setIdUsuario(u.getIdUsuario());
        c.setIdCatalogo(s.getIdCatalogo());
        if (dao.registrar(c)) {
            Estilo.info(this, "Cita agendada: " + m.getNombre() + " con " + u.getNombre() + " el " + f + " a las " + cbHora.getSelectedItem() + ".");
            fecha = LocalDate.parse(f);
            limpiar();
            cargarCitas();
        } else Estilo.error(this, "No se pudo registrar la cita.");
    }

    private void limpiar() {
        txtCliente.setText("");
        cbMascota.removeAllItems();
        txtObs.setText("");
        lblCliente.setText(" ");
        lblAlertaMascota.setVisible(false);
        txtFecha.setText(fecha.toString());
    }

    private Cita seleccionada() {
        int fila = tablaCitas.getSelectedRow();
        if (fila < 0) { Estilo.aviso(this, "Selecciona una cita de la tabla."); return null; }
        int id = (int) modeloTabla.getValueAt(tablaCitas.convertRowIndexToModel(fila), 0);
        return citas.stream().filter(c -> c.getIdCita() == id).findFirst().orElse(null);
    }

    /** Cambia el estado de la cita seleccionada (FC07). */
    private void cambiarEstado(String estado) {
        Cita c = seleccionada();
        if (c == null) return;
        if ("Finalizada".equals(c.getEstado())) { Estilo.aviso(this, "La cita ya fue finalizada."); return; }
        if (estado.equals(c.getEstado())) return;
        if ("Cancelada".equals(estado) && !Estilo.confirmar(this, "¿Cancelar la cita de " + datos.nombreMascota(c.getIdMascota()) + "?")) return;
        if (dao.actualizarEstado(c.getIdCita(), estado)) cargarCitas();
        else Estilo.error(this, "No se pudo actualizar la cita.");
    }

    private void eliminar() {
        Cita c = seleccionada();
        if (c == null) return;
        if (!Estilo.confirmar(this, "¿Eliminar definitivamente esta cita? Use \"Cancelar cita\" si el cliente no asistirá.")) return;
        if (dao.eliminar(c.getIdCita())) cargarCitas();
        else Estilo.error(this, "No se pudo eliminar la cita.");
    }
}
