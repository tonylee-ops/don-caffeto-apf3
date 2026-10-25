package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {
    private static final String SELECT = "SELECT p.id_pedido, p.id_usuario, p.id_mesa, m.numero AS mesa, m.estado AS estado_mesa, "
            + "u.nombres AS mesero, p.modalidad, p.estado, p.total, p.direccion, p.telefono, p.creado_en, "
            + "(SELECT GROUP_CONCAT(o.texto SEPARATOR ' | ') FROM observaciones_pedido o "
            + "WHERE o.id_pedido = p.id_pedido) AS observacion "
            + "FROM pedidos p JOIN usuarios u ON u.id_usuario = p.id_usuario "
            + "LEFT JOIN mesas m ON m.id_mesa = p.id_mesa ";

    @Override
    public int registrar(Pedido pedido) throws SQLException {
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                int idPedido;
                try (PreparedStatement ps = cn.prepareStatement("INSERT INTO pedidos (id_usuario, id_mesa, "
                        + "modalidad, total, direccion, telefono) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, pedido.getIdUsuario());
                    if (pedido.getIdMesa() == null) {
                        ps.setNull(2, Types.INTEGER);
                    } else {
                        ps.setInt(2, pedido.getIdMesa());
                    }
                    ps.setString(3, pedido.getModalidad());
                    ps.setBigDecimal(4, pedido.getTotal());
                    ps.setString(5, pedido.getDireccion());
                    ps.setString(6, pedido.getTelefono());
                    ps.executeUpdate();
                    idPedido = idGenerado(ps);
                }
                for (DetallePedido d : pedido.getDetalles()) {
                    insertarLinea(cn, idPedido, d);
                }
                if (pedido.getObservacion() != null) {
                    try (PreparedStatement ps = cn.prepareStatement(
                            "INSERT INTO observaciones_pedido (id_pedido, id_usuario, texto) VALUES (?, ?, ?)")) {
                        ps.setInt(1, idPedido);
                        ps.setInt(2, pedido.getIdUsuario());
                        ps.setString(3, pedido.getObservacion());
                        ps.executeUpdate();
                    }
                }
                if (pedido.getIdMesa() != null) {
                    try (PreparedStatement ps = cn.prepareStatement("UPDATE mesas SET estado = 'OCUPADA', "
                            + "ocupada_desde = NOW() WHERE id_mesa = ? AND estado = 'LIBRE'")) {
                        ps.setInt(1, pedido.getIdMesa());
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("La mesa ya tiene un pedido. Actualiza el mapa de mesas.");
                        }
                    }
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
    public boolean agregarProductos(int idPedido, List<DetallePedido> nuevos, java.math.BigDecimal importe)
            throws SQLException {
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement("UPDATE pedidos p JOIN mesas m ON m.id_mesa = p.id_mesa "
                        + "SET p.total = p.total + ?, "
                        + "p.estado = CASE WHEN p.estado = 'LISTO' THEN 'PENDIENTE' ELSE p.estado END "
                        + "WHERE p.id_pedido = ? AND m.estado = 'OCUPADA' "
                        + "AND p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')")) {
                    ps.setBigDecimal(1, importe);
                    ps.setInt(2, idPedido);
                    if (ps.executeUpdate() == 0) {
                        cn.rollback();
                        return false;
                    }
                }
                for (DetallePedido d : nuevos) {
                    insertarLinea(cn, idPedido, d);
                }
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    @Override
    public void agregarObservacion(int idPedido, int idUsuario, String texto) throws SQLException {
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO observaciones_pedido (id_pedido, id_usuario, texto) VALUES (?, ?, ?)")) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idUsuario);
            ps.setString(3, texto);
            ps.executeUpdate();
        }
    }

    private static void insertarLinea(Connection cn, int idPedido, DetallePedido d) throws SQLException {
        int idDetalle;
        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO detalle_pedido (id_pedido, "
                + "id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getPrecioUnitario());
            ps.executeUpdate();
            idDetalle = idGenerado(ps);
        }
        if (d.getNota() != null) {
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO observaciones_detalle (id_detalle, texto) VALUES (?, ?)")) {
                ps.setInt(1, idDetalle);
                ps.setString(2, d.getNota());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public List<Pedido> listarPorFecha(LocalDate fecha) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT + "WHERE DATE(p.creado_en) = ? ORDER BY p.id_pedido DESC")) {
            ps.setObject(1, fecha);
            return leer(ps);
        }
    }

    @Override
    public List<Pedido> listarPorEstado(String... estados) throws SQLException {
        String marcas = String.join(", ", Collections.nCopies(estados.length, "?"));
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(SELECT + "WHERE p.estado IN (" + marcas + ") ORDER BY p.id_pedido")) {
            for (int i = 0; i < estados.length; i++) {
                ps.setString(i + 1, estados[i]);
            }
            return leer(ps);
        }
    }

    @Override
    public Pedido pedidoDeMesa(int idMesa) throws SQLException {
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(SELECT
                + "WHERE p.id_mesa = ? AND p.estado NOT IN ('PAGADO', 'CANCELADO') ORDER BY p.id_pedido DESC LIMIT 1")) {
            ps.setInt(1, idMesa);
            List<Pedido> lista = leer(ps);
            return lista.isEmpty() ? null : lista.get(0);
        }
    }

    @Override
    public List<DetallePedido> listarDetalle(int idPedido) throws SQLException {
        String sql = "SELECT d.id_producto, pr.nombre, d.cantidad, d.precio_unitario, "
                + "(SELECT GROUP_CONCAT(o.texto SEPARATOR ', ') FROM observaciones_detalle o "
                + "WHERE o.id_detalle = d.id_detalle) AS nota "
                + "FROM detalle_pedido d JOIN productos pr ON pr.id_producto = d.id_producto "
                + "WHERE d.id_pedido = ? ORDER BY d.id_detalle";
        List<DetallePedido> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido d = new DetallePedido(rs.getInt("id_producto"), rs.getString("nombre"),
                            rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario"));
                    d.setNota(rs.getString("nota"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    @Override
    public boolean cambiarEstado(int idPedido, String actual, String nuevo) throws SQLException {
        try (Connection cn = Conexion.obtener()) {
            cn.setAutoCommit(false);
            try {
                int filas;
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE pedidos SET estado = ? WHERE id_pedido = ? AND estado = ?")) {
                    ps.setString(1, nuevo);
                    ps.setInt(2, idPedido);
                    ps.setString(3, actual);
                    filas = ps.executeUpdate();
                }
                if (filas == 1 && "CANCELADO".equals(nuevo)) {
                    liberarMesa(cn, idPedido);
                }
                cn.commit();
                return filas == 1;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    static void liberarMesa(Connection cn, int idPedido) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE mesas SET estado = 'LIBRE', ocupada_desde = NULL WHERE id_mesa = "
                + "(SELECT id_mesa FROM pedidos WHERE id_pedido = ?)")) {
            ps.setInt(1, idPedido);
            ps.executeUpdate();
        }
    }

    private static int idGenerado(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private List<Pedido> leer(PreparedStatement ps) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setIdPedido(rs.getInt("id_pedido"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setIdMesa(rs.getObject("id_mesa", Integer.class));
                p.setNumeroMesa(rs.getObject("mesa", Integer.class));
                p.setEstadoMesa(rs.getString("estado_mesa"));
                p.setMesero(rs.getString("mesero"));
                p.setModalidad(rs.getString("modalidad"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getBigDecimal("total"));
                p.setDireccion(rs.getString("direccion"));
                p.setTelefono(rs.getString("telefono"));
                p.setObservacion(rs.getString("observacion"));
                p.setFecha(rs.getTimestamp("creado_en").toLocalDateTime());
                lista.add(p);
            }
        }
        return lista;
    }
}
