package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Pelicula;
import servicio.SistemaData;
import util.AppStyles;

public class DetallePeliculaFrame extends JFrame {

    public DetallePeliculaFrame(Pelicula pelicula) {
        setTitle("Detalle de película");
        AppStyles.aplicarIcono(this);
        setSize(560, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Detalle de película"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Información completa antes de alquilar o administrar el catálogo."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JTextArea area = new JTextArea(generarTexto(pelicula));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 14));
        add(new JScrollPane(area), BorderLayout.CENTER);

        var cerrar = AppStyles.botonNeutro("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.setOpaque(false);
        acciones.add(cerrar);
        add(acciones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String generarTexto(Pelicula pelicula) {
        if (pelicula == null) {
            return "No se encontró información de la película.";
        }
        return "========== INFORMACIÓN DE LA PELÍCULA ==========\n\n"
                + "Título: " + pelicula.getTitulo() + "\n"
                + "Idioma: " + pelicula.getIdioma() + "\n"
                + "Género: " + pelicula.getGenero() + "\n"
                + "Año de estreno: " + pelicula.getAnio() + "\n"
                + "Duración: " + pelicula.getDuracionTexto() + "\n"
                + "Formato: " + pelicula.getFormato() + "\n"
                + "Precio de alquiler: $" + String.format("%,.2f", pelicula.getPrecio()) + "\n"
                + "Copias disponibles: " + pelicula.getCantidad() + "\n"
                + "Disponibilidad: " + (pelicula.estaDisponible() ? "Disponible" : "No disponible") + "\n\n"
                + "Descripción:\n" + pelicula.getDescripcion();
    }
}
