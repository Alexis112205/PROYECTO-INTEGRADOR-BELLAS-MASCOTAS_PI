package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.controller.Permisos;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.ClienteDAO;
import com.bellasmascotas.dao.MascotaDAO;
import com.bellasmascotas.model.Cliente;
import com.bellasmascotas.model.Mascota;
import com.bellasmascotas.view.Estilo;
import com.bellasmascotas.view.Refrescable;
import com.bellasmascotas.view.Tarjeta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Módulo Clientes: directorio de propietarios (CU04, CU05; RF03, RF04, FC03). */
public class PnlClientes extends JPanel implements Refrescable {

    public JTextField txtBuscar, txtFechaInicio, txtFechaFin;
    public JButton btnBuscar, btnFiltrarFecha, btnLimpiarFiltro, btnNuevo, btnEditar, btnEliminar;
    public JTable tablaClientes;
    public DefaultTableModel modeloTabla;
    private final JLabel lblTotal = new JLabel();
    private final ClienteDAO dao = new ClienteDAO();
    private List<Cliente> listaActual = List.of();

    public PnlClientes() {
        setLayout(new BorderLayout());
        JPanel c = Estilo.contenedor();
        add(c);
        lblTotal.setFont(Estilo.fuente(Font.PLAIN, 12));
        c.add(Estilo.encabezado("Clientes", "Directorio de propietarios: datos de contacto, mascotas y fidelización.", lblTotal),
                BorderLayout.NORTH);

        Tarjeta t = new Tarjeta(new BorderLayout(0, 10));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        txtBuscar = Estilo.campo("Buscar por cédula, nombres o apellidos…");
        txtBuscar.setPreferredSize(new Dimension(280, 34));
        btnBuscar = Estilo.botonSecundario("Buscar");
        txtFechaInicio = Estilo.campo("Desde AAAA-MM-DD");
        txtFechaInicio.setPreferredSize(new Dimension(140, 34));
        txtFechaFin = Estilo.campo("Hasta AAAA-MM-DD");
        txtFechaFin.setPreferredSize(new Dimension(140, 34));
        btnFiltrarFecha = Estilo.botonSecundario("Filtrar");
        btnLimpiarFiltro = Estilo.botonSecundario("Todos");
        barra.add(txtBuscar); barra.add(btnBuscar);
        JPanel fechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fechas.setOpaque(false);
        fechas.add(Estilo.etiqueta("Fecha de registro:"));
        fechas.add(txtFechaInicio); fechas.add(txtFechaFin); fechas.add(btnFiltrarFecha); fechas.add(btnLimpiarFiltro);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        btnNuevo = Estilo.botonPrimario("+ Nuevo");
        btnEditar = Estilo.botonSecundario("Editar");
        btnEliminar = Estilo.botonPeligro("Eliminar");
        acciones.add(btnNuevo); acciones.add(btnEditar);
        // Solo el Administrador puede eliminar clientes (FC03)
        if (Sesion.esRol(Permisos.ADMINISTRADOR)) acciones.add(btnEliminar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(barra, BorderLayout.WEST);
        norte.add(acciones, BorderLayout.EAST);
        fechas.setBorder(new javax.swing.border.EmptyBorder(8, 0, 0, 0));
        norte.add(fechas, BorderLayout.SOUTH);
        t.add(norte, BorderLayout.NORTH);

        modeloTabla = Estilo.modelo("ID", "Cédula", "Nombre", "Teléfono", "Dirección", "Mascotas", "Registro", "Última visita", "Puntos");
        tablaClientes = Estilo.tabla(modeloTabla);
        Estilo.ocultarColumna(tablaClientes, 0);
        tablaClientes.getColumnModel().getColumn(8).setCellRenderer(new Estilo.PillRenderer());
        tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(180);
        t.add(Estilo.scroll(tablaClientes), BorderLayout.CENTER);
        JLabel hint = Estilo.etiqueta("Validaciones: cédula numérica y única; nombres sin números; teléfono de 7 a 15 dígitos."
                + (Sesion.esRol(Permisos.ADMINISTRADOR) ? "" : " La eliminación de clientes solo está habilitada para el Administrador."));
        t.add(hint, BorderLayout.SOUTH);
        c.add(t, BorderLayout.CENTER);

        // ── Eventos ──
        btnNuevo.addActionListener(e -> {
            new DlgNuevoCliente(SwingUtilities.getWindowAncestor(this)).setVisible(true);
            refrescar();
        });
        btnBuscar.addActionListener(e -> buscar(txtBuscar.getText().trim()));
        txtBuscar.addActionListener(e -> buscar(txtBuscar.getText().trim()));
        btnFiltrarFecha.addActionListener(e -> filtrarPorFechas());
        btnLimpiarFiltro.addActionListener(e -> {
            txtBuscar.setText(""); txtFechaInicio.setText(""); txtFechaFin.setText("");
            refrescar();
        });
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) editar(); }
        });
    }

    @Override
    public void refrescar() {
        String texto = txtBuscar.getText().trim();
        mostrar(texto.isEmpty() ? dao.obtenerTodos() : dao.buscarPorCedulaONombre(texto));
    }

    /** Busca por cédula, nombres o apellidos (CU05, RF04). */
    public void buscar(String texto) {
        txtBuscar.setText(texto);
        mostrar(texto.isEmpty() ? dao.obtenerTodos() : dao.buscarPorCedulaONombre(texto));
    }

    private void filtrarPorFechas() {
        String ini = txtFechaInicio.getText().trim(), fin = txtFechaFin.getText().trim();
        if (ini.isEmpty() || fin.isEmpty()) { Estilo.aviso(this, "Ingresa ambas fechas para filtrar.\nFormato: AAAA-MM-DD"); return; }
        if (!Estilo.esFechaValida(ini) || !Estilo.esFechaValida(fin)) { Estilo.aviso(this, "Formato de fecha incorrecto.\nUsa el formato AAAA-MM-DD."); return; }
        if (ini.compareTo(fin) > 0) { Estilo.aviso(this, "La fecha inicial no puede ser mayor que la final."); return; }
        mostrar(dao.buscarPorFechas(ini, fin));
    }

    private void mostrar(List<Cliente> lista) {
        listaActual = lista;
        Map<Integer, List<Mascota>> porCliente = new MascotaDAO().listarTodo().stream()
                .collect(Collectors.groupingBy(Mascota::getIdCliente));
        modeloTabla.setRowCount(0);
        for (Cliente c : lista) {
            String mascotas = porCliente.getOrDefault(c.getIdCliente(), List.of()).stream()
                    .map(Mascota::toString).collect(Collectors.joining(", "));
            modeloTabla.addRow(new Object[]{c.getIdCliente(), c.getCedula(), c.getNombreCompleto(), c.getTelefono(),
                    c.getDireccion(), mascotas.isEmpty() ? "—" : mascotas, c.getFechaRegistro(),
                    c.getFechaUltimaVisita() == null ? "—" : c.getFechaUltimaVisita(), String.valueOf(c.getPuntosAcumulados())});
        }
        lblTotal.setText(lista.size() + " clientes");
    }

    private Cliente seleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(tablaClientes.convertRowIndexToModel(fila), 0);
        return listaActual.stream().filter(c -> c.getIdCliente() == id).findFirst().orElse(null);
    }

    private void editar() {
        Cliente c = seleccionado();
        if (c == null) { Estilo.aviso(this, "Selecciona un cliente de la tabla para editar."); return; }
        new DlgEditarCliente(SwingUtilities.getWindowAncestor(this), c).setVisible(true);
        refrescar();
    }

    private void eliminar() {
        Cliente c = seleccionado();
        if (c == null) { Estilo.aviso(this, "Selecciona un cliente de la tabla para eliminar."); return; }
        if (!Estilo.confirmar(this, "¿Eliminar al cliente " + c.getNombreCompleto() + "?")) return;
        if (dao.eliminar(c.getIdCliente())) {
            Estilo.info(this, "Cliente eliminado.");
            refrescar();
        } else {
            Estilo.error(this, "No se puede eliminar: el cliente tiene mascotas registradas.\nElimine o reasigne primero sus mascotas.");
        }
    }
}
