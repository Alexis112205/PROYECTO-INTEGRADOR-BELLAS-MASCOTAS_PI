package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.controller.Permisos;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.CatalogoDAO;
import com.bellasmascotas.dao.InsumoDAO;
import com.bellasmascotas.dao.UsuarioDAO;
import com.bellasmascotas.model.*;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Módulo Usuarios y catálogo de servicios con su receta de insumos (CU03, CU13; FC02, FC08). */
public class PnlUsuarios extends JPanel implements Refrescable {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<ServicioCatalogo> servicios = new ArrayList<>();
    private List<Insumo> insumos = new ArrayList<>();
    private List<RecetaServicio> recetaActual = new ArrayList<>();
    private Usuario usuarioActual;
    private ServicioCatalogo servicioActual;

    // Usuarios
    public DefaultTableModel modeloUsuarios = Estilo.modelo("ID", "Nombre", "Usuario", "Rol", "Contraseña");
    public JTable tablaUsuarios = Estilo.tabla(modeloUsuarios);
    public JTextField txtNombre = Estilo.campo("Nombre completo");
    public JTextField txtUsername = Estilo.campo("Nombre de usuario");
    public JComboBox<Rol> cbRol = Estilo.combo();
    public JPasswordField txtPassword = new JPasswordField();
    public JButton btnNuevoUsuario, btnGuardarUsuario, btnEliminarUsuario;
    private final JLabel lblFormUsuario = Estilo.negrita("Nuevo usuario", 14);

    // Catálogo
    public DefaultTableModel modeloCatalogo = Estilo.modelo("ID", "Servicio", "Precio de venta", "Descripción");
    public JTable tablaCatalogo = Estilo.tabla(modeloCatalogo);
    public JTextField txtTipo = Estilo.campo("Nombre del servicio");
    public JTextField txtPrecio = Estilo.campo("Pesos");
    public JTextField txtDescripcion = Estilo.campo("Descripción");
    public JButton btnNuevoServicio, btnGuardarServicio, btnEliminarServicio;
    private final JLabel lblReceta = Estilo.negrita("Receta del servicio", 14);
    public DefaultTableModel modeloReceta = Estilo.modelo("ID", "Insumo", "Cantidad usada");
    public JTable tablaReceta = Estilo.tabla(modeloReceta);
    public JComboBox<Insumo> cbInsumo = Estilo.combo();
    public JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    public JButton btnAgregarInsumo, btnQuitarInsumo;

    public PnlUsuarios() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        c.add(Estilo.encabezado("Usuarios y catálogo de servicios",
                "Cuentas de empleados con su rol y servicios que ofrece la estética con su receta de insumos.", null), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(1, 2, 14, 0));
        grid.setOpaque(false);

        // ── Usuarios ──
        Tarjeta tu = new Tarjeta(new BorderLayout(0, 10));
        btnNuevoUsuario = Estilo.botonPrimario("+ Nuevo usuario");
        tu.add(Estilo.encabezadoTarjeta("Usuarios del sistema", btnNuevoUsuario), BorderLayout.NORTH);
        Estilo.ocultarColumna(tablaUsuarios, 0);
        tablaUsuarios.getColumnModel().getColumn(3).setCellRenderer(new Estilo.PillRenderer());
        tu.add(Estilo.scroll(tablaUsuarios), BorderLayout.CENTER);
        JPanel fu = new JPanel();
        fu.setOpaque(false);
        fu.setLayout(new BoxLayout(fu, BoxLayout.Y_AXIS));
        lblFormUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFormUsuario.setBorder(new EmptyBorder(6, 0, 0, 0));
        fu.add(lblFormUsuario);
        txtPassword.putClientProperty("JTextField.placeholderText", "Vacío = no cambiar");
        txtPassword.putClientProperty("FlatLaf.style", "arc:8; showRevealButton:true");
        fu.add(dosColumnas("Nombre", txtNombre, "Usuario (único)", txtUsername));
        fu.add(dosColumnas("Rol", cbRol, "Contraseña", txtPassword));
        JPanel bu = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bu.setOpaque(false);
        bu.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuardarUsuario = Estilo.botonPrimario("Guardar");
        btnEliminarUsuario = Estilo.botonPeligro("Eliminar");
        bu.add(btnGuardarUsuario); bu.add(btnEliminarUsuario);
        fu.add(bu);
        tu.add(fu, BorderLayout.SOUTH);
        grid.add(tu);

