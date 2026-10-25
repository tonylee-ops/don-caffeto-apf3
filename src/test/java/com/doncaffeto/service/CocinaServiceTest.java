package com.doncaffeto.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CocinaServiceTest {
    @Test
    public void elPedidoAvanzaPasoAPaso() {
        assertEquals("EN_PREPARACION", CocinaService.siguienteEstado("PENDIENTE"));
        assertEquals("LISTO", CocinaService.siguienteEstado("EN_PREPARACION"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unPedidoListoNoAvanzaEnCocina() {
        CocinaService.siguienteEstado("LISTO");
    }

    @Test
    public void elCocineroAvanzaElPedido() throws Exception {
        DatosFalsos.PedidoDAOFalso dao = new DatosFalsos.PedidoDAOFalso();
        String nuevo = new CocinaService(dao).avanzar(DatosFalsos.pedido(3, "EN_PREPARACION", "21.00"),
                DatosFalsos.COCINA);
        assertEquals("LISTO", nuevo);
        assertEquals("3:EN_PREPARACION->LISTO", dao.cambios.get(0));
    }

    @Test(expected = SecurityException.class)
    public void elMeseroNoCambiaEstadosDeCocina() throws Exception {
        new CocinaService(new DatosFalsos.PedidoDAOFalso())
                .avanzar(DatosFalsos.pedido(3, "PENDIENTE", "21.00"), DatosFalsos.MESERO);
    }
}
