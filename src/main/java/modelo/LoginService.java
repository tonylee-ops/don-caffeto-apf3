package modelo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {
    public static final int MAX_INTENTOS = 3;

    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

    private final UsuarioDAO usuarioDAO;
    private final Map<String, Integer> intentosFallidos = new HashMap<>();

    public LoginService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario ingresar(String username, String password) throws SQLException {
        if (Validador.estaVacio(username) || Validador.estaVacio(password)) {
            throw new IllegalArgumentException("Ingresa tu usuario y contraseña.");
        }
        String user = username.trim().toLowerCase();
        int intentos = intentosFallidos.getOrDefault(user, 0);
        if (intentos >= MAX_INTENTOS) {
            LOG.warn("Usuario bloqueado intentó ingresar: {}", user);
            throw new IllegalArgumentException("Usuario bloqueado por " + MAX_INTENTOS
                    + " intentos fallidos. Pide al administrador que lo revise.");
        }
        Usuario u = usuarioDAO.buscarPorUsername(user);
        if (u == null || !BCrypt.checkpw(password, u.getPasswordHash())) {
            intentosFallidos.put(user, intentos + 1);
            LOG.warn("Inicio de sesión fallido: {}", user);

            throw new IllegalArgumentException("Usuario o contraseña incorrectos. Intentos restantes: "
                    + (MAX_INTENTOS - intentos - 1));
        }
        intentosFallidos.remove(user);
        LOG.info("Ingresó {} ({})", user, u.getRol());
        return u;
    }
}
