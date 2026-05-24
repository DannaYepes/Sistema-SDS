package servicio;

import modelo.Usuario;

public class SeguridadService {

    public boolean login(String user, String pass, String tipo) {
        if (user == null || pass == null || tipo == null) {
            return false;
        }
        Usuario usuario = SistemaData.usuarios.login(user.trim(), pass.trim(), tipo.trim().toLowerCase());
        if (usuario == null) {
            return false;
        }
        SistemaData.iniciarSesion(usuario);
        return true;
    }
}
