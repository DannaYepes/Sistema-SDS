package modelo;

import java.io.Serializable;

public class Pelicula implements Serializable {

    private static final long serialVersionUID = -2821252686341711463L;

    private String titulo;
    private String idioma;
    private String genero;
    private int anio;
    private int cantidad;
    private double precio;
    private String formato;
    private int duracionMinutos;
    private String descripcion;

    public Pelicula(String titulo, String idioma, String genero, int anio, int cantidad, double precio) {
        this(titulo, idioma, genero, anio, cantidad, precio, "Digital", 90, "Sin descripción registrada.");
    }

    public Pelicula(String titulo, String idioma, String genero, int anio, int cantidad, double precio, String formato) {
        this(titulo, idioma, genero, anio, cantidad, precio, formato, 90, "Sin descripción registrada.");
    }

    public Pelicula(String titulo, String idioma, String genero, int anio, int cantidad, double precio,
            String formato, int duracionMinutos, String descripcion) {
        validarDatos(titulo, idioma, genero, anio, cantidad, precio, formato, duracionMinutos, descripcion);
        this.titulo = titulo.trim();
        this.idioma = idioma.trim();
        this.genero = genero.trim();
        this.anio = anio;
        this.cantidad = cantidad;
        this.precio = precio;
        this.formato = formato.trim();
        this.duracionMinutos = duracionMinutos;
        this.descripcion = descripcion.trim();
    }

    public void actualizarDatos(String titulo, String idioma, String genero, int anio, int cantidad, double precio, String formato) {
        actualizarDatos(titulo, idioma, genero, anio, cantidad, precio, formato, 90, "Sin descripción registrada.");
    }

    public void actualizarDatos(String titulo, String idioma, String genero, int anio, int cantidad, double precio,
            String formato, int duracionMinutos, String descripcion) {
        validarDatos(titulo, idioma, genero, anio, cantidad, precio, formato, duracionMinutos, descripcion);
        this.titulo = titulo.trim();
        this.idioma = idioma.trim();
        this.genero = genero.trim();
        this.anio = anio;
        this.cantidad = cantidad;
        this.precio = precio;
        this.formato = formato.trim();
        this.duracionMinutos = duracionMinutos;
        this.descripcion = descripcion.trim();
    }

    private void validarDatos(String titulo, String idioma, String genero, int anio, int cantidad, double precio,
            String formato, int duracionMinutos, String descripcion) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la película es obligatorio.");
        }
        if (titulo.trim().length() > 40) {
            throw new IllegalArgumentException("El título de la película no puede superar 40 caracteres.");
        }
        if (idioma == null || idioma.trim().isEmpty()) {
            throw new IllegalArgumentException("El idioma de la película es obligatorio.");
        }
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("El género de la película es obligatorio.");
        }
        if (formato == null || formato.trim().isEmpty()) {
            throw new IllegalArgumentException("El formato de la película es obligatorio.");
        }
        if (anio < 1888 || anio > 2100) {
            throw new IllegalArgumentException("El año de estreno debe estar entre 1888 y 2100.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de copias no puede ser negativa.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio de alquiler debe ser mayor a 0.");
        }
        if (duracionMinutos <= 0 || duracionMinutos > 999) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 999 minutos.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la película es obligatoria.");
        }
        if (descripcion.trim().length() > 180) {
            throw new IllegalArgumentException("La descripción de la película no puede superar 180 caracteres.");
        }
    }

    public String getTitulo() {
        return titulo == null ? "" : titulo;
    }

    public String getIdioma() {
        return idioma == null ? "" : idioma;
    }

    public String getGenero() {
        return genero == null ? "" : genero;
    }

    public int getAnio() {
        return anio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public String getFormato() {
        return formato == null || formato.trim().isEmpty() ? "Digital" : formato;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public String getDuracionTexto() {
        return duracionMinutos <= 0 ? "No registrada" : duracionMinutos + " min";
    }

    public String getDescripcion() {
        return descripcion == null || descripcion.trim().isEmpty() ? "Sin descripción registrada." : descripcion;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de copias no puede ser negativa.");
        }
        this.cantidad = cantidad;
    }

    public void setPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio de alquiler debe ser mayor a 0.");
        }
        this.precio = precio;
    }

    public boolean estaDisponible() {
        return cantidad > 0;
    }

    public void alquilar() {
        if (cantidad > 0) {
            cantidad--;
        } else {
            throw new IllegalStateException("No hay copias disponibles para esta película.");
        }
    }

    public void devolver() {
        cantidad++;
    }

    public boolean coincideTitulo(String texto) {
        return texto != null && getTitulo().toLowerCase().contains(texto.toLowerCase());
    }

    public boolean coincideGenero(String texto) {
        return texto != null && getGenero().toLowerCase().contains(texto.toLowerCase());
    }

    public boolean coincideIdioma(String texto) {
        return texto != null && getIdioma().toLowerCase().contains(texto.toLowerCase());
    }

    public boolean coincideFormato(String texto) {
        return texto != null && getFormato().toLowerCase().contains(texto.toLowerCase());
    }

    public boolean coincideAnio(int anio) {
        return this.anio == anio;
    }

    public boolean coincideDisponibilidad(boolean disponible) {
        return disponible == estaDisponible();
    }

    @Override
    public String toString() {
        return getTitulo() + " - " + getGenero() + " (" + anio + ")";
    }
}
