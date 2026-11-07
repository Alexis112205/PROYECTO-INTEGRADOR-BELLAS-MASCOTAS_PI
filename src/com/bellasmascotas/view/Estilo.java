package com.bellasmascotas.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Colores, fuentes y componentes reutilizables de la interfaz (diseño del mockup).
 * Centralizar el estilo aquí mantiene todas las pantallas consistentes (CC02).
 */
public final class Estilo {

    private Estilo() { }

    // ── Colores del mockup ──
    public static final Color FONDO       = new Color(244, 247, 247);
    public static final Color BLANCO      = Color.WHITE;
    public static final Color BORDE       = new Color(221, 230, 230);
    public static final Color TEXTO       = new Color(30, 43, 51);
    public static final Color TEXTO_SUAVE = new Color(107, 124, 133);
    public static final Color PRIMARIO    = new Color(30, 111, 107);
    public static final Color PRIMARIO_CLARO = new Color(225, 242, 241);
    public static final Color AZUL        = new Color(31, 78, 121);
    public static final Color SELECCION   = new Color(234, 246, 245);

    // ── Fuentes ──
    public static final String FUENTE = "Segoe UI";
    public static Font fuente(int estilo, int tam) { return new Font(FUENTE, estilo, tam); }

    // ── Formatos ──
    private static final NumberFormat PESOS = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"));

    /** Formatea un valor en pesos colombianos: $ 35.000 */
    public static String pesos(double valor) { return "$ " + PESOS.format(Math.round(valor)); }

    /** Fecha de hoy en formato AAAA-MM-DD. */
    public static String hoy() { return LocalDate.now().toString(); }

    /** true si el texto es una fecha válida con formato AAAA-MM-DD. */
    public static boolean esFechaValida(String texto) {
        if (texto == null || !texto.trim().matches("\\d{4}-\\d{2}-\\d{2}")) return false;
        try { LocalDate.parse(texto.trim()); return true; } catch (DateTimeParseException e) { return false; }
    }

