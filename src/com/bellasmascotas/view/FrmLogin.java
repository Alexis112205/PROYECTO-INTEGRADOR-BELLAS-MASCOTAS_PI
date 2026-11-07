package com.bellasmascotas.view;

import com.bellasmascotas.controller.MenuController;
import com.bellasmascotas.controller.Sesion;
import com.bellasmascotas.dao.Conexion;
import com.bellasmascotas.dao.UsuarioDAO;
import com.bellasmascotas.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Connection;

/** Ventana de inicio de sesión (CU01, RF01). */
public class FrmLogin extends JFrame {

    public JTextField txtUsuario;
    public JPasswordField txtPassword;
    public JButton btnEntrar;
    private JLabel lblError;

    public FrmLogin() {
        setTitle("Bellas Mascotas - Iniciar sesión");
        setSize(960, 600);
        setMinimumSize(new Dimension(900, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        btnEntrar.addActionListener(e -> ingresar());
        txtPassword.addActionListener(e -> ingresar());
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
    }

    /** Valida los campos, consulta la base de datos y abre el menú según el rol. */
    private void ingresar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Ingrese usuario y contraseña.");
            return;
        }
        try (Connection c = Conexion.obtenerConexion()) {
            if (c == null) {
                mostrarError("No hay conexión con MySQL. Revise config.properties y que el servidor esté encendido.");
                return;
            }
        } catch (Exception ignored) { }

        Usuario u = new UsuarioDAO().validarLogin(user, pass);
        if (u == null) {
            mostrarError("Usuario o contraseña incorrectos. Intente de nuevo.");
            txtPassword.setText("");
            return;
        }
        Sesion.iniciar(u);
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        new MenuController(menu);
        menu.setVisible(true);
        dispose();
    }

    private void mostrarError(String msg) {
        lblError.setText("<html>" + msg + "</html>");
        lblError.setVisible(true);
    }

    private void initComponents() {
        JPanel fondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(225, 242, 241), getWidth(), getHeight(), new Color(251, 244, 230)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(fondo);

        Tarjeta tarjeta = new Tarjeta(new GridLayout(1, 2));
        tarjeta.setBorder(new EmptyBorder(0, 0, 0, 0));
        tarjeta.setPreferredSize(new Dimension(820, 470));

        // Lado izquierdo: identidad del negocio
        JPanel izq = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilo.AZUL);
                g2.fillRoundRect(0, 0, getWidth() + 20, getHeight(), 16, 16);
                g2.dispose();
            }
        };
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBorder(new EmptyBorder(40, 30, 40, 30));
        JLabel logo = new JLabel(logoCircular(200));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel t1 = new JLabel("Sistema de Gestión");
        t1.setFont(Estilo.fuente(Font.BOLD, 22));
        t1.setForeground(Color.WHITE);
        t1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel t2 = new JLabel("<html><div style='text-align:center'>Clientes, mascotas, citas, inventario<br>y reportes en un solo lugar.</div></html>");
        t2.setFont(Estilo.fuente(Font.PLAIN, 13));
        t2.setForeground(new Color(220, 230, 240));
        t2.setAlignmentX(Component.CENTER_ALIGNMENT);
        izq.add(Box.createVerticalGlue());
        izq.add(logo);
        izq.add(Box.createVerticalStrut(18));
        izq.add(t1);
        izq.add(Box.createVerticalStrut(6));
        izq.add(t2);
        izq.add(Box.createVerticalGlue());

        // Lado derecho: formulario
        JPanel der = new JPanel();
        der.setOpaque(false);
        der.setLayout(new BoxLayout(der, BoxLayout.Y_AXIS));
        der.setBorder(new EmptyBorder(50, 40, 40, 40));
        JLabel tit = Estilo.titulo("Iniciar sesión");
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = Estilo.subtitulo("<html>Ingrese con el usuario asignado por el administrador.</html>");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        der.add(tit);
        der.add(Box.createVerticalStrut(4));
        der.add(sub);
        der.add(Box.createVerticalStrut(10));

        txtUsuario = Estilo.campo("Usuario");
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Contraseña");
        txtPassword.putClientProperty("FlatLaf.style", "arc:8; showRevealButton:true");
        Estilo.fila(der, "Usuario", txtUsuario);
        Estilo.fila(der, "Contraseña", txtPassword);

        lblError = Estilo.alerta("", true);
        lblError.setVisible(false);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        der.add(Box.createVerticalStrut(12));
        der.add(lblError);

        btnEntrar = Estilo.botonPrimario("Ingresar");
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        der.add(Box.createVerticalStrut(16));
        der.add(btnEntrar);
        JLabel nota = Estilo.subtitulo("<html>La contraseña se valida con hash SHA-256. El menú se ajusta al rol del usuario.</html>");
        nota.setFont(Estilo.fuente(Font.PLAIN, 11));
        nota.setAlignmentX(Component.LEFT_ALIGNMENT);
        der.add(Box.createVerticalStrut(12));
        der.add(nota);
        der.add(Box.createVerticalGlue());

        tarjeta.add(izq);
        tarjeta.add(der);
        fondo.add(tarjeta);
    }

    /** Carga el logo del negocio recortado en círculo con fondo blanco. */
    static ImageIcon logoCircular(int d) {
        BufferedImage out = new BufferedImage(d, d, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setColor(Color.WHITE);
        g2.fillOval(0, 0, d, d);
        URL url = FrmLogin.class.getResource("/resources/images/Logo.png");
        if (url != null) {
            g2.setClip(new Ellipse2D.Float(0, 0, d, d));
            g2.drawImage(new ImageIcon(url).getImage(), 0, 0, d, d, null);
        }
        g2.dispose();
        return new ImageIcon(out);
    }
}
