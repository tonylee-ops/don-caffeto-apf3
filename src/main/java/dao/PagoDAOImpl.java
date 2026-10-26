package dao;

import modelo.Pago;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PagoDAOImpl implements PagoDAO {
    @Override
    public void registrarCobro(Pago pago, BigDecimal subtotal, BigDecimal igv) throws SQLException {
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE pedidos SET estado = 'PAGADO' WHERE id_pedido = ? AND estado = 'LISTO'")) {
                    ps.setInt(1, pago.getIdPedido());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("El pedido ya fue cobrado o todavía no está LISTO.");
                    }
                }

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

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO ventas (id_pedido, id_pago, subtotal, igv, total) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, pago.getIdPedido());
                    ps.setInt(2, idPago);
                    ps.setBigDecimal(3, subtotal);
                    ps.setBigDecimal(4, igv);
                    ps.setBigDecimal(5, pago.getMonto());
                    ps.executeUpdate();
                }

                PedidoDAOImpl.liberarMesa(cn, pago.getIdPedido());
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }
}
