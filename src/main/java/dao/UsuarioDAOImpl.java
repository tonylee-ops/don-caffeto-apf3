package dao;

import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final String SELECT = "SELECT u.id_usuario, u.username, u.password_hash, u.nombres, u.activo, "
            + "r.nombre AS rol FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol ";

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT + "WHERE u.username = ? AND u.activo = 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT + "ORDER BY r.id_rol, u.nombres");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    @Override
    public void guardar(Usuario u, String hashClave) throws SQLException {
        String rol = "(SELECT id_rol FROM roles WHERE nombre = ?)";
        try (Connection cn = Conexion.obtener()) {
            if (u.getIdUsuario() == 0) {
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO usuarios (id_rol, username, "
                        + "password_hash, nombres, activo) VALUES (" + rol + ", ?, ?, ?, ?)")) {
                    ps.setString(1, u.getRol());
                    ps.setString(2, u.getUsername());
                    ps.setString(3, hashClave);
                    ps.setString(4, u.getNombres());
                    ps.setBoolean(5, u.isActivo());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = cn.prepareStatement("UPDATE usuarios SET id_rol = " + rol
                        + ", username = ?, nombres = ?, activo = ?, password_hash = COALESCE(?, password_hash) "
                        + "WHERE id_usuario = ?")) {
                    ps.setString(1, u.getRol());
                    ps.setString(2, u.getUsername());
                    ps.setString(3, u.getNombres());
                    ps.setBoolean(4, u.isActivo());
                    ps.setString(5, hashClave);
                    ps.setInt(6, u.getIdUsuario());
                    ps.executeUpdate();
                }
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(rs.getInt("id_usuario"), rs.getString("username"), rs.getString("nombres"),
                rs.getString("rol"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
