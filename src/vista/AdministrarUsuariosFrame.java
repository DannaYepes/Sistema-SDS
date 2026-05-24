package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class AdministrarUsuariosFrame extends JFrame {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Usuario", "Rol", "Documento", "Nombre", "Celular", "Nivel", "Alquileres", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable tabla;
    private JTextField txtUsuario;
    private JComboBox<String> cmbRol;
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtCelular;
    private JPasswordField txtClave;
    private JCheckBox chkActivo;

    public AdministrarUsuariosFrame() {
        if (!SistemaData.esAdminActual()) {
            AppStyles.mostrarAdvertencia(null, "Solo el administrador puede administrar usuarios.");
            return;
        }
        setTitle("Administrar usuarios");
        AppStyles.aplicarIcono(this);
        setSize(1120, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirContenido();
        construirAcciones();
        cargarTabla();

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Administrar usuarios"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Solo el administrador puede crear, editar, activar o desactivar cuentas del sistema."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setOpaque(false);

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        contenido.add(AppStyles.scrollTabla(tabla), BorderLayout.CENTER);
        contenido.add(construirFormulario(), BorderLayout.SOUTH);
        add(contenido, BorderLayout.CENTER);
    }

    private JPanel construirFormulario() {
        JPanel form = AppStyles.crearTarjeta();
        form.setLayout(new GridBagLayout());

        txtUsuario = AppStyles.campoTexto();
        txtUsuario.setEditable(false);
        txtUsuario.setFocusable(false);
        cmbRol = AppStyles.combo(new String[]{"admin", "empleado", "cliente"});
        txtDocumento = AppStyles.campoTexto();
        txtNombre = AppStyles.campoTexto();
        txtCelular = AppStyles.campoTexto();
        txtClave = new JPasswordField();
        AppStyles.aplicarEstiloCampo(txtClave);
        chkActivo = new JCheckBox("Activo");
        chkActivo.setOpaque(false);
        chkActivo.setSelected(true);

        InputValidators.soloNumeros(txtDocumento, 10);
        InputValidators.alfanumericoConEspacios(txtNombre, 25);
        InputValidators.soloNumeros(txtCelular, 10);

        form.add(AppStyles.etiqueta("Usuario"), AppStyles.gbc(0, 0, 1));
        form.add(AppStyles.etiqueta("Rol"), AppStyles.gbc(1, 0, 1));
        form.add(AppStyles.etiqueta("Documento"), AppStyles.gbc(2, 0, 1));
        form.add(AppStyles.etiqueta("Nombre"), AppStyles.gbc(3, 0, 1));
        form.add(AppStyles.etiqueta("Celular"), AppStyles.gbc(4, 0, 1));
        form.add(AppStyles.etiqueta("Nueva clave (opcional)"), AppStyles.gbc(5, 0, 1));

        form.add(txtUsuario, AppStyles.gbc(0, 1, 1));
        form.add(cmbRol, AppStyles.gbc(1, 1, 1));
        form.add(txtDocumento, AppStyles.gbc(2, 1, 1));
        form.add(txtNombre, AppStyles.gbc(3, 1, 1));
        form.add(txtCelular, AppStyles.gbc(4, 1, 1));
        form.add(txtClave, AppStyles.gbc(5, 1, 1));
        form.add(chkActivo, AppStyles.gbc(0, 2, 1));

        return form;
    }

    private void construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnActualizar = AppStyles.botonPrimario("Actualizar usuario");
        var btnCrearAdmin = AppStyles.botonAdvertencia("Crear administrador");
        var btnCrearEmpleado = AppStyles.botonAdvertencia("Crear empleado");
        var btnCrearCliente = AppStyles.botonSecundario("Crear cliente");
        var btnLimpiar = AppStyles.botonNeutro("Limpiar selección");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnActualizar);
        acciones.add(btnCrearAdmin);
        acciones.add(btnCrearEmpleado);
        acciones.add(btnCrearCliente);
        acciones.add(btnLimpiar);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnCrearAdmin.addActionListener(e -> new RegistroUsuarioFrame("admin", false));
        btnCrearEmpleado.addActionListener(e -> new RegistroUsuarioFrame("empleado", false));
        btnCrearCliente.addActionListener(e -> new RegistroUsuarioFrame("cliente", false));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Usuario u : SistemaData.usuarios.getLista()) {
            int alquileres = u.getRol().equalsIgnoreCase("cliente")
                    ? SistemaData.usuarios.contarAlquileresHistoricosCliente(u)
                    : u.getAlquileresRegistrados();
            modelo.addRow(new Object[]{
                u.getUsuario(),
                u.getRol(),
                u.getDocumento(),
                u.getNombreCompleto(),
                u.getCelular(),
                u.getRol().equalsIgnoreCase("cliente") ? SistemaData.usuarios.obtenerNivelCliente(u) : "No aplica",
                alquileres,
                u.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void cargarSeleccion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            return;
        }
        int fila = tabla.convertRowIndexToModel(filaVista);
        txtUsuario.setText(valor(fila, 0));
        cmbRol.setSelectedItem(valor(fila, 1));
        txtDocumento.setText(valor(fila, 2));
        txtNombre.setText(valor(fila, 3));
        txtCelular.setText(valor(fila, 4));
        chkActivo.setSelected("Activo".equalsIgnoreCase(valor(fila, 7)));
        txtClave.setText("");
    }

    private void actualizarUsuario() {
        String usuario = txtUsuario.getText().trim();
        if (usuario.isEmpty()) {
            AppStyles.mostrarAdvertencia(this, "Selecciona un usuario de la tabla para actualizarlo.");
            return;
        }
        if (!AppStyles.confirmar(this, "¿Deseas actualizar los datos del usuario " + usuario + "?", "Confirmar actualización")) {
            return;
        }
        try {
            SistemaData.usuarios.actualizarUsuario(
                    usuario,
                    cmbRol.getSelectedItem().toString(),
                    txtDocumento.getText().trim(),
                    txtNombre.getText().trim(),
                    txtCelular.getText().trim(),
                    chkActivo.isSelected(),
                    new String(txtClave.getPassword()).trim()
            );
            ArchivoService.guardarTodo();
            cargarTabla();
            AppStyles.mostrarExito(this, "Usuario actualizado correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudo actualizar");
        }
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        txtUsuario.setText("");
        cmbRol.setSelectedIndex(0);
        txtDocumento.setText("");
        txtNombre.setText("");
        txtCelular.setText("");
        txtClave.setText("");
        chkActivo.setSelected(true);
    }

    private String valor(int fila, int columna) {
        Object value = modelo.getValueAt(fila, columna);
        return value == null ? "" : value.toString();
    }
}