    // ── Textos ──
    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 24));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 13));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 12));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JLabel negrita(String texto, int tam) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, tam));
        l.setForeground(TEXTO);
        return l;
    }

    /** Encabezado de pantalla: título, subtítulo y un panel opcional a la derecha. */
    public static JPanel encabezado(String titulo, String subtitulo, JComponent derecha) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo(titulo));
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo(subtitulo));
        p.add(textos, BorderLayout.WEST);
        if (derecha != null) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
            envoltura.setOpaque(false);
            envoltura.add(derecha);
            p.add(envoltura, BorderLayout.EAST);
        }
        p.setBorder(new EmptyBorder(0, 0, 14, 0));
        return p;
    }

    /** Encabezado de una tarjeta: título en negrita y texto pequeño a la derecha. */
    public static JPanel encabezadoTarjeta(String titulo, JComponent derecha) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(negrita(titulo, 15), BorderLayout.WEST);
        if (derecha != null) p.add(derecha, BorderLayout.EAST);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));
        return p;
    }

    // ── Botones ──
    public static JButton botonPrimario(String texto) {
        return boton(texto, "background:#1E6F6B; foreground:#FFFFFF; font:bold; borderWidth:0; focusWidth:0; arc:10");
    }

    public static JButton botonSecundario(String texto) {
        return boton(texto, "background:#FFFFFF; foreground:#1E2B33; borderColor:#D5DFE0; focusWidth:0; arc:10");
    }

    public static JButton botonPeligro(String texto) {
        return boton(texto, "background:#FFF7F6; foreground:#B3362F; borderColor:#F0C7C3; focusWidth:0; arc:10");
    }

    private static JButton boton(String texto, String estilo) {
        JButton b = new JButton(texto);
        b.putClientProperty("FlatLaf.style", estilo);
        b.setFont(fuente(Font.PLAIN, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMargin(new Insets(7, 14, 7, 14));
        return b;
    }

    // ── Campos ──
    public static JTextField campo(String placeholder) {
        JTextField t = new JTextField();
        t.putClientProperty("JTextField.placeholderText", placeholder);
        t.putClientProperty("FlatLaf.style", "arc:8");
        t.setFont(fuente(Font.PLAIN, 13));
        t.setPreferredSize(new Dimension(150, 34));
        return t;
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> c = new JComboBox<>();
        c.putClientProperty("FlatLaf.style", "arc:8");
        c.setFont(fuente(Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(150, 34));
        return c;
    }

    /** Agrega etiqueta + campo en un formulario vertical. */
    public static void fila(JPanel form, String etiqueta, JComponent campo) {
        JLabel l = etiqueta(etiqueta);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(8, 0, 4, 0));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (campo instanceof JTextField || campo instanceof JComboBox || campo instanceof JSpinner) {
            campo.setPreferredSize(new Dimension(Math.max(120, campo.getPreferredSize().width), 34));
        }
        fijarAlto(campo);
        form.add(l);
        form.add(campo);
    }

    /** Evita que BoxLayout estire verticalmente el componente (lo deja con su alto preferido). */
    public static <C extends JComponent> C fijarAlto(C c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    // ── Tablas ──
    /** Modelo de tabla de solo lectura. */
    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    /** Tabla con el estilo del mockup. */
    public static JTable tabla(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setRowHeight(36);
        t.setFont(fuente(Font.PLAIN, 13));
        t.setForeground(TEXTO);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(238, 242, 242));
        t.setSelectionBackground(SELECCION);
        t.setSelectionForeground(TEXTO);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setReorderingAllowed(false);
        JTableHeader h = t.getTableHeader();
        h.setFont(fuente(Font.PLAIN, 12));
        h.setForeground(TEXTO_SUAVE);
        h.setBackground(new Color(250, 252, 252));
        h.setPreferredSize(new Dimension(0, 32));
        DefaultTableCellRenderer celda = new DefaultTableCellRenderer();
        celda.setBorder(new EmptyBorder(0, 10, 0, 10));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, sel, false, r, c);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        });
        return t;
    }

    /** Envuelve la tabla en un scroll sin borde. */
    public static JScrollPane scroll(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(BorderFactory.createLineBorder(new Color(229, 236, 236)));
        s.getViewport().setBackground(BLANCO);
        return s;
    }

    /** Oculta una columna (por ejemplo, el ID que se usa internamente). */
    public static void ocultarColumna(JTable t, int col) {
        t.getColumnModel().getColumn(col).setMinWidth(0);
        t.getColumnModel().getColumn(col).setMaxWidth(0);
        t.getColumnModel().getColumn(col).setPreferredWidth(0);
    }

    /** Texto de dos líneas para una celda: principal en negrita y detalle en gris. */
    public static String dosLineas(String principal, String detalle) {
        return dosLineas(principal, detalle, null);
    }

    /**
     * Igual que dosLineas, pero con una marca roja junto al texto principal (por ejemplo, "Cuidado").
     * Se usa texto y no un símbolo porque algunas fuentes de Windows no dibujan ⚠ dentro de las tablas.
     */
    public static String dosLineas(String principal, String detalle, String marca) {
        String m = marca == null ? "" : " <span style='color:#B3362F;font-size:9px'><b>" + html(marca) + "</b></span>";
        return "<html><nobr><b>" + html(principal) + "</b>" + m + "</nobr><br><nobr><span style='color:#7D8D95;font-size:9px'>"
                + html(detalle) + "</span></nobr></html>";
    }

    private static String html(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ── Etiquetas de estado (pills) ──
    /** Colores de fondo y texto según el estado mostrado. */
    public static Color[] coloresEstado(String texto) {
        String t = texto == null ? "" : texto.toLowerCase();
        if (t.startsWith("finalizada") || t.startsWith("disponible") || t.startsWith("estilista") || t.startsWith("sin alertas") || t.startsWith("al día") || t.startsWith("no agresivo"))
            return new Color[]{new Color(227, 244, 234), new Color(46, 125, 79)};
        if (t.startsWith("en curso") || t.startsWith("administrador") || t.matches("\\d+"))
            return new Color[]{new Color(227, 238, 249), AZUL};
        if (t.startsWith("cancelada") || t.startsWith("agotado") || t.startsWith("agresivo") || t.startsWith("vencida"))
            return new Color[]{new Color(253, 228, 226), new Color(179, 54, 47)};
        if (t.startsWith("stock bajo") || t.startsWith("alergia") || t.startsWith("recepci") || t.startsWith("bozal") || t.startsWith("por vencer"))
            return new Color[]{new Color(255, 243, 214), new Color(154, 106, 0)};
        return new Color[]{new Color(238, 242, 242), new Color(90, 106, 114)};
    }

    /** Renderizador que dibuja el valor de la celda como una etiqueta redondeada de color. */
    public static class PillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
            String texto = v == null ? "" : v.toString();
            Color fondoFila = sel ? SELECCION : BLANCO;
            Color[] col = coloresEstado(texto);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            p.setBackground(fondoFila);
            if (!texto.isEmpty()) p.add(new Pill(texto, col[0], col[1]));
            return p;
        }
    }

    /** Etiqueta redondeada de color (estado de citas, insumos, roles y alertas). */
    public static class Pill extends JLabel {
        private final Color fondo;
        public Pill(String texto, Color fondo, Color frente) {
            super(texto);
            this.fondo = fondo;
            setForeground(frente);
            setFont(fuente(Font.BOLD, 11));
            setBorder(new EmptyBorder(3, 9, 3, 9));
        }
        public Pill(String texto) { this(texto, coloresEstado(texto)[0], coloresEstado(texto)[1]); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Avisos ──
    /** Recuadro de alerta amarillo (advertencia) o rojo (peligro). */
    public static JLabel alerta(String texto, boolean peligro) {
        JLabel l = new JLabel("<html>" + texto + "</html>");
        l.setOpaque(true);
        l.setFont(fuente(Font.PLAIN, 12));
        l.setBackground(peligro ? new Color(253, 236, 234) : new Color(255, 246, 224));
        l.setForeground(peligro ? new Color(158, 47, 40) : new Color(138, 93, 0));
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(peligro ? new Color(243, 200, 195) : new Color(243, 223, 168)),
                new EmptyBorder(8, 10, 8, 10)));
        return l;
    }

    public static void info(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Bellas Mascotas", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Revise los datos", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String msg) {
        return JOptionPane.showConfirmDialog(padre, msg, "Confirmar", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Panel de contenido de un módulo: fondo gris claro y márgenes. */
    public static JPanel contenedor() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }
}
