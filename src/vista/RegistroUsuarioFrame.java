package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Usuario;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class RegistroUsuarioFrame extends JFrame {

    private final String rol;
    private final boolean volverAlLogin;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtCelular;

    public RegistroUsuarioFrame(String rol) {
        this(rol, true);
    }

    public RegistroUsuarioFrame(String rol, boolean volverAlLogin) {
        this.rol = rol == null ? "cliente" : rol.trim().toLowerCase();
        this.volverAlLogin = volverAlLogin;
        if (!this.rol.equals("cliente") && !SistemaData.esAdminActual()) {
            AppStyles.mostrarAdvertencia(null, "Solo el administrador puede crear cuentas de empleado o administrador.");
            return;
        }
        setTitle("Crear usuario " + this.rol);
        AppStyles.aplicarIcono(this);
        setSize(540, this.rol.equals("cliente") ? 480 : 390);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirFormulario();
        construirBotones();

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Crear usuario " + rol), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("El usuario quedará guardado y podrá registrar sus propias acciones en el sistema."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirFormulario() {
        JPanel formulario = AppStyles.crearTarjeta();
        formulario.setLayout(new GridBagLayout());

        txtUsuario = AppStyles.campoTexto();
        txtClave = new JPasswordField();
        AppStyles.aplicarEstiloCampo(txtClave);
        txtDocumento = AppStyles.campoTexto();
        txtNombre = AppStyles.campoTexto();
        txtCelular = AppStyles.campoTexto();

        InputValidators.soloNumeros(txtDocumento, 10);
        InputValidators.alfanumericoConEspacios(txtNombre, 25);
        InputValidators.soloNumeros(txtCelular, 10);

        formulario.add(AppStyles.etiqueta("Usuario"), AppStyles.gbc(0, 0, 1));
        formulario.add(txtUsuario, AppStyles.gbc(1, 0, 2));
        formulario.add(AppStyles.etiqueta("Clave"), AppStyles.gbc(0, 1, 1));
        formulario.add(txtClave, AppStyles.gbc(1, 1, 2));

        if (rol.equals("cliente")) {
            formulario.add(AppStyles.etiqueta("Documento"), AppStyles.gbc(0, 2, 1));
            formulario.add(txtDocumento, AppStyles.gbc(1, 2, 2));
            formulario.add(AppStyles.etiqueta("Nombre"), AppStyles.gbc(0, 3, 1));
            formulario.add(txtNombre, AppStyles.gbc(1, 3, 2));
            formulario.add(AppStyles.etiqueta("Celular"), AppStyles.gbc(0, 4, 1));
            formulario.add(txtCelular, AppStyles.gbc(1, 4, 2));
        } else {
            formulario.add(AppStyles.etiqueta("Nombre visible"), AppStyles.gbc(0, 2, 1));
            formulario.add(txtNombre, AppStyles.gbc(1, 2, 2));
        }

        add(formulario, BorderLayout.CENTER);
    }

    private void construirBotones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnVolver = AppStyles.botonNeutro("Volver");
        var btnCrear = AppStyles.botonPrimario("Guardar usuario");
        acciones.add(btnVolver);
        acciones.add(btnCrear);
        add(acciones, BorderLayout.SOUTH);

        btnVolver.addActionListener(e -> volver());
        btnCrear.addActionListener(e -> crearUsuario());
    }

    private void crearUsuario() {
        try {
            String usuario = txtUsuario.getText().trim();
            String clave = new String(txtClave.getPassword()).trim();
            String documento = txtDocumento.getText().trim();
            String nombre = txtNombre.getText().trim();
            String celular = txtCelular.getText().trim();

            if (rol.equals("cliente")) {
                validarDatosCliente(documento, nombre, celular);
            } else if (nombre.isEmpty()) {
                nombre = usuario;
            }

            Usuario nuevo = SistemaData.usuarios.crearUsuario(usuario, clave, rol, documento, nombre, celular);
            ArchivoService.guardarTodo();
            AppStyles.mostrarExito(this, "Usuario creado correctamente: " + nuevo.getUsuario());
            volver();
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo crear el usuario");
        }
    }

    private void validarDatosCliente(String documento, String nombre, String celular) {
        if (!InputValidators.documentoValido(documento)) {
            throw new IllegalArgumentException("El documento debe ser numérico y tener entre 6 y 10 dígitos.");
        }
        if (!InputValidators.nombreClienteValido(nombre)) {
            throw new IllegalArgumentException("El nombre debe ser alfanumérico y tener máximo 25 caracteres.");
        }
        if (!InputValidators.celularValido(celular)) {
            throw new IllegalArgumentException("El celular debe ser numérico y tener máximo 10 dígitos.");
        }
    }

    private void volver() {
        if (volverAlLogin) {
            new LoginFrame(rol);
        }
        dispose();
    }
}
