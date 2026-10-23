package com.doncaffeto.dao;

import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Acceso a datos de pedidos.
 *
 * @author Equipo Don Caffeto
 */
public interface PedidoDAO {

    /**
     * Guarda el pedido y su detalle en una sola transacción.
     *
     * @param pedido pedido a guardar
     * @return número del pedido generado
     * @throws SQLException si falla el registro
     */
    int registrar(Pedido pedido) throws SQLException;

    /**
     * @param fecha día a consultar
     * @return pedidos registrados ese día
     * @throws SQLException si falla la consulta
     */
    List<Pedido> listarPorFecha(LocalDate fecha) throws SQLException;

    /**
     * @param idPedido número del pedido
     * @return líneas del pedido
     * @throws SQLException si falla la consulta
     */
    List<DetallePedido> listarDetalle(int idPedido) throws SQLException;

    /**
     * Cambia el estado del pedido (usa el procedimiento sp_cambiar_estado_pedido).
     *
     * @param idPedido  número del pedido
     * @param estado    estado nuevo
     * @param idUsuario usuario que hace el cambio
     * @throws SQLException si el cambio no es válido (por ejemplo, falta de stock)
     */
    void cambiarEstado(int idPedido, String estado, int idUsuario) throws SQLException;
}
