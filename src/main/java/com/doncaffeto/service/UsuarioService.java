package com.doncaffeto.service;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Validador;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Gestión de usuarios (solo administrador). Las contraseñas se guardan cifradas con BCrypt.
 *
 * @author Equipo Don Caffeto
 */
public class UsuarioService {

    /** Roles que existen en la base de datos. */
    public static final String[] ROLES = {"ADMIN", "MESERO", "COCINA", "CAJERO"};

    /** Largo mínimo de contraseña. */
    public static final int MINIMO_CLAVE = 8;

    private final UsuarioDAO usuarioDAO;

    /**
     * @param usuarioDAO acceso a usuarios
     */
    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * @param admin administrador en sesión
     * @return todos los usuarios
     * @throws SQLException si falla la base de datos
     */
    public List<Usuario> listar(Usuario admin) throws SQLException {
        Autorizacion.exigir(admin, Autorizacion.ADMIN);
        return usuarioDAO.listar();
    }

    /**
     * Revisa los datos del usuario y su contraseña.
     *
     * @param u     usuario
     * @param clave contraseña escrita (vacía = no cambiarla, solo en usuarios existentes)
     * @param admin administrador en sesión
     * @throws IllegalArgumentException con el dato incorrecto
     */
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

    /**
     * @param u     usuario nuevo (código 0) o existente
     * @param clave contraseña nueva (vacía = no cambiarla)
     * @param admin administrador en sesión
     * @throws SQLException si falla la base de datos (por ejemplo, usuario repetido)
     */
    public void guardar(Usuario u, String clave, Usuario admin) throws SQLException {
        Autorizacion.exigir(admin, Autorizacion.ADMIN);
        u.setUsername(StringUtils.trimToEmpty(u.getUsername()).toLowerCase());
        u.setNombres(StringUtils.trim(u.getNombres()));
        validar(u, clave, admin);
        String hash = Validador.estaVacio(clave) ? null : BCrypt.hashpw(clave, BCrypt.gensalt());
        usuarioDAO.guardar(u, hash);
    }
}
