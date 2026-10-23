package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.util.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL de {@link PedidoDAO}.
 *
 * @author Equipo Don Caffeto
 */
public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public int registrar(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedido (id_mesa, id_usuario, modalidad, direccion_entrega, telefono_contacto) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                int idPedido;
                try (PreparedStatement ps = cn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    if (pedido.getIdMesa() == null) {
                        ps.setNull(1, Types.INTEGER);
                    } else {
                        ps.setInt(1, pedido.getIdMesa());
                    }
                    ps.setInt(2, pedido.getIdUsuario());
                    ps.setString(3, pedido.getModalidad());
                    ps.setString(4, pedido.getDireccion());
                    ps.setString(5, pedido.getTelefono());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        idPedido = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                    for (DetallePedido d : pedido.getDetalles()) {
                        ps.setInt(1, idPedido);
                        ps.setInt(2, d.getIdProducto());
                        ps.setInt(3, d.getCantidad());
                        ps.setBigDecimal(4, d.getPrecioUnitario());
                        ps.executeUpdate();
                    }
                }
                try (CallableStatement cs = cn.prepareCall("{call sp_recalcular_pedido(?)}")) {
                    cs.setInt(1, idPedido);
                    cs.execute();
                }
                cn.commit();
                return idPedido;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Pedido> listarPorFecha(LocalDate fecha) throws SQLException {
        String sql = "SELECT p.id_pedido, p.created_at, p.modalidad, m.numero AS mesa, u.nombres AS mesero, "
                + "p.estado, p.total FROM pedido p JOIN usuario u ON u.id_usuario = p.id_usuario "
                + "LEFT JOIN mesa m ON m.id_mesa = p.id_mesa WHERE DATE(p.created_at) = ? ORDER BY p.id_pedido DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setIdPedido(rs.getInt("id_pedido"));
                    p.setFecha(rs.getTimestamp("created_at").toLocalDateTime());
                    p.setModalidad(rs.getString("modalidad"));
                    p.setNumeroMesa(rs.getObject("mesa", Integer.class));
                    p.setMesero(rs.getString("mesero"));
                    p.setEstado(rs.getString("estado"));
                    p.setTotal(rs.getBigDecimal("total"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    @Override
    public List<DetallePedido> listarDetalle(int idPedido) throws SQLException {
        String sql = "SELECT d.id_producto, pr.nombre, d.cantidad, d.precio_unitario FROM detalle_pedido d "
                + "JOIN producto pr ON pr.id_producto = d.id_producto WHERE d.id_pedido = ? ORDER BY d.id_detalle";
        List<DetallePedido> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetallePedido(rs.getInt("id_producto"), rs.getString("nombre"),
                            rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
                }
            }
        }
        return lista;
    }

    @Override
    public void cambiarEstado(int idPedido, String estado, int idUsuario) throws SQLException {
        try (Connection cn = Conexion.obtener();
             CallableStatement cs = cn.prepareCall("{call sp_cambiar_estado_pedido(?, ?, ?, ?)}")) {
            cs.setInt(1, idPedido);
            cs.setString(2, estado);
            cs.setInt(3, idUsuario);
            cs.setString(4, null);
            cs.execute();
        }
    }
}
