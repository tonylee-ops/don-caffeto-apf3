package com.doncaffeto.service;

import com.doncaffeto.dao.PagoDAO;
import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.Pago;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cobro de pedidos (lo usa el cajero).
 *
 * @author Equipo Don Caffeto
 */
public class PagoService {

    /** Los precios incluyen IGV del 18 %. */
    private static final BigDecimal FACTOR_IGV = new BigDecimal("1.18");

    private static final Logger LOG = LoggerFactory.getLogger(PagoService.class);

    private final PagoDAO pagoDAO;
    private final PedidoDAO pedidoDAO;

    /**
     * @param pagoDAO   acceso a pagos y ventas
     * @param pedidoDAO acceso a pedidos
     */
    public PagoService(PagoDAO pagoDAO, PedidoDAO pedidoDAO) {
        this.pagoDAO = pagoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    /**
     * @param total total con IGV incluido
     * @return IGV contenido en el total
     */
    public static BigDecimal igv(BigDecimal total) {
        BigDecimal base = total.divide(FACTOR_IGV, 2, RoundingMode.HALF_UP);
        return total.subtract(base);
    }

    /**
     * @param total    total a pagar
     * @param recibido dinero entregado por el cliente
     * @return vuelto con 2 decimales (0.00 si no alcanza)
     */
    public static BigDecimal vuelto(BigDecimal total, BigDecimal recibido) {
        BigDecimal v = recibido.subtract(total).setScale(2, RoundingMode.HALF_UP);
        return v.signum() < 0 ? BigDecimal.ZERO.setScale(2) : v;
    }

    /**
     * @param usuario cajero o administrador
     * @return pedidos LISTOS para cobrar (los de salón, solo si el mesero pidió la cuenta)
     * @throws SQLException si falla la base de datos
     */
    public List<Pedido> porCobrar(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.CAJA);
        List<Pedido> lista = new ArrayList<>();
        for (Pedido p : pedidoDAO.listarPorEstado("LISTO")) {
            if (puedeCobrarse(p)) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * @param p pedido LISTO
     * @return true si es para llevar o delivery, o si su mesa ya pidió la cuenta
     */
    public static boolean puedeCobrarse(Pedido p) {
        return p.getIdMesa() == null || "CUENTA".equals(p.getEstadoMesa());
    }

    /**
     * Registra el cobro en una transacción (pago + venta + pedido PAGADO + mesa libre en gris).
     *
     * @param pedido   pedido LISTO
     * @param metodo   EFECTIVO, YAPE, PLIN o TARJETA
     * @param recibido dinero recibido (solo efectivo; en los otros métodos es el total)
     * @param usuario  cajero o administrador
     * @return el pago registrado (con el vuelto)
     * @throws SQLException si falla la base de datos
     */
    public Pago cobrar(Pedido pedido, String metodo, BigDecimal recibido, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.CAJA);
        if (!"LISTO".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo se cobran pedidos LISTOS.");
        }
        if (!puedeCobrarse(pedido)) {
            throw new IllegalArgumentException("El mesero todavía no pidió la cuenta de esta mesa.");
        }
        BigDecimal total = pedido.getTotal();
        BigDecimal dinero = "EFECTIVO".equals(metodo) ? recibido : total;
        if (dinero == null || dinero.compareTo(total) < 0) {
            throw new IllegalArgumentException("El monto recibido no alcanza para pagar " + total + ".");
        }
        Pago pago = new Pago();
        pago.setIdPedido(pedido.getIdPedido());
        pago.setIdUsuario(usuario.getIdUsuario());
        pago.setMetodo(metodo);
        pago.setMonto(total);
        pago.setRecibido(dinero);
        pago.setVuelto(vuelto(total, dinero));
        BigDecimal igv = igv(total);
        pagoDAO.registrarCobro(pago, total.subtract(igv), igv);
        LOG.info("Pedido {} cobrado con {} por {}", pedido.getIdPedido(), metodo, usuario.getUsername());
        return pago;
    }
}
