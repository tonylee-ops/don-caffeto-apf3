package services;

import dao.PagoDAO;
import modelo.Pago;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

public class PagoServiceTest {
    private Pago registrado;
    private BigDecimal igvRegistrado;
    private PagoService service;

    @Before
    public void preparar() {
        PagoDAO daoFalso = (pago, subtotal, igv) -> {
            registrado = pago;
            igvRegistrado = igv;
        };
        service = new PagoService(daoFalso, new DatosFalsos.PedidoDAOFalso());
    }

    @Test
    public void calculaElIgvIncluidoEnElPrecio() {
        assertEquals(new BigDecimal("3.36"), PagoService.igv(new BigDecimal("22.00")));
    }

    @Test
    public void calculaElVuelto() {
        assertEquals(new BigDecimal("28.00"), PagoService.vuelto(new BigDecimal("22.00"), new BigDecimal("50.00")));
        assertEquals(new BigDecimal("0.00"), PagoService.vuelto(new BigDecimal("22.00"), new BigDecimal("10.00")));
    }

    @Test
    public void cobraEnEfectivoConVuelto() throws Exception {
        Pago p = service.cobrar(DatosFalsos.pedido(2, "LISTO", "28.00"), "EFECTIVO", new BigDecimal("50.00"),
                DatosFalsos.CAJERO);
        assertEquals(new BigDecimal("22.00"), p.getVuelto());
        assertEquals(4, registrado.getIdUsuario());
        assertEquals(new BigDecimal("4.27"), igvRegistrado);
    }

    @Test
    public void conYapeSeCobraElTotalExacto() throws Exception {
        Pago p = service.cobrar(DatosFalsos.pedido(2, "LISTO", "28.00"), "YAPE", null, DatosFalsos.CAJERO);
        assertEquals(new BigDecimal("28.00"), p.getRecibido());
        assertEquals(new BigDecimal("0.00"), p.getVuelto());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaEfectivoInsuficiente() throws Exception {
        service.cobrar(DatosFalsos.pedido(2, "LISTO", "28.00"), "EFECTIVO", new BigDecimal("20.00"),
                DatosFalsos.CAJERO);
    }

    @Test
    public void unaMesaSeCobraSoloCuandoPidieronLaCuenta() {
        assertEquals(false, PagoService.puedeCobrarse(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "OCUPADA", "22.00")));
        assertEquals(true, PagoService.puedeCobrarse(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "CUENTA", "22.00")));
        assertEquals(true, PagoService.puedeCobrarse(DatosFalsos.pedido(2, "LISTO", "28.00")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void noCobraUnaMesaSinCuentaPedida() throws Exception {
        service.cobrar(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "OCUPADA", "22.00"), "YAPE", null, DatosFalsos.CAJERO);
    }

    @Test
    public void cobraUnaMesaConCuentaPedida() throws Exception {
        service.cobrar(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "CUENTA", "22.00"), "YAPE", null, DatosFalsos.CAJERO);
        assertEquals(5, registrado.getIdPedido());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noCobraPedidosQueNoEstanListos() throws Exception {
        service.cobrar(DatosFalsos.pedido(3, "EN_PREPARACION", "21.00"), "YAPE", null, DatosFalsos.CAJERO);
    }

    @Test(expected = SecurityException.class)
    public void elMeseroNoPuedeCobrar() throws Exception {
        service.cobrar(DatosFalsos.pedido(2, "LISTO", "28.00"), "YAPE", null, DatosFalsos.MESERO);
    }
}
