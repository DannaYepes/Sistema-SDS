package modelo;

import java.io.Serializable;

public class Cliente implements Serializable {

    private static final long serialVersionUID = -6750224215669492456L;

    private String documento;
    private String nombre;
    private String celular;
    private int totalPeliculasRentadas;

    public Cliente(String nombre) {
        this("", nombre, "");
    }

    public Cliente(String documento, String nombre, String celular) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        this.documento = normalizar(documento);
        this.nombre = nombre.trim();
        this.celular = normalizar(celular);
        this.totalPeliculasRentadas = 0;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public String getDocumento() {
        return documento == null ? "" : documento;
    }

    public boolean tieneDocumento() {
        return !getDocumento().trim().isEmpty();
    }

    public String getNombre() {
        return nombre == null ? "" : nombre;
    }

    public String getCelular() {
        return celular == null ? "" : celular;
    }

    public void incrementarAlquileres() {
        totalPeliculasRentadas++;
    }

    public int getTotalPeliculasRentadas() {
        return totalPeliculasRentadas;
    }

    public String getIdentificacionVisible() {
        return tieneDocumento() ? getDocumento() + " - " + getNombre() : getNombre();
    }
}
