package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;

public class BienvenidaFrame extends JFrame {

    public BienvenidaFrame() {
        SistemaData.cerrarSesion();
        setTitle("Sistema SDS - Alquiler de Películas");
        AppStyles.aplicarIcono(this);
        setSize(540, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Sistema de alquiler de películas"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Selecciona el tipo de ingreso."), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel tarjeta = AppStyles.crearTarjeta();
        tarjeta.setLayout(new GridLayout(4, 1, 12, 12));

        var admin = AppStyles.botonPrimario("Ingresar como administrador");
        var empleado = AppStyles.botonSecundario("Ingresar como empleado");
        var cliente = AppStyles.botonNeutro("Ingresar como cliente");
        var salir = AppStyles.botonPeligro("Salir del sistema");

        tarjeta.add(admin);
        tarjeta.add(empleado);
        tarjeta.add(cliente);
        tarjeta.add(salir);
        add(tarjeta, BorderLayout.CENTER);

        admin.addActionListener(e -> abrirLogin("admin"));
        empleado.addActionListener(e -> abrirLogin("empleado"));
        cliente.addActionListener(e -> abrirLogin("cliente"));
        salir.addActionListener(e -> confirmarSalida());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        setVisible(true);
    }

    private void abrirLogin(String tipo) {
        new LoginFrame(tipo);
        dispose();
    }

    private void confirmarSalida() {
        if (AppStyles.confirmar(this,
                "¿Desea guardar la información y salir del sistema?",
                "Confirmar salida")) {
            ArchivoService.guardarTodo();
            System.exit(0);
        }
    }
}
