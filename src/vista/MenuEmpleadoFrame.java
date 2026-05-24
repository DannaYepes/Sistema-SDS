package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import servicio.SistemaData;
import util.AppStyles;

public class MenuEmpleadoFrame extends JFrame {

    private final boolean permitirVolverDashboard;

    public MenuEmpleadoFrame() {
        this(false);
    }

    public MenuEmpleadoFrame(boolean permitirVolverDashboard) {
        this.permitirVolverDashboard = permitirVolverDashboard;
        setTitle("Otras opciones - Empleado");
        AppStyles.aplicarIcono(this);
        setSize(560, permitirVolverDashboard ? 420 : 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Otras opciones"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Gestiona alquileres, devoluciones, reservas, alertas, recomendaciones e historial."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel tarjeta = AppStyles.crearTarjeta();
        tarjeta.setLayout(new GridLayout(permitirVolverDashboard ? 6 : 5, 1, 10, 10));
        var alquiler = AppStyles.botonPrimario("Alquiler / devolución / reservas");
        var alertas = AppStyles.botonAdvertencia("Alertas de vencimiento");
        var top = AppStyles.botonSecundario("Top películas");
        var historialCliente = AppStyles.botonSecundario("Historial por cliente");
        var historial = AppStyles.botonNeutro("Historial general");
        var volverDashboard = AppStyles.botonNeutro("Volver al dashboard");
        tarjeta.add(alquiler);
        tarjeta.add(alertas);
        tarjeta.add(top);
        tarjeta.add(historialCliente);
        tarjeta.add(historial);
        if (permitirVolverDashboard) {
            tarjeta.add(volverDashboard);
        }
        add(tarjeta, BorderLayout.CENTER);

        alquiler.addActionListener(e -> new AlquilerFrame());
        alertas.addActionListener(e -> new AlertasVencimientoFrame());
        top.addActionListener(e -> new TopPeliculasFrame());
        historialCliente.addActionListener(e -> new HistorialClienteFrame());
        historial.addActionListener(e -> new HistorialFrame());
        volverDashboard.addActionListener(e -> volverAlDashboard());

        setVisible(true);
    }

    private void volverAlDashboard() {
        if (!permitirVolverDashboard) {
            dispose();
            return;
        }
        new DashboardFrame("empleado");
        dispose();
    }
}
