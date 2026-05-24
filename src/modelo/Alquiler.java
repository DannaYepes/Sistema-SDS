package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import servicio.SistemaData;

public class Alquiler implements Serializable {

    private static final long serialVersionUID = -4146959603363945394L;

    private Cliente cliente;
    private Pelicula pelicula;
    private LocalDate fechaAlquiler;
    private LocalDate fechaDevolucion;
    private double total;
    private boolean devuelto;
    private double multa;
    private Pago pago;
    private String usuarioRegistro;
    private String rolRegistro;
    private double subtotalSinDescuento;
    private double descuento;
    private double porcentajeDescuento;
    private String beneficioAplicado;

    public Alquiler(Cliente cliente, Pelicula pelicula, int dias) {
        this(cliente, pelicula, dias, "Efectivo");
    }

    public Alquiler(Cliente cliente, Pelicula pelicula, int dias, String metodoPago) {
        this(cliente, pelicula, dias, metodoPago, 0, "Sin beneficio aplicado");
    }

    public Alquiler(Cliente cliente, Pelicula pelicula, int dias, String metodoPago, double porcentajeDescuento, String beneficioAplicado) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (pelicula == null) {
            throw new IllegalArgumentException("La película es obligatoria.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días de alquiler deben ser mayores a 0.");
        }
        this.cliente = cliente;
        this.pelicula = pelicula;
        this.fechaAlquiler = LocalDate.now();
        this.fechaDevolucion = fechaAlquiler.plusDays(dias);
        this.subtotalSinDescuento = pelicula.getPrecio() * dias;
        this.porcentajeDescuento = Math.max(0, Math.min(1, porcentajeDescuento));
        this.descuento = Math.min(subtotalSinDescuento, subtotalSinDescuento * this.porcentajeDescuento);
        this.total = Math.max(0, subtotalSinDescuento - descuento);
        this.devuelto = false;
        this.multa = 0;
        this.pago = new Pago(metodoPago, Math.max(total, 1));
        this.usuarioRegistro = "Sin usuario";
        this.rolRegistro = "No registrado";
        this.beneficioAplicado = beneficioAplicado == null || beneficioAplicado.trim().isEmpty()
                ? "Sin beneficio aplicado"
                : beneficioAplicado.trim();
    }

    public void registrarDevolucion() {
        if (!devuelto) {
            multa = calcularMultaActual();
            devuelto = true;
        }
    }

    public double calcularMultaActual() {
        LocalDate hoy = LocalDate.now();
        if (fechaDevolucion != null && hoy.isAfter(fechaDevolucion)) {
            long dias = ChronoUnit.DAYS.between(fechaDevolucion, hoy);
            return dias * SistemaData.configuracion.getMultaPorDiaRetraso();
        }
        return 0;
    }

    public long getDiasRetrasoActual() {
        LocalDate hoy = LocalDate.now();
        if (fechaDevolucion != null && hoy.isAfter(fechaDevolucion)) {
            return ChronoUnit.DAYS.between(fechaDevolucion, hoy);
        }
        return 0;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public double getMulta() {
        return multa;
    }

    public double getMultaActual() {
        return devuelto ? multa : calcularMultaActual();
    }

    public double getTotal() {
        return total;
    }

    public double getSubtotalSinDescuento() {
        return subtotalSinDescuento <= 0 ? total : subtotalSinDescuento;
    }

    public double getDescuento() {
        return Math.max(0, descuento);
    }

    public double getPorcentajeDescuento() {
        return Math.max(0, porcentajeDescuento);
    }

    public String getBeneficioAplicado() {
        return beneficioAplicado == null || beneficioAplicado.trim().isEmpty() ? "Sin beneficio aplicado" : beneficioAplicado;
    }

    public double getTotalConMulta() {
        return total + getMultaActual();
    }

    public double getTotalConMultaActual() {
        return total + getMultaActual();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public LocalDate getFechaAlquiler() {
        return fechaAlquiler;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public Pago getPago() {
        if (pago == null) {
            pago = new Pago("No registrado", Math.max(total, 1));
        }
        return pago;
    }

    public String getEstado() {
        if (devuelto) {
            return "Devuelto";
        }
        return getDiasRetrasoActual() > 0 ? "Vencido" : "Pendiente";
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
