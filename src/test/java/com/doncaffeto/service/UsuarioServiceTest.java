package com.doncaffeto.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class UsuarioServiceTest {
    private String hashGuardado;
    private UsuarioService service;

    @Before
    public void preparar() {
        UsuarioDAO daoFalso = new UsuarioDAO() {
            @Override
            public Usuario buscarPorUsername(String username) {
                return null;
            }

            @Override
            public List<Usuario> listar() {
                return List.of();
            }

            @Override
            public void guardar(Usuario usuario, String hashClave) {
                hashGuardado = hashClave;
            }
        };
        service = new UsuarioService(daoFalso);
    }

    @Test
    public void laClaveSeGuardaCifrada() throws Exception {
        service.guardar(new Usuario(0, "Mesero2", "Ana Torres", "MESERO"), "ClaveSegura1", DatosFalsos.ADMIN);
        assertNotNull(hashGuardado);
        assertTrue(hashGuardado.startsWith("$2"));
    }

    @Test
    public void alEditarSinClaveNoSeCambia() throws Exception {
        service.guardar(new Usuario(2, "mesero1", "Julio Ramos", "MESERO"), "", DatosFalsos.ADMIN);
        assertNull(hashGuardado);
    }

    @Test(expected = IllegalArgumentException.class)
    public void usuarioNuevoNecesitaClaveDeOchoCaracteres() throws Exception {
        service.guardar(new Usuario(0, "mesero2", "Ana", "MESERO"), "123", DatosFalsos.ADMIN);
    }

    @Test(expected = SecurityException.class)
    public void soloElAdministradorGestionaUsuarios() throws Exception {
        service.guardar(new Usuario(0, "mesero2", "Ana", "MESERO"), "ClaveSegura1", DatosFalsos.CAJERO);
    }
}
