package services;

import dao.PagoDAO;
import dao.PedidoDAO;
import modelo.Pago;
import modelo.Pedido;
import modelo.Usuario;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagoService {
    private static final BigDecimal FACTOR_IGV = new BigDecimal("1.18");

    private static final Logger LOG = LoggerFactory.getLogger(PagoService.class);

    private final PagoDAO pagoDAO;
    private final PedidoDAO pedidoDAO;

    public PagoService(PagoDAO pagoDAO, PedidoDAO pedidoDAO) {
        this.pagoDAO = pagoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public static BigDecimal igv(BigDecimal total) {
        BigDecimal base = total.divide(FACTOR_IGV, 2, RoundingMode.HALF_UP);
        return total.subtract(base);
    }

    public static BigDecimal vuelto(BigDecimal total, BigDecimal recibido) {
        BigDecimal v = recibido.subtract(total).setScale(2, RoundingMode.HALF_UP);
        return v.signum() < 0 ? BigDecimal.ZERO.setScale(2) : v;
    }

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

    public static boolean puedeCobrarse(Pedido p) {
        return p.getIdMesa() == null || "CUENTA".equals(p.getEstadoMesa());
    }

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
