package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelo.Alquiler;
import modelo.Reserva;
import modelo.Usuario;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class HistorialClienteFrame extends JFrame {

    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextArea resumen;
    private final DefaultTableModel modeloAlquileres = new DefaultTableModel(
            new Object[]{"Película", "Alquiler", "Devolución", "Estado", "Subtotal", "Descuento", "Multa", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloReservas = new DefaultTableModel(
            new Object[]{"Película", "Reserva", "Vencimiento", "Estado", "Prioridad"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public HistorialClienteFrame() {
        setTitle("Historial por cliente");
        AppStyles.aplicarIcono(this);
        setSize(1180, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirContenido();
        construirAcciones();

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Historial por cliente"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Consulta alquileres, multas, descuentos, reservas y nivel comercial por documento o nombre."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setOpaque(false);

        JPanel filtros = AppStyles.crearTarjeta();
        filtros.setLayout(new GridBagLayout());
        txtDocumento = AppStyles.campoTexto();
        txtNombre = AppStyles.campoTexto();
        InputValidators.soloNumeros(txtDocumento, 10);
        InputValidators.alfanumericoConEspacios(txtNombre, 25);
        filtros.add(AppStyles.etiqueta("Documento"), AppStyles.gbc(0, 0, 1));
        filtros.add(txtDocumento, AppStyles.gbc(1, 0, 1));
        filtros.add(AppStyles.etiqueta("Nombre"), AppStyles.gbc(2, 0, 1));
        filtros.add(txtNombre, AppStyles.gbc(3, 0, 1));
        contenido.add(filtros, BorderLayout.NORTH);

        JPanel centro = new JPanel(new java.awt.GridLayout(1, 2, 10, 10));
        centro.setOpaque(false);
        JTable tablaAlquileres = new JTable(modeloAlquileres);
        JTable tablaReservas = new JTable(modeloReservas);
        tablaAlquileres.setAutoCreateRowSorter(true);
        tablaReservas.setAutoCreateRowSorter(true);
        centro.add(AppStyles.scrollTabla(tablaAlquileres));
        centro.add(AppStyles.scrollTabla(tablaReservas));
        contenido.add(centro, BorderLayout.CENTER);

        resumen = new JTextArea("Busca un cliente para ver su resumen comercial.");
        resumen.setEditable(false);
        resumen.setRows(5);
        resumen.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        resumen.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenido.add(new javax.swing.JScrollPane(resumen), BorderLayout.SOUTH);
        add(contenido, BorderLayout.CENTER);
    }

    private void construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnBuscar = AppStyles.botonPrimario("Buscar cliente");
        var btnLimpiar = AppStyles.botonNeutro("Limpiar búsqueda");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnBuscar);
        acciones.add(btnLimpiar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void buscar() {
        String documento = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (documento.isEmpty() && nombre.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Ingresa documento o nombre del cliente.");
            return;
        }
        modeloAlquileres.setRowCount(0);
        modeloReservas.setRowCount(0);

        int alquileres = 0;
        double totalPagado = 0;
        double multas = 0;
        double descuentos = 0;
        for (Alquiler a : SistemaData.alquiler.obtenerPorCliente(documento, nombre)) {
            alquileres++;
            totalPagado += a.getTotalConMultaActual();
            multas += a.getMultaActual();
            descuentos += a.getDescuento();
            modeloAlquileres.addRow(new Object[]{
                a.getPelicula().getTitulo(),
                a.getFechaAlquiler(),
                a.getFechaDevolucion(),
                a.getEstado(),
                "$" + String.format("%,.2f", a.getSubtotalSinDescuento()),
                "$" + String.format("%,.2f", a.getDescuento()),
                "$" + String.format("%,.2f", a.getMultaActual()),
                "$" + String.format("%,.2f", a.getTotalConMultaActual())
            });
        }

        int reservas = 0;
        for (Reserva r : SistemaData.reservas.getLista()) {
            boolean coincideDocumento = !documento.isEmpty() && r.getDocumentoCliente().equalsIgnoreCase(documento);
            boolean coincideNombre = !nombre.isEmpty() && r.getCliente().equalsIgnoreCase(nombre);
            if (coincideDocumento || coincideNombre) {
                reservas++;
                modeloReservas.addRow(new Object[]{
                    r.getPelicula(),
                    r.getFechaReserva(),
                    r.getFechaVencimiento(),
                    r.getEstado(),
                    r.isPrioritaria() ? "Premium" : "Normal"
                });
            }
        }

        Usuario usuario = SistemaData.usuarios.buscarClientePorDocumentoONombre(documento, nombre);
        String nombreVisible = usuario == null ? (nombre.isEmpty() ? "No registrado" : nombre) : usuario.getNombreCompleto();
        String documentoVisible = usuario == null ? (documento.isEmpty() ? "No registrado" : documento) : usuario.getDocumento();
        String nivel = SistemaData.usuarios.obtenerNivelCliente(documentoVisible, nombreVisible);
        resumen.setText("Cliente: " + nombreVisible
                + "\nDocumento: " + documentoVisible
                + "\nNivel comercial: " + nivel
                + "\nAlquileres registrados: " + alquileres
                + "\nReservas registradas: " + reservas
                + "\nDescuentos acumulados: $" + String.format("%,.2f", descuentos)
                + "\nMultas actuales/acumuladas: $" + String.format("%,.2f", multas)
                + "\nTotal pagado/pendiente actual: $" + String.format("%,.2f", totalPagado));
    }

    private void limpiar() {
        txtDocumento.setText("");
        txtNombre.setText("");
        modeloAlquileres.setRowCount(0);
        modeloReservas.setRowCount(0);
        resumen.setText("Busca un cliente para ver su resumen comercial.");
    }
}
