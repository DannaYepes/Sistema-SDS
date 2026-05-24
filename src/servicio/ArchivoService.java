package servicio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import modelo.Alquiler;
import modelo.Pelicula;
import modelo.Reserva;
import modelo.Usuario;

public class ArchivoService {

    private static final String CARPETA_DATOS = "data";
    private static final String PELICULAS = "peliculas.dat";
    private static final String ALQUILERES = "alquileres.dat";
    private static final String RESERVAS = "reservas.dat";
    private static final String USUARIOS = "usuarios.dat";
    private static final String PRECIOS_FORMATO = "precios_formatos.dat";
    private static final String CONFIGURACION_NEGOCIO = "configuracion_negocio.dat";

    public static void guardarTodo() {
        asegurarCarpetaDatos();
        guardarObjeto(rutaDatos(PELICULAS), SistemaData.catalogo.getLista());
        guardarObjeto(rutaDatos(ALQUILERES), SistemaData.alquiler.getLista());
        guardarObjeto(rutaDatos(RESERVAS), SistemaData.reservas.getLista());
        guardarObjeto(rutaDatos(USUARIOS), SistemaData.usuarios.getLista());
        guardarObjeto(rutaDatos(PRECIOS_FORMATO), SistemaData.preciosFormato.getPrecios());
        guardarObjeto(rutaDatos(CONFIGURACION_NEGOCIO), SistemaData.configuracion);
    }

    @SuppressWarnings("unchecked")
    public static void cargarTodo() {
        asegurarCarpetaDatos();

        Object configuracion = cargarObjeto(CONFIGURACION_NEGOCIO);
        if (configuracion instanceof ConfiguracionNegocioService config) {
            SistemaData.configuracion = config;
        }

        Object precios = cargarObjeto(PRECIOS_FORMATO);
        if (precios instanceof Map<?, ?>) {
            SistemaData.preciosFormato.setPrecios((Map<String, Double>) precios);
        }

        Object peliculas = cargarObjeto(PELICULAS);
        if (peliculas instanceof ArrayList<?>) {
            SistemaData.catalogo.setLista((ArrayList<Pelicula>) peliculas);
        }

        Object alquileres = cargarObjeto(ALQUILERES);
        if (alquileres instanceof ArrayList<?>) {
            SistemaData.alquiler.setLista((ArrayList<Alquiler>) alquileres);
        }

        Object reservas = cargarObjeto(RESERVAS);
        if (reservas instanceof ArrayList<?>) {
            SistemaData.reservas.setLista((ArrayList<Reserva>) reservas);
        }

        Object usuarios = cargarObjeto(USUARIOS);
        if (usuarios instanceof ArrayList<?>) {
            SistemaData.usuarios.setLista((ArrayList<Usuario>) usuarios);
        }

        SistemaData.usuarios.inicializarUsuariosDefecto();
        SistemaData.reservas.depurarReservasVencidas();
        guardarTodo();
    }

    private static void asegurarCarpetaDatos() {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            System.out.println("No fue posible crear la carpeta de datos: " + carpeta.getAbsolutePath());
        }
    }

    private static String rutaDatos(String archivo) {
        return CARPETA_DATOS + File.separator + archivo;
    }

    private static void guardarObjeto(String ruta, Object objeto) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ruta))) {
            out.writeObject(objeto);
        } catch (IOException e) {
            System.out.println("No fue posible guardar el archivo " + ruta + ": " + e.getMessage());
        }
    }

    private static Object cargarObjeto(String archivo) {
        String rutaPrincipal = rutaDatos(archivo);
        Object objeto = cargarObjetoDesdeRuta(rutaPrincipal, false);
        if (objeto != null) {
            return objeto;
        }

        objeto = cargarObjetoDesdeRuta(archivo, true);
        if (objeto != null) {
            guardarObjeto(rutaPrincipal, objeto);
        }
        return objeto;
    }

    private static Object cargarObjetoDesdeRuta(String ruta, boolean mostrarAviso) {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return in.readObject();
        } catch (Exception e) {
            if (mostrarAviso) {
                System.out.println("No se pudieron leer los datos del archivo " + ruta + ": " + e.getMessage());
            }
            return null;
        }
    }
}
