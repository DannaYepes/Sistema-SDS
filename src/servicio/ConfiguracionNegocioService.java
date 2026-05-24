package servicio;

import java.io.Serializable;

public class ConfiguracionNegocioService implements Serializable {

    private static final long serialVersionUID = 2026052301L;

    public static final double MULTA_DIA_DEFECTO = 1000.0;
    public static final int HORAS_VENCIMIENTO_RESERVA_DEFECTO = 48;
    public static final int ALQUILERES_NIVEL_FRECUENTE = 3;
    public static final int ALQUILERES_NIVEL_PREMIUM = 6;
    public static final int CADA_CUANTOS_ALQUILERES_BONO = 5;
    public static final double DESCUENTO_FRECUENTE = 0.10;
    public static final double DESCUENTO_PREMIUM = 0.15;

    private double multaPorDiaRetraso = MULTA_DIA_DEFECTO;
    private int horasVencimientoReserva = HORAS_VENCIMIENTO_RESERVA_DEFECTO;

    public double getMultaPorDiaRetraso() {
        if (multaPorDiaRetraso <= 0) {
            multaPorDiaRetraso = MULTA_DIA_DEFECTO;
        }
        return multaPorDiaRetraso;
    }

    public void setMultaPorDiaRetraso(double multaPorDiaRetraso) {
        if (multaPorDiaRetraso <= 0) {
            throw new IllegalArgumentException("La multa por día de retraso debe ser mayor a 0.");
        }
        this.multaPorDiaRetraso = multaPorDiaRetraso;
    }

    public int getHorasVencimientoReserva() {
        if (horasVencimientoReserva <= 0) {
            horasVencimientoReserva = HORAS_VENCIMIENTO_RESERVA_DEFECTO;
        }
        return horasVencimientoReserva;
    }

    public void setHorasVencimientoReserva(int horasVencimientoReserva) {
        if (horasVencimientoReserva < 1 || horasVencimientoReserva > 168) {
            throw new IllegalArgumentException("El vencimiento de reservas debe estar entre 1 y 168 horas.");
        }
        this.horasVencimientoReserva = horasVencimientoReserva;
    }

    public String resumenReglas() {
        return "Multa por día de retraso: $" + String.format("%,.2f", getMultaPorDiaRetraso())
                + "\nVencimiento automático de reservas: " + getHorasVencimientoReserva() + " horas"
                + "\nNivel Frecuente: desde " + ALQUILERES_NIVEL_FRECUENTE + " alquileres (10% descuento)"
                + "\nNivel Premium: desde " + ALQUILERES_NIVEL_PREMIUM + " alquileres (15% descuento y prioridad en reservas)"
                + "\nBono frecuente: cada " + CADA_CUANTOS_ALQUILERES_BONO + " alquileres, el siguiente alquiler queda gratis.";
    }
}
