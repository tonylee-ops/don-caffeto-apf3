package com.doncaffeto.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

public class PedidoServiceTest {
    private DatosFalsos.PedidoDAOFalso dao;
    private DatosFalsos.MesaDAOFalso mesas;
    private PedidoService service;
    private Pedido pedido;
    private Producto americano;
    private Producto sandwich;

    @Before
    public void preparar() {
        dao = new DatosFalsos.PedidoDAOFalso();
        mesas = new DatosFalsos.MesaDAOFalso();
        service = new PedidoService(dao, mesas);
        pedido = new Pedido();
        americano = new Producto(2, "Americano", "Cafés calientes", new BigDecimal("6.00"));
        sandwich = new Producto(10, "Sándwich de pollo", "Sándwiches", new BigDecimal("10.00"));
    }

    @Test
    public void calculaElTotalDelPedido() {
        service.agregarProducto(pedido, americano, 2, "");
        service.agregarProducto(pedido, sandwich, 1, null);
        assertEquals(new BigDecimal("22.00"), service.calcularTotal(pedido));
    }

    @Test
    public void mismoProductoYMismaNotaSumaLaCantidad() {
        service.agregarProducto(pedido, americano, 1, null);
        service.agregarProducto(pedido, americano, 2, "  ");
        assertEquals(1, pedido.getDetalles().size());
        assertEquals(3, pedido.getDetalles().get(0).getCantidad());
    }

    @Test
    public void otraNotaCreaOtraLinea() {
        service.agregarProducto(pedido, americano, 1, null);
        service.agregarProducto(pedido, americano, 1, "sin azúcar");
        assertEquals(2, pedido.getDetalles().size());
        assertNull(pedido.getDetalles().get(0).getNota());
        assertEquals("sin azúcar", pedido.getDetalles().get(1).getNota());
    }

    @Test
    public void restarHastaCeroQuitaLaLinea() {
        service.agregarProducto(pedido, americano, 1, null);
        service.cambiarCantidad(pedido, 0, +1);
        assertEquals(2, pedido.getDetalles().get(0).getCantidad());
        service.cambiarCantidad(pedido, 0, -1);
        service.cambiarCantidad(pedido, 0, -1);
        assertEquals(0, pedido.getDetalles().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noAceptaCantidadCero() {
        service.agregarProducto(pedido, americano, 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void salonNecesitaMesa() {
        pedido.setModalidad("SALON");
        service.agregarProducto(pedido, americano, 1, null);
        service.validar(pedido);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deliveryNecesitaTelefonoValido() {
        pedido.setModalidad("DELIVERY");
        pedido.setDireccion("Jr. Los Cafetales 120");
        pedido.setTelefono("123");
        service.agregarProducto(pedido, americano, 1, null);
        service.validar(pedido);
    }

    @Test
    public void guardarCalculaTotalYAsignaMesero() throws Exception {
        pedido.setModalidad("PARA_LLEVAR");
        pedido.setObservacion("   ");
        service.agregarProducto(pedido, sandwich, 2, null);
        assertEquals(10, service.guardar(pedido, DatosFalsos.MESERO));
        assertEquals(new BigDecimal("20.00"), dao.registrado.getTotal());
        assertEquals(2, dao.registrado.getIdUsuario());
        assertNull(dao.registrado.getObservacion());
    }

    @Test(expected = SecurityException.class)
    public void elCajeroNoPuedeCrearPedidos() throws Exception {
        pedido.setModalidad("PARA_LLEVAR");
        service.agregarProducto(pedido, sandwich, 1, null);
        service.guardar(pedido, DatosFalsos.CAJERO);
    }

    @Test
    public void agregarProductosSumaElImporteYMarcaLaNota() throws Exception {
        Pedido nuevos = new Pedido();
        service.agregarProducto(nuevos, sandwich, 2, null);
        service.agregarProducto(nuevos, americano, 1, "sin azúcar");
        service.agregarAPedido(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "OCUPADA", "22.00"), nuevos.getDetalles(),
                DatosFalsos.MESERO);
        assertEquals(new BigDecimal("26.00"), dao.importeAgregado);
        assertEquals("Agregado", nuevos.getDetalles().get(0).getNota());
        assertEquals("Agregado · sin azúcar", nuevos.getDetalles().get(1).getNota());
    }

    @Test
    public void elTecladoFijaLaCantidad() {
        service.agregarProducto(pedido, americano, 1, null);
        service.fijarCantidad(pedido, 0, 12);
        assertEquals(12, pedido.getDetalles().get(0).getCantidad());
    }

    @Test
    public void seAgreganObservacionesAUnPedidoEnCocina() throws Exception {
        service.agregarObservacion(DatosFalsos.pedidoDeMesa(5, 3, "PENDIENTE", "OCUPADA", "22.00"), "  sin cubiertos ",
                DatosFalsos.MESERO);
        assertEquals("5:obs:sin cubiertos", dao.cambios.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSeAgregaSiYaPidieronLaCuenta() throws Exception {
        Pedido nuevos = new Pedido();
        service.agregarProducto(nuevos, sandwich, 1, null);
        service.agregarAPedido(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "CUENTA", "22.00"), nuevos.getDetalles(),
                DatosFalsos.MESERO);
    }

    @Test
    public void laCuentaSePideCuandoLaCocinaTermino() throws Exception {
        service.pedirCuenta(DatosFalsos.pedidoDeMesa(5, 3, "LISTO", "OCUPADA", "22.00"), DatosFalsos.MESERO);
        assertEquals(Integer.valueOf(3), mesas.cuentas.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSePideCuentaSiLaCocinaNoTermina() throws Exception {
        service.pedirCuenta(DatosFalsos.pedidoDeMesa(5, 3, "EN_PREPARACION", "OCUPADA", "22.00"),
                DatosFalsos.MESERO);
    }

    @Test(expected = SecurityException.class)
    public void laCocinaNoVeLasMesas() throws Exception {
        service.mesas(DatosFalsos.COCINA);
    }

    @Test
    public void soloSeAnulanPedidosPendientes() throws Exception {
        service.anular(DatosFalsos.pedido(5, "PENDIENTE", "10.00"), DatosFalsos.MESERO);
        assertEquals("5:PENDIENTE->CANCELADO", dao.cambios.get(0));
        try {
            service.anular(DatosFalsos.pedido(6, "LISTO", "10.00"), DatosFalsos.MESERO);
            org.junit.Assert.fail("No debe anular un pedido LISTO");
        } catch (IllegalArgumentException e) {
            assertEquals(1, dao.cambios.size());
        }
    }
}
