package servicio;

import java.util.ArrayList;
import modelo.Cliente;
import modelo.Reserva;

public class ReservaService {

    private ArrayList<Reserva> lista = new ArrayList<>();

    public Reserva reservar(Cliente cliente, String pelicula) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio para registrar la reserva.");
        }
        depurarReservasVencidas();
        if (existeReservaPendiente(cliente, pelicula)) {
            throw new IllegalStateException("Este cliente ya tiene una reserva pendiente para esta película.");
        }
        Reserva reserva = new Reserva(cliente, pelicula);
        reserva.setPrioritaria(SistemaData.usuarios.esClientePremium(cliente.getDocumento(), cliente.getNombre()));
        if (SistemaData.usuarioActual != null) {
            reserva.setUsuarioRegistro(SistemaData.usuarioActual.getUsuario(), SistemaData.usuarioActual.getRol());
        }
        if (reserva.isPrioritaria()) {
            lista.add(0, reserva);
        } else {
            lista.add(reserva);
        }
        return reserva;
    }

    public Reserva reservar(String cliente, String pelicula) {
        return reservar(new Cliente(cliente), pelicula);
    }

    public boolean atender(Cliente cliente, String pelicula) {
        if (cliente == null) {
            return false;
        }
        return atender(cliente.getDocumento(), cliente.getNombre(), pelicula);
    }

    public boolean atender(String documentoCliente, String nombreCliente, String pelicula) {
        depurarReservasVencidas();
        Reserva reserva = buscarPendiente(documentoCliente, nombreCliente, pelicula);
        if (reserva == null) {
            return false;
        }
        reserva.marcarAtendida();
        return true;
    }

    public boolean atender(String cliente, String pelicula) {
        return atender("", cliente, pelicula);
    }

    public boolean cancelar(String documentoCliente, String nombreCliente, String pelicula, String motivo) {
        Reserva reserva = buscarPendiente(documentoCliente, nombreCliente, pelicula);
        if (reserva == null) {
            return false;
        }
        reserva.cancelar(motivo);
        return true;
    }

    public Reserva buscarPendiente(Cliente cliente, String pelicula) {
        if (cliente == null) {
            return null;
        }
        return buscarPendiente(cliente.getDocumento(), cliente.getNombre(), pelicula);
    }

    public Reserva buscarPendiente(String documentoCliente, String nombreCliente, String pelicula) {
        depurarReservasVencidas();
        if ((estaVacio(documentoCliente) && estaVacio(nombreCliente)) || estaVacio(pelicula)) {
            return null;
        }
        for (Reserva r : lista) {
            if (r.estaPendienteActiva()
                    && reservaCoincide(r, documentoCliente, nombreCliente)
                    && r.getPelicula().equalsIgnoreCase(pelicula.trim())) {
                return r;
            }
        }
        return null;
    }

    public Reserva buscarPendiente(String cliente, String pelicula) {
        return buscarPendiente("", cliente, pelicula);
    }

    public boolean existeReservaPendiente(Cliente cliente, String pelicula) {
        return buscarPendiente(cliente, pelicula) != null;
    }

    public boolean existeReservaPendiente(String cliente, String pelicula) {
        return buscarPendiente(cliente, pelicula) != null;
    }

    public ArrayList<Reserva> obtenerPendientes() {
        depurarReservasVencidas();
        ArrayList<Reserva> pendientes = new ArrayList<>();
        for (Reserva r : lista) {
            if (r.estaPendienteActiva()) {
                pendientes.add(r);
            }
        }
        return pendientes;
    }

    public ArrayList<Reserva> obtenerPendientesPorCliente(String documentoCliente, String nombreCliente) {
        depurarReservasVencidas();
        ArrayList<Reserva> pendientes = new ArrayList<>();
        if (estaVacio(documentoCliente) && estaVacio(nombreCliente)) {
            return pendientes;
        }
        for (Reserva r : lista) {
            if (r.estaPendienteActiva() && reservaCoincide(r, documentoCliente, nombreCliente)) {
                pendientes.add(r);
            }
        }
        return pendientes;
    }

    public ArrayList<Reserva> obtenerPendientesPorCliente(String cliente) {
        return obtenerPendientesPorCliente("", cliente);
    }

    public int depurarReservasVencidas() {
        int total = 0;
        for (Reserva r : lista) {
            if (r.isVencida()) {
                r.cancelar("Vencida automáticamente por superar "
                        + SistemaData.configuracion.getHorasVencimientoReserva() + " horas sin ser atendida.");
                total++;
            }
        }
        return total;
    }

    public ArrayList<Reserva> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Reserva> lista) {
        if (lista != null) {
            this.lista = lista;
        }
    }

    private boolean reservaCoincide(Reserva reserva, String documentoConsulta, String nombreConsulta) {
        String documentoReserva = normalizar(reserva.getDocumentoCliente());
        String documento = normalizar(documentoConsulta);
        String nombre = normalizar(nombreConsulta);

        if (!documento.isEmpty()) {
            if (!documentoReserva.isEmpty()) {
                return documentoReserva.equalsIgnoreCase(documento);
            }
            return !nombre.isEmpty() && reserva.getCliente().equalsIgnoreCase(nombre);
        }
        return !nombre.isEmpty() && reserva.getCliente().equalsIgnoreCase(nombre);
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
