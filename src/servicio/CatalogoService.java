package servicio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import modelo.Pelicula;

public class CatalogoService {

    private ArrayList<Pelicula> lista = new ArrayList<>();

    public void agregar(Pelicula p) {
        if (p == null) {
            throw new IllegalArgumentException("La película no puede ser nula");
        }
        if (buscar(p.getTitulo()) != null) {
            throw new IllegalArgumentException("Ya existe una película con ese título");
        }
        lista.add(p);
        ordenarCatalogo();
    }

    public boolean editar(String tituloOriginal, Pelicula datosActualizados) {
        Pelicula existente = buscar(tituloOriginal);
        if (existente == null) {
            return false;
        }
        Pelicula duplicada = buscar(datosActualizados.getTitulo());
        if (duplicada != null && duplicada != existente) {
            throw new IllegalArgumentException("Ya existe otra película con ese título");
        }
        existente.actualizarDatos(
                datosActualizados.getTitulo(),
                datosActualizados.getIdioma(),
                datosActualizados.getGenero(),
                datosActualizados.getAnio(),
                datosActualizados.getCantidad(),
                datosActualizados.getPrecio(),
                datosActualizados.getFormato(),
                datosActualizados.getDuracionMinutos(),
                datosActualizados.getDescripcion()
        );
        ordenarCatalogo();
        return true;
    }

    public boolean eliminar(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        if (SistemaData.alquiler.tieneAlquilerPendientePorPelicula(titulo)) {
            throw new IllegalStateException("No se puede eliminar la película porque tiene alquileres pendientes. Primero registra las devoluciones.");
        }
        return lista.removeIf(p -> p.getTitulo().equalsIgnoreCase(titulo.trim()));
    }

    public void actualizarPrecioPorFormato(String formato, double nuevoPrecio) {
        if (formato == null || formato.trim().isEmpty()) {
            throw new IllegalArgumentException("El formato es obligatorio.");
        }
        if (nuevoPrecio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        for (Pelicula p : lista) {
            if (p.getFormato().equalsIgnoreCase(formato.trim())) {
                p.setPrecio(nuevoPrecio);
            }
        }
        ordenarCatalogo();
    }

    public Pelicula buscar(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        for (Pelicula p : lista) {
            if (p.getTitulo().equalsIgnoreCase(titulo.trim())) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Pelicula> buscarPorNombre(String nombre) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>(lista);
        }
        for (Pelicula p : lista) {
            if (p.coincideTitulo(nombre.trim())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ArrayList<Pelicula> buscarPorGenero(String genero) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        if (genero == null || genero.trim().isEmpty()) {
            return resultado;
        }
        for (Pelicula p : lista) {
            if (p.coincideGenero(genero.trim())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ArrayList<Pelicula> buscarPorAnio(int anio) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        for (Pelicula p : lista) {
            if (p.coincideAnio(anio)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ArrayList<Pelicula> buscarDisponibles(boolean disponibles) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        for (Pelicula p : lista) {
            if (p.coincideDisponibilidad(disponibles)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ArrayList<Pelicula> filtrar(String criterio, String texto) {
        String filtro = criterio == null ? "Todos" : criterio.trim();
        String busqueda = texto == null ? "" : texto.trim();

        if (filtro.equalsIgnoreCase("Todos")) {
            return buscarEnTodo(busqueda);
        }
        if (filtro.equalsIgnoreCase("Título") || filtro.equalsIgnoreCase("Titulo") || filtro.equalsIgnoreCase("Nombre")) {
            return buscarPorNombre(busqueda);
        }
        if (filtro.equalsIgnoreCase("Género") || filtro.equalsIgnoreCase("Genero")) {
            return buscarPorGenero(busqueda);
        }
        if (filtro.equalsIgnoreCase("Año") || filtro.equalsIgnoreCase("Anio")) {
            if (busqueda.isEmpty()) {
                return new ArrayList<>(lista);
            }
            return buscarPorAnio(Integer.parseInt(busqueda));
        }
        if (filtro.equalsIgnoreCase("Disponibles")) {
            return buscarDisponibles(true);
        }
        if (filtro.equalsIgnoreCase("No disponibles")) {
            return buscarDisponibles(false);
        }
        return buscarEnTodo(busqueda);
    }

    private ArrayList<Pelicula> buscarEnTodo(String texto) {
        ArrayList<Pelicula> resultado = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return new ArrayList<>(lista);
        }
        String valor = normalizar(texto);
        for (Pelicula p : lista) {
            if (normalizar(p.getTitulo()).contains(valor)
                    || normalizar(p.getGenero()).contains(valor)
                    || normalizar(p.getIdioma()).contains(valor)
                    || normalizar(p.getFormato()).contains(valor)
                    || String.valueOf(p.getAnio()).contains(valor)
                    || String.valueOf(p.getCantidad()).contains(valor)
                    || String.valueOf((long) p.getPrecio()).contains(valor)
                    || normalizar(p.estaDisponible() ? "Disponible" : "No disponible").contains(valor)
                    || normalizar(p.getDescripcion()).contains(valor)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.toLowerCase().trim();
    }

    public ArrayList<Pelicula> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Pelicula> lista) {
        if (lista != null) {
            this.lista = lista;
            ordenarCatalogo();
        }
    }

    private void ordenarCatalogo() {
        lista.sort(Comparator.comparing(Pelicula::getTitulo, String.CASE_INSENSITIVE_ORDER));
    }

    public List<String> obtenerTitulos() {
        ArrayList<String> titulos = new ArrayList<>();
        for (Pelicula p : lista) {
            titulos.add(p.getTitulo());
        }
        return titulos;
    }
}
