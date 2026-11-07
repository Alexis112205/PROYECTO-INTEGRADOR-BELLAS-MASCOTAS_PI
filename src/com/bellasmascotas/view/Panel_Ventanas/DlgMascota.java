package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.MascotaDAO;
import com.bellasmascotas.model.Cliente;
import com.bellasmascotas.model.Mascota;
import com.bellasmascotas.view.Estilo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;

/** Formulario para registrar o editar una mascota con foto y cuidados especiales (CU06, RF05, FC05). */
public class DlgMascota extends JDialog {

    private final JTextField txtNombre = Estilo.campo("Nombre de la mascota");
    private final JComboBox<String> cbEspecie = Estilo.combo();
    private final JTextField txtRaza = Estilo.campo("Raza");
    private final JTextField txtEdad = Estilo.campo("Años");
    private final JComboBox<Cliente> cbCliente = Estilo.combo();
    private final JTextField txtAlergias = Estilo.campo("Ej.: shampoo de coco (vacío si no tiene)");
    private final JTextArea txtObs = new JTextArea(3, 20);
    private final JCheckBox chkAgresivo = new JCheckBox("Es agresivo");
    private final JCheckBox chkBozal = new JCheckBox("Requiere bozal");
    private final PnlFotoMascota pnlFoto = new PnlFotoMascota();
    private final Mascota mascota;
    private boolean guardado;

    public DlgMascota(Window owner, Mascota existente, Collection<Cliente> clientes) {
        super(owner, existente == null ? "Nueva mascota" : "Editar mascota", Dialog.ModalityType.APPLICATION_MODAL);
        this.mascota = existente == null ? new Mascota() : existente;

        for (String e : new String[]{"Perro", "Gato", "Otro"}) cbEspecie.addItem(e);
        for (Cliente c : clientes) cbCliente.addItem(c);

        JPanel raiz = new JPanel(new BorderLayout(18, 0));
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel izq = new JPanel(new BorderLayout(0, 10));
        izq.setOpaque(false);
        izq.add(Estilo.negrita(existente == null ? "Nueva mascota" : "Editar mascota", 18), BorderLayout.NORTH);
        izq.add(pnlFoto, BorderLayout.CENTER);
        raiz.add(izq, BorderLayout.WEST);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Estilo.fila(form, "Nombre", txtNombre);
        JPanel fila1 = new JPanel(new GridLayout(1, 3, 8, 0));
        fila1.setOpaque(false);
        fila1.add(conEtiqueta("Especie", cbEspecie));
        fila1.add(conEtiqueta("Raza", txtRaza));
        fila1.add(conEtiqueta("Edad", txtEdad));
        fila1.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(fila1);
        Estilo.fila(form, "Propietario", cbCliente);
        Estilo.fila(form, "Alergias", txtAlergias);
        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        checks.setOpaque(false);
        chkAgresivo.setOpaque(false); chkBozal.setOpaque(false);
        checks.add(chkAgresivo); checks.add(Box.createHorizontalStrut(18)); checks.add(chkBozal);
        checks.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(checks);
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setFont(Estilo.fuente(Font.PLAIN, 13));
        JScrollPane sObs = new JScrollPane(txtObs);
        sObs.setPreferredSize(new Dimension(300, 70));
        Estilo.fila(form, "Observaciones (preferencias de corte, comportamiento)", sObs);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton cancelar = Estilo.botonSecundario("Cancelar");
        JButton guardar = Estilo.botonPrimario("Guardar");
        botones.add(cancelar); botones.add(guardar);
        form.add(Box.createVerticalStrut(14));
        form.add(botones);
        raiz.add(form, BorderLayout.CENTER);
        setContentPane(raiz);

        if (existente != null) cargar(existente);
        cancelar.addActionListener(e -> dispose());
        guardar.addActionListener(e -> guardar());
        setSize(760, 520);
        setLocationRelativeTo(owner);
    }

    private JPanel conEtiqueta(String etiqueta, JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        Estilo.fila(p, etiqueta, c);
        return p;
    }

    private void cargar(Mascota m) {
        txtNombre.setText(m.getNombre());
        cbEspecie.setSelectedItem(m.getEspecie());
        txtRaza.setText(m.getRaza());
        txtEdad.setText(String.valueOf(m.getEdad()));
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getIdCliente() == m.getIdCliente()) cbCliente.setSelectedIndex(i);
        }
        txtAlergias.setText(m.getAlergias() == null ? "" : m.getAlergias());
        txtObs.setText(m.getObservaciones());
        chkAgresivo.setSelected(m.isAgresivo());
        chkBozal.setSelected(m.isRequiereBozal());
        pnlFoto.setRutaImagen(m.getRutaImagen());
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { Estilo.aviso(this, "El nombre de la mascota es obligatorio."); return; }
        if (cbCliente.getSelectedItem() == null) { Estilo.aviso(this, "Seleccione el propietario de la mascota."); return; }
        int edad;
        try {
            edad = txtEdad.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0 || edad > 40) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Estilo.aviso(this, "La edad debe ser un número entero entre 0 y 40.");
            return;
        }
        mascota.setNombre(nombre);
        mascota.setEspecie((String) cbEspecie.getSelectedItem());
        mascota.setRaza(txtRaza.getText().trim());
        mascota.setEdad(edad);
        mascota.setIdCliente(((Cliente) cbCliente.getSelectedItem()).getIdCliente());
        mascota.setAlergias(txtAlergias.getText().trim().isEmpty() ? null : txtAlergias.getText().trim());
        mascota.setObservaciones(txtObs.getText().trim());
        mascota.setAgresivo(chkAgresivo.isSelected());
        mascota.setRequiereBozal(chkBozal.isSelected());
        try {
            mascota.setRutaImagen(pnlFoto.guardarFoto());
        } catch (IOException ex) {
            Estilo.error(this, "No se pudo guardar la foto: " + ex.getMessage());
            return;
        }
        MascotaDAO dao = new MascotaDAO();
        boolean ok = mascota.getIdMascota() == 0 ? dao.registrarMascota(mascota) : dao.modificar(mascota);
        if (ok) { guardado = true; dispose(); }
        else Estilo.error(this, "No se pudo guardar la mascota. Verifique la conexión con la base de datos.");
    }

    /** true si el usuario guardó los cambios. */
    public boolean fueGuardado() { return guardado; }
}
