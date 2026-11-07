package com.bellasmascotas.view.Panel_Ventanas;

import com.bellasmascotas.dao.ClienteDAO;
import com.bellasmascotas.model.Cliente;
import com.bellasmascotas.view.Estilo;

import java.awt.*;

/** Diálogo para actualizar los datos de un cliente (CU04, FC03). La cédula no se modifica. */
public class DlgEditarCliente extends DlgNuevoCliente {

    private final Cliente clienteActual;

    public DlgEditarCliente(Window owner, Cliente cliente) {
        super(owner);
        this.clienteActual = cliente;
        setTitle("Editar cliente");
        getContentPane().removeAll();
        construir("Editar cliente", "La cédula no se puede modificar.");
        txtCedula.setText(cliente.getCedula());
        txtCedula.setEditable(false);
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());
        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void guardarCambios() {
        String error = validar(false);
        if (error != null) { Estilo.aviso(this, error); return; }
        clienteActual.setNombres(txtNombres.getText().trim());
        clienteActual.setApellidos(txtApellidos.getText().trim());
        clienteActual.setTelefono(txtTelefono.getText().trim());
        clienteActual.setDireccion(txtDireccion.getText().trim());
        if (new ClienteDAO().modificar(clienteActual)) {
            Estilo.info(this, "Datos del cliente actualizados.");
            dispose();
        } else {
            Estilo.error(this, "No se pudo actualizar. Verifique la conexión con la base de datos.");
        }
    }
}
