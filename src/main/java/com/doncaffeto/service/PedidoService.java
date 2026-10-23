package com.doncaffeto.service;

import com.doncaffeto.dao.MesaDAO;
import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Validador;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reglas de negocio de los pedidos (lo usa el mesero).
 *
 * @author Equipo Don Caffeto
 */
public class PedidoService {

    private static final Logger LOG = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoDAO pedidoDAO;
    private final MesaDAO mesaDAO;

    /**
     * @param pedidoDAO acceso a pedidos
     * @param mesaDAO   acceso a mesas
     */
    public PedidoService(PedidoDAO pedidoDAO, MesaDAO mesaDAO) {
        this.pedidoDAO = pedidoDAO;
        this.mesaDAO = mesaDAO;
    }

    /**
     * Agrega un producto al pedido. Si ya estaba con la misma nota, suma la cantidad.
     *
     * @param pedido   pedido en registro
     * @param producto producto elegido
     * @param cantidad cantidad (mayor que 0)
     * @param nota     nota para cocina (puede estar vacía)
     */
    public void agregarProducto(Pedido pedido, Producto producto, int cantidad, String nota) {
        Preconditions.checkArgument(producto != null, "Selecciona un producto de la carta.");
        Preconditions.checkArgument(cantidad > 0, "La cantidad debe ser mayor que 0.");
        String n = StringUtils.trimToNull(nota);
        for (DetallePedido d : pedido.getDetalles()) {
            if (d.getIdProducto() == producto.getIdProducto() && StringUtils.equals(d.getNota(), n)) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        DetallePedido d = new DetallePedido(producto.getIdProducto(), producto.getNombre(), cantidad,
                producto.getPrecio());
        d.setNota(n);
        pedido.getDetalles().add(d);
    }

    /**
     * Suma o resta unidades a una línea. Si llega a 0, la línea se quita.
     *
     * @param pedido pedido en registro
     * @param indice posición de la línea
     * @param cambio +1 para sumar, -1 para restar
     */
    public void cambiarCantidad(Pedido pedido, int indice, int cambio) {
        DetallePedido d = pedido.getDetalles().get(indice);
        int nueva = d.getCantidad() + cambio;
        if (nueva <= 0) {
            pedido.getDetalles().remove(indice);
        } else {
            d.setCantidad(nueva);
        }
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
            throw new IllegalArgumentException("Agrega al menos un producto.");
        }
        if ("SALON".equals(pedido.getModalidad()) && pedido.getIdMesa() == null) {
            throw new IllegalArgumentException("Selecciona la mesa.");
        }
        if ("DELIVERY".equals(pedido.getModalidad())) {
            if (Validador.estaVacio(pedido.getDireccion())) {
                throw new IllegalArgumentException("Escribe la dirección de entrega.");
            }
            if (!Validador.esTelefono(pedido.getTelefono())) {
                throw new IllegalArgumentException("El teléfono debe tener 9 dígitos.");
            }
        }
    }

    /**
     * Valida y guarda el pedido. Queda PENDIENTE y aparece en la pantalla de cocina.
     *
     * @param pedido  pedido a guardar
     * @param usuario mesero que lo registra
     * @return número del pedido
     * @throws SQLException si falla la base de datos
     */
    public int guardar(Pedido pedido, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        validar(pedido);
        pedido.setIdUsuario(usuario.getIdUsuario());
        pedido.setTotal(calcularTotal(pedido));
        pedido.setObservacion(StringUtils.trimToNull(pedido.getObservacion()));
        if (pedido.getTelefono() != null) {
            pedido.setTelefono(StringUtils.deleteWhitespace(pedido.getTelefono()));
        }
        int id = pedidoDAO.registrar(pedido);
        LOG.info("Pedido {} registrado por {} ({})", id, usuario.getUsername(), pedido.getModalidad());
        return id;
    }

    /**
     * @return mesas libres para pedidos de salón
     * @throws SQLException si falla la base de datos
     */
    public List<Mesa> mesasLibres() throws SQLException {
        return mesaDAO.listarLibres();
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
     * @return productos del pedido con sus notas
     * @throws SQLException si falla la base de datos
     */
    public List<DetallePedido> detalle(int idPedido) throws SQLException {
        return pedidoDAO.listarDetalle(idPedido);
    }

    /**
     * Anula un pedido que la cocina todavía no empezó.
     *
     * @param pedido  pedido seleccionado
     * @param usuario mesero o administrador
     * @throws SQLException si falla la base de datos
     */
    public void anular(Pedido pedido, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo se anulan pedidos PENDIENTES (la cocina aún no los empezó).");
        }
        if (!pedidoDAO.cambiarEstado(pedido.getIdPedido(), "PENDIENTE", "CANCELADO")) {
            throw new IllegalArgumentException("El pedido ya cambió de estado. Actualiza la lista.");
        }
        LOG.info("Pedido {} anulado por {}", pedido.getIdPedido(), usuario.getUsername());
    }
}
