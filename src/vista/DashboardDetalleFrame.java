package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.Alquiler;
import modelo.Pelicula;
import modelo.Reserva;
import servicio.ReporteService;
import servicio.SistemaData;
import util.AppStyles;

public class DashboardDetalleFrame extends JFrame {

    public enum TipoDetalle {
        PELICULAS_DISPONIBLES,
        ALQUILERES_PENDIENTES,
        RESERVAS_PENDIENTES,
        INGRESOS_MES
    }

    private final TipoDetalle tipo;
    private JTable tabla;
    private JTextArea areaDetalle;

    public DashboardDetalleFrame(TipoDetalle tipo) {
        this.tipo = tipo == null ? TipoDetalle.PELICULAS_DISPONIBLES : tipo;
        setTitle(tituloVentana());
        AppStyles.aplicarIcono(this);
        setSize(980, 620);
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
        header.add(AppStyles.titulo(tituloVentana()), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Selecciona una fila para ver la información completa del registro."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setOpaque(false);

        DefaultTableModel modelo = crearModelo();
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarDetalleSeleccionado();
            }
        });

        areaDetalle = new JTextArea();
        areaDetalle.setRows(8);
        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);
        areaDetalle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        areaDetalle.setForeground(AppStyles.TEXT);
        areaDetalle.setBackground(java.awt.Color.WHITE);
        areaDetalle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        areaDetalle.setText("Selecciona un registro para ver más información.");

        JScrollPane scrollDetalle = new JScrollPane(areaDetalle);
        scrollDetalle.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 216, 225)));

        contenido.add(AppStyles.scrollTabla(tabla), BorderLayout.CENTER);
        contenido.add(scrollDetalle, BorderLayout.SOUTH);
        add(contenido, BorderLayout.CENTER);

        if (modelo.getRowCount() > 0) {
            tabla.setRowSelectionInterval(0, 0);
        } else {
            areaDetalle.setText("No hay registros para mostrar en este momento.");
        }
    }

    private void construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnActualizar = AppStyles.botonNeutro("Actualizar");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnActualizar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> {
            getContentPane().removeAll();
            construirEncabezado();
            construirContenido();
            construirAcciones();
            revalidate();
            repaint();
        });
        btnCerrar.addActionListener(e -> dispose());
    }

    private DefaultTableModel crearModelo() {
        return switch (tipo) {
            case PELICULAS_DISPONIBLES -> modeloPeliculasDisponibles();
            case ALQUILERES_PENDIENTES -> modeloAlquileresPendientes();
            case RESERVAS_PENDIENTES -> modeloReservasPendientes();
            case INGRESOS_MES -> modeloIngresosMes();
        };
    }

    private DefaultTableModel modeloPeliculasDisponibles() {
        DefaultTableModel modelo = modeloNoEditable(new Object[]{"Título", "Género", "Año", "Idioma", "Formato", "Duración", "Copias", "Precio", "Estado"});
        for (Pelicula p : SistemaData.catalogo.getLista()) {
            if (p.estaDisponible()) {
                modelo.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getAnio(),
                    p.getIdioma(),
                    p.getFormato(),
                    p.getDuracionTexto(),
                    p.getCantidad(),
                    "$" + formato(p.getPrecio()),
                    "Disponible"
                });
            }
        }
        return modelo;
    }

    private DefaultTableModel modeloAlquileresPendientes() {
        DefaultTableModel modelo = modeloNoEditable(new Object[]{"Documento", "Cliente", "Película", "Fecha alquiler", "Fecha devolución", "Subtotal", "Multa", "Total", "Estado"});
        for (Alquiler a : SistemaData.alquiler.obtenerPendientes()) {
            modelo.addRow(new Object[]{
                a.getCliente().getDocumento(),
                a.getCliente().getNombre(),
                a.getPelicula().getTitulo(),
                a.getFechaAlquiler(),
                a.getFechaDevolucion(),
                "$" + formato(a.getTotal()),
                "$" + formato(a.getMultaActual()),
                "$" + formato(a.getTotalConMultaActual()),
                a.getEstado()
            });
        }
        return modelo;
    }

    private DefaultTableModel modeloReservasPendientes() {
        DefaultTableModel modelo = modeloNoEditable(new Object[]{"Documento", "Cliente", "Película", "Fecha reserva", "Estado", "Registró", "Rol"});
        for (Reserva r : SistemaData.reservas.obtenerPendientes()) {
            modelo.addRow(new Object[]{
                r.getDocumentoCliente(),
                r.getCliente(),
                r.getPelicula(),
                r.getFechaReserva(),
                r.getEstado(),
                r.getUsuarioRegistro(),
                r.getRolRegistro()
            });
        }
        return modelo;
    }

    private DefaultTableModel modeloIngresosMes() {
        DefaultTableModel modelo = modeloNoEditable(new Object[]{"Fecha", "Cliente", "Película", "Método pago", "Subtotal", "Multa", "Total", "Estado"});
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            LocalDate fecha = a.getFechaAlquiler();
            if (fecha != null && fecha.getMonth() == hoy.getMonth() && fecha.getYear() == hoy.getYear()) {
                modelo.addRow(new Object[]{
                    fecha,
                    a.getCliente().getNombre(),
                    a.getPelicula().getTitulo(),
                    a.getPago().getMetodo(),
                    "$" + formato(a.getTotal()),
                    "$" + formato(a.getMultaActual()),
                    "$" + formato(a.getTotalConMultaActual()),
                    a.getEstado()
                });
            }
        }
        return modelo;
    }

    private DefaultTableModel modeloNoEditable(Object[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void actualizarDetalleSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            areaDetalle.setText("Selecciona un registro para ver más información.");
            return;
        }
        int fila = tabla.convertRowIndexToModel(filaVista);
        areaDetalle.setText(switch (tipo) {
            case PELICULAS_DISPONIBLES -> detallePelicula(fila);
            case ALQUILERES_PENDIENTES -> detalleAlquiler(fila);
            case RESERVAS_PENDIENTES -> detalleReserva(fila);
            case INGRESOS_MES -> detalleIngreso(fila);
        });
        areaDetalle.setCaretPosition(0);
    }

    private String detallePelicula(int fila) {
        String titulo = valor(fila, 0);
        Pelicula pelicula = SistemaData.catalogo.buscar(titulo);
        if (pelicula == null) {
            return "No se encontró la película seleccionada.";
        }
        return "Película: " + pelicula.getTitulo()
                + "\nGénero: " + pelicula.getGenero()
                + "\nIdioma: " + pelicula.getIdioma()
                + "\nAño: " + pelicula.getAnio()
                + "\nFormato: " + pelicula.getFormato()
                + "\nDuración: " + pelicula.getDuracionTexto()
                + "\nCopias disponibles: " + pelicula.getCantidad()
                + "\nPrecio por día: $" + formato(pelicula.getPrecio())
                + "\nEstado: " + (pelicula.estaDisponible() ? "Disponible" : "No disponible")
                + "\nDescripción: " + pelicula.getDescripcion();
    }

    private String detalleAlquiler(int fila) {
        String documento = valor(fila, 0);
        String cliente = valor(fila, 1);
        String titulo = valor(fila, 2);
        Alquiler alquiler = SistemaData.alquiler.buscarPendiente(documento, cliente, titulo);
        if (alquiler == null) {
            return "No se encontró el alquiler pendiente seleccionado.";
        }
        return "Cliente: " + alquiler.getCliente().getNombre()
                + "\nDocumento: " + alquiler.getCliente().getDocumento()
                + "\nPelícula: " + alquiler.getPelicula().getTitulo()
                + "\nFecha de alquiler: " + alquiler.getFechaAlquiler()
                + "\nFecha de devolución: " + alquiler.getFechaDevolucion()
                + "\nDías de retraso: " + alquiler.getDiasRetrasoActual()
                + "\nMétodo de pago: " + alquiler.getPago().getMetodo()
                + "\nComprobante: " + alquiler.getPago().getId()
                + "\nSubtotal: $" + formato(alquiler.getTotal())
                + "\nMulta actual: $" + formato(alquiler.getMultaActual())
                + "\nTotal actual: $" + formato(alquiler.getTotalConMultaActual())
                + "\nEstado: " + alquiler.getEstado()
                + "\nRegistrado por: " + alquiler.getUsuarioRegistro() + " (" + alquiler.getRolRegistro() + ")";
    }

    private String detalleReserva(int fila) {
        String documento = valor(fila, 0);
        String cliente = valor(fila, 1);
        String titulo = valor(fila, 2);
        Reserva reserva = SistemaData.reservas.buscarPendiente(documento, cliente, titulo);
        if (reserva == null) {
            return "No se encontró la reserva pendiente seleccionada.";
        }
        Pelicula pelicula = SistemaData.catalogo.buscar(reserva.getPelicula());
        return "Cliente: " + reserva.getCliente()
                + "\nDocumento: " + reserva.getDocumentoCliente()
                + "\nPelícula reservada: " + reserva.getPelicula()
                + "\nFecha de reserva: " + reserva.getFechaReserva()
                + "\nEstado: " + reserva.getEstado()
                + "\nDisponibilidad actual: " + (pelicula != null && pelicula.estaDisponible() ? "Disponible" : "No disponible")
                + "\nCopias disponibles: " + (pelicula == null ? "Película no encontrada" : pelicula.getCantidad())
                + "\nRegistrado por: " + reserva.getUsuarioRegistro() + " (" + reserva.getRolRegistro() + ")";
    }

    private String detalleIngreso(int fila) {
        String cliente = valor(fila, 1);
        String titulo = valor(fila, 2);
        for (Alquiler alquiler : SistemaData.alquiler.getLista()) {
            if (alquiler.getCliente().getNombre().equalsIgnoreCase(cliente)
                    && alquiler.getPelicula().getTitulo().equalsIgnoreCase(titulo)
                    && String.valueOf(alquiler.getFechaAlquiler()).equals(valor(fila, 0))) {
                return "Cliente: " + alquiler.getCliente().getNombre()
                        + "\nDocumento: " + alquiler.getCliente().getDocumento()
                        + "\nPelícula: " + alquiler.getPelicula().getTitulo()
                        + "\nFecha: " + alquiler.getFechaAlquiler()
                        + "\nMétodo de pago: " + alquiler.getPago().getMetodo()
                        + "\nComprobante: " + alquiler.getPago().getId()
                        + "\nSubtotal: $" + formato(alquiler.getTotal())
                        + "\nMulta: $" + formato(alquiler.getMultaActual())
                        + "\nTotal: $" + formato(alquiler.getTotalConMultaActual())
                        + "\nEstado: " + alquiler.getEstado()
                        + "\nRegistrado por: " + alquiler.getUsuarioRegistro() + " (" + alquiler.getRolRegistro() + ")";
            }
        }
        return "No se encontró el detalle del ingreso seleccionado.";
    }

    private String valor(int fila, int columna) {
        Object value = tabla.getModel().getValueAt(fila, columna);
        return value == null ? "" : value.toString();
    }

    private String tituloVentana() {
        return switch (tipo) {
            case PELICULAS_DISPONIBLES -> "Detalle de películas disponibles";
            case ALQUILERES_PENDIENTES -> "Detalle de películas alquiladas";
            case RESERVAS_PENDIENTES -> "Detalle de reservas pendientes";
            case INGRESOS_MES -> "Detalle de ingresos del mes";
        };
    }

    private String formato(double valor) {
        return new ReporteService().formato(valor);
    }
}
