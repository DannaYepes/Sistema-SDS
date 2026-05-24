package servicio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PrecioFormatoService {

    private LinkedHashMap<String, Double> precios = new LinkedHashMap<>();

    public PrecioFormatoService() {
        cargarPreciosDefecto();
    }

    private void cargarPreciosDefecto() {
        precios.put("Digital", 8000.0);
        precios.put("DVD", 5000.0);
        precios.put("BluRay", 7000.0);
        precios.put("Streaming", 10000.0);
    }

    public ArrayList<String> obtenerFormatos() {
        return new ArrayList<>(precios.keySet());
    }

    public String[] obtenerFormatosArray() {
        ArrayList<String> formatos = obtenerFormatos();
        return formatos.toArray(new String[0]);
    }

    public double obtenerPrecio(String formato) {
        if (formato == null) {
            return precios.get("Digital");
        }
        for (Map.Entry<String, Double> entry : precios.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(formato.trim())) {
                return entry.getValue();
            }
        }
        return precios.get("Digital");
    }

    public void actualizarPrecio(String formato, double precio) {
        if (formato == null || formato.trim().isEmpty()) {
            throw new IllegalArgumentException("El formato es obligatorio.");
        }
        if (!existeFormato(formato)) {
            throw new IllegalArgumentException("El formato seleccionado no existe.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio por formato debe ser mayor a 0.");
        }
        String formatoReal = obtenerNombreReal(formato);
        precios.put(formatoReal, precio);
    }

    public boolean existeFormato(String formato) {
        return obtenerNombreReal(formato) != null;
    }

    private String obtenerNombreReal(String formato) {
        if (formato == null) {
            return null;
        }
        for (String key : precios.keySet()) {
            if (key.equalsIgnoreCase(formato.trim())) {
                return key;
            }
        }
        return null;
    }

    public LinkedHashMap<String, Double> getPrecios() {
        return new LinkedHashMap<>(precios);
    }

    public void setPrecios(Map<String, Double> nuevosPrecios) {
        cargarPreciosDefecto();
        if (nuevosPrecios == null) {
            return;
        }
        for (Map.Entry<String, Double> entry : nuevosPrecios.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0 && existeFormato(entry.getKey())) {
                actualizarPrecio(entry.getKey(), entry.getValue());
            }
        }
    }
}
