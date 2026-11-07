package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.ClienteDAO;
import com.bellasmascotas.model.Cliente;
import com.bellasmascotas.view.Estilo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/** Diálogo para registrar un cliente con validación de formatos (CU04, RF03). */
public class DlgNuevoCliente extends JDialog {

    public JTextField txtCedula, txtNombres, txtApellidos, txtTelefono, txtDireccion;
    public JButton btnGuardar, btnCancelar;

    public DlgNuevoCliente(Window owner) {
        super(owner, "Nuevo cliente", Dialog.ModalityType.APPLICATION_MODAL);
        construir("Nuevo cliente", "Todos los campos son obligatorios.");
        btnGuardar.addActionListener(e -> guardarNuevoCliente());
        btnCancelar.addActionListener(e -> dispose());
    }

    protected void construir(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = Estilo.negrita(titulo, 18);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = Estilo.subtitulo(subtitulo);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t); p.add(s); p.add(Box.createVerticalStrut(6));
        txtCedula = Estilo.campo("Solo números");
        txtNombres = Estilo.campo("Nombres");
        txtApellidos = Estilo.campo("Apellidos");
        txtTelefono = Estilo.campo("7 a 15 dígitos");
        txtDireccion = Estilo.campo("Dirección de residencia");
        Estilo.fila(p, "Cédula", txtCedula);
        Estilo.fila(p, "Nombres", txtNombres);
        Estilo.fila(p, "Apellidos", txtApellidos);
        Estilo.fila(p, "Teléfono", txtTelefono);
        Estilo.fila(p, "Dirección", txtDireccion);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCancelar = Estilo.botonSecundario("Cancelar");
        btnGuardar = Estilo.botonPrimario("Guardar");
        botones.add(btnCancelar); botones.add(btnGuardar);
        p.add(Box.createVerticalStrut(18));
        p.add(botones);
        setContentPane(p);
        getRootPane().setDefaultButton(btnGuardar);
        setSize(440, 470);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    /** Valida los datos (RNF03). Devuelve null si son válidos o el mensaje de error. */
    protected String validar(boolean conCedula) {
        if ((conCedula && txtCedula.getText().trim().isEmpty()) || txtNombres.getText().trim().isEmpty()
                || txtApellidos.getText().trim().isEmpty() || txtTelefono.getText().trim().isEmpty()
                || txtDireccion.getText().trim().isEmpty()) return "Por favor, complete todos los campos obligatorios.";
        if (conCedula && !txtCedula.getText().trim().matches("\\d{5,15}")) return "La cédula debe contener únicamente números (5 a 15 dígitos).";
        if (txtNombres.getText().matches(".*\\d.*")) return "Los nombres no pueden contener números.";
        if (txtApellidos.getText().matches(".*\\d.*")) return "Los apellidos no pueden contener números.";
        if (!txtTelefono.getText().trim().matches("\\d{7,15}")) return "El teléfono debe contener entre 7 y 15 números.";
        return null;
    }

    private void guardarNuevoCliente() {
        String error = validar(true);
        if (error != null) { Estilo.aviso(this, error); return; }
        ClienteDAO dao = new ClienteDAO();
        String cedula = txtCedula.getText().trim();
        if (dao.existeCedula(cedula)) {
            Estilo.aviso(this, "Ya existe un cliente con la cédula " + cedula + ".");
            return;
        }
        Cliente c = new Cliente();
        c.setCedula(cedula);
        c.setNombres(txtNombres.getText().trim());
        c.setApellidos(txtApellidos.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setDireccion(txtDireccion.getText().trim());
        c.setFechaRegistro(LocalDate.now().toString());
        if (dao.RegistrarCliente(c)) {
            Estilo.info(this, "Cliente " + c.getNombreCompleto() + " registrado.");
            dispose();
        } else {
            Estilo.error(this, "No se pudo guardar. Verifique la conexión con la base de datos.");
        }
    }
}
