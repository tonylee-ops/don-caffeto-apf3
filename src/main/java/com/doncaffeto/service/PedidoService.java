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

public class PedidoService {
    private static final Logger LOG = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoDAO pedidoDAO;
    private final MesaDAO mesaDAO;

    public PedidoService(PedidoDAO pedidoDAO, MesaDAO mesaDAO) {
        this.pedidoDAO = pedidoDAO;
        this.mesaDAO = mesaDAO;
    }

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

    public void cambiarCantidad(Pedido pedido, int indice, int cambio) {
        DetallePedido d = pedido.getDetalles().get(indice);
        int nueva = d.getCantidad() + cambio;
        if (nueva <= 0) {
            pedido.getDetalles().remove(indice);
        } else {
            d.setCantidad(nueva);
        }
    }

    public BigDecimal calcularTotal(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : pedido.getDetalles()) {
            total = total.add(d.getImporte());
        }
        return total;
    }

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

    public List<Mesa> mesas(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        return ImmutableList.copyOf(mesaDAO.listar());
    }

    public Pedido pedidoDeMesa(Mesa mesa, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        return pedidoDAO.pedidoDeMesa(mesa.getIdMesa());
    }

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

    public void agregarObservacion(Pedido pedido, String texto, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.PEDIDOS);
        String t = StringUtils.trimToNull(texto);
        if (t == null) {
            throw new IllegalArgumentException("Escribe la observación.");
        }
        pedidoDAO.agregarObservacion(pedido.getIdPedido(), usuario.getIdUsuario(), StringUtils.abbreviate(t, 200));
    }

    public void fijarCantidad(Pedido pedido, int indice, int cantidad) {
        Preconditions.checkArgument(cantidad > 0 && cantidad <= 99, "La cantidad debe estar entre 1 y 99.");
        pedido.getDetalles().get(indice).setCantidad(cantidad);
    }

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

    public List<Pedido> listar(LocalDate fecha) throws SQLException {
        return ImmutableList.copyOf(pedidoDAO.listarPorFecha(fecha));
    }

    public List<DetallePedido> detalle(int idPedido) throws SQLException {
        return pedidoDAO.listarDetalle(idPedido);
    }

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
