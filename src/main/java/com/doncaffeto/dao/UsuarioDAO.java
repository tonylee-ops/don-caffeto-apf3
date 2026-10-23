package com.doncaffeto.dao;

import com.doncaffeto.model.Usuario;
import java.sql.SQLException;

/**
 * Acceso a datos de usuarios.
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
}
