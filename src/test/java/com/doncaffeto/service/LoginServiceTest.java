package com.doncaffeto.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServiceTest {
    private LoginService service;

    @Before
    public void preparar() {
        Usuario mesero = new Usuario(2, "mesero1", "Julio", "MESERO");
        mesero.setPasswordHash(BCrypt.hashpw("Caffeto2026!", BCrypt.gensalt()));
        UsuarioDAO daoFalso = new UsuarioDAO() {
            @Override
            public Usuario buscarPorUsername(String username) {
                return "mesero1".equals(username) ? mesero : null;
            }

            @Override
            public List<Usuario> listar() {
                return List.of(mesero);
            }

            @Override
            public void guardar(Usuario usuario, String hashClave) {
            }
        };
        service = new LoginService(daoFalso);
    }

    @Test
    public void ingresaConClaveCorrecta() throws Exception {
        assertEquals("MESERO", service.ingresar("Mesero1", "Caffeto2026!").getRol());
    }

    @Test
    public void mismoMensajeSiElUsuarioNoExiste() throws Exception {
        try {
            service.ingresar("intruso", "x");
            fail("Debió rechazarlo");
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
    public void permisosPorRol() {
        assertTrue(Autorizacion.permite(DatosFalsos.MESERO, Autorizacion.PEDIDOS));
        assertFalse(Autorizacion.permite(DatosFalsos.MESERO, Autorizacion.CAJA));
        assertTrue(Autorizacion.permite(DatosFalsos.CAJERO, Autorizacion.CAJA));
        assertFalse(Autorizacion.permite(DatosFalsos.COCINA, Autorizacion.ADMIN));
        assertTrue(Autorizacion.permite(DatosFalsos.ADMIN, Autorizacion.COCINA));
        assertFalse(Autorizacion.permite(null, Autorizacion.PEDIDOS));
    }
}
