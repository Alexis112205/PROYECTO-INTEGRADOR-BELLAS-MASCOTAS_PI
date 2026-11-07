package com.bellasmascotas.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Tarjeta blanca con bordes redondeados y borde gris claro (componente base del mockup). */
public class Tarjeta extends JPanel {

    private final Color fondo;
    private final Color borde;

    public Tarjeta() { this(new BorderLayout(), Estilo.BLANCO, Estilo.BORDE); }

    public Tarjeta(LayoutManager layout) { this(layout, Estilo.BLANCO, Estilo.BORDE); }

    public Tarjeta(LayoutManager layout, Color fondo, Color borde) {
        super(layout);
        this.fondo = fondo;
        this.borde = borde;
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setColor(borde);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }

    /** Tarjeta de indicador (KPI): título pequeño, valor grande y detalle. */
    public static Tarjeta indicador(String titulo, JLabel valor, JLabel detalle) {
        Tarjeta t = new Tarjeta();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        JLabel tit = Estilo.etiqueta(titulo);
        valor.setFont(Estilo.fuente(Font.BOLD, 26));
        valor.setForeground(Estilo.TEXTO);
        detalle.setFont(Estilo.fuente(Font.PLAIN, 11));
        detalle.setForeground(Estilo.TEXTO_SUAVE);
        t.add(tit);
        t.add(Box.createVerticalStrut(6));
        t.add(valor);
        t.add(Box.createVerticalStrut(2));
        t.add(detalle);
        return t;
    }
}
