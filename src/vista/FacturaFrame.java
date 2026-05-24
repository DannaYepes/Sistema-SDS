package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.Alquiler;
import modelo.Cliente;
import servicio.PdfExportService;
import servicio.SistemaData;
import util.AppStyles;

public class FacturaFrame extends JFrame {

    private final String documentoCliente;
    private final String nombreCliente;
    private final JTextArea area;
    private Alquiler ultimo;
    private int totalCliente;

    public FacturaFrame(String nombreCliente) {
        this("", nombreCliente);
    }

    public FacturaFrame(Cliente cliente) {
        this(cliente == null ? "" : cliente.getDocumento(), cliente == null ? "" : cliente.getNombre());
    }

    public FacturaFrame(String documentoCliente, String nombreCliente) {
        this.documentoCliente = documentoCliente == null ? "" : documentoCliente.trim();
        this.nombreCliente = nombreCliente == null ? "" : nombreCliente.trim();
        setTitle("Factura / Comprobante de pago");
        AppStyles.aplicarIcono(this);
        setSize(760, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setOpaque(false);
        header.add(new javax.swing.JLabel(AppStyles.logo(58)), BorderLayout.WEST);
        JPanel textos = new JPanel(new BorderLayout(8, 4));
        textos.setOpaque(false);
        textos.add(AppStyles.titulo("Factura profesional"), BorderLayout.NORTH);
        textos.add(AppStyles.subtitulo("Comprobante formal del último alquiler, listo para imprimir o exportar en PDF."), BorderLayout.CENTER);
        textos.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        header.add(textos, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        area = new JTextArea(generarTextoFactura());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        add(new JScrollPane(area), BorderLayout.CENTER);

        construirBotones();
        setVisible(true);
    }

    private void construirBotones() {
        var imprimir = AppStyles.botonPrimario("Imprimir factura");
        var exportar = AppStyles.botonSecundario("Exportar comprobante PDF");
        var cerrar = AppStyles.botonNeutro("Cerrar");
        imprimir.addActionListener(e -> imprimirFactura());
        exportar.addActionListener(e -> exportarPdf());
        cerrar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        acciones.add(imprimir);
        acciones.add(exportar);
        acciones.add(cerrar);
        add(acciones, BorderLayout.SOUTH);
    }

    private String generarTextoFactura() {
        ultimo = SistemaData.alquiler.buscarUltimo(documentoCliente, nombreCliente);
        if (ultimo == null) {
            String cliente = documentoCliente.isEmpty() ? nombreCliente : documentoCliente + " - " + nombreCliente;
            return "No existen alquileres registrados para el cliente: " + cliente;
        }

        totalCliente = SistemaData.alquiler.contarAlquileresPorCliente(
                ultimo.getCliente().getDocumento(),
                ultimo.getCliente().getNombre()
        );

        String separador = "============================================================\n";
        return separador
                + "                  " + AppStyles.APP_NAME.toUpperCase() + "\n"
                + "          " + AppStyles.APP_SLOGAN + "\n"
                + separador
                + "FACTURA / COMPROBANTE DE ALQUILER\n"
                + "Código de pago: " + ultimo.getPago().getId() + "\n\n"
                + "DATOS DEL CLIENTE\n"
                + "Documento: " + valorVisible(ultimo.getCliente().getDocumento()) + "\n"
                + "Cliente: " + ultimo.getCliente().getNombre() + "\n"
                + "Películas rentadas por el cliente: " + totalCliente + "\n\n"
                + "DETALLE DE LA PELÍCULA\n"
                + "Película: " + ultimo.getPelicula().getTitulo() + "\n"
                + "Género: " + ultimo.getPelicula().getGenero() + "\n"
                + "Idioma: " + ultimo.getPelicula().getIdioma() + "\n"
                + "Año: " + ultimo.getPelicula().getAnio() + "\n"
                + "Duración: " + ultimo.getPelicula().getDuracionTexto() + "\n"
                + "Formato: " + ultimo.getPelicula().getFormato() + "\n"
                + "Descripción: " + ultimo.getPelicula().getDescripcion() + "\n\n"
                + "PERIODO Y ESTADO\n"
                + "Fecha alquiler: " + ultimo.getFechaAlquiler() + "\n"
                + "Fecha devolución pactada: " + ultimo.getFechaDevolucion() + "\n"
                + "Estado: " + ultimo.getEstado() + "\n"
                + "Días de retraso actual: " + ultimo.getDiasRetrasoActual() + "\n\n"
                + "RESUMEN DE PAGO\n"
                + "Método: " + ultimo.getPago().getMetodo() + "\n"
                + "Subtotal: $" + String.format("%,.2f", ultimo.getTotal()) + "\n"
                + "Multa por retraso actual: $" + String.format("%,.2f", ultimo.getMultaActual()) + "\n"
                + "TOTAL ACTUAL: $" + String.format("%,.2f", ultimo.getTotalConMultaActual()) + "\n"
                + separador
                + "Registrado por: " + ultimo.getUsuarioRegistro() + " / " + ultimo.getRolRegistro() + "\n"
                + "Este comprobante es válido para impresión o archivo digital.\n";
    }

    private void imprimirFactura() {
        try {
            boolean impresa = area.print();
            if (impresa) {
                AppStyles.mostrarExito(this, "La factura fue enviada al diálogo de impresión.");
            }
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo imprimir");
        }
    }

    private void exportarPdf() {
        if (ultimo == null) {
            AppStyles.mostrarAdvertencia(this, "No hay información de factura para exportar.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar comprobante PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("comprobante_" + ultimo.getPago().getId() + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File destino = PdfExportService.asegurarExtensionPdf(chooser.getSelectedFile());
            PdfExportService.exportarFactura(ultimo, totalCliente, destino);
            AppStyles.mostrarExito(this, "Comprobante PDF exportado correctamente en:\n" + destino.getAbsolutePath());
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo exportar el comprobante");
        }
    }

    private String valorVisible(String valor) {
        return valor == null || valor.trim().isEmpty() ? "No registrado" : valor.trim();
    }
}
