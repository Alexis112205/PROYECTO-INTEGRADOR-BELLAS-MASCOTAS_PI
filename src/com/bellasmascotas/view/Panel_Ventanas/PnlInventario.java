package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.CatalogoDAO;
import com.bellasmascotas.dao.Conexion;
import com.bellasmascotas.dao.InsumoDAO;
import com.bellasmascotas.model.Insumo;
import com.bellasmascotas.model.RecetaServicio;
import com.bellasmascotas.model.ServicioCatalogo;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Módulo Inventario de insumos (CU12, RF09). El estado se calcula a partir del stock. */
public class PnlInventario extends JPanel implements Refrescable {

    private final InsumoDAO dao = new InsumoDAO();
    private List<Insumo> todos = new ArrayList<>();
    private List<ServicioCatalogo> servicios = new ArrayList<>();
    private List<List<RecetaServicio>> recetas = new ArrayList<>();
    private int filtro = 0; // 0 todos, 1 stock bajo, 2 agotados
    private int umbral;
    private Insumo actual;

    public DefaultTableModel modeloTabla = Estilo.modelo("Código", "Insumo", "Existencias", "Precio de costo", "Valor en stock", "Estado");
    public JTable tablaInsumos = Estilo.tabla(modeloTabla);
    public JTextField txtBuscar = Estilo.campo("Buscar insumo…");
    public JTextField txtNombre = Estilo.campo("Nombre del insumo");
    public JTextField txtStock = Estilo.campo("Unidades");
    public JTextField txtPrecio = Estilo.campo("Pesos");
    public JButton btnNuevo, btnEliminar, btnGuardar;
    private final JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
    private final JLabel lblTituloForm = Estilo.negrita("Nuevo insumo", 15);
    private final JLabel lblCodigo = Estilo.etiqueta("");
    private final DefaultTableModel modeloUso = Estilo.modelo("Servicio", "Cantidad por servicio");
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblHint = Estilo.etiqueta("");

