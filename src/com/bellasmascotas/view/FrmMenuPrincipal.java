package com.bellasmascotas.view;

import com.bellasmascotas.controller.Permisos;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.model.Usuario;
import com.bellasmascotas.view.Panel_Ventanas.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal: menú lateral con los módulos permitidos para el rol,
 * barra superior y área de contenido con CardLayout (una sola ventana).
 */
public class FrmMenuPrincipal extends JFrame {

    public JPanel pnlVistaDinamica;
    public CardLayout navegador;

    public BtnMenu btnInicio, btnAgenda, btnClientes, btnMascotas, btnVentas,
                   btnInventario, btnReportes, btnUsuarios;
    public JButton btnSalir;
    public JTextField txtBuscar;
    public JLabel lblCampanita;
    public JLabel lblContador;

    // Paneles de cada módulo
    public PnlDashboard pnlDashboard;
    public PnlAgenda pnlAgenda;
    public PnlClientes pnlClientes;
    public PnlMascotas pnlMascotas;
    public PnlVentas pnlVentas;
    public PnlInventario pnlInventario;
    public PnlReportes pnlReportes;
    public PnlUsuarios pnlUsuarios;

    /** Relación botón del menú → nombre de la tarjeta en el CardLayout. */
    public final Map<BtnMenu, String> tarjetas = new LinkedHashMap<>();

    public FrmMenuPrincipal() {
        setTitle("Bellas Mascotas - Sistema de Gestión");
        setSize(1366, 800);
        setMinimumSize(new Dimension(1180, 700));
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Estilo.FONDO);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        Usuario u = Sesion.getUsuario();
        boolean estilista = Sesion.esRol(Permisos.ESTILISTA);

