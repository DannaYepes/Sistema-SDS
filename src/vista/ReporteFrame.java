package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import servicio.PdfExportService;
import servicio.ReporteService;
import servicio.SistemaData;
import util.AppStyles;

public class ReporteFrame extends JFrame {

    private final JTextArea area;

    public ReporteFrame() {
        setTitle("Reportes del sistema");
        AppStyles.aplicarIcono(this);
        setSize(760, 560);
        setMinimumSize(new java.awt.Dimension(740, 540));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setOpaque(false);
        header.add(new javax.swing.JLabel(AppStyles.logo(58)), BorderLayout.WEST);
        JPanel textos = new JPanel(new BorderLayout(8, 4));
        textos.setOpaque(false);
        textos.add(AppStyles.titulo("Reportes"), BorderLayout.NORTH);
        textos.add(AppStyles.subtitulo("Resumen diario, semanal y mensual con exportación profesional en PDF."), BorderLayout.CENTER);
        textos.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        header.add(textos, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        area = new JTextArea(new ReporteService().resumenTexto());
        area.setEditable(false);
        area.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 15));
        area.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JScrollPane(area), BorderLayout.CENTER);

        var btnExportar = AppStyles.botonSecundario("Exportar reporte ejecutivo PDF");
        var btnTop = AppStyles.botonSecundario("Top películas");
        var btnActualizar = AppStyles.botonNeutro("Actualizar reporte");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        btnExportar.addActionListener(e -> exportarPdf());
        btnTop.addActionListener(e -> new TopPeliculasFrame());
        btnActualizar.addActionListener(e -> area.setText(new ReporteService().resumenTexto()));
        btnCerrar.addActionListener(e -> dispose());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        acciones.add(btnExportar);
        acciones.add(btnTop);
        acciones.add(btnActualizar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void exportarPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("reporte_ejecutivo_sistema_sds.pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File destino = PdfExportService.asegurarExtensionPdf(chooser.getSelectedFile());
            PdfExportService.exportarReporte(destino);
            AppStyles.mostrarExito(this, "Reporte PDF exportado correctamente en:\n" + destino.getAbsolutePath());
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo exportar el reporte");
        }
    }
}
