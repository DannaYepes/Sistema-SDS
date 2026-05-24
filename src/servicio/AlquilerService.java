package servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Alquiler;
import modelo.Cliente;
import modelo.Pelicula;

public class AlquilerService {

    private ArrayList<Alquiler> lista = new ArrayList<>();

    public Alquiler alquilar(Cliente cliente, Pelicula pelicula, int dias, String metodoPago) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (pelicula == null) {
            throw new IllegalArgumentException("La película es obligatoria.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días de alquiler deben ser mayores a 0.");
        }
        if (buscarPendiente(cliente, pelicula.getTitulo()) != null) {
            throw new IllegalStateException("Este cliente ya tiene alquilada esta película. Debe devolverla antes de volver a alquilarla.");
        }
        if (!pelicula.estaDisponible()) {
            throw new IllegalStateException("No hay copias disponibles. Puede registrar una reserva para el cliente.");
        }

        BeneficioAlquiler beneficio = calcularBeneficio(cliente);
        pelicula.alquilar();
        Alquiler alquiler = new Alquiler(cliente, pelicula, dias, metodoPago, beneficio.porcentajeDescuento, beneficio.descripcion);
        if (SistemaData.usuarioActual != null) {
            alquiler.setUsuarioRegistro(SistemaData.usuarioActual.getUsuario(), SistemaData.usuarioActual.getRol());
        }
        cliente.incrementarAlquileres();
        lista.add(alquiler);
        SistemaData.usuarios.registrarAlquilerCliente(cliente.getDocumento(), cliente.getNombre());
        SistemaData.reservas.atender(cliente, pelicula.getTitulo());
        return alquiler;
    }

    private BeneficioAlquiler calcularBeneficio(Cliente cliente) {
        if (cliente == null) {
            return new BeneficioAlquiler(0, "Sin beneficio aplicado");
        }
        int historico = contarAlquileresPorCliente(cliente.getDocumento(), cliente.getNombre());
        int siguiente = historico + 1;
        if (siguiente > 0 && siguiente % ConfiguracionNegocioService.CADA_CUANTOS_ALQUILERES_BONO == 0) {
            return new BeneficioAlquiler(1.0, "Bono de cliente frecuente: alquiler número " + siguiente + " gratis");
        }
        if (historico >= ConfiguracionNegocioService.ALQUILERES_NIVEL_PREMIUM) {
            return new BeneficioAlquiler(ConfiguracionNegocioService.DESCUENTO_PREMIUM, "Nivel Premium: 15% de descuento");
        }
        if (historico >= ConfiguracionNegocioService.ALQUILERES_NIVEL_FRECUENTE) {
            return new BeneficioAlquiler(ConfiguracionNegocioService.DESCUENTO_FRECUENTE, "Nivel Frecuente: 10% de descuento");
        }
        return new BeneficioAlquiler(0, "Sin beneficio aplicado");
    }

    public Alquiler alquilar(Cliente cliente, Pelicula pelicula, int dias) {
        return alquilar(cliente, pelicula, dias, "Efectivo");
    }

    public boolean devolver(String documentoCliente, String nombreCliente, String tituloPelicula) {
        Alquiler alquiler = buscarPendiente(documentoCliente, nombreCliente, tituloPelicula);
        if (alquiler == null) {
            return false;
        }
        alquiler.registrarDevolucion();
        alquiler.getPelicula().devolver();
        return true;
    }

    public boolean devolver(Cliente cliente, String tituloPelicula) {
        if (cliente == null) {
            return false;
        }
        return devolver(cliente.getDocumento(), cliente.getNombre(), tituloPelicula);
    }

    public boolean devolver(String nombreCliente, String tituloPelicula) {
        return devolver("", nombreCliente, tituloPelicula);
    }

    public Alquiler buscarPendiente(Cliente cliente, String tituloPelicula) {
        if (cliente == null) {
            return null;
        }
        return buscarPendiente(cliente.getDocumento(), cliente.getNombre(), tituloPelicula);
    }

    public Alquiler buscarPendiente(String documentoCliente, String nombreCliente, String tituloPelicula) {
        if ((estaVacio(documentoCliente) && estaVacio(nombreCliente)) || estaVacio(tituloPelicula)) {
            return null;
        }
        for (Alquiler a : lista) {
            if (clienteCoincide(a.getCliente(), documentoCliente, nombreCliente)
                    && a.getPelicula().getTitulo().equalsIgnoreCase(tituloPelicula.trim())
                    && !a.isDevuelto()) {
                return a;
            }
        }
        return null;
    }

    public Alquiler buscarPendiente(String nombreCliente, String tituloPelicula) {
        return buscarPendiente("", nombreCliente, tituloPelicula);
    }

    public boolean tieneAlquilerPendientePorPelicula(String tituloPelicula) {
        if (estaVacio(tituloPelicula)) {
            return false;
        }
        for (Alquiler a : lista) {
            if (!a.isDevuelto() && a.getPelicula().getTitulo().equalsIgnoreCase(tituloPelicula.trim())) {
                return true;
            }
        }
        return false;
    }

    public Alquiler buscarUltimo(String documentoCliente, String nombreCliente) {
        if (estaVacio(documentoCliente) && estaVacio(nombreCliente)) {
            return null;
        }
        for (int i = lista.size() - 1; i >= 0; i--) {
            Alquiler alquiler = lista.get(i);
            if (clienteCoincide(alquiler.getCliente(), documentoCliente, nombreCliente)) {
                return alquiler;
            }
        }
        return null;
    }

    public Alquiler buscarUltimo(String nombreCliente) {
        return buscarUltimo("", nombreCliente);
    }

    public ArrayList<Alquiler> obtenerPendientes() {
        ArrayList<Alquiler> pendientes = new ArrayList<>();
        for (Alquiler a : lista) {
            if (!a.isDevuelto()) {
                pendientes.add(a);
            }
        }
        return pendientes;
    }

    public ArrayList<Alquiler> obtenerVencidos() {
        ArrayList<Alquiler> vencidos = new ArrayList<>();
        for (Alquiler a : obtenerPendientes()) {
            if (a.getDiasRetrasoActual() > 0) {
                vencidos.add(a);
            }
        }
        return vencidos;
    }

    public ArrayList<Alquiler> obtenerVencenHoy() {
        ArrayList<Alquiler> vencenHoy = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (Alquiler a : obtenerPendientes()) {
            if (hoy.equals(a.getFechaDevolucion())) {
                vencenHoy.add(a);
            }
        }
        return vencenHoy;
    }

    public ArrayList<Alquiler> obtenerPendientesPorCliente(String documentoCliente, String nombreCliente) {
        ArrayList<Alquiler> pendientes = new ArrayList<>();
        if (estaVacio(documentoCliente) && estaVacio(nombreCliente)) {
            return pendientes;
        }
        for (Alquiler a : lista) {
            if (!a.isDevuelto() && clienteCoincide(a.getCliente(), documentoCliente, nombreCliente)) {
                pendientes.add(a);
            }
        }
        return pendientes;
    }

    public ArrayList<Alquiler> obtenerPendientesPorCliente(String nombreCliente) {
        return obtenerPendientesPorCliente("", nombreCliente);
    }

    public ArrayList<Alquiler> obtenerPorCliente(String documentoCliente, String nombreCliente) {
        ArrayList<Alquiler> resultado = new ArrayList<>();
        if (estaVacio(documentoCliente) && estaVacio(nombreCliente)) {
            return resultado;
        }
        for (Alquiler a : lista) {
            if (clienteCoincide(a.getCliente(), documentoCliente, nombreCliente)) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    public int contarAlquileresPorCliente(String documentoCliente, String nombreCliente) {
        int total = 0;
        if (estaVacio(documentoCliente) && estaVacio(nombreCliente)) {
            return total;
        }
        for (Alquiler a : lista) {
            if (clienteCoincide(a.getCliente(), documentoCliente, nombreCliente)) {
                total++;
            }
        }
        return total;
    }

    public int contarAlquileresPorCliente(String nombreCliente) {
        return contarAlquileresPorCliente("", nombreCliente);
    }

    public ArrayList<Alquiler> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Alquiler> lista) {
        if (lista != null) {
            this.lista = lista;
        }
    }

    private boolean clienteCoincide(Cliente cliente, String documentoConsulta, String nombreConsulta) {
        if (cliente == null) {
            return false;
        }
        String documentoAlquiler = normalizar(cliente.getDocumento());
        String documento = normalizar(documentoConsulta);
        String nombre = normalizar(nombreConsulta);

        if (!documento.isEmpty()) {
            if (!documentoAlquiler.isEmpty()) {
                return documentoAlquiler.equalsIgnoreCase(documento);
            }
            return !nombre.isEmpty() && cliente.getNombre().equalsIgnoreCase(nombre);
        }
        return !nombre.isEmpty() && cliente.getNombre().equalsIgnoreCase(nombre);
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static final class BeneficioAlquiler {

        private final double porcentajeDescuento;
        private final String descripcion;

        private BeneficioAlquiler(double porcentajeDescuento, String descripcion) {
            this.porcentajeDescuento = porcentajeDescuento;
            this.descripcion = descripcion;
        }
    }
}
