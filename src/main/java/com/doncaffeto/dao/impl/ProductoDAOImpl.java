package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.ProductoDAO;
import com.doncaffeto.model.Producto;
import com.doncaffeto.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL de {@link ProductoDAO}.
 *
 * @author Equipo Don Caffeto
 */
public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public List<Producto> listarDisponibles() throws SQLException {
        String sql = "SELECT p.id_producto, p.nombre, c.nombre AS categoria, p.precio FROM producto p "
                + "JOIN categoria_producto c ON c.id_categoria = p.id_categoria "
                + "WHERE p.disponible = 1 ORDER BY c.id_categoria, p.nombre";
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Producto(rs.getInt("id_producto"), rs.getString("nombre"),
                        rs.getString("categoria"), rs.getBigDecimal("precio")));
            }
        }
        return lista;
    }
}
