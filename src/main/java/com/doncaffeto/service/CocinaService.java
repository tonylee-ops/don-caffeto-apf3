package com.doncaffeto.service;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.google.common.collect.ImmutableMap;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CocinaService {
    private static final Map<String, String> SIGUIENTE = ImmutableMap.of(
            "PENDIENTE", "EN_PREPARACION",
            "EN_PREPARACION", "LISTO");

    private static final Logger LOG = LoggerFactory.getLogger(CocinaService.class);

    private final PedidoDAO pedidoDAO;

    public CocinaService(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    public static String siguienteEstado(String estado) {
        String siguiente = SIGUIENTE.get(estado);
        if (siguiente == null) {
            throw new IllegalArgumentException("La cocina no puede cambiar un pedido " + estado + ".");
        }
        return siguiente;
    }

    public List<Pedido> pedidosEnCocina(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.COCINA);
        return pedidoDAO.listarPorEstado("PENDIENTE", "EN_PREPARACION");
    }

    public String avanzar(Pedido pedido, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.COCINA);
        String nuevo = siguienteEstado(pedido.getEstado());
        if (!pedidoDAO.cambiarEstado(pedido.getIdPedido(), pedido.getEstado(), nuevo)) {
            throw new IllegalArgumentException("El pedido ya cambió de estado. Actualiza la lista.");
        }
        LOG.info("Pedido {} pasó a {}", pedido.getIdPedido(), nuevo);
        return nuevo;
    }
}
