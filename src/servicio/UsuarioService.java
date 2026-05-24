package servicio;

import java.util.ArrayList;
import modelo.Usuario;
import util.InputValidators;

public class UsuarioService {

    private ArrayList<Usuario> lista = new ArrayList<>();

    public void inicializarUsuariosDefecto() {
        garantizarUsuarioDefecto("admin", "123", "admin", "Administrador");
        garantizarUsuarioDefecto("empleado", "123", "empleado", "Empleado");
    }

    private void garantizarUsuarioDefecto(String nombreUsuario, String clave, String rol, String nombreVisible) {
        Usuario existente = buscarPorUsuario(nombreUsuario);
        if (existente != null
                && existente.getRol().equalsIgnoreCase(rol)
                && existente.validarClave(clave)) {
            return;
        }
        lista.removeIf(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario));
        lista.add(new Usuario(nombreUsuario, clave, rol, "", nombreVisible, ""));
    }

    public Usuario login(String nombreUsuario, String clave, String rol) {
        Usuario usuario = buscarPorUsuario(nombreUsuario);
        if (usuario == null) {
            return null;
        }
        if (!usuario.getRol().equalsIgnoreCase(rol)) {
            return null;
        }
        if (!usuario.validarClave(clave)) {
            return null;
        }
        return usuario;
    }

    public Usuario crearUsuario(String nombreUsuario, String clave, String rol, String documento, String nombreCompleto, String celular) {
        validarDatosBasicos(nombreUsuario, clave, rol);
        validarDatosPerfil(rol, documento, nombreCompleto, celular, true);
        if (buscarPorUsuario(nombreUsuario) != null) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese nombre de usuario.");
        }
        Usuario nuevo = new Usuario(nombreUsuario, clave, rol, documento, nombreCompleto, celular);
        lista.add(nuevo);
        return nuevo;
    }

    public void actualizarUsuario(String usuario, String rol, String documento, String nombreCompleto, String celular, boolean activo, String nuevaClave) {
        if (!SistemaData.esAdminActual()) {
            throw new SecurityException("Solo el administrador puede modificar usuarios del sistema.");
        }
        Usuario existente = buscarPorUsuario(usuario);
        if (existente == null) {
            throw new IllegalArgumentException("No se encontró el usuario seleccionado.");
        }
        validarRol(rol);
        validarDatosPerfil(rol, documento, nombreCompleto, celular, false);

        boolean quedaSinAdmin = existente.getRol().equalsIgnoreCase("admin")
                && (!rol.equalsIgnoreCase("admin") || !activo)
                && contarAdministradoresActivos() <= 1;
        if (quedaSinAdmin) {
            throw new IllegalStateException("Debe quedar al menos un administrador activo en el sistema.");
        }

        existente.actualizarDatosAdministrativos(rol, documento, nombreCompleto, celular, activo);
        if (nuevaClave != null && !nuevaClave.trim().isEmpty()) {
            existente.cambiarClave(nuevaClave);
        }
    }

    private void validarDatosBasicos(String nombreUsuario, String clave, String rol) {
        if (nombreUsuario == null || !nombreUsuario.trim().matches("[A-Za-z0-9_]{4,20}")) {
            throw new IllegalArgumentException("El usuario debe ser alfanumérico, sin espacios, entre 4 y 20 caracteres.");
        }
        if (clave == null || clave.trim().length() < 3 || clave.trim().length() > 20) {
            throw new IllegalArgumentException("La clave debe tener entre 3 y 20 caracteres.");
        }
        validarRol(rol);
    }

    private void validarRol(String rol) {
        if (rol == null || !(rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("empleado") || rol.equalsIgnoreCase("cliente"))) {
            throw new IllegalArgumentException("El rol debe ser admin, empleado o cliente.");
        }
    }

    private void validarDatosPerfil(String rol, String documento, String nombreCompleto, String celular, boolean creando) {
        if (rol == null) {
            return;
        }
        String nombre = nombreCompleto == null ? "" : nombreCompleto.trim();
        if (nombre.isEmpty() && creando) {
            throw new IllegalArgumentException("El nombre visible es obligatorio.");
        }
        if (!nombre.isEmpty() && !InputValidators.nombreClienteValido(nombre)) {
            throw new IllegalArgumentException("El nombre debe ser alfanumérico y tener máximo 25 caracteres.");
        }
        if (rol.equalsIgnoreCase("cliente")) {
            if (!InputValidators.documentoValido(documento)) {
                throw new IllegalArgumentException("El documento del cliente debe ser numérico y tener entre 6 y 10 dígitos.");
            }
            if (!InputValidators.celularValido(celular)) {
                throw new IllegalArgumentException("El celular del cliente debe ser numérico y tener máximo 10 dígitos.");
            }
        } else {
            if (documento != null && !documento.trim().isEmpty() && !InputValidators.documentoValido(documento)) {
                throw new IllegalArgumentException("El documento debe ser numérico y tener entre 6 y 10 dígitos.");
            }
            if (celular != null && !celular.trim().isEmpty() && !InputValidators.celularValido(celular)) {
                throw new IllegalArgumentException("El celular debe ser numérico y tener máximo 10 dígitos.");
            }
        }
    }

    public Usuario buscarPorUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            return null;
        }
        for (Usuario u : lista) {
            if (u.getUsuario().equalsIgnoreCase(nombreUsuario.trim())) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarClientePorDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            return null;
        }
        for (Usuario u : lista) {
            if (u.getRol().equalsIgnoreCase("cliente") && u.getDocumento().equalsIgnoreCase(documento.trim())) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarClientePorDocumentoONombre(String documento, String nombre) {
        Usuario porDocumento = buscarClientePorDocumento(documento);
        if (porDocumento != null) {
            return porDocumento;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        for (Usuario u : lista) {
            if (u.getRol().equalsIgnoreCase("cliente") && u.getNombreCompleto().equalsIgnoreCase(nombre.trim())) {
                return u;
            }
        }
        return null;
    }

    public void registrarAlquilerCliente(String documento, String nombre) {
        Usuario usuario = buscarClientePorDocumentoONombre(documento, nombre);
        if (usuario != null) {
            usuario.incrementarAlquileresRegistrados();
        }
    }

    public int contarAdministradoresActivos() {
        int total = 0;
        for (Usuario u : lista) {
            if (u.getRol().equalsIgnoreCase("admin") && u.isActivo()) {
                total++;
            }
        }
        return total;
    }

    public int contarAlquileresHistoricosCliente(Usuario usuario) {
        if (usuario == null) {
            return 0;
        }
        int porHistorial = SistemaData.alquiler.contarAlquileresPorCliente(usuario.getDocumento(), usuario.getNombreCompleto());
        return Math.max(porHistorial, usuario.getAlquileresRegistrados());
    }

    public String obtenerNivelCliente(Usuario usuario) {
        int total = contarAlquileresHistoricosCliente(usuario);
        return nivelPorTotal(total);
    }

    public String obtenerNivelCliente(String documento, String nombre) {
        Usuario usuario = buscarClientePorDocumentoONombre(documento, nombre);
        int total = usuario == null
                ? SistemaData.alquiler.contarAlquileresPorCliente(documento, nombre)
                : contarAlquileresHistoricosCliente(usuario);
        return nivelPorTotal(total);
    }

    public double obtenerPorcentajeDescuento(String documento, String nombre) {
        int total = SistemaData.alquiler.contarAlquileresPorCliente(documento, nombre);
        if (total >= ConfiguracionNegocioService.ALQUILERES_NIVEL_PREMIUM) {
            return ConfiguracionNegocioService.DESCUENTO_PREMIUM;
        }
        if (total >= ConfiguracionNegocioService.ALQUILERES_NIVEL_FRECUENTE) {
            return ConfiguracionNegocioService.DESCUENTO_FRECUENTE;
        }
        return 0;
    }

    public boolean aplicaBonoGratisSiguienteAlquiler(String documento, String nombre) {
        int historico = SistemaData.alquiler.contarAlquileresPorCliente(documento, nombre);
        int siguiente = historico + 1;
        return siguiente > 0 && siguiente % ConfiguracionNegocioService.CADA_CUANTOS_ALQUILERES_BONO == 0;
    }

    public boolean esClientePremium(String documento, String nombre) {
        return "Premium".equalsIgnoreCase(obtenerNivelCliente(documento, nombre));
    }

    private String nivelPorTotal(int total) {
        if (total >= ConfiguracionNegocioService.ALQUILERES_NIVEL_PREMIUM) {
            return "Premium";
        }
        if (total >= ConfiguracionNegocioService.ALQUILERES_NIVEL_FRECUENTE) {
            return "Frecuente";
        }
        return "Nuevo";
    }

    public String resumenCliente(String documento, String nombre) {
        int total = SistemaData.alquiler.contarAlquileresPorCliente(documento, nombre);
        String nivel = obtenerNivelCliente(documento, nombre);
        double descuento = obtenerPorcentajeDescuento(documento, nombre) * 100;
        boolean bono = aplicaBonoGratisSiguienteAlquiler(documento, nombre);
        return "Nivel: " + nivel
                + " | Alquileres históricos: " + total
                + " | Descuento base: " + String.format("%.0f", descuento) + "%"
                + (bono ? " | Próximo alquiler gratis" : "");
    }

    public ArrayList<Usuario> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Usuario> lista) {
        if (lista != null) {
            this.lista = lista;
        }
    }
}
