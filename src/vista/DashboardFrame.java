package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JFrame;
import servicio.ReporteService;
import servicio.SistemaData;
import util.AppStyles;

public class DashboardFrame extends JFrame {

    private static final DecimalFormat FORMATO_MONEDA = crearFormatoMoneda();
    private final String rol;
    private JPanel tarjetas;

    public DashboardFrame(String rol) {
        this.rol = rol == null ? "empleado" : rol.trim().toLowerCase();
        setTitle("Dashboard - " + AppStyles.APP_NAME);
        AppStyles.aplicarIcono(this);
        setSize(1180, 625);
        setMinimumSize(new java.awt.Dimension(1180, 625));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirTarjetas();
        construirAcciones();

        setVisible(true);
        mostrarAlertasOperativas();
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setOpaque(false);

        JLabel logo = new JLabel(AppStyles.logo(70));
        JPanel textos = new JPanel(new BorderLayout(4, 4));
        textos.setOpaque(false);
        textos.add(AppStyles.titulo("Dashboard inicial"), BorderLayout.NORTH);
        textos.add(AppStyles.subtitulo("Indicadores rápidos para tomar decisiones. Haz clic sobre una tarjeta para ver el detalle."), BorderLayout.CENTER);
        textos.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);

        header.add(logo, BorderLayout.WEST);
        header.add(textos, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    private void construirTarjetas() {
        tarjetas = new JPanel(new GridLayout(2, 2, 14, 14));
        tarjetas.setOpaque(false);
        add(tarjetas, BorderLayout.CENTER);
        refrescarTarjetas();
    }

    private void refrescarTarjetas() {
        tarjetas.removeAll();
        ReporteService reporte = new ReporteService();
        tarjetas.add(crearTarjetaMetrica(
                "Películas disponibles",
                String.valueOf(reporte.peliculasDisponibles()),
                reporte.copiasDisponibles() + " copias listas para alquilar",
                new Color(18, 130, 101),
                DashboardDetalleFrame.TipoDetalle.PELICULAS_DISPONIBLES));
        tarjetas.add(crearTarjetaMetrica(
                "Películas alquiladas",
                String.valueOf(reporte.peliculasAlquiladas()),
                "Pendientes de devolución. Vencidas: " + reporte.alquileresVencidos(),
                new Color(10, 82, 132),
                DashboardDetalleFrame.TipoDetalle.ALQUILERES_PENDIENTES));
        tarjetas.add(crearTarjetaMetrica(
                "Reservas pendientes",
                String.valueOf(reporte.reservasPendientes()),
                "Solicitudes activas de clientes",
                new Color(111, 66, 193),
                DashboardDetalleFrame.TipoDetalle.RESERVAS_PENDIENTES));
        tarjetas.add(crearTarjetaMetrica(
                "Ingresos del mes",
                "$" + FORMATO_MONEDA.format(reporte.ingresosDelMes()),
                reporte.totalAlquileresDelMes() + " alquileres registrados este mes",
                new Color(191, 105, 22),
                DashboardDetalleFrame.TipoDetalle.INGRESOS_MES));
        tarjetas.revalidate();
        tarjetas.repaint();
    }

    private JPanel crearTarjetaMetrica(String titulo, String valor, String detalle, Color color,
            DashboardDetalleFrame.TipoDetalle tipoDetalle) {
        JPanel card = AppStyles.crearTarjetaEstado(color);
        card.setLayout(new BorderLayout(6, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText("Ver detalle de " + titulo.toLowerCase());

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 17));
        lblTitulo.setForeground(AppStyles.PRIMARY_DARK);

        JLabel lblValor = new JLabel(valor, SwingConstants.LEFT);
        lblValor.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 38));
        lblValor.setForeground(color);

        JLabel lblDetalle = AppStyles.subtitulo(detalle + "  |  Clic para abrir detalle");

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblDetalle, BorderLayout.SOUTH);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new DashboardDetalleFrame(tipoDetalle);
            }
        });
        return card;
    }

    private void construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);

        var btnCatalogo = AppStyles.botonPrimario("Catálogo");
        var btnAlquiler = AppStyles.botonSecundario("Alquiler / reservas");
        var btnHistorial = AppStyles.botonSecundario("Historial");
        var btnReportes = AppStyles.botonNeutro("Reportes");
        var btnRefrescar = AppStyles.botonNeutro("Actualizar");
        var btnAlertas = AppStyles.botonAdvertencia("Alertas");
        var btnTop = AppStyles.botonSecundario("Top películas");
        var btnMenu = AppStyles.botonNeutro("Otras opciones");
        var btnSalir = AppStyles.botonPeligro("Cerrar sesión");

        if (rol.equals("admin")) {
            acciones.add(btnCatalogo);
        }
        acciones.add(btnAlquiler);
        acciones.add(btnHistorial);
        acciones.add(btnReportes);
        acciones.add(btnRefrescar);
        acciones.add(btnAlertas);
        acciones.add(btnTop);
        acciones.add(btnMenu);
        acciones.add(btnSalir);
        add(acciones, BorderLayout.SOUTH);

        btnCatalogo.addActionListener(e -> new CatalogoFrame());
        btnAlquiler.addActionListener(e -> new AlquilerFrame());
        btnHistorial.addActionListener(e -> new HistorialFrame());
        btnReportes.addActionListener(e -> new ReporteFrame());
        btnRefrescar.addActionListener(e -> refrescarTarjetas());
        btnAlertas.addActionListener(e -> new AlertasVencimientoFrame());
        btnTop.addActionListener(e -> new TopPeliculasFrame());
        btnMenu.addActionListener(e -> {
            if (rol.equals("admin")) {
                new MenuAdminFrame(true);
            } else {
                new MenuEmpleadoFrame(true);
            }
            dispose();
        });
        btnSalir.addActionListener(e -> cerrarSesion());
    }


    private void mostrarAlertasOperativas() {
        if (!(rol.equals("admin") || rol.equals("empleado"))) {
            return;
        }
        if (!SistemaData.consumirAlertaOperativaSesion()) {
            return;
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            ReporteService reporte = new ReporteService();
            int vencidos = reporte.alquileresVencidos();
            int vencenHoy = reporte.alquileresVencenHoy();
            if (vencidos > 0 || vencenHoy > 0) {
                AppStyles.mostrarAdvertencia(this,
                        "Alertas operativas:\n"
                        + "Películas vencidas: " + vencidos + "\n"
                        + "Películas que vencen hoy: " + vencenHoy + "\n\n"
                        + "Puedes revisar el detalle desde el botón Alertas.");
            }
        });
    }

    private void cerrarSesion() {
        if (!AppStyles.confirmarCerrarSesion(this)) {
            return;
        }
        SistemaData.cerrarSesion();
        new BienvenidaFrame();
        dispose();
    }

    private static DecimalFormat crearFormatoMoneda() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "CO"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", simbolos);
    }
}
