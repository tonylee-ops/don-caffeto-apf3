package modelo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface PedidoDAO {
    int registrar(Pedido pedido) throws SQLException;

    boolean agregarProductos(int idPedido, List<DetallePedido> nuevos, java.math.BigDecimal importe)
            throws SQLException;

    void agregarObservacion(int idPedido, int idUsuario, String texto) throws SQLException;

    List<Pedido> listarPorFecha(LocalDate fecha) throws SQLException;

    List<Pedido> listarPorEstado(String... estados) throws SQLException;

    Pedido pedidoDeMesa(int idMesa) throws SQLException;

    List<DetallePedido> listarDetalle(int idPedido) throws SQLException;

    boolean cambiarEstado(int idPedido, String actual, String nuevo) throws SQLException;
}
