package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {
    @Override
    public List<Producto> listar(boolean soloDisponibles) throws SQLException {
        String sql = "SELECT p.id_producto, p.id_categoria, p.nombre, p.precio, p.disponible, c.nombre AS categoria "
                + "FROM productos p JOIN categorias c ON c.id_categoria = p.id_categoria "
                + (soloDisponibles ? "WHERE p.disponible = 1 " : "") + "ORDER BY c.id_categoria, p.nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto(rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("categoria"),
                        rs.getBigDecimal("precio"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setDisponible(rs.getBoolean("disponible"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public void guardar(Producto p) throws SQLException {
        String sql = p.getIdProducto() == 0
                ? "INSERT INTO productos (id_categoria, nombre, precio, disponible) VALUES (?, ?, ?, ?)"
                : "UPDATE productos SET id_categoria = ?, nombre = ?, precio = ?, disponible = ? WHERE id_producto = ?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdCategoria());
            ps.setString(2, p.getNombre());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setBoolean(4, p.isDisponible());
            if (p.getIdProducto() != 0) {
                ps.setInt(5, p.getIdProducto());
            }
            ps.executeUpdate();
        }
    }
}
