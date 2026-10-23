package com.doncaffeto.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Pruebas de seguridad del inicio de sesión. Se usa un DAO falso en memoria en lugar de MySQL.
 */
public class LoginServiceTest {

    private LoginService service;

    @Before
    public void preparar() {
        Usuario mesero = new Usuario(2, "mesero1", "Julio", "MESERO");
        mesero.setPasswordHash(BCrypt.hashpw("Caffeto2026!", BCrypt.gensalt()));
        UsuarioDAO daoFalso = username -> "mesero1".equals(username) ? mesero : null;
        service = new LoginService(daoFalso);
    }

    @Test
    public void ingresaConClaveCorrecta() throws Exception {
        Usuario u = service.ingresar("Mesero1", "Caffeto2026!");
        assertEquals("MESERO", u.getRol());
    }

    @Test
    public void rechazaClaveIncorrecta() throws Exception {
        try {
            service.ingresar("mesero1", "otra");
            fail("Debió rechazar la clave");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Usuario o contraseña incorrectos"));
        }
    }

    @Test
    public void bloqueaDespuesDeTresIntentos() throws Exception {
        for (int i = 0; i < 3; i++) {
            try {
                service.ingresar("mesero1", "mala");
            } catch (IllegalArgumentException e) {
                // intento fallido esperado
            }
        }
        try {
            service.ingresar("mesero1", "Caffeto2026!");
            fail("Debió estar bloqueado");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("bloqueado"));
        }
    }

    @Test
    public void soloMeseroYAdminRegistranPedidos() {
        assertTrue(LoginService.puedeRegistrarPedidos("MESERO"));
        assertTrue(LoginService.puedeRegistrarPedidos("ADMIN"));
        assertFalse(LoginService.puedeRegistrarPedidos("CAJERO"));
    }
}
