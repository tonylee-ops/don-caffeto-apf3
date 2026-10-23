package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.PagoDAO;
import com.doncaffeto.model.Pago;
import com.doncaffeto.util.Conexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementación MySQL de {@link PagoDAO}. El cobro se guarda en una transacción.
 *
 * @author Equipo Don Caffeto
 */
public class PagoDAOImpl implements PagoDAO {

    @Override
    public void registrarCobro(Pago pago, BigDecimal subtotal, BigDecimal igv) throws SQLException {
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                // 1. el pedido debe seguir LISTO (evita cobrar dos veces el mismo pedido)
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE pedidos SET estado = 'PAGADO' WHERE id_pedido = ? AND estado = 'LISTO'")) {
                    ps.setInt(1, pago.getIdPedido());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("El pedido ya fue cobrado o todavía no está LISTO.");
                    }
                }
                // 2. pago
                int idPago;
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO pagos (id_pedido, id_usuario, metodo, "
                        + "monto, recibido, vuelto) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pago.getIdPedido());
                    ps.setInt(2, pago.getIdUsuario());
                    ps.setString(3, pago.getMetodo());
                    ps.setBigDecimal(4, pago.getMonto());
                    ps.setBigDecimal(5, pago.getRecibido());
                    ps.setBigDecimal(6, pago.getVuelto());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        idPago = rs.getInt(1);
                    }
                }
                // 3. venta
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO ventas (id_pedido, id_pago, subtotal, igv, total) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, pago.getIdPedido());
                    ps.setInt(2, idPago);
                    ps.setBigDecimal(3, subtotal);
                    ps.setBigDecimal(4, igv);
                    ps.setBigDecimal(5, pago.getMonto());
                    ps.executeUpdate();
                }
                // 4. la mesa queda libre
                PedidoDAOImpl.liberarMesa(cn, pago.getIdPedido());
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }
}
