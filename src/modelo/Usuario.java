package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String clave;
    private String rol;
    private String documento;
    private String nombreCompleto;
    private String celular;
    private LocalDateTime fechaCreacion;
    private boolean activo;
    private int alquileresRegistrados;

    public Usuario(String usuario, String clave, String rol, String documento, String nombreCompleto, String celular) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (clave == null || clave.trim().length() < 3) {
            throw new IllegalArgumentException("La clave debe tener mínimo 3 caracteres.");
        }
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }
        this.usuario = usuario.trim();
        this.clave = clave.trim();
        this.rol = rol.trim().toLowerCase();
        this.documento = normalizar(documento);
        this.nombreCompleto = normalizar(nombreCompleto);
        this.celular = normalizar(celular);
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
        this.alquileresRegistrados = 0;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public boolean validarClave(String clave) {
        return activo && this.clave != null && this.clave.equals(clave == null ? "" : clave.trim());
    }

    public String getUsuario() {
        return usuario == null ? "" : usuario;
    }

    public String getClave() {
        return clave == null ? "" : clave;
    }

    public String getRol() {
        return rol == null ? "" : rol;
    }

    public String getDocumento() {
        return documento == null ? "" : documento;
    }

    public String getNombreCompleto() {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            return getUsuario();
        }
        return nombreCompleto;
    }

    public String getCelular() {
        return celular == null ? "" : celular;
    }

    public LocalDateTime getFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        return fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getAlquileresRegistrados() {
        return Math.max(0, alquileresRegistrados);
    }

    public void incrementarAlquileresRegistrados() {
        alquileresRegistrados = Math.max(0, alquileresRegistrados) + 1;
    }

    public void actualizarDatosCliente(String documento, String nombreCompleto, String celular) {
        this.documento = normalizar(documento);
        this.nombreCompleto = normalizar(nombreCompleto);
        this.celular = normalizar(celular);
    }

    public void actualizarDatosAdministrativos(String rol, String documento, String nombreCompleto, String celular, boolean activo) {
        this.rol = normalizar(rol).toLowerCase();
        this.documento = normalizar(documento);
        this.nombreCompleto = normalizar(nombreCompleto);
        this.celular = normalizar(celular);
        this.activo = activo;
    }

    public void cambiarClave(String nuevaClave) {
        if (nuevaClave == null || nuevaClave.trim().length() < 3 || nuevaClave.trim().length() > 20) {
            throw new IllegalArgumentException("La nueva clave debe tener entre 3 y 20 caracteres.");
        }
        this.clave = nuevaClave.trim();
    }

    public String getEtiquetaRegistro() {
        return getUsuario() + " (" + getRol() + ")";
    }
}
