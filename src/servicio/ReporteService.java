package servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.Alquiler;
import modelo.Pelicula;

public class ReporteService {

    public double ingresosTotales() {
        double total = 0;
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            total += a.getTotalConMulta();
        }
        return total;
    }

    public double ingresosDelDia() {
        double total = 0;
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            if (hoy.equals(a.getFechaAlquiler())) {
                total += a.getTotalConMulta();
            }
        }
        return total;
    }

    public double ingresosDeLaSemana() {
        double total = 0;
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(6);
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            LocalDate fecha = a.getFechaAlquiler();
            if (fecha != null && !fecha.isBefore(inicioSemana) && !fecha.isAfter(hoy)) {
                total += a.getTotalConMulta();
            }
        }
        return total;
    }

    public double ingresosDelMes() {
        double total = 0;
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            LocalDate fecha = a.getFechaAlquiler();
            if (fecha != null
                    && fecha.getMonth() == hoy.getMonth()
                    && fecha.getYear() == hoy.getYear()) {
                total += a.getTotalConMulta();
            }
        }
        return total;
    }

    public double descuentosDelMes() {
        double total = 0;
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            LocalDate fecha = a.getFechaAlquiler();
            if (fecha != null && fecha.getMonth() == hoy.getMonth() && fecha.getYear() == hoy.getYear()) {
                total += a.getDescuento();
            }
        }
        return total;
    }

    public double multasActualesPendientes() {
        double total = 0;
        for (Alquiler a : SistemaData.alquiler.obtenerPendientes()) {
            total += a.getMultaActual();
        }
        return total;
    }

    public int totalAlquileres() {
        return SistemaData.alquiler.getLista().size();
    }

    public int totalAlquileresDelMes() {
        int total = 0;
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            LocalDate fecha = a.getFechaAlquiler();
            if (fecha != null
                    && fecha.getMonth() == hoy.getMonth()
                    && fecha.getYear() == hoy.getYear()) {
                total++;
            }
        }
        return total;
    }

    public int peliculasDisponibles() {
        int total = 0;
        for (Pelicula pelicula : SistemaData.catalogo.getLista()) {
            if (pelicula.estaDisponible()) {
                total++;
            }
        }
        return total;
    }

    public int copiasDisponibles() {
        int total = 0;
        for (Pelicula pelicula : SistemaData.catalogo.getLista()) {
            total += Math.max(0, pelicula.getCantidad());
        }
        return total;
    }

    public int peliculasAlquiladas() {
        return SistemaData.alquiler.obtenerPendientes().size();
    }

    public int alquileresVencidos() {
        return SistemaData.alquiler.obtenerVencidos().size();
    }

    public int alquileresVencenHoy() {
        return SistemaData.alquiler.obtenerVencenHoy().size();
    }

    public int alquileresPendientes() {
        return SistemaData.alquiler.obtenerPendientes().size();
    }

    public int reservasPendientes() {
        return SistemaData.reservas.obtenerPendientes().size();
    }

    public ArrayList<ResumenPelicula> topPeliculasMasAlquiladas(int limite) {
        Map<String, ResumenPelicula> mapa = new LinkedHashMap<>();
        for (Alquiler a : SistemaData.alquiler.getLista()) {
            String titulo = a.getPelicula().getTitulo();
            ResumenPelicula resumen = mapa.computeIfAbsent(titulo,
                    k -> new ResumenPelicula(titulo, a.getPelicula().getGenero(), a.getPelicula().getFormato()));
            resumen.cantidad++;
            resumen.ingresos += a.getTotalConMultaActual();
        }
        ArrayList<ResumenPelicula> top = new ArrayList<>(mapa.values());
        top.sort(Comparator.comparingInt(ResumenPelicula::getCantidad).reversed()
                .thenComparing(ResumenPelicula::getIngresos, Comparator.reverseOrder())
                .thenComparing(ResumenPelicula::getTitulo));
        if (limite > 0 && top.size() > limite) {
            return new ArrayList<>(top.subList(0, limite));
        }
        return top;
    }

    public String resumenTexto() {
        StringBuilder builder = new StringBuilder();
        builder.append("REPORTE EJECUTIVO - Sistema SDS\n")
                .append("----------------------------------------\n")
                .append("Películas disponibles: ").append(peliculasDisponibles()).append(" títulos\n")
                .append("Copias disponibles: ").append(copiasDisponibles()).append(" unidades\n")
                .append("Películas alquiladas pendientes: ").append(peliculasAlquiladas()).append("\n")
                .append("Alquileres vencidos: ").append(alquileresVencidos()).append("\n")
                .append("Alquileres que vencen hoy: ").append(alquileresVencenHoy()).append("\n")
                .append("Reservas pendientes activas: ").append(reservasPendientes()).append("\n")
                .append("Multas pendientes actuales: $").append(formato(multasActualesPendientes())).append("\n\n")
                .append("Ingresos totales: $").append(formato(ingresosTotales()))
                .append("\nIngresos del día: $").append(formato(ingresosDelDia()))
                .append("\nIngresos de la semana: $").append(formato(ingresosDeLaSemana()))
                .append("\nIngresos del mes: $").append(formato(ingresosDelMes()))
                .append("\nDescuentos aplicados del mes: $").append(formato(descuentosDelMes()))
                .append("\nPelículas alquiladas en el mes: ").append(totalAlquileresDelMes())
                .append("\n\nREGLAS DE NEGOCIO ACTIVAS\n")
                .append(SistemaData.configuracion.resumenReglas())
                .append("\n\nTOP PELÍCULAS MÁS ALQUILADAS\n");

        ArrayList<ResumenPelicula> top = topPeliculasMasAlquiladas(5);
        if (top.isEmpty()) {
            builder.append("No hay alquileres registrados todavía.");
        } else {
            int posicion = 1;
            for (ResumenPelicula item : top) {
                builder.append(posicion++).append(". ")
                        .append(item.getTitulo()).append(" - ")
                        .append(item.getCantidad()).append(" alquiler(es), ingresos $")
                        .append(formato(item.getIngresos())).append("\n");
            }
        }
        return builder.toString();
    }

    public String formato(double valor) {
        return String.format("%,.2f", valor);
    }

    public static class ResumenPelicula {

        private final String titulo;
        private final String genero;
        private final String formato;
        private int cantidad;
        private double ingresos;

        public ResumenPelicula(String titulo, String genero, String formato) {
            this.titulo = titulo;
            this.genero = genero;
            this.formato = formato;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getGenero() {
            return genero;
        }

        public String getFormato() {
            return formato;
        }

        public int getCantidad() {
            return cantidad;
        }

        public Double getIngresos() {
            return ingresos;
        }
    }
}
