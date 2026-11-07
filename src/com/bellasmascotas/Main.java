package com.bellasmascotas;

import com.bellasmascotas.view.FrmLogin;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/** Punto de entrada: aplica el tema FlatLaf y abre la ventana de ingreso. */
public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Component.accentColor", new Color(30, 111, 107));
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