        // ── 1. MENÚ LATERAL ──
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(Color.WHITE);
        lateral.setPreferredSize(new Dimension(230, 0));
        lateral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Estilo.BORDE), new EmptyBorder(16, 12, 16, 12)));

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        marca.setOpaque(false);
        marca.add(new JLabel(FrmLogin.logoCircular(46)));
        JLabel nombre = new JLabel("<html><b style='font-size:12px;color:#1F4E79'>Bellas Mascotas</b><br>"
                + "<span style='font-size:8px;color:#6B7C85'>ESTÉTICA CANINA</span></html>");
        nombre.setFont(Estilo.fuente(Font.PLAIN, 13));
        marca.add(nombre);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        arriba.add(marca);
        arriba.add(Box.createVerticalStrut(18));
        JLabel sec = new JLabel("MÓDULOS");
        sec.setFont(Estilo.fuente(Font.PLAIN, 10));
        sec.setForeground(new Color(138, 154, 162));
        sec.setBorder(new EmptyBorder(0, 8, 8, 0));
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        arriba.add(sec);

        btnInicio     = new BtnMenu("Panel de inicio");
        btnAgenda     = new BtnMenu("Agenda de citas");
        btnClientes   = new BtnMenu("Clientes");
        btnMascotas   = new BtnMenu("Mascotas");
        btnVentas     = new BtnMenu(estilista ? "Mi agenda" : "Registrar servicio");
        btnInventario = new BtnMenu("Inventario");
        btnReportes   = new BtnMenu("Reportes y gastos");
        btnUsuarios   = new BtnMenu("Usuarios y catálogo");

        // ── 2. CONTENEDOR DINÁMICO ──
        navegador = new CardLayout();
        pnlVistaDinamica = new JPanel(navegador);
        pnlVistaDinamica.setBackground(Estilo.FONDO);

        // Solo se crean los módulos que el rol puede usar (RF02)
        agregar(arriba, btnInicio, Permisos.Modulo.DASHBOARD, "Inicio", () -> pnlDashboard = new PnlDashboard());
        agregar(arriba, btnAgenda, Permisos.Modulo.AGENDA, "Agenda", () -> pnlAgenda = new PnlAgenda());
        agregar(arriba, btnClientes, Permisos.Modulo.CLIENTES, "Clientes", () -> pnlClientes = new PnlClientes());
        if (estilista) {   // Para el estilista, "Mi agenda" va antes que Mascotas
            agregar(arriba, btnVentas, Permisos.Modulo.VENTAS, "Ventas", () -> pnlVentas = new PnlVentas());
            agregar(arriba, btnMascotas, Permisos.Modulo.MASCOTAS, "Mascotas", () -> pnlMascotas = new PnlMascotas());
        } else {
            agregar(arriba, btnMascotas, Permisos.Modulo.MASCOTAS, "Mascotas", () -> pnlMascotas = new PnlMascotas());
            agregar(arriba, btnVentas, Permisos.Modulo.VENTAS, "Ventas", () -> pnlVentas = new PnlVentas());
        }
        agregar(arriba, btnInventario, Permisos.Modulo.INVENTARIO, "Inventario", () -> pnlInventario = new PnlInventario());
        agregar(arriba, btnReportes, Permisos.Modulo.REPORTES, "Reportes", () -> pnlReportes = new PnlReportes());
        agregar(arriba, btnUsuarios, Permisos.Modulo.USUARIOS, "Usuarios", () -> pnlUsuarios = new PnlUsuarios());
        lateral.add(arriba, BorderLayout.NORTH);

        // Pie del menú: tipo de acceso y usuario
        JPanel abajo = new JPanel();
        abajo.setOpaque(false);
        abajo.setLayout(new BoxLayout(abajo, BoxLayout.Y_AXIS));
        String[] acceso = descripcionAcceso(u.getIdRol());
        Tarjeta caja = new Tarjeta(new BorderLayout(), new Color(240, 247, 246), new Color(211, 232, 230));
        caja.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lblAcceso = new JLabel("<html><b style='color:#1E6F6B'>" + acceso[0] + "</b><br>"
                + "<span style='color:#3C4C55'>" + acceso[1] + "</span></html>");
        lblAcceso.setFont(Estilo.fuente(Font.PLAIN, 11));
        caja.add(lblAcceso);
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);
        abajo.add(caja);
        abajo.add(Box.createVerticalStrut(12));
        JPanel yo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        yo.setOpaque(false);
        yo.add(avatar(iniciales(u.getNombre())));
        JLabel lblYo = new JLabel("<html>" + u.getNombre() + "<br><span style='color:#6B7C85;font-size:9px'>"
                + Permisos.nombreRol(u.getIdRol()) + "</span></html>");
        lblYo.setFont(Estilo.fuente(Font.PLAIN, 12));
        yo.add(lblYo);
        yo.setAlignmentX(Component.LEFT_ALIGNMENT);
        abajo.add(yo);
        lateral.add(abajo, BorderLayout.SOUTH);
        add(lateral, BorderLayout.WEST);

        // ── 3. BARRA SUPERIOR ──
        JPanel derecho = new JPanel(new BorderLayout());
        derecho.setBackground(Estilo.FONDO);
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setPreferredSize(new Dimension(0, 58));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE), new EmptyBorder(10, 24, 10, 24)));
        txtBuscar = Estilo.campo("Buscar cliente por cédula o nombre…  (Enter)");
        txtBuscar.setPreferredSize(new Dimension(380, 36));
        JPanel izqBarra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izqBarra.setOpaque(false);
        izqBarra.add(txtBuscar);
        txtBuscar.setVisible(Sesion.puede(Permisos.Modulo.CLIENTES));
        barra.add(izqBarra, BorderLayout.WEST);

        JPanel derBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derBarra.setOpaque(false);
        JLabel rolActivo = Estilo.etiqueta("Rol activo");
        derBarra.add(rolActivo);
        JLabel rol = new JLabel(Permisos.nombreRol(u.getIdRol()));
        rol.setFont(Estilo.fuente(Font.BOLD, 12));
        rol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 223, 224), 1, true), new EmptyBorder(7, 12, 7, 12)));
        derBarra.add(rol);
        lblCampanita = new JLabel("🔔");
        lblCampanita.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblCampanita.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblCampanita.setToolTipText("Notificaciones");
        lblContador = new JLabel("0");
        lblContador.setOpaque(true);
        lblContador.setBackground(new Color(217, 83, 79));
        lblContador.setForeground(Color.WHITE);
        lblContador.setFont(Estilo.fuente(Font.BOLD, 10));
        lblContador.setBorder(new EmptyBorder(1, 5, 1, 5));
        JPanel campana = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        campana.setOpaque(false);
        campana.add(lblCampanita);
        campana.add(lblContador);
        campana.setVisible(Sesion.puede(Permisos.Modulo.NOTIFICACIONES));
        derBarra.add(campana);
        btnSalir = Estilo.botonSecundario("Cerrar sesión");
        derBarra.add(btnSalir);
        barra.add(derBarra, BorderLayout.EAST);

        derecho.add(barra, BorderLayout.NORTH);
        derecho.add(pnlVistaDinamica, BorderLayout.CENTER);
        add(derecho, BorderLayout.CENTER);
    }

    /** Agrega el botón al menú y crea el panel solo si el rol tiene permiso sobre el módulo. */
    private void agregar(JPanel menu, BtnMenu btn, Permisos.Modulo modulo, String nombreTarjeta, java.util.function.Supplier<JPanel> crear) {
        if (!Sesion.puede(modulo)) return;
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(btn);
        menu.add(Box.createVerticalStrut(3));
        pnlVistaDinamica.add(crear.get(), nombreTarjeta);
        tarjetas.put(btn, nombreTarjeta);
        btn.modulo = modulo;
    }

    private String[] descripcionAcceso(int idRol) {
        switch (idRol) {
            case Permisos.ADMINISTRADOR: return new String[]{"Acceso total", "Todos los módulos habilitados."};
            case Permisos.ESTILISTA:     return new String[]{"Operación de estética", "Agenda, fichas y servicios."};
            case Permisos.RECEPCION:     return new String[]{"Atención y ventas", "Clientes, mascotas, citas y cobro."};
            default:                     return new String[]{"Sin acceso", "Consulte al administrador."};
        }
    }

    private static String iniciales(String nombre) {
        String[] partes = nombre.trim().split("\\s+");
        String s = partes[0].substring(0, 1) + (partes.length > 1 ? partes[1].substring(0, 1) : "");
        return s.toUpperCase();
    }

    private static JLabel avatar(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilo.AZUL);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setForeground(Color.WHITE);
        l.setFont(Estilo.fuente(Font.BOLD, 12));
        l.setPreferredSize(new Dimension(34, 34));
        return l;
    }

    /** Opción del menú lateral. */
    public static class BtnMenu extends JPanel {
        public final JLabel label;
        public Permisos.Modulo modulo;
        private boolean activo;

        public BtnMenu(String texto) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setPreferredSize(new Dimension(200, 38));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 8));
            label = new JLabel(texto);
            label.setIcon(new IconoCuadro());
            label.setIconTextGap(10);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            add(label, BorderLayout.CENTER);
            setActivo(false);
        }

        public void setActivo(boolean activo) {
            this.activo = activo;
            label.setFont(Estilo.fuente(activo ? Font.BOLD : Font.PLAIN, 13));
            label.setForeground(activo ? Estilo.PRIMARIO : new Color(60, 76, 85));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (activo) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilo.PRIMARIO_CLARO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
            super.paintComponent(g);
        }

        /** Ícono simple (cuadro redondeado) que toma el color del texto. */
        private class IconoCuadro implements Icon {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(label.getForeground());
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(x + 1, y + 1, 13, 13, 5, 5);
                g2.dispose();
            }
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
        }
    }
}
