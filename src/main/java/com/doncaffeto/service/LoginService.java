package com.doncaffeto.service;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Validador;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inicio de sesión: valida la contraseña con BCrypt y bloquea al usuario tras 3 intentos fallidos.
 *
 * @author Equipo Don Caffeto
 */
public class LoginService {

    /** Intentos permitidos antes de bloquear. */
    public static final int MAX_INTENTOS = 3;

    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

    private final UsuarioDAO usuarioDAO;
    private final Map<String, Integer> intentosFallidos = new HashMap<>();

    /**
     * @param usuarioDAO acceso a datos de usuarios (se recibe la interfaz, no la clase MySQL)
     */
    public LoginService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * @param username nombre de acceso
     * @param password contraseña escrita por el usuario
     * @return el usuario si los datos son correctos
     * @throws IllegalArgumentException con el mensaje para mostrar si algo falla
     * @throws SQLException si falla la base de datos
     */
    public Usuario ingresar(String username, String password) throws SQLException {
        if (Validador.estaVacio(username) || Validador.estaVacio(password)) {
            throw new IllegalArgumentException("Ingrese usuario y contraseña.");
        }
        String user = username.trim().toLowerCase();
        int intentos = intentosFallidos.getOrDefault(user, 0);
        if (intentos >= MAX_INTENTOS) {
            LOG.warn("Usuario bloqueado intentó ingresar: {}", user);
            throw new IllegalArgumentException("Usuario bloqueado por " + MAX_INTENTOS + " intentos fallidos.");
        }
        Usuario u = usuarioDAO.buscarPorUsername(user);
        if (u == null || !BCrypt.checkpw(password, u.getPasswordHash())) {
            intentosFallidos.put(user, intentos + 1);
            LOG.warn("Inicio de sesión fallido: {}", user);
            // mismo mensaje si el usuario no existe o la clave está mal (no se revela cuál falló)
            throw new IllegalArgumentException("Usuario o contraseña incorrectos. Intentos restantes: "
                    + (MAX_INTENTOS - intentos - 1));
        }
        intentosFallidos.remove(user);
        LOG.info("Ingresó {} ({})", user, u.getRol());
        return u;
    }

    /**
     * @param rol rol del usuario
     * @return true si puede registrar pedidos (solo MESERO y ADMIN)
     */
    public static boolean puedeRegistrarPedidos(String rol) {
        return "MESERO".equals(rol) || "ADMIN".equals(rol);
    }
}
