package services;

import dao.UsuarioDAO;
import modelo.Usuario;
import modelo.Validador;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioService {
    public static final String[] ROLES = {"ADMIN", "MESERO", "COCINA", "CAJERO"};

    public static final int MINIMO_CLAVE = 8;

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listar(Usuario admin) throws SQLException {
        Autorizacion.exigir(admin, Autorizacion.ADMIN);
        return usuarioDAO.listar();
    }

    public void validar(Usuario u, String clave, Usuario admin) {
        if (!StringUtils.isAlphanumeric(u.getUsername()) || u.getUsername().length() < 3) {
            throw new IllegalArgumentException("El usuario debe tener al menos 3 letras o números, sin espacios.");
        }
        if (Validador.estaVacio(u.getNombres())) {
            throw new IllegalArgumentException("Escribe los nombres.");
        }
        if (!StringUtils.equalsAny(u.getRol(), ROLES)) {
            throw new IllegalArgumentException("Elige un rol.");
        }
        boolean nuevo = u.getIdUsuario() == 0;
        if ((nuevo || !Validador.estaVacio(clave)) && StringUtils.length(clave) < MINIMO_CLAVE) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MINIMO_CLAVE + " caracteres.");
        }
        if (u.getIdUsuario() == admin.getIdUsuario() && !u.isActivo()) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario.");
        }
    }

    public void guardar(Usuario u, String clave, Usuario admin) throws SQLException {
        Autorizacion.exigir(admin, Autorizacion.ADMIN);
        u.setUsername(StringUtils.trimToEmpty(u.getUsername()).toLowerCase());
        u.setNombres(StringUtils.trim(u.getNombres()));
        validar(u, clave, admin);
        String hash = Validador.estaVacio(clave) ? null : BCrypt.hashpw(clave, BCrypt.gensalt());
        usuarioDAO.guardar(u, hash);
    }
}
