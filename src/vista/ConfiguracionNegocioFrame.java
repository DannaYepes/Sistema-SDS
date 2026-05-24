package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import servicio.ArchivoService;
import servicio.SistemaData;
import util.AppStyles;
import util.InputValidators;

public class ConfiguracionNegocioFrame extends JFrame {

    private JTextField txtMultaDia;
    private JTextField txtHorasReserva;

    public ConfiguracionNegocioFrame() {
        if (!SistemaData.esAdminActual()) {
            AppStyles.mostrarAdvertencia(null, "Solo el administrador puede configurar reglas de negocio.");
            return;
        }
        setTitle("Reglas del negocio");
        AppStyles.aplicarIcono(this);
        setSize(900, 460);
        setMinimumSize(new java.awt.Dimension(760, 440));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(AppStyles.crearPanelPrincipal());

        construirEncabezado();
        construirFormulario();
        construirAcciones();

        setVisible(true);
    }

    private void construirEncabezado() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        header.add(AppStyles.titulo("Reglas del negocio"), BorderLayout.NORTH);
        header.add(AppStyles.subtitulo("Configura multa por retraso, vencimiento de reservas y consulta reglas de membresía."), BorderLayout.CENTER);
        header.add(AppStyles.sesion(SistemaData.getSesionTexto()), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private void construirFormulario() {
        JPanel form = AppStyles.crearTarjeta();
        form.setLayout(new GridBagLayout());
        txtMultaDia = AppStyles.campoTexto(String.format("%.0f", SistemaData.configuracion.getMultaPorDiaRetraso()));
        txtHorasReserva = AppStyles.campoTexto(String.valueOf(SistemaData.configuracion.getHorasVencimientoReserva()));
        InputValidators.moneda(txtMultaDia, 12);
        InputValidators.soloNumeros(txtHorasReserva, 3);

        form.add(AppStyles.etiqueta("Multa por día de retraso"), AppStyles.gbc(0, 0, 1));
        form.add(txtMultaDia, AppStyles.gbc(1, 0, 1));
        form.add(AppStyles.etiqueta("Horas para vencer reserva"), AppStyles.gbc(0, 1, 1));
        form.add(txtHorasReserva, AppStyles.gbc(1, 1, 1));
        form.add(AppStyles.etiqueta("Reglas de membresía"), AppStyles.gbc(0, 2, 1));
        form.add(AppStyles.subtitulo("Frecuente: 3 alquileres = 10%. Premium: 6 alquileres = 15% y prioridad. Cada 5 alquileres: bono gratis."), AppStyles.gbc(1, 2, 1));
        add(form, BorderLayout.CENTER);
    }

    private void construirAcciones() {
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        acciones.setOpaque(false);
        var btnGuardar = AppStyles.botonPrimario("Guardar reglas");
        var btnResumen = AppStyles.botonNeutro("Ver resumen");
        var btnCerrar = AppStyles.botonNeutro("Cerrar");
        acciones.add(btnGuardar);
        acciones.add(btnResumen);
        acciones.add(btnCerrar);
        add(acciones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnResumen.addActionListener(e -> AppStyles.mostrarInfo(this, SistemaData.configuracion.resumenReglas(), "Reglas activas"));
        btnCerrar.addActionListener(e -> dispose());
    }

    private void guardar() {
        try {
            double multa = Double.parseDouble(txtMultaDia.getText().trim().replace(".", "").replace(",", "."));
            int horas = Integer.parseInt(txtHorasReserva.getText().trim());
            SistemaData.configuracion.setMultaPorDiaRetraso(multa);
            SistemaData.configuracion.setHorasVencimientoReserva(horas);
            ArchivoService.guardarTodo();
            AppStyles.mostrarExito(this, "Reglas de negocio actualizadas correctamente.");
        } catch (Exception ex) {
            AppStyles.mostrarError(this, ex.getMessage(), "No se pudieron guardar las reglas");
        }
    }
}
