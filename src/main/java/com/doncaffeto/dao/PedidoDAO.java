package com.doncaffeto.dao;

import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Acceso a datos de pedidos, su detalle y sus observaciones.
 *
 * @author Equipo Don Caffeto
 */
public interface PedidoDAO {

    /**
     * Guarda el pedido, su detalle y sus observaciones en una sola transacción y ocupa la mesa
     * (pasa a naranja y empieza su cronómetro).
     *
     * @param pedido pedido a guardar
     * @return número del pedido generado
     * @throws SQLException si falla el registro o si la mesa ya estaba ocupada
     */
    int registrar(Pedido pedido) throws SQLException;

    /**
     * Agrega productos a un pedido de mesa que aún no pidió la cuenta, en una sola transacción:
     * guarda las líneas, suma su importe al total y, si el pedido ya estaba LISTO, lo devuelve a
     * PENDIENTE para que la cocina prepare lo nuevo.
     *
     * @param idPedido número del pedido
     * @param nuevos   productos a agregar
     * @param importe  suma de los importes nuevos
     * @return false si la mesa ya pidió la cuenta o el pedido se cerró
     * @throws SQLException si falla el registro
     */
    boolean agregarProductos(int idPedido, List<DetallePedido> nuevos, java.math.BigDecimal importe)
            throws SQLException;

    /**
     * @param fecha día a consultar
     * @return pedidos registrados ese día
     * @throws SQLException si falla la consulta
     */
    List<Pedido> listarPorFecha(LocalDate fecha) throws SQLException;

    /**
     * @param estados estados a buscar (por ejemplo PENDIENTE y EN_PREPARACION para cocina)
     * @return pedidos en esos estados, del más antiguo al más nuevo
     * @throws SQLException si falla la consulta
     */
    List<Pedido> listarPorEstado(String... estados) throws SQLException;

    /**
     * @param idMesa código de la mesa
     * @return el pedido abierto (no pagado ni cancelado) de la mesa, o null si no tiene
     * @throws SQLException si falla la consulta
     */
    Pedido pedidoDeMesa(int idMesa) throws SQLException;

    /**
     * @param idPedido número del pedido
     * @return productos del pedido con sus notas
     * @throws SQLException si falla la consulta
     */
    List<DetallePedido> listarDetalle(int idPedido) throws SQLException;

    /**
     * Cambia el estado solo si el pedido sigue en el estado esperado. Si se cancela, libera la mesa.
     *
     * @param idPedido número del pedido
     * @param actual   estado que debe tener ahora
     * @param nuevo    estado nuevo
     * @return true si se cambió; false si otro usuario ya lo había cambiado
     * @throws SQLException si falla la actualización
     */
    boolean cambiarEstado(int idPedido, String actual, String nuevo) throws SQLException;
}