    public PnlInventario() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        lblTotal.setFont(Estilo.fuente(Font.PLAIN, 12));
        c.add(Estilo.encabezado("Inventario de insumos", "Existencias, costo y consumo de los insumos de la estética.", lblTotal), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1;

        Tarjeta lista = new Tarjeta(new BorderLayout(0, 10));
        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        pnlTabs.setOpaque(false);
        pnlTabs.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(pnlTabs);
        norte.add(Box.createVerticalStrut(10));
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        txtBuscar.setPreferredSize(new Dimension(260, 34));
        JPanel bIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bIzq.setOpaque(false);
        bIzq.add(txtBuscar);
        barra.add(bIzq, BorderLayout.WEST);
        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        btnNuevo = Estilo.botonPrimario("+ Nuevo");
        btnEliminar = Estilo.botonPeligro("Eliminar");
        acc.add(btnNuevo); acc.add(btnEliminar);
        barra.add(acc, BorderLayout.EAST);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(barra);
        lista.add(norte, BorderLayout.NORTH);
        tablaInsumos.getColumnModel().getColumn(5).setCellRenderer(new Estilo.PillRenderer());
        tablaInsumos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaInsumos.getColumnModel().getColumn(1).setPreferredWidth(200);
        lista.add(Estilo.scroll(tablaInsumos), BorderLayout.CENTER);
        lista.add(lblHint, BorderLayout.SOUTH);
        g.gridx = 0; g.weightx = 1.6; g.insets = new Insets(0, 0, 0, 14);
        lista.setPreferredSize(new Dimension(10, 10));
        grid.add(lista, g);

        Tarjeta form = new Tarjeta(new BorderLayout());
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.add(lblTituloForm, BorderLayout.WEST);
        cab.add(lblCodigo, BorderLayout.EAST);
        form.add(cab, BorderLayout.NORTH);
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        Estilo.fila(f, "Nombre", txtNombre);
        JPanel fila = new JPanel(new GridLayout(1, 2, 8, 0));
        fila.setOpaque(false);
        JPanel a = new JPanel(); a.setOpaque(false); a.setLayout(new BoxLayout(a, BoxLayout.Y_AXIS));
        Estilo.fila(a, "Existencias", txtStock);
        JPanel b = new JPanel(); b.setOpaque(false); b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        Estilo.fila(b, "Precio de costo", txtPrecio);
        fila.add(a); fila.add(b);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(Estilo.fijarAlto(fila));
        btnGuardar = Estilo.botonPrimario("Guardar");
        JPanel pg = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        pg.setOpaque(false);
        pg.add(btnGuardar);
        pg.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(Estilo.fijarAlto(pg));
        JLabel lu = Estilo.negrita("Uso en recetas de servicio", 14);
        lu.setBorder(new EmptyBorder(10, 0, 8, 0));
        lu.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.add(lu);
        JTable tUso = Estilo.tabla(modeloUso);
        tUso.setRowHeight(30);
        JScrollPane su = Estilo.scroll(tUso);
        su.setAlignmentX(Component.LEFT_ALIGNMENT);
        su.setPreferredSize(new Dimension(200, 160));
        f.add(su);
        form.add(f, BorderLayout.CENTER);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(0, 0, 0, 0);
        form.setPreferredSize(new Dimension(10, 10));
        grid.add(form, g);
        c.add(grid, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { mostrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { mostrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { mostrar(); }
        });
        tablaInsumos.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) seleccionar(); });
        btnNuevo.addActionListener(e -> { tablaInsumos.clearSelection(); limpiarFormulario(); });
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        limpiarFormulario();
    }

    @Override
    public void refrescar() {
        umbral = Conexion.propiedadEntera("negocio.umbral_stock_bajo", 5);
        lblHint.setText("Estado calculado: Agotado = 0 unidades; Stock bajo = " + umbral + " unidades o menos. "
                + "El stock se descuenta automáticamente con cada servicio registrado.");
        todos = dao.listarTodo();
        servicios = new CatalogoDAO().listarTodo();
        recetas = new ArrayList<>();
        for (ServicioCatalogo s : servicios) recetas.add(new CatalogoDAO().listarReceta(s.getIdCatalogo()));
        int idSel = actual == null ? 0 : actual.getIdInsumo();
        construirTabs();
        mostrar();
        lblTotal.setText(todos.size() + " insumos");
        for (int i = 0; i < modeloTabla.getRowCount(); i++) if ((int) modeloTabla.getValueAt(i, 0) == idSel) tablaInsumos.setRowSelectionInterval(i, i);
    }

    private void construirTabs() {
        pnlTabs.removeAll();
        long bajos = todos.stream().filter(i -> i.getCantidadStock() > 0 && i.getCantidadStock() <= umbral).count();
        long agotados = todos.stream().filter(i -> i.getCantidadStock() <= 0).count();
        String[] t = {"Todos · " + todos.size(), "Stock bajo · " + bajos, "Agotados · " + agotados};
        for (int k = 0; k < 3; k++) {
            int idx = k;
            JLabel l = new JLabel(t[k]);
            boolean on = filtro == k;
            l.setFont(Estilo.fuente(on ? Font.BOLD : Font.PLAIN, 12));
            l.setForeground(on ? Estilo.PRIMARIO : Estilo.TEXTO_SUAVE);
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, on ? 2 : 0, 0, Estilo.PRIMARIO), new EmptyBorder(0, 0, 4, 0)));
            l.setCursor(new Cursor(Cursor.HAND_CURSOR));
            l.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { filtro = idx; construirTabs(); mostrar(); }
            });
            pnlTabs.add(l);
        }
        pnlTabs.revalidate(); pnlTabs.repaint();
    }

    private String estado(Insumo i) {
        if (i.getCantidadStock() <= 0) return "Agotado";
        if (i.getCantidadStock() <= umbral) return "Stock bajo";
        return "Disponible";
    }

    private void mostrar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);
        for (Insumo i : todos) {
            String est = estado(i);
            if (filtro == 1 && !est.equals("Stock bajo")) continue;
            if (filtro == 2 && !est.equals("Agotado")) continue;
            if (!texto.isEmpty() && !i.getNombre().toLowerCase().contains(texto)) continue;
            modeloTabla.addRow(new Object[]{i.getIdInsumo(), i.getNombre(), i.getCantidadStock() + " uds.",
                    Estilo.pesos(i.getPrecioCosto()), Estilo.pesos(i.getPrecioCosto() * Math.max(0, i.getCantidadStock())), est});
        }
    }

    private void seleccionar() {
        int fila = tablaInsumos.getSelectedRow();
        if (fila < 0) return;
        int id = (int) modeloTabla.getValueAt(tablaInsumos.convertRowIndexToModel(fila), 0);
        actual = todos.stream().filter(i -> i.getIdInsumo() == id).findFirst().orElse(null);
        if (actual == null) return;
        lblTituloForm.setText("Editar insumo");
        lblCodigo.setText("Código " + actual.getIdInsumo());
        txtNombre.setText(actual.getNombre());
        txtStock.setText(String.valueOf(actual.getCantidadStock()));
        txtPrecio.setText(String.valueOf((long) actual.getPrecioCosto()));
        modeloUso.setRowCount(0);
        for (int k = 0; k < servicios.size(); k++) {
            for (RecetaServicio r : recetas.get(k)) {
                if (r.getIdInsumo() == actual.getIdInsumo()) modeloUso.addRow(new Object[]{servicios.get(k).getTipo(), r.getCantidadUsada()});
            }
        }
    }

    private void limpiarFormulario() {
        actual = null;
        lblTituloForm.setText("Nuevo insumo");
        lblCodigo.setText("");
        txtNombre.setText(""); txtStock.setText(""); txtPrecio.setText("");
        modeloUso.setRowCount(0);
        txtNombre.requestFocus();
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        int stock;
        double precio;
        if (nombre.isEmpty() || txtStock.getText().isBlank() || txtPrecio.getText().isBlank()) {
            Estilo.aviso(this, "Complete el nombre, las existencias y el precio de costo."); return;
        }
        try {
            stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { Estilo.aviso(this, "Las existencias deben ser un número entero igual o mayor que 0."); return; }
        try {
            precio = Double.parseDouble(txtPrecio.getText().trim().replace(".", "").replace(",", "."));
            if (precio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { Estilo.aviso(this, "El precio de costo debe ser un número mayor que 0."); return; }
        boolean ok;
        if (actual == null) ok = dao.insertarInsumo(nombre, stock, precio);
        else {
            actual.setNombre(nombre); actual.setCantidadStock(stock); actual.setPrecioCosto(precio);
            ok = dao.modificar(actual);
        }
        if (ok) { Estilo.info(this, "Insumo guardado."); refrescar(); }
        else Estilo.error(this, "No se pudo guardar el insumo.");
    }

    private void eliminar() {
        if (actual == null) { Estilo.aviso(this, "Selecciona un insumo de la tabla."); return; }
        if (!Estilo.confirmar(this, "¿Eliminar el insumo " + actual.getNombre() + "?")) return;
        if (dao.eliminar(actual.getIdInsumo())) { limpiarFormulario(); refrescar(); }
        else Estilo.error(this, "No se puede eliminar: el insumo está en la receta de algún servicio.\nQuítelo primero de la receta en Usuarios y catálogo.");
    }
}
