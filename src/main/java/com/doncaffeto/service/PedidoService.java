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
     * Valida y guarda el pedido. Queda PENDIENTE, aparece en la pantalla de cocina y su mesa
     * pasa a naranja.
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
     * @param usuario mesero o administrador
     * @return las mesas del salón con su estado y cronómetro
     * @throws SQLException si falla la base de datos
     */
    public List<Mesa> mesas(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        return ImmutableList.copyOf(mesaDAO.listar());
    }

    /**
     * Los pedidos de salón solo se ven entrando a su mesa.
     *
     * @param mesa    mesa elegida en el mapa
     * @param usuario mesero o administrador
     * @return el pedido abierto de la mesa, o null si está libre
     * @throws SQLException si falla la base de datos
     */
    public Pedido pedidoDeMesa(Mesa mesa, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        return pedidoDAO.pedidoDeMesa(mesa.getIdMesa());
    }

    /**
     * Agrega más productos al pedido de una mesa. La cocina los ve con la nota «Agregado» y, si el
     * pedido ya estaba listo, vuelve a Pendiente para que los prepare.
     *
     * @param pedido  pedido de la mesa
     * @param nuevos  productos a agregar
     * @param usuario mesero o administrador
     * @throws SQLException si falla la base de datos
     */
    public void agregarAPedido(Pedido pedido, List<DetallePedido> nuevos, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        if (nuevos.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un producto.");
        }
        if ("CUENTA".equals(pedido.getEstadoMesa())) {
            throw new IllegalArgumentException("La cuenta ya fue pedida: no se pueden agregar productos.");
        }
        BigDecimal importe = BigDecimal.ZERO;
        for (DetallePedido d : nuevos) {
            importe = importe.add(d.getImporte());
            d.setNota(d.getNota() == null ? "Agregado" : "Agregado · " + d.getNota());
        }
        if (!pedidoDAO.agregarProductos(pedido.getIdPedido(), nuevos, importe)) {
            throw new IllegalArgumentException("No se pudo agregar: la mesa ya pidió la cuenta o el pedido se cerró.");
        }
        LOG.info("Mesa {}: {} productos agregados al pedido {}", pedido.getNumeroMesa(), nuevos.size(),
                pedido.getIdPedido());
    }

    /**
     * Agrega una observación general a un pedido que ya está en cocina.
     *
     * @param pedido  pedido de la mesa
     * @param texto   observación
     * @param usuario mesero o administrador
     * @throws SQLException si falla la base de datos
     */
    public void agregarObservacion(Pedido pedido, String texto, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        String t = StringUtils.trimToNull(texto);
        if (t == null) {
            throw new IllegalArgumentException("Escribe la observación.");
        }
        pedidoDAO.agregarObservacion(pedido.getIdPedido(), usuario.getIdUsuario(), StringUtils.abbreviate(t, 200));
    }

    /**
     * Fija la cantidad de una línea (lo usa el teclado numérico).
     *
     * @param pedido   pedido en registro
     * @param indice   posición de la línea
     * @param cantidad cantidad nueva (mayor que 0)
     */
    public void fijarCantidad(Pedido pedido, int indice, int cantidad) {
        Preconditions.checkArgument(cantidad > 0 && cantidad <= 99, "La cantidad debe estar entre 1 y 99.");
        pedido.getDetalles().get(indice).setCantidad(cantidad);
    }

    /**
     * El mesero pide la cuenta: la mesa pasa a verde y el pedido aparece en caja.
     * Solo se puede cuando la cocina ya terminó el pedido.
     *
     * @param pedido  pedido de la mesa
     * @param usuario mesero o administrador
     * @throws SQLException si falla la base de datos
     */
    public void pedirCuenta(Pedido pedido, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        if (pedido.getIdMesa() == null) {
            throw new IllegalArgumentException("Solo los pedidos de salón piden la cuenta.");
        }
        if (!"LISTO".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("La cocina todavía no termina este pedido.");
        }
        if (!mesaDAO.pedirCuenta(pedido.getIdMesa())) {
            throw new IllegalArgumentException("La cuenta de esta mesa ya fue pedida.");
        }
        LOG.info("Mesa {} pidió la cuenta (pedido {})", pedido.getNumeroMesa(), pedido.getIdPedido());
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
