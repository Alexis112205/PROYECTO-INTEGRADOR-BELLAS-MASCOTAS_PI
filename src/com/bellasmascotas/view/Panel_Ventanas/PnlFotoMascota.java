package com.bellasmascotas.view.Panel_Ventanas;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * RF4 - Gestión de imágenes.
 * Panel con un espacio visual para previsualizar la foto de la mascota y un botón
 * que abre un JFileChooser para elegirla.
 *
 * Solo se guarda en la base de datos la RUTA de la imagen (columna mascotas.ruta_imagen),
 * no la imagen en sí, para mantener la base de datos ligera.
 */
public class PnlFotoMascota extends JPanel {

    // Carpeta (relativa al proyecto) donde se copian las fotos
    private static final String CARPETA_FOTOS = "fotos";
    private static final long   MAX_BYTES     = 5L * 1024 * 1024; // 5 MB
    private static final int    LADO_VISTA    = 180;

    private final JLabel  lblVista;
    private final JButton btnCargar;
    private final JButton btnQuitar;

    private File   archivoSeleccionado; // foto elegida que aún no se copia a la carpeta
    private String rutaActual;          // ruta ya guardada (la que va en ruta_imagen)

    public PnlFotoMascota() {
        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        lblVista = new JLabel("Sin foto", SwingConstants.CENTER);
        lblVista.setPreferredSize(new Dimension(LADO_VISTA, LADO_VISTA));
        lblVista.setOpaque(true);
        lblVista.setBackground(Color.WHITE);
        lblVista.setForeground(Color.GRAY);
        lblVista.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        add(lblVista, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.setBorder(new EmptyBorder(0, 0, 0, 0));

        btnCargar = com.bellasmascotas.view.Estilo.botonSecundario("Cargar foto");
        btnQuitar = com.bellasmascotas.view.Estilo.botonSecundario("Quitar");
        pnlBotones.add(btnCargar);
        pnlBotones.add(btnQuitar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> elegirImagen());
        btnQuitar.addActionListener(e -> limpiar());
    }

    // ------------------------------------------------------------------
    // Selección de la imagen con JFileChooser
    // ------------------------------------------------------------------
    private void elegirImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar fotografía de la mascota");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Imágenes (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // el usuario canceló
        }

        File archivo = chooser.getSelectedFile();

        if (archivo.length() > MAX_BYTES) {
            JOptionPane.showMessageDialog(this,
                    "La imagen supera los 5 MB. Elige una más liviana.",
                    "Imagen muy grande", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BufferedImage img = leerImagen(archivo);
        if (img == null) {
            JOptionPane.showMessageDialog(this,
                    "El archivo no es una imagen válida.",
                    "Formato no válido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        archivoSeleccionado = archivo;
        mostrarVistaPrevia(img);
    }

    // ------------------------------------------------------------------
    // API pública para usar desde el formulario de mascotas
    // ------------------------------------------------------------------

    /** true si el usuario eligió una foto nueva que todavía no se ha guardado. */
    public boolean hayFotoNueva() {
        return archivoSeleccionado != null;
    }

    /**
     * Copia la foto elegida a la carpeta "fotos" (con un nombre único) y devuelve
     * la ruta relativa para guardarla en mascotas.ruta_imagen.
     * Si no se eligió foto nueva, devuelve la ruta que ya tenía (o null si no hay foto).
     * Llamar a este método justo al guardar la mascota.
     */
    public String guardarFoto() throws IOException {
        if (archivoSeleccionado == null) {
            return rutaActual;
        }
        File carpeta = new File(CARPETA_FOTOS);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + CARPETA_FOTOS);
        }
        String nombre = archivoSeleccionado.getName();
        String ext = nombre.contains(".") ? nombre.substring(nombre.lastIndexOf('.')).toLowerCase() : ".png";
        File destino = new File(carpeta, UUID.randomUUID() + ext);

        Files.copy(archivoSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

        rutaActual = CARPETA_FOTOS + "/" + destino.getName();
        archivoSeleccionado = null;
        return rutaActual;
    }

    /** Carga la vista previa de una foto ya guardada (para editar una mascota existente). */
    public void setRutaImagen(String ruta) {
        archivoSeleccionado = null;
        rutaActual = (ruta == null || ruta.trim().isEmpty()) ? null : ruta.trim();

        if (rutaActual == null) {
            mostrarSinFoto();
            return;
        }
        BufferedImage img = leerImagen(new File(rutaActual));
        if (img == null) {
            lblVista.setIcon(null);
            lblVista.setText("Foto no encontrada");
        } else {
            mostrarVistaPrevia(img);
        }
    }

    /** Ruta guardada actualmente (puede ser null). */
    public String getRutaImagen() {
        return rutaActual;
    }

    /** Deja el panel vacío (para un formulario nuevo o al pulsar "Quitar"). */
    public void limpiar() {
        archivoSeleccionado = null;
        rutaActual = null;
        mostrarSinFoto();
    }

    // ------------------------------------------------------------------
    // Utilidades internas
    // ------------------------------------------------------------------
    private BufferedImage leerImagen(File archivo) {
        try {
            return ImageIO.read(archivo); // devuelve null si no es una imagen
        } catch (IOException e) {
            return null;
        }
    }

    private void mostrarVistaPrevia(BufferedImage img) {
        // Se escala manteniendo la proporción para que quepa en el recuadro
        double escala = Math.min((double) LADO_VISTA / img.getWidth(),
                                 (double) LADO_VISTA / img.getHeight());
        int ancho = Math.max(1, (int) (img.getWidth() * escala));
        int alto  = Math.max(1, (int) (img.getHeight() * escala));
        Image escalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        lblVista.setText(null);
        lblVista.setIcon(new ImageIcon(escalada));
    }

    private void mostrarSinFoto() {
        lblVista.setIcon(null);
        lblVista.setText("Sin foto");
    }
}