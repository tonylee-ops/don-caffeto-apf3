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

/**
 * Pantalla de cocina: consulta los pedidos y avanza su estado.
 *
 * @author Equipo Don Caffeto
 */
public class CocinaService {

    /** Estado siguiente de cada estado de cocina. */
    private static final Map<String, String> SIGUIENTE = ImmutableMap.of(
            "PENDIENTE", "EN_PREPARACION",
            "EN_PREPARACION", "LISTO");

    private static final Logger LOG = LoggerFactory.getLogger(CocinaService.class);

    private final PedidoDAO pedidoDAO;

    /**
     * @param pedidoDAO acceso a pedidos
     */
    public CocinaService(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    /**
     * @param estado estado actual
     * @return estado siguiente
     * @throws IllegalArgumentException si la cocina ya no puede avanzar ese pedido
     */
    public static String siguienteEstado(String estado) {
        String siguiente = SIGUIENTE.get(estado);
        if (siguiente == null) {
            throw new IllegalArgumentException("La cocina no puede cambiar un pedido " + estado + ".");
        }
        return siguiente;
    }

    /**
     * @param usuario cocinero o administrador
     * @return pedidos PENDIENTES y EN_PREPARACION, del más antiguo al más nuevo
     * @throws SQLException si falla la base de datos
     */
    public List<Pedido> pedidosEnCocina(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.COCINA);
        return pedidoDAO.listarPorEstado("PENDIENTE", "EN_PREPARACION");
    }

    /**
     * Pasa el pedido al estado siguiente (PENDIENTE → EN_PREPARACION → LISTO).
     *
     * @param pedido  pedido seleccionado
     * @param usuario cocinero o administrador
     * @return estado nuevo
     * @throws SQLException si falla la base de datos
     */
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
