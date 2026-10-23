package com.doncaffeto.service;

import static org.junit.Assert.assertEquals;

import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de las reglas de pedidos (no necesitan base de datos).
 */
public class PedidoServiceTest {

    private PedidoService service;
    private Pedido pedido;
    private Producto americano;
    private Producto sandwich;

    @Before
    public void preparar() {
        service = new PedidoService(null); // estas reglas no usan el DAO
        pedido = new Pedido();
        americano = new Producto(2, "Americano", "Cafés calientes", new BigDecimal("6.00"));
        sandwich = new Producto(16, "Sándwich de pollo", "Sándwiches", new BigDecimal("10.00"));
    }

    @Test
    public void calculaElTotalDelPedido() {
        service.agregarProducto(pedido, americano, 2);
        service.agregarProducto(pedido, sandwich, 1);
        assertEquals(new BigDecimal("22.00"), service.calcularTotal(pedido));
    }

    @Test
    public void mismoProductoSumaLaCantidad() {
        service.agregarProducto(pedido, americano, 1);
        service.agregarProducto(pedido, americano, 2);
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(3, pedido.getDetalles().get(0).getCantidad());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noAceptaCantidadCero() {
        service.agregarProducto(pedido, americano, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pedidoVacioNoEsValido() {
        pedido.setModalidad("PARA_LLEVAR");
        service.validar(pedido);
    }

    @Test(expected = IllegalArgumentException.class)
    public void salonNecesitaMesa() {
        pedido.setModalidad("SALON");
        service.agregarProducto(pedido, americano, 1);
        service.validar(pedido);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deliveryNecesitaTelefonoValido() {
        pedido.setModalidad("DELIVERY");
        pedido.setDireccion("Jr. Los Cafetales 120");
        pedido.setTelefono("123");
        service.agregarProducto(pedido, americano, 1);
        service.validar(pedido);
    }

    @Test
    public void deliveryCompletoEsValido() {
        pedido.setModalidad("DELIVERY");
        pedido.setDireccion("Jr. Los Cafetales 120");
        pedido.setTelefono("964333444");
        service.agregarProducto(pedido, americano, 1);
        service.validar(pedido); // no debe lanzar error
    }
}
