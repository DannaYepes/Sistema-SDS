package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import servicio.SistemaData;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private String documentoCliente;
    private String cliente;
    private String pelicula;
    private LocalDateTime fechaReserva;
    private boolean atendida;
    private String usuarioRegistro;
    private String rolRegistro;
    private LocalDateTime fechaVencimiento;
    private boolean cancelada;
    private String motivoCancelacion;
    private boolean prioritaria;

    public Reserva(String cliente, String pelicula) {
        this("", cliente, pelicula);
    }

    public Reserva(Cliente cliente, String pelicula) {
        this(cliente == null ? "" : cliente.getDocumento(), cliente == null ? "" : cliente.getNombre(), pelicula);
    }

    public Reserva(String documentoCliente, String cliente, String pelicula) {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El cliente es obligatorio para registrar la reserva.");
        }
        if (pelicula == null || pelicula.trim().isEmpty()) {
            throw new IllegalArgumentException("La película es obligatoria para registrar la reserva.");
        }
        this.documentoCliente = documentoCliente == null ? "" : documentoCliente.trim();
        this.cliente = cliente.trim();
        this.pelicula = pelicula.trim();
        this.fechaReserva = LocalDateTime.now();
        this.fechaVencimiento = fechaReserva.plusHours(SistemaData.configuracion.getHorasVencimientoReserva());
        this.atendida = false;
        this.cancelada = false;
        this.motivoCancelacion = "";
        this.prioritaria = false;
        this.usuarioRegistro = "Sin usuario";
        this.rolRegistro = "No registrado";
    }

    public String getDocumentoCliente() {
        return documentoCliente == null ? "" : documentoCliente;
    }

    public String getCliente() {
        return cliente == null ? "" : cliente;
    }

    public String getPelicula() {
        return pelicula == null ? "" : pelicula;
    }

    public LocalDateTime getFechaReserva() {
        if (fechaReserva == null) {
            fechaReserva = LocalDateTime.now();
        }
        return fechaReserva;
    }

    public LocalDateTime getFechaVencimiento() {
        if (fechaVencimiento == null) {
            fechaVencimiento = getFechaReserva().plusHours(SistemaData.configuracion.getHorasVencimientoReserva());
        }
        return fechaVencimiento;
    }

    public boolean isAtendida() {
        return atendida;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public void marcarAtendida() {
        this.atendida = true;
        this.cancelada = false;
        this.motivoCancelacion = "";
    }

    public void cancelar(String motivo) {
        this.cancelada = true;
        this.motivoCancelacion = motivo == null || motivo.trim().isEmpty() ? "Cancelada" : motivo.trim();
    }

    public boolean isVencida() {
        return !atendida && !cancelada && LocalDateTime.now().isAfter(getFechaVencimiento());
    }

    public boolean estaPendienteActiva() {
        return !atendida && !cancelada && !isVencida();
    }

    public String getEstado() {
        if (atendida) {
            return "Atendida";
        }
        if (cancelada) {
            return "Cancelada";
        }
        if (isVencida()) {
            return "Vencida";
        }
        return prioritaria ? "Reservado prioritario" : "Reservado";
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion == null ? "" : motivoCancelacion;
    }

    public void setUsuarioRegistro(String usuarioRegistro, String rolRegistro) {
        this.usuarioRegistro = usuarioRegistro == null || usuarioRegistro.trim().isEmpty() ? "Sin usuario" : usuarioRegistro.trim();
        this.rolRegistro = rolRegistro == null || rolRegistro.trim().isEmpty() ? "No registrado" : rolRegistro.trim();
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro == null || usuarioRegistro.trim().isEmpty() ? "Sin usuario" : usuarioRegistro;
    }

    public String getRolRegistro() {
        return rolRegistro == null || rolRegistro.trim().isEmpty() ? "No registrado" : rolRegistro;
    }
}
