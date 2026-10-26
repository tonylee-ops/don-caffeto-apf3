package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {
    @Override
    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT id_categoria, nombre FROM categorias ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
            }
        }
        return lista;
    }
}
