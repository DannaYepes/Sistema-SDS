package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import modelo.Pelicula;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class CatalogoFrame extends JFrame {

    private static final DecimalFormat FORMATO_MONEDA = crearFormatoMoneda();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Título", "Idioma", "Género", "Año", "Duración", "Cantidad", "Precio", "Formato", "Disponibilidad", "Descripción"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable tabla;
    private JTextField txtTitulo;
    private JTextField txtIdioma;
    private JTextField txtGenero;
    private JTextField txtAnio;
    private JTextField txtDuracion;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTextField txtDescripcion;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFormato;
    private JComboBox<String> cmbFiltro;
    private String tituloSeleccionado;
    private boolean cargandoSeleccion;

    public CatalogoFrame() {
        setTitle("Gestión del Catálogo de Películas");
        AppStyles.aplicarIcono(this);
        setSize(1220, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirContenido();
        cargarTabla(SistemaData.catalogo.getLista());

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Catálogo de películas"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Agrega duración, descripción, formato, precio configurado y disponibilidad antes de alquilar."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setOpaque(false);

        JPanel filtros = AppStyles.crearTarjeta();
        filtros.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        cmbFiltro = AppStyles.combo(new String[]{"Todos", "Título", "Género", "Año", "Disponibles", "No disponibles"});
        txtBuscar = AppStyles.campoTexto();
        txtBuscar.setColumns(24);

        var btnBuscar = AppStyles.botonPrimario("Buscar");
        var btnMostrarTodo = AppStyles.botonNeutro("Mostrar todo");
        var btnDetalle = AppStyles.botonSecundario("Ver detalle");

        filtros.add(AppStyles.etiqueta("Búsqueda:"));
        filtros.add(cmbFiltro);
        filtros.add(txtBuscar);
        filtros.add(btnBuscar);
        filtros.add(btnMostrarTodo);
        filtros.add(btnDetalle);

        btnBuscar.addActionListener(e -> buscar(true));
        btnDetalle.addActionListener(e -> verDetalle());
        configurarBusquedaEnTiempoReal();

        btnMostrarTodo.addActionListener(e -> {
            txtBuscar.setText("");
            cmbFiltro.setSelectedIndex(0);
            cargarTabla(SistemaData.catalogo.getLista());
        });

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });

        JScrollPane scroll = AppStyles.scrollTabla(tabla);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(construirFormulario(), BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);
    }

    private JPanel construirFormulario() {
        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        txtTitulo = AppStyles.campoTexto();
        txtIdioma = AppStyles.campoTexto();
        txtGenero = AppStyles.campoTexto();
        txtAnio = AppStyles.campoTexto();
        txtDuracion = AppStyles.campoTexto();
        txtCantidad = AppStyles.campoTexto();
        txtPrecio = AppStyles.campoTexto();
        txtDescripcion = AppStyles.campoTexto();
        cmbFormato = AppStyles.combo(SistemaData.preciosFormato.obtenerFormatosArray());

        InputValidators.alfanumericoConEspacios(txtTitulo, 40);
        InputValidators.soloLetrasConEspacios(txtIdioma, 15);
        InputValidators.soloLetrasConEspacios(txtGenero, 20);
        InputValidators.soloNumeros(txtAnio, 4);
        InputValidators.soloNumeros(txtDuracion, 3);
        InputValidators.soloNumeros(txtCantidad, 4);
        InputValidators.moneda(txtPrecio, 15);
        InputValidators.textoLibre(txtDescripcion, 180);

        txtPrecio.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                formatearCampoPrecio();
            }
        });

        cmbFormato.addActionListener(e -> actualizarPrecioDesdeFormato());
        actualizarPrecioDesdeFormato();

        formulario.add(AppStyles.etiqueta("Título"), AppStyles.gbc(0, 0, 1));
        formulario.add(AppStyles.etiqueta("Idioma"), AppStyles.gbc(1, 0, 1));
        formulario.add(AppStyles.etiqueta("Género"), AppStyles.gbc(2, 0, 1));
        formulario.add(AppStyles.etiqueta("Año"), AppStyles.gbc(3, 0, 1));
        formulario.add(AppStyles.etiqueta("Duración (min)"), AppStyles.gbc(4, 0, 1));

        formulario.add(txtTitulo, AppStyles.gbc(0, 1, 1));
        formulario.add(txtIdioma, AppStyles.gbc(1, 1, 1));
        formulario.add(txtGenero, AppStyles.gbc(2, 1, 1));
        formulario.add(txtAnio, AppStyles.gbc(3, 1, 1));
        formulario.add(txtDuracion, AppStyles.gbc(4, 1, 1));

        formulario.add(AppStyles.etiqueta("Copias"), AppStyles.gbc(0, 2, 1));
        formulario.add(AppStyles.etiqueta("Formato"), AppStyles.gbc(1, 2, 1));
        formulario.add(AppStyles.etiqueta("Precio"), AppStyles.gbc(2, 2, 1));
        formulario.add(AppStyles.etiqueta("Descripción"), AppStyles.gbc(3, 2, 2));

        formulario.add(txtCantidad, AppStyles.gbc(0, 3, 1));
        formulario.add(cmbFormato, AppStyles.gbc(1, 3, 1));
        formulario.add(txtPrecio, AppStyles.gbc(2, 3, 1));
        formulario.add(txtDescripcion, AppStyles.gbc(3, 3, 2));

        var btnAgregar = AppStyles.botonSecundario("Agregar");
        var btnEditar = AppStyles.botonPrimario("Editar");
        var btnEliminar = AppStyles.botonPeligro("Eliminar");
        var btnLimpiar = AppStyles.botonNeutro("Limpiar");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");

        formulario.add(btnAgregar, AppStyles.gbcBoton(0, 4));
        formulario.add(btnEditar, AppStyles.gbcBoton(1, 4));
        formulario.add(btnEliminar, AppStyles.gbcBoton(2, 4));
        formulario.add(btnLimpiar, AppStyles.gbcBoton(3, 4));
        formulario.add(btnCerrar, AppStyles.gbcBoton(4, 4));

        btnAgregar.addActionListener(e -> agregar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnCerrar.addActionListener(e -> dispose());

        return formulario;
    }

    private void agregar() {
        try {
            SistemaData.catalogo.agregar(leerPeliculaDesdeFormulario());
            ArchivoService.guardarTodo();
            cargarTabla(SistemaData.catalogo.getLista());
            limpiarCampos();
            AppStyles.mostrarExito(this, "Película agregada correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo agregar");
        }
    }

    private void editar() {
        if (tituloSeleccionado == null) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película de la tabla para editarla.");
            return;
        }

        if (!AppStyles.confirmar(this,
                "¿Deseas editar la película \"" + tituloSeleccionado + "\"?",
                "Confirmar edición")) {
            return;
        }

        try {
            boolean editado = SistemaData.catalogo.editar(tituloSeleccionado, leerPeliculaDesdeFormulario());

            if (!editado) {
                AppStyles.mostrarAdvertencia(this, "La película seleccionada ya no existe.");
                return;
            }

            ArchivoService.guardarTodo();
            cargarTabla(SistemaData.catalogo.getLista());
            limpiarCampos();
            AppStyles.mostrarExito(this, "Película editada correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo editar");
        }
    }

    private void eliminar() {
        if (tituloSeleccionado == null) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película de la tabla para eliminarla.");
            return;
        }

        if (!AppStyles.confirmar(this,
                "¿Deseas eliminar la película \"" + tituloSeleccionado + "\"?",
                "Confirmar eliminación")) {
            return;
        }

        try {
            SistemaData.catalogo.eliminar(tituloSeleccionado);
            ArchivoService.guardarTodo();
            cargarTabla(SistemaData.catalogo.getLista());
            limpiarCampos();
            AppStyles.mostrarExito(this, "Película eliminada correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo eliminar");
        }
    }

    private void configurarBusquedaEnTiempoReal() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar(false);
            }
        });
        cmbFiltro.addActionListener(e -> buscar(false));
    }

    private void buscar(boolean mostrarErrores) {
        try {
            String criterio = cmbFiltro.getSelectedItem().toString();
            String texto = txtBuscar.getText().trim();

            if ((criterio.equals("Título") || criterio.equals("Género") || criterio.equals("Año")) && texto.isEmpty()) {
                cargarTabla(SistemaData.catalogo.getLista());
                return;
            }

            cargarTabla(SistemaData.catalogo.filtrar(criterio, texto));
        } catch (NumberFormatException ex) {
            modeloTabla.setRowCount(0);
            if (mostrarErrores) {
                AppStyles.mostrarAdvertencia(this, "El año debe ser numérico.");
            }
        }
    }

    private Pelicula leerPeliculaDesdeFormulario() {
        String titulo = txtTitulo.getText().trim();
        String idioma = txtIdioma.getText().trim();
        String genero = txtGenero.getText().trim();
        String anioTexto = txtAnio.getText().trim();
        String duracionTexto = txtDuracion.getText().trim();
        String cantidadTexto = txtCantidad.getText().trim();
        String precioTexto = txtPrecio.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String formato = cmbFormato.getSelectedItem().toString();

        validarCamposCatalogo(titulo, idioma, genero, anioTexto, duracionTexto, cantidadTexto, precioTexto, descripcion);

        int anio = Integer.parseInt(anioTexto);
        int duracion = Integer.parseInt(duracionTexto);
        int cantidad = Integer.parseInt(cantidadTexto);
        double precio = convertirMonedaADouble(precioTexto);

        return new Pelicula(titulo, idioma, genero, anio, cantidad, precio, formato, duracion, descripcion);
    }

    private void validarCamposCatalogo(
            String titulo,
            String idioma,
            String genero,
            String anioTexto,
            String duracionTexto,
            String cantidadTexto,
            String precioTexto,
            String descripcion
    ) {
        if (!titulo.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]{1,40}")) {
            throw new IllegalArgumentException("El título es obligatorio y no debe superar 40 caracteres.");
        }

        if (!idioma.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]{1,15}")) {
            throw new IllegalArgumentException("El idioma es obligatorio y solo debe contener letras.");
        }

        if (!genero.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]{1,20}")) {
            throw new IllegalArgumentException("El género es obligatorio y solo debe contener letras.");
        }

        if (!anioTexto.matches("\\d{4}")) {
            throw new IllegalArgumentException("El año es obligatorio y debe contener exactamente 4 dígitos numéricos.");
        }

        if (!duracionTexto.matches("\\d{1,3}") || Integer.parseInt(duracionTexto) <= 0) {
            throw new IllegalArgumentException("La duración es obligatoria y debe estar entre 1 y 999 minutos.");
        }

        if (!cantidadTexto.matches("\\d{1,4}")) {
            throw new IllegalArgumentException("El número de copias es obligatorio.");
        }

        if (Integer.parseInt(cantidadTexto) < 0) {
            throw new IllegalArgumentException("El número de copias no puede ser negativo.");
        }

        if (!precioTexto.matches("\\d{1,3}(\\.\\d{3})*(,\\d{2})?|\\d+(,\\d{2})?")) {
            throw new IllegalArgumentException("El precio es obligatorio y debe tener formato de moneda. Ejemplo: 20.000,00");
        }

        if (convertirMonedaADouble(precioTexto) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }

        if (descripcion.isEmpty() || descripcion.length() > 180) {
            throw new IllegalArgumentException("La descripción es obligatoria y no puede superar 180 caracteres.");
        }
    }

    private void cargarTabla(List<Pelicula> peliculas) {
        modeloTabla.setRowCount(0);

        for (Pelicula p : peliculas) {
            modeloTabla.addRow(new Object[]{
                p.getTitulo(),
                p.getIdioma(),
                p.getGenero(),
                p.getAnio(),
                p.getDuracionTexto(),
                p.getCantidad(),
                "$" + FORMATO_MONEDA.format(p.getPrecio()),
                p.getFormato(),
                p.estaDisponible() ? "Disponible" : "No disponible",
                p.getDescripcion()
            });
        }
    }

    private void cargarSeleccionEnFormulario() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        tituloSeleccionado = modeloTabla.getValueAt(filaModelo, 0).toString();

        Pelicula p = SistemaData.catalogo.buscar(tituloSeleccionado);

        if (p == null) {
            return;
        }

        cargandoSeleccion = true;
        txtTitulo.setText(p.getTitulo());
        txtIdioma.setText(p.getIdioma());
        txtGenero.setText(p.getGenero());
        txtAnio.setText(String.valueOf(p.getAnio()));
        txtDuracion.setText(p.getDuracionMinutos() <= 0 ? "" : String.valueOf(p.getDuracionMinutos()));
        txtCantidad.setText(String.valueOf(p.getCantidad()));
        txtPrecio.setText(FORMATO_MONEDA.format(p.getPrecio()));
        txtDescripcion.setText(p.getDescripcion());
        cmbFormato.setSelectedItem(p.getFormato());
        cargandoSeleccion = false;
    }

    private void limpiarCampos() {
        tituloSeleccionado = null;
        tabla.clearSelection();

        txtTitulo.setText("");
        txtIdioma.setText("");
        txtGenero.setText("");
        txtAnio.setText("");
        txtDuracion.setText("");
        txtCantidad.setText("");
        txtDescripcion.setText("");

        cmbFormato.setSelectedIndex(0);
        actualizarPrecioDesdeFormato();
        txtTitulo.requestFocus();
    }

    private void verDetalle() {
        if (tituloSeleccionado == null) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película de la tabla para ver el detalle.");
            return;
        }
        Pelicula pelicula = SistemaData.catalogo.buscar(tituloSeleccionado);
        if (pelicula == null) {
            AppStyles.mostrarAdvertencia(this, "La película seleccionada ya no existe.");
            return;
        }
        new DetallePeliculaFrame(pelicula);
    }

    private void actualizarPrecioDesdeFormato() {
        if (cargandoSeleccion || cmbFormato == null || cmbFormato.getSelectedItem() == null) {
            return;
        }
        String formato = cmbFormato.getSelectedItem().toString();
        double precio = SistemaData.preciosFormato.obtenerPrecio(formato);
        txtPrecio.setText(FORMATO_MONEDA.format(precio));
    }

    private void formatearCampoPrecio() {
        String precioTexto = txtPrecio.getText().trim();

        if (precioTexto.isEmpty()) {
            return;
        }

        try {
            double valor = convertirMonedaADouble(precioTexto);

            if (valor > 0) {
                txtPrecio.setText(FORMATO_MONEDA.format(valor));
            }
        } catch (IllegalArgumentException ex) {
            // La validación final se muestra al agregar o editar.
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
            return Double.parseDouble(normalizado);
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
