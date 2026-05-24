package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.Cliente;
import modelo.Usuario;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class ClienteFrame extends JFrame {

    public static String nombreCliente;
    private JTextField doc;
    private JTextField nombre;
    private JTextField tel;

    public ClienteFrame() {
        this(SistemaData.usuarioActual);
    }

    public ClienteFrame(Usuario usuario) {
        setTitle("Datos del cliente");
        AppStyles.aplicarIcono(this);
        setSize(620, 405);
        setMinimumSize(new java.awt.Dimension(620, 405));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Ingreso de cliente"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Tus datos son de solo lectura. Si necesitas corregirlos, solicita actualización al administrador."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        doc = AppStyles.campoTexto(usuario == null ? "" : usuario.getDocumento());
        nombre = AppStyles.campoTexto(usuario == null ? "" : usuario.getNombreCompleto());
        tel = AppStyles.campoTexto(usuario == null ? "" : usuario.getCelular());

        doc.setEditable(false);
        nombre.setEditable(false);
        tel.setEditable(false);
        doc.setFocusable(false);
        nombre.setFocusable(false);
        tel.setFocusable(false);

        formulario.add(AppStyles.etiqueta("Documento"), AppStyles.gbc(0, 0, 1));
        formulario.add(doc, AppStyles.gbc(1, 0, 2));
        formulario.add(AppStyles.etiqueta("Nombre"), AppStyles.gbc(0, 1, 1));
        formulario.add(nombre, AppStyles.gbc(1, 1, 2));
        formulario.add(AppStyles.etiqueta("Celular"), AppStyles.gbc(0, 2, 1));
        formulario.add(tel, AppStyles.gbc(1, 2, 2));
        formulario.add(AppStyles.etiqueta("Nivel de cliente"), AppStyles.gbc(0, 3, 1));
        formulario.add(AppStyles.subtitulo(usuario == null ? "No registrado" : SistemaData.usuarios.obtenerNivelCliente(usuario)), AppStyles.gbc(1, 3, 2));
        add(formulario, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnContinuar = AppStyles.botonPrimario("Continuar a alquiler");
        var btnVolver = AppStyles.botonPeligro("Cerrar sesión");
        acciones.add(btnVolver);
        acciones.add(btnContinuar);
        add(acciones, BorderLayout.SOUTH);

        btnContinuar.addActionListener(e -> continuar());
        btnVolver.addActionListener(e -> volverInicio());

        setVisible(true);
    }

    private void continuar() {
        try {
            String documentoTexto = doc.getText().trim();
            String nombreTexto = nombre.getText().trim();
            String celularTexto = tel.getText().trim();

            if (!InputValidators.documentoValido(documentoTexto)
                    || !InputValidators.nombreClienteValido(nombreTexto)
                    || !InputValidators.celularValido(celularTexto)) {
                throw new IllegalArgumentException("Tus datos de cliente están incompletos o inválidos. Solicita al administrador que actualice documento, nombre y celular antes de alquilar.");
            }

            nombreCliente = nombreTexto;
            Cliente cliente = new Cliente(documentoTexto, nombreTexto, celularTexto);
            new AlquilerFrame(cliente);
            dispose();
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "Datos de cliente no válidos");
        }
    }

    private void volverInicio() {
        if (!AppStyles.confirmarCerrarSesion(this)) {
            return;
        }
        SistemaData.cerrarSesion();
        new BienvenidaFrame();
        dispose();
    }
}
