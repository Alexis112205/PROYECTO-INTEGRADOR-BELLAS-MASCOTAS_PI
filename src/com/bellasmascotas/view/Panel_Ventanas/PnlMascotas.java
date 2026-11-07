package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.MascotaDAO;
import com.bellasmascotas.dao.VacunaDAO;
import com.bellasmascotas.model.Cliente;
import com.bellasmascotas.model.ControlVacuna;
import com.bellasmascotas.model.Mascota;
import com.bellasmascotas.view.Datos;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Módulo Mascotas: fichas, cuidados especiales y control de vacunas (CU06, CU07; RF05, FC05, FC06). */
public class PnlMascotas extends JPanel implements Refrescable {

    public JTextField txtBuscar;
    public JComboBox<String> cbEspecie;
    public JButton btnNueva, btnEditar, btnEliminar, btnRegistrarVacuna, btnEliminarVacuna;
    public JTable tablaMascotas, tablaVacunas;
    public DefaultTableModel modeloTabla, modeloVacunas;

    private final MascotaDAO dao = new MascotaDAO();
    private final VacunaDAO vacunaDAO = new VacunaDAO();
    private Datos datos;
    private List<Mascota> visibles = new ArrayList<>();
    private List<ControlVacuna> vacunasActuales = new ArrayList<>();
    private Mascota actual;

    // Ficha
    private final JLabel lblTituloFicha = Estilo.negrita("Ficha", 15);
    private final JLabel lblPropietario = Estilo.etiqueta("");
    private final JLabel lblFoto = new JLabel("<html><center>Foto de la<br>mascota</center></html>", SwingConstants.CENTER);
    private final JLabel lblDatos = new JLabel();
    private final JPanel pnlEstados = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    private final JLabel lblAlerta = Estilo.alerta("", false);
    private final JTextArea txtObs = new JTextArea();
    private final JLabel lblTotal = new JLabel();

