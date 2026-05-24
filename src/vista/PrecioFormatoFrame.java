package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class PrecioFormatoFrame extends JFrame {

    private static final DecimalFormat FORMATO_MONEDA = crearFormatoMoneda();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Formato", "Precio configurado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable tabla;
    private JTextField txtFormato;
    private JTextField txtPrecio;

    public PrecioFormatoFrame() {
        setTitle("Configuración de precios por formato");
        AppStyles.aplicarIcono(this);
        setSize(620, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirContenido();
        cargarTabla();

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Precios por formato"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Configura el precio base de alquiler para Digital, DVD, BluRay y Streaming."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setOpaque(false);

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
        JScrollPane scroll = AppStyles.scrollTabla(tabla);
        contenido.add(scroll, BorderLayout.CENTER);

        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        txtFormato = AppStyles.campoTexto();
        txtFormato.setEditable(false);
        txtPrecio = AppStyles.campoTexto();
        InputValidators.moneda(txtPrecio, 15);

        formulario.add(AppStyles.etiqueta("Formato"), AppStyles.gbc(0, 0, 1));
        formulario.add(AppStyles.etiqueta("Nuevo precio"), AppStyles.gbc(1, 0, 1));
        formulario.add(txtFormato, AppStyles.gbc(0, 1, 1));
        formulario.add(txtPrecio, AppStyles.gbc(1, 1, 1));
        contenido.add(formulario, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnActualizar = AppStyles.botonPrimario("Actualizar precio");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnActualizar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizarPrecio());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Map.Entry<String, Double> entry : SistemaData.preciosFormato.getPrecios().entrySet()) {
            modeloTabla.addRow(new Object[]{
                entry.getKey(),
                "$" + FORMATO_MONEDA.format(entry.getValue())
            });
        }
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        String formato = modeloTabla.getValueAt(filaModelo, 0).toString();
        txtFormato.setText(formato);
        txtPrecio.setText(FORMATO_MONEDA.format(SistemaData.preciosFormato.obtenerPrecio(formato)));
    }

    private void actualizarPrecio() {
        String formato = txtFormato.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        if (formato.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona un formato de la tabla.");
            return;
        }

        try {
            double nuevoPrecio = convertirMonedaADouble(precioTexto);
            SistemaData.preciosFormato.actualizarPrecio(formato, nuevoPrecio);

            if (AppStyles.confirmar(this,
                    "Precio configurado para " + formato + ": $" + FORMATO_MONEDA.format(nuevoPrecio) + "\n\n"
                    + "¿Deseas actualizar también las películas existentes con este formato?",
                    "Aplicar precio por formato")) {
                SistemaData.catalogo.actualizarPrecioPorFormato(formato, nuevoPrecio);
            }

            ArchivoService.guardarTodo();
            cargarTabla();
            AppStyles.mostrarExito(this, "Precio por formato actualizado correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo actualizar");
        }
    }

    private double convertirMonedaADouble(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El precio es obligatorio.");
        }

        String limpio = texto.trim()
                .replace("$", "")
                .replace(" ", "");

        String normalizado = limpio
                .replace(".", "")
                .replace(",", ".");

        try {
            double valor = Double.parseDouble(normalizado);
            if (valor <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a 0.");
            }
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe tener formato de moneda. Ejemplo: 20.000,00");
        }
    }

    private static DecimalFormat crearFormatoMoneda() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "CO"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');

        DecimalFormat formato = new DecimalFormat("#,##0.00", simbolos);
        formato.setParseBigDecimal(false);

        return formato;
    }
}