        // ── Catálogo ──
        Tarjeta tc = new Tarjeta(new BorderLayout(0, 10));
        btnNuevoServicio = Estilo.botonPrimario("+ Nuevo");
        tc.add(Estilo.encabezadoTarjeta("Catálogo de servicios", btnNuevoServicio), BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        centro.setOpaque(false);
        Estilo.ocultarColumna(tablaCatalogo, 0);
        tablaCatalogo.setRowHeight(30);
        centro.add(Estilo.scroll(tablaCatalogo));
        JPanel pr = new JPanel(new BorderLayout(0, 6));
        pr.setOpaque(false);
        JPanel cabR = new JPanel(new BorderLayout(0, 6));
        cabR.setOpaque(false);
        cabR.add(lblReceta, BorderLayout.NORTH);
        JPanel addR = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        addR.setOpaque(false);
        cbInsumo.setPreferredSize(new Dimension(200, 32));
        spCantidad.setPreferredSize(new Dimension(60, 32));
        btnAgregarInsumo = Estilo.botonSecundario("Agregar");
        btnQuitarInsumo = Estilo.botonPeligro("Quitar");
        addR.add(cbInsumo); addR.add(spCantidad); addR.add(btnAgregarInsumo); addR.add(btnQuitarInsumo);
        cabR.add(addR, BorderLayout.CENTER);
        pr.add(cabR, BorderLayout.NORTH);
        Estilo.ocultarColumna(tablaReceta, 0);
        tablaReceta.setRowHeight(28);
        pr.add(Estilo.scroll(tablaReceta), BorderLayout.CENTER);
        centro.add(pr);
        tc.add(centro, BorderLayout.CENTER);
        JPanel fs = new JPanel();
        fs.setOpaque(false);
        fs.setLayout(new BoxLayout(fs, BoxLayout.Y_AXIS));
        fs.add(dosColumnas("Servicio", txtTipo, "Precio de venta (COP)", txtPrecio));
        Estilo.fila(fs, "Descripción", txtDescripcion);
        JPanel bs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bs.setOpaque(false);
        bs.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuardarServicio = Estilo.botonPrimario("Guardar servicio");
        btnEliminarServicio = Estilo.botonPeligro("Eliminar");
        bs.add(btnGuardarServicio); bs.add(btnEliminarServicio);
        fs.add(bs);
        tc.add(fs, BorderLayout.SOUTH);
        grid.add(tc);
        c.add(grid, BorderLayout.CENTER);

        // ── Eventos ──
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) seleccionarUsuario(); });
        btnNuevoUsuario.addActionListener(e -> limpiarUsuario());
        btnGuardarUsuario.addActionListener(e -> guardarUsuario());
        btnEliminarUsuario.addActionListener(e -> eliminarUsuario());
        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) seleccionarServicio(); });
        btnNuevoServicio.addActionListener(e -> limpiarServicio());
        btnGuardarServicio.addActionListener(e -> guardarServicio());
        btnEliminarServicio.addActionListener(e -> eliminarServicio());
        btnAgregarInsumo.addActionListener(e -> agregarInsumo());
        btnQuitarInsumo.addActionListener(e -> quitarInsumo());
        limpiarUsuario();
        limpiarServicio();
    }

    private JPanel dosColumnas(String e1, JComponent c1, String e2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.setOpaque(false);
        JPanel a = new JPanel(); a.setOpaque(false); a.setLayout(new BoxLayout(a, BoxLayout.Y_AXIS));
        Estilo.fila(a, e1, c1);
        JPanel b = new JPanel(); b.setOpaque(false); b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        Estilo.fila(b, e2, c2);
        p.add(a); p.add(b);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return Estilo.fijarAlto(p);
    }

    @Override
    public void refrescar() {
        cbRol.removeAllItems();
        for (Rol r : usuarioDAO.listarRoles()) cbRol.addItem(r);
        usuarios = usuarioDAO.listarTodo();
        modeloUsuarios.setRowCount(0);
        for (Usuario u : usuarios)
            modeloUsuarios.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getUsername(), Permisos.nombreRol(u.getIdRol()), "SHA-256"});
        insumos = new InsumoDAO().listarTodo();
        cbInsumo.removeAllItems();
        for (Insumo i : insumos) cbInsumo.addItem(i);
        cargarCatalogo();
    }

    // ───────────── Usuarios (FC02) ─────────────
    private void seleccionarUsuario() {
        int f = tablaUsuarios.getSelectedRow();
        if (f < 0) return;
        int id = (int) modeloUsuarios.getValueAt(tablaUsuarios.convertRowIndexToModel(f), 0);
        usuarioActual = usuarios.stream().filter(u -> u.getIdUsuario() == id).findFirst().orElse(null);
        if (usuarioActual == null) return;
        lblFormUsuario.setText("Editar usuario");
        txtNombre.setText(usuarioActual.getNombre());
        txtUsername.setText(usuarioActual.getUsername());
        txtPassword.setText("");
        txtPassword.putClientProperty("JTextField.placeholderText", "Vacío = no cambiar");
        for (int i = 0; i < cbRol.getItemCount(); i++) if (cbRol.getItemAt(i).getIdRol() == usuarioActual.getIdRol()) cbRol.setSelectedIndex(i);
    }

    private void limpiarUsuario() {
        usuarioActual = null;
        tablaUsuarios.clearSelection();
        lblFormUsuario.setText("Nuevo usuario");
        txtNombre.setText(""); txtUsername.setText(""); txtPassword.setText("");
        txtPassword.putClientProperty("JTextField.placeholderText", "Mínimo 6 caracteres");
    }

    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim(), user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        Rol rol = (Rol) cbRol.getSelectedItem();
        if (nombre.isEmpty() || user.isEmpty() || rol == null) { Estilo.aviso(this, "Complete nombre, usuario y rol."); return; }
        if (!user.matches("[a-zA-Z0-9._]{3,50}")) { Estilo.aviso(this, "El usuario debe tener de 3 a 50 caracteres, sin espacios."); return; }
        if (usuarioActual == null && pass.length() < 6) { Estilo.aviso(this, "La contraseña debe tener al menos 6 caracteres."); return; }
        if (usuarioActual != null && !pass.isEmpty() && pass.length() < 6) { Estilo.aviso(this, "La nueva contraseña debe tener al menos 6 caracteres."); return; }
        boolean repetido = usuarios.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user)
                && (usuarioActual == null || u.getIdUsuario() != usuarioActual.getIdUsuario()));
        if (repetido) { Estilo.aviso(this, "Ya existe un usuario con el nombre \"" + user + "\"."); return; }
        boolean ok;
        if (usuarioActual == null) {
            ok = usuarioDAO.insertar(new Usuario(0, nombre, user, pass, rol.getIdRol()));
        } else {
            if (usuarioActual.getIdUsuario() == Sesion.getUsuario().getIdUsuario() && rol.getIdRol() != Permisos.ADMINISTRADOR) {
                Estilo.aviso(this, "No puede quitarse a sí mismo el rol de Administrador."); return;
            }
            ok = usuarioDAO.modificar(new Usuario(usuarioActual.getIdUsuario(), nombre, user, pass, rol.getIdRol()));
        }
        if (ok) { Estilo.info(this, "Usuario guardado."); limpiarUsuario(); refrescar(); }
        else Estilo.error(this, "No se pudo guardar el usuario.");
    }

    private void eliminarUsuario() {
        if (usuarioActual == null) { Estilo.aviso(this, "Selecciona un usuario de la tabla."); return; }
        if (usuarioActual.getIdUsuario() == Sesion.getUsuario().getIdUsuario()) { Estilo.aviso(this, "No puede eliminar su propio usuario."); return; }
        if (!Estilo.confirmar(this, "¿Eliminar el usuario " + usuarioActual.getUsername() + "?")) return;
        if (usuarioDAO.eliminar(usuarioActual.getIdUsuario())) { limpiarUsuario(); refrescar(); }
        else Estilo.error(this, "No se puede eliminar: el usuario tiene citas, servicios o gastos registrados.\nPuede cambiarle el rol o la contraseña.");
    }

    // ───────────── Catálogo y receta (FC08) ─────────────
    private void cargarCatalogo() {
        int idSel = servicioActual == null ? 0 : servicioActual.getIdCatalogo();
        servicios = catalogoDAO.listarTodo();
        modeloCatalogo.setRowCount(0);
        for (ServicioCatalogo s : servicios)
            modeloCatalogo.addRow(new Object[]{s.getIdCatalogo(), s.getTipo(), Estilo.pesos(s.getPrecioVenta()), s.getDescripcion()});
        for (int i = 0; i < modeloCatalogo.getRowCount(); i++)
            if ((int) modeloCatalogo.getValueAt(i, 0) == idSel) tablaCatalogo.setRowSelectionInterval(i, i);
    }

    private void seleccionarServicio() {
        int f = tablaCatalogo.getSelectedRow();
        if (f < 0) return;
        int id = (int) modeloCatalogo.getValueAt(tablaCatalogo.convertRowIndexToModel(f), 0);
        servicioActual = servicios.stream().filter(s -> s.getIdCatalogo() == id).findFirst().orElse(null);
        if (servicioActual == null) return;
        txtTipo.setText(servicioActual.getTipo());
        txtPrecio.setText(String.valueOf((long) servicioActual.getPrecioVenta()));
        txtDescripcion.setText(servicioActual.getDescripcion() == null ? "" : servicioActual.getDescripcion());
        cargarReceta();
    }

    private void cargarReceta() {
        modeloReceta.setRowCount(0);
        boolean hay = servicioActual != null;
        btnAgregarInsumo.setEnabled(hay);
        btnQuitarInsumo.setEnabled(hay);
        if (!hay) { lblReceta.setText("Receta del servicio"); return; }
        lblReceta.setText("Receta de “" + servicioActual.getTipo() + "”");
        recetaActual = catalogoDAO.listarReceta(servicioActual.getIdCatalogo());
        for (RecetaServicio r : recetaActual) {
            String nombre = insumos.stream().filter(i -> i.getIdInsumo() == r.getIdInsumo()).map(Insumo::getNombre).findFirst().orElse("—");
            modeloReceta.addRow(new Object[]{r.getIdReceta(), nombre, r.getCantidadUsada()});
        }
    }

    private void limpiarServicio() {
        servicioActual = null;
        tablaCatalogo.clearSelection();
        txtTipo.setText(""); txtPrecio.setText(""); txtDescripcion.setText("");
        cargarReceta();
    }

    private void guardarServicio() {
        String tipo = txtTipo.getText().trim();
        if (tipo.isEmpty() || txtPrecio.getText().isBlank()) { Estilo.aviso(this, "Complete el nombre y el precio del servicio."); return; }
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim().replace(".", "").replace(",", "."));
            if (precio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { Estilo.aviso(this, "El precio debe ser un número mayor que 0."); return; }
        ServicioCatalogo s = servicioActual == null ? new ServicioCatalogo() : servicioActual;
        s.setTipo(tipo);
        s.setPrecioVenta(precio);
        s.setDescripcion(txtDescripcion.getText().trim().isEmpty() ? null : txtDescripcion.getText().trim());
        boolean ok = servicioActual == null ? catalogoDAO.insertar(s) : catalogoDAO.modificar(s);
        if (ok) { Estilo.info(this, "Servicio guardado."); cargarCatalogo(); }
        else Estilo.error(this, "No se pudo guardar el servicio.");
    }

    private void eliminarServicio() {
        if (servicioActual == null) { Estilo.aviso(this, "Selecciona un servicio de la tabla."); return; }
        if (!Estilo.confirmar(this, "¿Eliminar el servicio " + servicioActual.getTipo() + "?")) return;
        if (catalogoDAO.eliminar(servicioActual.getIdCatalogo())) { limpiarServicio(); cargarCatalogo(); }
        else Estilo.error(this, "No se puede eliminar: el servicio tiene citas, receta o registros históricos.");
    }

    private void agregarInsumo() {
        Insumo i = (Insumo) cbInsumo.getSelectedItem();
        if (servicioActual == null || i == null) return;
        if (recetaActual.stream().anyMatch(r -> r.getIdInsumo() == i.getIdInsumo())) {
            Estilo.aviso(this, "El insumo ya está en la receta. Quítelo y agréguelo con la nueva cantidad."); return;
        }
        RecetaServicio r = new RecetaServicio();
        r.setIdCatalogo(servicioActual.getIdCatalogo());
        r.setIdInsumo(i.getIdInsumo());
        r.setCantidadUsada((int) spCantidad.getValue());
        if (catalogoDAO.agregarInsumoReceta(r)) cargarReceta();
        else Estilo.error(this, "No se pudo agregar el insumo a la receta.");
    }

    private void quitarInsumo() {
        int f = tablaReceta.getSelectedRow();
        if (f < 0) { Estilo.aviso(this, "Selecciona un insumo de la receta."); return; }
        if (catalogoDAO.quitarInsumoReceta((int) modeloReceta.getValueAt(f, 0))) cargarReceta();
    }
}
