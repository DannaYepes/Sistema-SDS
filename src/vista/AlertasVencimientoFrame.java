package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Alquiler;
import util.AppStyles;
import servicio.SistemaData;

public class AlertasVencimientoFrame extends JFrame {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Documento", "Cliente", "Película", "Fecha devolución", "Días retraso", "Multa actual", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public AlertasVencimientoFrame() {
        setTitle("Alertas de vencimiento");
        AppStyles.aplicarIcono(this);
        setSize(980, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        cargarDatos();
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        add(AppStyles.scrollTabla(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnActualizar = AppStyles.botonNeutro("Actualizar");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnActualizar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);
        btnActualizar.addActionListener(e -> cargarDatos());
        btnCerrar.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Alertas de vencimiento"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Películas que vencen hoy o ya tienen retraso. Útil para gestión de cobranza y devoluciones."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        for (Alquiler a : SistemaData.alquiler.obtenerPendientes()) {
            if (a.getDiasRetrasoActual() > 0 || java.time.LocalDate.now().equals(a.getFechaDevolucion())) {
                modelo.addRow(new Object[]{
                    a.getCliente().getDocumento(),
                    a.getCliente().getNombre(),
                    a.getPelicula().getTitulo(),
                    a.getFechaDevolucion(),
                    a.getDiasRetrasoActual(),
                    "$" + String.format("%,.2f", a.getMultaActual()),
                    a.getEstado()
                });
            }
        }
    }
}
