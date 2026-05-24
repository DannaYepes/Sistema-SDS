package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import modelo.Alquiler;
import modelo.Cliente;
import modelo.Pelicula;
import modelo.Reserva;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class AlquilerFrame extends JFrame {

    private final DefaultTableModel modeloCatalogo = new DefaultTableModel(
            new Object[]{"Título", "Género", "Año", "Duración", "Formato", "Disponibles", "Precio", "Disponibilidad"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloPendientes = new DefaultTableModel(
            new Object[]{"Documento", "Cliente", "Película", "Fecha alquiler", "Fecha devolución", "Subtotal", "Multa actual", "Total actual", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloReservas = new DefaultTableModel(
            new Object[]{"Documento", "Cliente", "Película", "Fecha reserva", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable tablaCatalogo;
    private JTable tablaPendientes;
    private JTable tablaReservas;
    private JTextField txtDocumento;
    private JTextField txtCliente;
    private JTextField txtPelicula;
    private JTextField txtDias;
    private JTextField txtBuscarCatalogo;
    private JComboBox<String> cmbFiltroCatalogo;
    private JComboBox<String> cmbMetodoPago;
    private boolean modoCliente;
    private Cliente clienteInicial;

    public AlquilerFrame() {
        this((Cliente) null);
    }

    public AlquilerFrame(String clienteInicial) {
        this(crearClienteInicial(clienteInicial));
    }

    public AlquilerFrame(Cliente clienteInicial) {
        this.modoCliente = SistemaData.esClienteActual();
        this.clienteInicial = resolverClienteInicial(clienteInicial);
        setTitle("Alquiler, Devolución y Reservas de Películas");
        AppStyles.aplicarIcono(this);
        setSize(1160, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();

        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setOpaque(false);
        contenido.add(construirFormulario(), BorderLayout.NORTH);
        contenido.add(construirTablas(), BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        construirBotones();
        refrescarTablas();

        setVisible(true);
    }

    private static Cliente crearClienteInicial(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            return null;
        }
        return new Cliente("", nombreCliente.trim(), "");
    }

    private Cliente resolverClienteInicial(Cliente cliente) {
        if (cliente != null) {
            return cliente;
        }
        if (modoCliente && SistemaData.usuarioActual != null) {
            return new Cliente(
                    SistemaData.usuarioActual.getDocumento(),
                    SistemaData.usuarioActual.getNombreCompleto(),
                    SistemaData.usuarioActual.getCelular()
            );
        }
        return null;
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Alquiler / devolución / reservas"), BorderLayout.NORTH);
        String subtitulo = modoCliente
                ? "Selecciona una película, valida disponibilidad, alquila o reserva desde tu usuario seguro."
                : "Registra alquileres con documento del cliente, atiende reservas y controla multas antes de la devolución.";
        header.add(AppStyles.subtitulo(subtitulo), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private JPanel construirFormulario() {
        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        txtDocumento = AppStyles.campoTexto(clienteInicial == null ? "" : clienteInicial.getDocumento());
        txtCliente = AppStyles.campoTexto(clienteInicial == null ? "" : clienteInicial.getNombre());
        txtPelicula = AppStyles.campoTexto();
        txtPelicula.setEditable(false);
        txtDias = AppStyles.campoTexto("1");
        InputValidators.soloNumeros(txtDocumento, 10);
        InputValidators.alfanumericoConEspacios(txtCliente, 25);
        InputValidators.soloNumeros(txtDias, 2);
        cmbMetodoPago = AppStyles.combo(new String[]{"Efectivo", "Transferencia", "Tarjeta", "Nequi", "Daviplata"});

        if (modoCliente) {
            txtDocumento.setEditable(false);
            txtCliente.setEditable(false);
        }

        formulario.add(AppStyles.etiqueta("Documento cliente"), AppStyles.gbc(0, 0, 1));
        formulario.add(AppStyles.etiqueta("Cliente"), AppStyles.gbc(1, 0, 1));
        formulario.add(AppStyles.etiqueta("Película seleccionada"), AppStyles.gbc(2, 0, 1));
        formulario.add(AppStyles.etiqueta("Días"), AppStyles.gbc(3, 0, 1));
        formulario.add(AppStyles.etiqueta("Método de pago"), AppStyles.gbc(4, 0, 1));

        formulario.add(txtDocumento, AppStyles.gbc(0, 1, 1));
        formulario.add(txtCliente, AppStyles.gbc(1, 1, 1));
        formulario.add(txtPelicula, AppStyles.gbc(2, 1, 1));
        formulario.add(txtDias, AppStyles.gbc(3, 1, 1));
        formulario.add(cmbMetodoPago, AppStyles.gbc(4, 1, 1));

        return formulario;
    }

    private JTabbedPane construirTablas() {
        JTabbedPane tabs = new JTabbedPane();

        tablaCatalogo = new JTable(modeloCatalogo);
        tablaCatalogo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCatalogo.setAutoCreateRowSorter(true);
        tablaCatalogo.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPeliculaDesdeCatalogo();
            }
        });

        tablaPendientes = new JTable(modeloPendientes);
        tablaPendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPendientes.setAutoCreateRowSorter(true);
        tablaPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarAlquilerPendiente();
            }
        });

        tablaReservas = new JTable(modeloReservas);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReservas.setAutoCreateRowSorter(true);
        tablaReservas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarReservaPendiente();
            }
        });

        JScrollPane scrollCatalogo = AppStyles.scrollTabla(tablaCatalogo);
        JScrollPane scrollPendientes = AppStyles.scrollTabla(tablaPendientes);
        JScrollPane scrollReservas = AppStyles.scrollTabla(tablaReservas);

        JPanel panelCatalogo = new JPanel(new BorderLayout(0, 8));
        panelCatalogo.setOpaque(false);
        panelCatalogo.add(construirFiltrosCatalogo(), BorderLayout.NORTH);
        panelCatalogo.add(scrollCatalogo, BorderLayout.CENTER);

        tabs.add("Catálogo", panelCatalogo);
        tabs.add(modoCliente ? "Mis alquileres pendientes" : "Alquileres pendientes", scrollPendientes);
        tabs.add(modoCliente ? "Mis reservas pendientes" : "Reservas pendientes", scrollReservas);
        return tabs;
    }

    private JPanel construirFiltrosCatalogo() {
        JPanel filtros = AppStyles.crearTarjeta();
        filtros.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        cmbFiltroCatalogo = AppStyles.combo(new String[]{"Todos", "Título", "Género", "Año", "Disponibles", "No disponibles"});
        txtBuscarCatalogo = AppStyles.campoTexto();
        txtBuscarCatalogo.setColumns(26);
        var btnLimpiar = AppStyles.botonNeutro("Limpiar búsqueda");
        filtros.add(AppStyles.etiqueta("Búsqueda:"));
        filtros.add(cmbFiltroCatalogo);
        filtros.add(txtBuscarCatalogo);
        filtros.add(btnLimpiar);
        configurarBusquedaCatalogoTiempoReal();
        btnLimpiar.addActionListener(e -> {
            txtBuscarCatalogo.setText("");
            cmbFiltroCatalogo.setSelectedIndex(0);
            filtrarCatalogo();
        });
        return filtros;
    }

    private void configurarBusquedaCatalogoTiempoReal() {
        txtBuscarCatalogo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarCatalogo();
            }
        });
        cmbFiltroCatalogo.addActionListener(e -> filtrarCatalogo());
    }

    private void filtrarCatalogo() {
        if (txtBuscarCatalogo == null || cmbFiltroCatalogo == null) {
            actualizarModeloCatalogo(SistemaData.catalogo.getLista());
            return;
        }
        try {
            String criterio = cmbFiltroCatalogo.getSelectedItem().toString();
            String texto = txtBuscarCatalogo.getText().trim();
            if ((criterio.equals("Título") || criterio.equals("Género") || criterio.equals("Año")) && texto.isEmpty()) {
                actualizarModeloCatalogo(SistemaData.catalogo.getLista());
                return;
            }
            actualizarModeloCatalogo(SistemaData.catalogo.filtrar(criterio, texto));
        } catch (NumberFormatException ex) {
            modeloCatalogo.setRowCount(0);
        }
    }

    private void actualizarModeloCatalogo(java.util.List<Pelicula> peliculas) {
        modeloCatalogo.setRowCount(0);
        for (Pelicula p : peliculas) {
            modeloCatalogo.addRow(new Object[]{
                p.getTitulo(),
                p.getGenero(),
                p.getAnio(),
                p.getDuracionTexto(),
                p.getFormato(),
                p.getCantidad(),
                "$" + String.format("%,.2f", p.getPrecio()),
                p.estaDisponible() ? "Disponible" : "No disponible"
            });
        }
    }

    private void construirBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        botones.setOpaque(false);

        var btnAlquilar = AppStyles.botonSecundario("Alquilar");
        var btnReservar = AppStyles.botonAdvertencia("Reservar");
        var btnAtenderReserva = AppStyles.botonPrimario("Atender reserva");
        var btnDevolver = AppStyles.botonPrimario("Registrar devolución");
        var btnDetalle = AppStyles.botonNeutro("Ver detalle");
        var btnRecomendaciones = AppStyles.botonSecundario("Recomendaciones");
        var btnFactura = AppStyles.botonNeutro("Ver factura");
        var btnRefrescar = AppStyles.botonNeutro("Actualizar tablas");
        var btnCerrar = modoCliente ? AppStyles.botonPeligro("Cerrar sesión") : AppStyles.botonNeutro("Cerrar");

        botones.add(btnAlquilar);
        botones.add(btnReservar);
        if (!modoCliente) {
            botones.add(btnAtenderReserva);
            botones.add(btnDevolver);
        }
        botones.add(btnDetalle);
        botones.add(btnRecomendaciones);
        if (!modoCliente) {
            botones.add(btnFactura);
        }
        botones.add(btnRefrescar);
        botones.add(btnCerrar);

        btnAlquilar.addActionListener(e -> alquilar());
        btnReservar.addActionListener(e -> reservar());
        if (!modoCliente) {
            btnAtenderReserva.addActionListener(e -> atenderReserva());
            btnDevolver.addActionListener(e -> devolver());
        }
        btnDetalle.addActionListener(e -> verDetalle());
        btnRecomendaciones.addActionListener(e -> verRecomendaciones());
        btnFactura.addActionListener(e -> verFactura());
        btnRefrescar.addActionListener(e -> refrescarTablas());
        btnCerrar.addActionListener(e -> cerrarVentana());

        add(botones, BorderLayout.SOUTH);
    }

    private void alquilar() {
        String titulo = txtPelicula.getText().trim();
        if (titulo.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película del catálogo.");
            return;
        }
        try {
            Cliente cliente = construirClienteDesdeFormulario();
            int dias = leerDias();
            Pelicula pelicula = SistemaData.catalogo.buscar(titulo);
            if (pelicula == null) {
                AppStyles.mostrarAdvertencia(this, "La película seleccionada no existe en el catálogo.");
                return;
            }
            Alquiler alquiler = SistemaData.alquiler.alquilar(
                    cliente,
                    pelicula,
                    dias,
                    cmbMetodoPago.getSelectedItem().toString()
            );
            ArchivoService.guardarTodo();
            refrescarTablas();
            AppStyles.mostrarExito(this,
                    "Alquiler realizado correctamente.\nSubtotal: $" + String.format("%,.2f", alquiler.getSubtotalSinDescuento())
                    + "\nDescuento: $" + String.format("%,.2f", alquiler.getDescuento())
                    + "\nTotal a pagar: $" + String.format("%,.2f", alquiler.getTotal())
                    + "\nBeneficio: " + alquiler.getBeneficioAplicado()
                    + "\nLa reserva pendiente de este cliente y película, si existía, quedó atendida.");
            if (!modoCliente) {
                new FacturaFrame(cliente);
            }
        } catch (NumberFormatException ex) {
            AppStyles.mostrarAdvertencia(this, "Los días deben ser un número entero válido.");
        } catch (IllegalStateException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no hay copias")) {
                if (AppStyles.confirmar(this,
                        ex.getMessage() + "\n¿Deseas registrar una reserva?",
                        "Sin disponibilidad")) {
                    reservar();
                }
            } else {
                AppStyles.mostrarAdvertencia(this, ex.getMessage());
            }
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo alquilar");
        }
    }

    private Cliente construirClienteDesdeFormulario() {
        String documento = txtDocumento.getText().trim();
        String nombre = txtCliente.getText().trim();

        if (!InputValidators.documentoValido(documento)) {
            throw new IllegalArgumentException("El documento del cliente es obligatorio y debe tener entre 6 y 10 dígitos numéricos.");
        }
        if (!InputValidators.nombreClienteValido(nombre)) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio, alfanumérico y de máximo 25 caracteres.");
        }

        String celular = "";
        if (modoCliente && SistemaData.usuarioActual != null) {
            celular = SistemaData.usuarioActual.getCelular();
        }
        return new Cliente(documento, nombre, celular);
    }

    private Cliente construirClienteDesdeTabla(String documento, String nombre) {
        return new Cliente(documento, nombre, "");
    }

    private void reservar() {
        String titulo = txtPelicula.getText().trim();
        if (titulo.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película para reservar.");
            return;
        }
        try {
            Cliente cliente = construirClienteDesdeFormulario();
            Pelicula pelicula = SistemaData.catalogo.buscar(titulo);
            if (pelicula == null) {
                AppStyles.mostrarAdvertencia(this, "La película seleccionada no existe en el catálogo.");
                return;
            }
            if (pelicula.estaDisponible()) {
                AppStyles.mostrarInfo(this,
                        "La película todavía tiene copias disponibles. Puedes alquilarla directamente.",
                        "Reserva no necesaria");
                return;
            }
            SistemaData.reservas.reservar(cliente, titulo);
            ArchivoService.guardarTodo();
            refrescarTablas();
            AppStyles.mostrarExito(this, "Reserva registrada correctamente para " + cliente.getIdentificacionVisible()
                    + ".\nEstado: " + SistemaData.reservas.buscarPendiente(cliente, titulo).getEstado()
                    + "\nVence automáticamente en " + SistemaData.configuracion.getHorasVencimientoReserva() + " horas si no es atendida.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo reservar");
        }
    }

    private void atenderReserva() {
        if (modoCliente) {
            AppStyles.mostrarAdvertencia(this, "Esta acción solo está permitida para empleados o administradores.");
            return;
        }
        int filaVista = tablaReservas.getSelectedRow();
        if (filaVista < 0) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una reserva pendiente en la tabla de reservas.");
            return;
        }
        int filaModelo = tablaReservas.convertRowIndexToModel(filaVista);
        String documento = modeloReservas.getValueAt(filaModelo, 0).toString();
        String nombre = modeloReservas.getValueAt(filaModelo, 1).toString();
        String titulo = modeloReservas.getValueAt(filaModelo, 2).toString();
        Pelicula pelicula = SistemaData.catalogo.buscar(titulo);

        if (pelicula == null) {
            AppStyles.mostrarAdvertencia(this, "La película reservada ya no existe en el catálogo.");
            return;
        }
        if (!pelicula.estaDisponible()) {
            AppStyles.mostrarInfo(this,
                    "La reserva sigue pendiente porque todavía no hay copias disponibles.",
                    "Sin copias disponibles");
            return;
        }

        if (!AppStyles.confirmar(this,
                "Hay copias disponibles para \"" + titulo + "\".\n"
                + "¿Deseas registrar el alquiler y marcar la reserva como atendida?",
                "Atender reserva")) {
            return;
        }

        try {
            Cliente cliente = construirClienteDesdeTabla(documento, nombre);
            int dias = leerDias();
            Alquiler alquiler = SistemaData.alquiler.alquilar(
                    cliente,
                    pelicula,
                    dias,
                    cmbMetodoPago.getSelectedItem().toString()
            );
            ArchivoService.guardarTodo();
            refrescarTablas();
            AppStyles.mostrarExito(this,
                    "Reserva atendida y alquiler registrado correctamente.\nSubtotal: $"
                    + String.format("%,.2f", alquiler.getSubtotalSinDescuento())
                    + "\nDescuento: $" + String.format("%,.2f", alquiler.getDescuento())
                    + "\nTotal a pagar: $" + String.format("%,.2f", alquiler.getTotal())
                    + "\nBeneficio: " + alquiler.getBeneficioAplicado());
            if (!modoCliente) {
                new FacturaFrame(cliente);
            }
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo atender la reserva");
        }
    }

    private void devolver() {
        if (modoCliente) {
            AppStyles.mostrarAdvertencia(this, "Esta acción solo está permitida para empleados o administradores.");
            return;
        }
        int filaVista = tablaPendientes.getSelectedRow();
        if (filaVista < 0) {
            AppStyles.mostrarAdvertencia(this, "Selecciona un alquiler pendiente en la tabla de devoluciones.");
            return;
        }
        int filaModelo = tablaPendientes.convertRowIndexToModel(filaVista);
        String documento = modeloPendientes.getValueAt(filaModelo, 0).toString();
        String cliente = modeloPendientes.getValueAt(filaModelo, 1).toString();
        String pelicula = modeloPendientes.getValueAt(filaModelo, 2).toString();
        Alquiler alquiler = SistemaData.alquiler.buscarPendiente(documento, cliente, pelicula);

        if (alquiler == null) {
            AppStyles.mostrarAdvertencia(this, "No se encontró ese alquiler pendiente.");
            return;
        }

        double multaActual = alquiler.getMultaActual();
        if (!AppStyles.confirmar(this,
                "Multa actual por retraso: $" + String.format("%,.2f", multaActual) + "\n"
                + "Total actualizado: $" + String.format("%,.2f", alquiler.getTotalConMultaActual()) + "\n\n"
                + "¿Registrar la devolución?",
                "Confirmar devolución")) {
            return;
        }

        boolean devuelto = SistemaData.alquiler.devolver(documento, cliente, pelicula);
        if (!devuelto) {
            AppStyles.mostrarAdvertencia(this, "No se encontró ese alquiler pendiente.");
            return;
        }
        ArchivoService.guardarTodo();
        refrescarTablas();
        AppStyles.mostrarExito(this, "Devolución registrada correctamente.");
        if (!modoCliente) {
            new FacturaFrame(documento, cliente);
        }
    }

    private int leerDias() {
        String texto = txtDias.getText().trim();
        if (texto.isEmpty()) {
            throw new NumberFormatException("Días vacío");
        }
        int dias = Integer.parseInt(texto);
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días de alquiler deben ser mayores a 0.");
        }
        return dias;
    }

    private void verFactura() {
        String documento = txtDocumento.getText().trim();
        String cliente = txtCliente.getText().trim();
        if ((documento.isEmpty() || cliente.isEmpty()) && tablaPendientes.getSelectedRow() >= 0) {
            int filaModelo = tablaPendientes.convertRowIndexToModel(tablaPendientes.getSelectedRow());
            documento = modeloPendientes.getValueAt(filaModelo, 0).toString();
            cliente = modeloPendientes.getValueAt(filaModelo, 1).toString();
        }
        if (documento.isEmpty() && cliente.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Ingresa o selecciona un cliente para ver su factura.");
            return;
        }
        new FacturaFrame(documento, cliente);
    }

    private void verDetalle() {
        String titulo = txtPelicula.getText().trim();
        if (titulo.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película del catálogo, un alquiler o una reserva.");
            return;
        }
        Pelicula pelicula = SistemaData.catalogo.buscar(titulo);
        if (pelicula == null) {
            AppStyles.mostrarAdvertencia(this, "La película seleccionada ya no existe.");
            return;
        }
        new DetallePeliculaFrame(pelicula);
    }


    private void verRecomendaciones() {
        String titulo = txtPelicula.getText().trim();
        if (titulo.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona una película del catálogo para ver recomendaciones similares.");
            return;
        }
        Pelicula pelicula = SistemaData.catalogo.buscar(titulo);
        if (pelicula == null) {
            AppStyles.mostrarAdvertencia(this, "La película seleccionada ya no existe.");
            return;
        }
        new RecomendacionesFrame(pelicula);
    }

    private void seleccionarPeliculaDesdeCatalogo() {
        int filaVista = tablaCatalogo.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tablaCatalogo.convertRowIndexToModel(filaVista);
        txtPelicula.setText(modeloCatalogo.getValueAt(filaModelo, 0).toString());
    }

    private void seleccionarAlquilerPendiente() {
        int filaVista = tablaPendientes.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tablaPendientes.convertRowIndexToModel(filaVista);
        txtDocumento.setText(modeloPendientes.getValueAt(filaModelo, 0).toString());
        txtCliente.setText(modeloPendientes.getValueAt(filaModelo, 1).toString());
        txtPelicula.setText(modeloPendientes.getValueAt(filaModelo, 2).toString());
    }

    private void seleccionarReservaPendiente() {
        int filaVista = tablaReservas.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int filaModelo = tablaReservas.convertRowIndexToModel(filaVista);
        txtDocumento.setText(modeloReservas.getValueAt(filaModelo, 0).toString());
        txtCliente.setText(modeloReservas.getValueAt(filaModelo, 1).toString());
        txtPelicula.setText(modeloReservas.getValueAt(filaModelo, 2).toString());
    }

    private void refrescarTablas() {
        filtrarCatalogo();

        modeloPendientes.setRowCount(0);
        for (Alquiler a : obtenerPendientesSegunRol()) {
            modeloPendientes.addRow(new Object[]{
                a.getCliente().getDocumento(),
                a.getCliente().getNombre(),
                a.getPelicula().getTitulo(),
                a.getFechaAlquiler(),
                a.getFechaDevolucion(),
                "$" + String.format("%,.2f", a.getTotal()),
                "$" + String.format("%,.2f", a.getMultaActual()),
                "$" + String.format("%,.2f", a.getTotalConMultaActual()),
                a.getEstado()
            });
        }

        modeloReservas.setRowCount(0);
        for (Reserva r : obtenerReservasSegunRol()) {
            modeloReservas.addRow(new Object[]{
                r.getDocumentoCliente(),
                r.getCliente(),
                r.getPelicula(),
                r.getFechaReserva(),
                r.getEstado()
            });
        }
    }

    private java.util.ArrayList<Alquiler> obtenerPendientesSegunRol() {
        if (modoCliente && clienteInicial != null) {
            return SistemaData.alquiler.obtenerPendientesPorCliente(clienteInicial.getDocumento(), clienteInicial.getNombre());
        }
        return SistemaData.alquiler.obtenerPendientes();
    }

    private java.util.ArrayList<Reserva> obtenerReservasSegunRol() {
        if (modoCliente && clienteInicial != null) {
            return SistemaData.reservas.obtenerPendientesPorCliente(clienteInicial.getDocumento(), clienteInicial.getNombre());
        }
        return SistemaData.reservas.obtenerPendientes();
    }

    private void cerrarVentana() {
        if (modoCliente) {
            if (!AppStyles.confirmarCerrarSesion(this)) {
                return;
            }
            SistemaData.cerrarSesion();
            new BienvenidaFrame();
            dispose();
            return;
        }
        dispose();
    }
}
