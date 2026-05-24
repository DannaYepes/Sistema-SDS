package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Alquiler;
import modelo.Reserva;
import servicio.SistemaData;
import util.AppStyles;

public class HistorialFrame extends JFrame {

    private final DefaultTableModel modeloAlquileres = new DefaultTableModel(
            new Object[]{"Documento", "Cliente", "Película", "Duración", "Formato", "Alquiler", "Devolución", "Estado", "Pago", "Subtotal bruto", "Descuento", "Multa actual", "Total actual", "Beneficio", "Rentadas cliente", "Usuario", "Rol"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel modeloReservas = new DefaultTableModel(
            new Object[]{"Documento", "Cliente", "Película", "Fecha reserva", "Vence", "Estado", "Prioridad", "Usuario", "Rol"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public HistorialFrame() {
        setTitle("Historial de alquileres y reservas");
        AppStyles.aplicarIcono(this);
        setSize(1320, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Historial"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Consulta alquileres, devoluciones, multas actuales, pagos y reservas pendientes o atendidas."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        cargarDatos();

        JTabbedPane tabs = new JTabbedPane();
        JTable tablaAlquileres = new JTable(modeloAlquileres);
        JTable tablaReservas = new JTable(modeloReservas);
        tablaAlquileres.setAutoCreateRowSorter(true);
        tablaReservas.setAutoCreateRowSorter(true);
        JScrollPane scrollAlquileres = AppStyles.scrollTabla(tablaAlquileres);
        JScrollPane scrollReservas = AppStyles.scrollTabla(tablaReservas);
        tabs.add("Alquileres", scrollAlquileres);
        tabs.add("Reservas", scrollReservas);
        add(tabs, BorderLayout.CENTER);

        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.setOpaque(false);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void cargarDatos() {
        modeloAlquileres.setRowCount(0);
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            String documento = a.getCliente().getDocumento();
            String cliente = a.getCliente().getNombre();
            modeloAlquileres.addRow(new Object[]{
                documento,
                cliente,
                a.getPelicula().getTitulo(),
                a.getPelicula().getDuracionTexto(),
                a.getPelicula().getFormato(),
                a.getFechaAlquiler(),
                a.getFechaDevolucion(),
                a.getEstado(),
                a.getPago().getMetodo(),
                "$" + String.format("%,.2f", a.getSubtotalSinDescuento()),
                "$" + String.format("%,.2f", a.getDescuento()),
                "$" + String.format("%,.2f", a.getMultaActual()),
                "$" + String.format("%,.2f", a.getTotalConMultaActual()),
                a.getBeneficioAplicado(),
                SistemaData.alquiler.contarAlquileresPorCliente(documento, cliente),
                a.getUsuarioRegistro(),
                a.getRolRegistro()
            });
        }

        modeloReservas.setRowCount(0);
        for (Reserva r : SistemaData.reservas.getLista()) {
            modeloReservas.addRow(new Object[]{
                r.getDocumentoCliente(),
                r.getCliente(),
                r.getPelicula(),
                r.getFechaReserva(),
                r.getFechaVencimiento(),
                r.getEstado(),
                r.isPrioritaria() ? "Premium" : "Normal",
                r.getUsuarioRegistro(),
                r.getRolRegistro()
            });
        }
    }
}
