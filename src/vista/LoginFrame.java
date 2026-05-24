package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import servicio.SeguridadService;
import servicio.SistemaData;
import util.AppStyles;

public class LoginFrame extends JFrame {

    private final String tipo;

    public LoginFrame(String tipo) {
        this.tipo = tipo;
        setTitle("Ingreso " + tipo);
        AppStyles.aplicarIcono(this);
        setSize(460, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Ingreso " + tipo), BorderLayout.NORTH);
        if (tipo.equalsIgnoreCase("cliente")) {
            header.add(AppStyles.subtitulo("Inicia sesión o crea una cuenta de cliente."), BorderLayout.CENTER);
        } else {
            header.add(AppStyles.subtitulo("Ingresa con un usuario autorizado."), BorderLayout.CENTER);
        }
        add(header, BorderLayout.NORTH);

        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        JTextField user = AppStyles.campoTexto();
        JPasswordField pass = new JPasswordField();
        AppStyles.aplicarEstiloCampo(pass);

        formulario.add(AppStyles.etiqueta("Usuario"), AppStyles.gbc(0, 0, 1));
        formulario.add(user, AppStyles.gbc(1, 0, 2));
        formulario.add(AppStyles.etiqueta("Clave"), AppStyles.gbc(0, 1, 1));
        formulario.add(pass, AppStyles.gbc(1, 1, 2));
        add(formulario, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnEntrar = AppStyles.botonPrimario("Entrar");
        var btnCrear = AppStyles.botonSecundario("Crear usuario");
        var btnVolver = AppStyles.botonNeutro("Volver");
        acciones.add(btnVolver);
        if (tipo.equalsIgnoreCase("cliente")) {
            acciones.add(btnCrear);
        }
        acciones.add(btnEntrar);
        add(acciones, BorderLayout.SOUTH);

        btnEntrar.addActionListener(e -> iniciarSesion(user.getText().trim(), new String(pass.getPassword()).trim()));
        btnCrear.addActionListener(e -> {
            new RegistroUsuarioFrame(tipo, true);
            dispose();
        });
        btnVolver.addActionListener(e -> volverInicio());

        setVisible(true);
    }

    private void iniciarSesion(String usuario, String clave) {
        if (usuario.isEmpty() || clave.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Complete usuario y clave.");
            return;
        }
        boolean acceso = new SeguridadService().login(usuario, clave, tipo);
        if (acceso) {
            if (tipo.equalsIgnoreCase("admin") || tipo.equalsIgnoreCase("empleado")) {
                new DashboardFrame(tipo);
            } else {
                new ClienteFrame(SistemaData.usuarioActual);
            }
            dispose();
        } else {
            AppStyles.mostrarAdvertencia(this, "Credenciales incorrectas o usuario no pertenece al rol seleccionado.");
        }
    }

    private void volverInicio() {
        new BienvenidaFrame();
        dispose();
    }
}
