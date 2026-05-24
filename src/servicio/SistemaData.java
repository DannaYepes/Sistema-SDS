package servicio;

import modelo.Usuario;

public class SistemaData {

    public static CatalogoService catalogo = new CatalogoService();
    public static AlquilerService alquiler = new AlquilerService();
    public static ReservaService reservas = new ReservaService();
    public static UsuarioService usuarios = new UsuarioService();
    public static PrecioFormatoService preciosFormato = new PrecioFormatoService();
    public static ConfiguracionNegocioService configuracion = new ConfiguracionNegocioService();
    public static Usuario usuarioActual;
    private static boolean alertaOperativaMostradaEnSesion = false;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
        alertaOperativaMostradaEnSesion = false;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        alertaOperativaMostradaEnSesion = false;
    }

    public static boolean consumirAlertaOperativaSesion() {
        if (alertaOperativaMostradaEnSesion) {
            return false;
        }
        alertaOperativaMostradaEnSesion = true;
        return true;
    }

    public static boolean esClienteActual() {
        return usuarioActual != null && usuarioActual.getRol().equalsIgnoreCase("cliente");
    }

    public static boolean esEmpleadoActual() {
        return usuarioActual != null && usuarioActual.getRol().equalsIgnoreCase("empleado");
    }

    public static boolean esAdminActual() {
        return usuarioActual != null && usuarioActual.getRol().equalsIgnoreCase("admin");
    }

    public static boolean puedeGestionarUsuarios() {
        return esAdminActual();
    }

    public static String getRegistradorActual() {
        return usuarioActual == null ? "Sin usuario" : usuarioActual.getEtiquetaRegistro();
    }

    public static String getSesionTexto() {
        if (usuarioActual == null) {
            return "Sesión: sin usuario autenticado";
        }
        String extra = usuarioActual.getRol().equalsIgnoreCase("cliente")
                ? " | Nivel: " + usuarios.obtenerNivelCliente(usuarioActual)
                : "";
        return "Usuario: " + usuarioActual.getNombreCompleto()
                + " | Cuenta: " + usuarioActual.getUsuario()
                + " | Rol: " + usuarioActual.getRol().toUpperCase()
                + extra;
    }
}