    public PnlMascotas() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        lblTotal.setFont(Estilo.fuente(Font.PLAIN, 12));
        c.add(Estilo.encabezado("Mascotas", "Fichas de las mascotas, cuidados especiales y control de vacunas.", lblTotal), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1;

        // ── Lista ──
        Tarjeta lista = new Tarjeta(new BorderLayout(0, 10));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        txtBuscar = Estilo.campo("Buscar mascota, raza o propietario…");
        txtBuscar.setPreferredSize(new Dimension(250, 34));
        cbEspecie = Estilo.combo();
        for (String e : new String[]{"Especie: Todas", "Perro", "Gato", "Otro"}) cbEspecie.addItem(e);
        barra.add(txtBuscar); barra.add(cbEspecie);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        btnNueva = Estilo.botonPrimario("+ Nueva");
        btnEditar = Estilo.botonSecundario("Editar");
        btnEliminar = Estilo.botonPeligro("Eliminar");
        acciones.add(btnNueva); acciones.add(btnEditar); acciones.add(btnEliminar);
        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(barra, BorderLayout.WEST);
        norte.add(acciones, BorderLayout.EAST);
        lista.add(norte, BorderLayout.NORTH);
        modeloTabla = Estilo.modelo("ID", "Mascota", "Especie", "Raza", "Edad", "Propietario", "Cuidados");
        tablaMascotas = Estilo.tabla(modeloTabla);
        Estilo.ocultarColumna(tablaMascotas, 0);
        tablaMascotas.getColumnModel().getColumn(6).setCellRenderer(new Estilo.PillRenderer());
        tablaMascotas.getColumnModel().getColumn(6).setPreferredWidth(150);
        lista.add(Estilo.scroll(tablaMascotas), BorderLayout.CENTER);
        g.gridx = 0; g.weightx = 1.55; g.insets = new Insets(0, 0, 0, 14);
        lista.setPreferredSize(new Dimension(10, 10));
        grid.add(lista, g);

        // ── Ficha ──
        Tarjeta ficha = new Tarjeta(new BorderLayout(0, 8));
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.add(lblTituloFicha, BorderLayout.WEST);
        cab.add(lblPropietario, BorderLayout.EAST);
        ficha.add(cab, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        lblFoto.setPreferredSize(new Dimension(96, 96));
        lblFoto.setOpaque(true);
        lblFoto.setBackground(new Color(238, 242, 242));
        lblFoto.setForeground(Estilo.TEXTO_SUAVE);
        lblFoto.setFont(Estilo.fuente(Font.PLAIN, 11));
        fila.add(lblFoto, BorderLayout.WEST);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        lblDatos.setFont(Estilo.fuente(Font.PLAIN, 13));
        pnlEstados.setOpaque(false);
        pnlEstados.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDatos.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(lblDatos); info.add(Box.createVerticalStrut(6)); info.add(pnlEstados);
        fila.add(info, BorderLayout.CENTER);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        cuerpo.add(fila);
        cuerpo.add(Box.createVerticalStrut(10));
        lblAlerta.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAlerta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        cuerpo.add(lblAlerta);
        txtObs.setEditable(false);
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setFont(Estilo.fuente(Font.PLAIN, 12));
        txtObs.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane sObs = new JScrollPane(txtObs);
        sObs.setPreferredSize(new Dimension(200, 56));
        Estilo.fila(cuerpo, "Observaciones", sObs);

        JPanel cabVac = new JPanel(new BorderLayout());
        cabVac.setOpaque(false);
        cabVac.add(Estilo.negrita("Control de vacunas", 14), BorderLayout.WEST);
        JPanel bVac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        bVac.setOpaque(false);
        btnRegistrarVacuna = Estilo.botonSecundario("+ Registrar");
        btnEliminarVacuna = Estilo.botonPeligro("Quitar");
        bVac.add(btnRegistrarVacuna); bVac.add(btnEliminarVacuna);
        cabVac.add(bVac, BorderLayout.EAST);
        cabVac.setBorder(new EmptyBorder(12, 0, 6, 0));
        cabVac.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabVac.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cuerpo.add(cabVac);
        modeloVacunas = Estilo.modelo("ID", "Vacuna", "Aplicación", "Próxima dosis");
        tablaVacunas = Estilo.tabla(modeloVacunas);
        Estilo.ocultarColumna(tablaVacunas, 0);
        tablaVacunas.getColumnModel().getColumn(3).setCellRenderer(new Estilo.PillRenderer());
        tablaVacunas.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaVacunas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaVacunas.getColumnModel().getColumn(3).setPreferredWidth(170);
        JScrollPane sVac = Estilo.scroll(tablaVacunas);
        sVac.setAlignmentX(Component.LEFT_ALIGNMENT);
        sVac.setPreferredSize(new Dimension(200, 150));
        cuerpo.add(sVac);
        ficha.add(cuerpo, BorderLayout.CENTER);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0);
        ficha.setPreferredSize(new Dimension(10, 10));
        grid.add(ficha, g);
        c.add(grid, BorderLayout.CENTER);

        // ── Eventos ──
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        cbEspecie.addActionListener(e -> filtrar());
        tablaMascotas.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) mostrarFicha(); });
        btnNueva.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> {
            if (actual == null) { Estilo.aviso(this, "Selecciona una mascota de la tabla."); return; }
            abrirFormulario(actual);
        });
        btnEliminar.addActionListener(e -> eliminar());
        btnRegistrarVacuna.addActionListener(e -> registrarVacuna());
        btnEliminarVacuna.addActionListener(e -> eliminarVacuna());
        mostrarFicha();
    }

    @Override
    public void refrescar() {
        int idSel = actual == null ? 0 : actual.getIdMascota();
        datos = new Datos();
        filtrar();
        lblTotal.setText(datos.mascotas.size() + " mascotas");
        seleccionarPorId(idSel);
    }

    private void filtrar() {
        if (datos == null) return;
        String texto = txtBuscar.getText().trim().toLowerCase();
        String especie = cbEspecie.getSelectedIndex() <= 0 ? null : (String) cbEspecie.getSelectedItem();
        visibles = new ArrayList<>();
        modeloTabla.setRowCount(0);
        for (Mascota m : datos.mascotas.values()) {
            String prop = datos.nombreCliente(m.getIdCliente());
            String todo = (m.getNombre() + " " + m.getRaza() + " " + prop).toLowerCase();
            if (!texto.isEmpty() && !todo.contains(texto)) continue;
            if (especie != null && !especie.equals(m.getEspecie())) continue;
            visibles.add(m);
            modeloTabla.addRow(new Object[]{m.getIdMascota(), m.getNombre(), m.getEspecie(), m.getRaza(),
                    m.getEdad() + (m.getEdad() == 1 ? " año" : " años"), prop, textoCuidados(m)});
        }
    }

    private String textoCuidados(Mascota m) {
        if (m.isAgresivo()) return m.isRequiereBozal() ? "Agresivo · Bozal" : "Agresivo";
        if (m.isRequiereBozal()) return "Bozal";
        if (m.getAlergias() != null && !m.getAlergias().isBlank()) return "Alergia";
        return "Sin alertas";
    }

    private void seleccionarPorId(int id) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((int) modeloTabla.getValueAt(i, 0) == id) {
                int v = tablaMascotas.convertRowIndexToView(i);
                tablaMascotas.setRowSelectionInterval(v, v);
                return;
            }
        }
        if (modeloTabla.getRowCount() > 0) tablaMascotas.setRowSelectionInterval(0, 0);
        else { actual = null; mostrarFicha(); }
    }

    /** Muestra la ficha de la mascota seleccionada con sus alertas de cuidado (FC05) y vacunas (FC06). */
    private void mostrarFicha() {
        int fila = tablaMascotas.getSelectedRow();
        actual = null;
        if (fila >= 0 && datos != null) actual = datos.mascotas.get((int) modeloTabla.getValueAt(tablaMascotas.convertRowIndexToModel(fila), 0));
        pnlEstados.removeAll();
        modeloVacunas.setRowCount(0);
        vacunasActuales = new ArrayList<>();
        boolean hay = actual != null;
        btnRegistrarVacuna.setEnabled(hay);
        btnEliminarVacuna.setEnabled(hay);
        if (!hay) {
            lblTituloFicha.setText("Ficha");
            lblPropietario.setText("");
            lblDatos.setText("Seleccione una mascota de la tabla.");
            lblAlerta.setVisible(false);
            txtObs.setText("");
            lblFoto.setIcon(null);
            pnlEstados.revalidate(); pnlEstados.repaint();
            return;
        }
        Cliente prop = datos.propietario(actual);
        lblTituloFicha.setText("Ficha: " + actual.getNombre());
        lblPropietario.setText("Propietario: " + (prop == null ? "—" : prop.getNombreCompleto()));
        lblDatos.setText("<html><b>" + actual.getRaza() + " · " + actual.getEspecie() + " · " + actual.getEdad() + " años</b>"
                + "<br><span style='color:#6B7C85;font-size:9px'>Teléfono del propietario: " + (prop == null ? "—" : prop.getTelefono())
                + (actual.getRutaImagen() == null ? "" : "<br>ruta_imagen: " + actual.getRutaImagen()) + "</span></html>");
        pnlEstados.add(new Estilo.Pill(actual.isAgresivo() ? "Agresivo" : "No agresivo"));
        pnlEstados.add(new Estilo.Pill(actual.isRequiereBozal() ? "Bozal" : "Sin bozal"));
        if (actual.getAlergias() != null && !actual.getAlergias().isBlank()) {
            lblAlerta.setText("<html><b>Alergias:</b> " + actual.getAlergias() + ". Revise los productos antes del servicio.</html>");
            lblAlerta.setVisible(true);
        } else lblAlerta.setVisible(false);
        txtObs.setText(actual.getObservaciones());
        cargarFoto(actual.getRutaImagen());

        vacunasActuales = vacunaDAO.listarPorMascota(actual.getIdMascota());
        LocalDate hoy = LocalDate.now();
        for (ControlVacuna v : vacunasActuales) {
            String prox = v.getProximaDosis();
            String estado = "—";
            if (prox != null) {
                LocalDate p = LocalDate.parse(prox);
                estado = (p.isBefore(hoy) ? "Vencida " : p.isAfter(hoy.plusDays(30)) ? "Al día " : "Por vencer ") + prox;
            }
            modeloVacunas.addRow(new Object[]{v.getIdVacuna(), v.getNombreVacuna(), v.getFechaAplicacion(), estado});
        }
        pnlEstados.revalidate(); pnlEstados.repaint();
    }

    private void cargarFoto(String ruta) {
        lblFoto.setIcon(null);
        if (ruta == null) return;
        try {
            BufferedImage img = ImageIO.read(new File(ruta));
            if (img == null) return;
            double e = Math.min(96.0 / img.getWidth(), 96.0 / img.getHeight());
            lblFoto.setIcon(new ImageIcon(img.getScaledInstance((int) (img.getWidth() * e), (int) (img.getHeight() * e), Image.SCALE_SMOOTH)));
        } catch (Exception ignored) { }
    }

    private void abrirFormulario(Mascota m) {
        if (datos == null || datos.clientes.isEmpty()) { Estilo.aviso(this, "Primero registre al propietario en el módulo Clientes."); return; }
        DlgMascota dlg = new DlgMascota(SwingUtilities.getWindowAncestor(this), m, datos.clientes.values());
        dlg.setVisible(true);
        if (dlg.fueGuardado()) {
            Estilo.info(this, m == null ? "Mascota registrada." : "Datos de la mascota actualizados.");
            refrescar();
        }
    }

    private void eliminar() {
        if (actual == null) { Estilo.aviso(this, "Selecciona una mascota de la tabla."); return; }
        if (!Estilo.confirmar(this, "¿Eliminar a " + actual.getNombre() + "? También se borrarán sus vacunas.")) return;
        if (dao.eliminar(actual.getIdMascota())) {
            actual = null;
            refrescar();
        } else {
            Estilo.error(this, "No se puede eliminar: la mascota tiene citas o servicios registrados.");
        }
    }

    /** Registra una vacuna para la mascota seleccionada (CU07, FC06). */
    private void registrarVacuna() {
        if (actual == null) return;
        JTextField nombre = Estilo.campo("Ej.: Rabia");
        JTextField aplicacion = Estilo.campo("AAAA-MM-DD");
        aplicacion.setText(Estilo.hoy());
        JTextField proxima = Estilo.campo("AAAA-MM-DD (opcional)");
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        Estilo.fila(p, "Vacuna", nombre);
        Estilo.fila(p, "Fecha de aplicación", aplicacion);
        Estilo.fila(p, "Próxima dosis", proxima);
        p.setPreferredSize(new Dimension(320, 170));
        if (JOptionPane.showConfirmDialog(this, p, "Registrar vacuna de " + actual.getNombre(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String n = nombre.getText().trim(), a = aplicacion.getText().trim(), pr = proxima.getText().trim();
        if (n.isEmpty()) { Estilo.aviso(this, "Escriba el nombre de la vacuna."); return; }
        if (!Estilo.esFechaValida(a)) { Estilo.aviso(this, "La fecha de aplicación debe tener el formato AAAA-MM-DD."); return; }
        if (!pr.isEmpty() && !Estilo.esFechaValida(pr)) { Estilo.aviso(this, "La próxima dosis debe tener el formato AAAA-MM-DD."); return; }
        if (!pr.isEmpty() && pr.compareTo(a) <= 0) { Estilo.aviso(this, "La próxima dosis debe ser posterior a la fecha de aplicación."); return; }
        ControlVacuna v = new ControlVacuna();
        v.setNombreVacuna(n);
        v.setFechaAplicacion(a);
        v.setProximaDosis(pr.isEmpty() ? null : pr);
        v.setIdMascota(actual.getIdMascota());
        if (vacunaDAO.registrar(v)) mostrarFicha();
        else Estilo.error(this, "No se pudo registrar la vacuna.");
    }

    private void eliminarVacuna() {
        int fila = tablaVacunas.getSelectedRow();
        if (fila < 0) { Estilo.aviso(this, "Selecciona una vacuna de la tabla."); return; }
        int id = (int) modeloVacunas.getValueAt(fila, 0);
        if (Estilo.confirmar(this, "¿Quitar este registro de vacuna?") && vacunaDAO.eliminar(id)) mostrarFicha();
    }
}
