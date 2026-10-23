package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.ReporteDAO;
import com.doncaffeto.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL de {@link ReporteDAO}.
 *
 * @author Equipo Don Caffeto
 */
public class ReporteDAOImpl implements ReporteDAO {

    @Override
    public List<Object[]> ventasPorDia(LocalDate desde, LocalDate hasta) throws SQLException {
        return consultar("SELECT DATE_FORMAT(v.fecha, '%d/%m/%Y'), COUNT(*), SUM(v.total) FROM ventas v "
                + "WHERE DATE(v.fecha) BETWEEN ? AND ? GROUP BY DATE(v.fecha), DATE_FORMAT(v.fecha, '%d/%m/%Y') "
                + "ORDER BY DATE(v.fecha)", desde, hasta);
    }

    @Override
    public List<Object[]> productosMasVendidos(LocalDate desde, LocalDate hasta) throws SQLException {
        return consultar("SELECT pr.nombre, SUM(d.cantidad), SUM(d.subtotal) FROM ventas v "
                + "JOIN detalle_pedido d ON d.id_pedido = v.id_pedido JOIN productos pr ON pr.id_producto = d.id_producto "
                + "WHERE DATE(v.fecha) BETWEEN ? AND ? GROUP BY pr.id_producto, pr.nombre "
                + "ORDER BY SUM(d.cantidad) DESC, pr.nombre", desde, hasta);
    }

    @Override
    public List<Object[]> ventasPorMetodo(LocalDate desde, LocalDate hasta) throws SQLException {
        return consultar("SELECT pa.metodo, COUNT(*), SUM(v.total) FROM ventas v JOIN pagos pa ON pa.id_pago = v.id_pago "
                + "WHERE DATE(v.fecha) BETWEEN ? AND ? GROUP BY pa.metodo ORDER BY SUM(v.total) DESC", desde, hasta);
    }

    private List<Object[]> consultar(String sql, LocalDate desde, LocalDate hasta) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getString(1), rs.getInt(2), rs.getBigDecimal(3)});
                }
            }
        }
        return filas;
    }
}
