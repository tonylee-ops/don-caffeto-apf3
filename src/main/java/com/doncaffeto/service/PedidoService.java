package com.doncaffeto.service;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.util.Validador;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reglas de negocio de los pedidos.
 *
 * @author Equipo Don Caffeto
 */
public class PedidoService {

    private static final Logger LOG = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoDAO pedidoDAO;

    /**
     * @param pedidoDAO acceso a datos de pedidos (se recibe la interfaz, no la clase MySQL)
     */
    public PedidoService(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    /**
     * Agrega un producto al pedido. Si ya estaba, suma la cantidad.
     *
     * @param pedido   pedido en registro
     * @param producto producto elegido
     * @param cantidad cantidad (mayor que 0)
     */
    public void agregarProducto(Pedido pedido, Producto producto, int cantidad) {
        Preconditions.checkNotNull(producto, "Seleccione un producto de la carta.");
        Preconditions.checkArgument(cantidad > 0, "La cantidad debe ser mayor que 0.");
        for (DetallePedido d : pedido.getDetalles()) {
            if (d.getIdProducto() == producto.getIdProducto()) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        pedido.getDetalles().add(new DetallePedido(producto.getIdProducto(), producto.getNombre(),
                cantidad, producto.getPrecio()));
    }

    /**
     * @param pedido pedido
     * @return suma de los importes de todas las líneas
     */
    public BigDecimal calcularTotal(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : pedido.getDetalles()) {
            total = total.add(d.getImporte());
        }
        return total;
    }

    /**
     * Revisa que el pedido tenga los datos que exige su modalidad.
     *
     * @param pedido pedido a revisar
     * @throws IllegalArgumentException con el mensaje del dato que falta
     */
    public void validar(Pedido pedido) {
        if (pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto.");
        }
        if ("SALON".equals(pedido.getModalidad()) && pedido.getIdMesa() == null) {
            throw new IllegalArgumentException("Seleccione la mesa.");
        }
        if ("DELIVERY".equals(pedido.getModalidad())) {
            if (Validador.estaVacio(pedido.getDireccion())) {
                throw new IllegalArgumentException("Ingrese la dirección de entrega.");
            }
            if (!Validador.esTelefono(pedido.getTelefono())) {
                throw new IllegalArgumentException("El teléfono debe tener 9 dígitos.");
            }
        }
    }

    /**
     * Valida y guarda el pedido.
     *
     * @param pedido pedido a guardar
     * @return número del pedido
     * @throws SQLException si falla la base de datos
     */
    public int guardar(Pedido pedido) throws SQLException {
        validar(pedido);
        int id = pedidoDAO.registrar(pedido);
        LOG.info("Pedido {} registrado ({})", id, pedido.getModalidad());
        return id;
    }

    /**
     * @param fecha día a consultar
     * @return pedidos del día (lista que no se puede modificar)
     * @throws SQLException si falla la base de datos
     */
    public List<Pedido> listar(LocalDate fecha) throws SQLException {
        return ImmutableList.copyOf(pedidoDAO.listarPorFecha(fecha));
    }

    /**
     * @param idPedido número del pedido
     * @return líneas del pedido
     * @throws SQLException si falla la base de datos
     */
    public List<DetallePedido> detalle(int idPedido) throws SQLException {
        return pedidoDAO.listarDetalle(idPedido);
    }

    /**
     * Envía el pedido a cocina. Solo se confirman pedidos REGISTRADOS.
     *
     * @param pedido    pedido seleccionado
     * @param idUsuario usuario que confirma
     * @throws SQLException si falla la base de datos (por ejemplo, falta de stock)
     */
    public void confirmar(Pedido pedido, int idUsuario) throws SQLException {
        if (!"REGISTRADO".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo se confirman pedidos en estado REGISTRADO.");
        }
        pedidoDAO.cambiarEstado(pedido.getIdPedido(), "CONFIRMADO", idUsuario);
        LOG.info("Pedido {} confirmado", pedido.getIdPedido());
    }
}
