package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.UsuarioDAO;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementación MySQL de {@link UsuarioDAO}.
 *
 * @author Equipo Don Caffeto
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT u.id_usuario, u.username, u.password_hash, u.nombres, r.nombre AS rol "
                + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol WHERE u.username = ? AND u.activo = 1";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario(rs.getInt("id_usuario"), rs.getString("username"),
                        rs.getString("nombres"), rs.getString("rol"));
                u.setPasswordHash(rs.getString("password_hash"));
                return u;
            }
        }
    }
}
