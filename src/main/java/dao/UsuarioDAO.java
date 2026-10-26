package dao;

import modelo.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    Usuario buscarPorUsername(String username) throws SQLException;

    List<Usuario> listar() throws SQLException;

    void guardar(Usuario usuario, String hashClave) throws SQLException;
}
