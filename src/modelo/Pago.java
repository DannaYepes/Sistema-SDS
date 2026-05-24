package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Pago implements Serializable {

    private static final long serialVersionUID = -6518026510884140918L;

    private String metodo;
    private double monto;
    private LocalDateTime fecha;
    private String id;

    public Pago(String metodo, double monto) {
        if (metodo == null || metodo.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
        }
        this.metodo = metodo.trim();
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generarComprobante() {
        return "----- COMPROBANTE DE PAGO -----\n"
                + "Código: " + getId() + "\n"
                + "Fecha: " + getFecha() + "\n"
                + "Método: " + getMetodo() + "\n"
                + "Monto: $" + String.format("%,.2f", getMonto()) + "\n"
                + "--------------------------------";
    }

    public String getMetodo() {
        return metodo == null || metodo.trim().isEmpty() ? "No registrado" : metodo;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha == null ? LocalDateTime.now() : fecha;
    }

    public String getId() {
        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        return id;
    }
}
