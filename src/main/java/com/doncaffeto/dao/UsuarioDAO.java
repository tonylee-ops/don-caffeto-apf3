package com.doncaffeto.dao;

import com.doncaffeto.model.Usuario;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos de la tabla usuarios.
 *
 * @author Equipo Don Caffeto
 */
public interface UsuarioDAO {

    /**
     * @param username nombre de acceso
     * @return el usuario activo o null si no existe
     * @throws SQLException si falla la consulta
     */
    Usuario buscarPorUsername(String username) throws SQLException;

    /**
     * @return todos los usuarios con su rol
     * @throws SQLException si falla la consulta
     */
    List<Usuario> listar() throws SQLException;

    /**
     * Crea el usuario si su código es 0; si no, lo actualiza.
     *
     * @param usuario   datos del usuario
     * @param hashClave contraseña cifrada nueva (null para no cambiarla)
     * @throws SQLException si falla el registro (por ejemplo, usuario repetido)
     */
    void guardar(Usuario usuario, String hashClave) throws SQLException;
}
