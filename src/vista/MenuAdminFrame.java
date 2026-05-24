package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import servicio.SistemaData;
import util.AppStyles;

public class MenuAdminFrame extends JFrame {

    private final boolean permitirVolverDashboard;

    public MenuAdminFrame() {
        this(false);
    }

    public MenuAdminFrame(boolean permitirVolverDashboard) {
        this.permitirVolverDashboard = permitirVolverDashboard;
        setTitle("Otras opciones - Administrador");
        AppStyles.aplicarIcono(this);
        setSize(620, permitirVolverDashboard ? 660 : 610);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Otras opciones"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Administra catálogo, usuarios, reglas de negocio, reportes, alertas e historial del sistema."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel tarjeta = AppStyles.crearTarjeta();
        tarjeta.setLayout(new GridLayout(permitirVolverDashboard ? 11 : 10, 1, 10, 10));

        var catalogo = AppStyles.botonPrimario("Gestionar catálogo");
        var administrarUsuarios = AppStyles.botonPrimario("Administrar usuarios");
        var crearEmpleado = AppStyles.botonAdvertencia("Crear usuario empleado");
        var crearAdmin = AppStyles.botonAdvertencia("Crear usuario administrador");
        var reglas = AppStyles.botonSecundario("Reglas del negocio");
        var preciosFormato = AppStyles.botonSecundario("Configurar precios por formato");
        var alertas = AppStyles.botonAdvertencia("Alertas de vencimiento");
        var top = AppStyles.botonSecundario("Top películas");
        var historialCliente = AppStyles.botonSecundario("Historial por cliente");
        var reportes = AppStyles.botonNeutro("Ver reportes");
        var volverDashboard = AppStyles.botonNeutro("Volver al dashboard");

        tarjeta.add(catalogo);
        tarjeta.add(administrarUsuarios);
        tarjeta.add(crearEmpleado);
        tarjeta.add(crearAdmin);
        tarjeta.add(reglas);
        tarjeta.add(preciosFormato);
        tarjeta.add(alertas);
        tarjeta.add(top);
        tarjeta.add(historialCliente);
        tarjeta.add(reportes);
        if (permitirVolverDashboard) {
            tarjeta.add(volverDashboard);
        }
        add(tarjeta, BorderLayout.CENTER);

        catalogo.addActionListener(e -> new CatalogoFrame());
        administrarUsuarios.addActionListener(e -> new AdministrarUsuariosFrame());
        crearEmpleado.addActionListener(e -> new RegistroUsuarioFrame("empleado", false));
        crearAdmin.addActionListener(e -> new RegistroUsuarioFrame("admin", false));
        reglas.addActionListener(e -> new ConfiguracionNegocioFrame());
        preciosFormato.addActionListener(e -> new PrecioFormatoFrame());
        alertas.addActionListener(e -> new AlertasVencimientoFrame());
        top.addActionListener(e -> new TopPeliculasFrame());
        historialCliente.addActionListener(e -> new HistorialClienteFrame());
        reportes.addActionListener(e -> new ReporteFrame());
        volverDashboard.addActionListener(e -> volverAlDashboard());

        setVisible(true);
    }

    private void volverAlDashboard() {
        if (!permitirVolverDashboard) {
            dispose();
            return;
        }
        new DashboardFrame("admin");
        dispose();
    }
}
