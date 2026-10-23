package com.doncaffeto.service;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos y DAO falsos en memoria para probar sin MySQL.
 */
public class DatosFalsos {

    static final Usuario ADMIN = new Usuario(1, "admin", "Rosa", "ADMIN");
    static final Usuario MESERO = new Usuario(2, "mesero1", "Julio", "MESERO");
    static final Usuario COCINA = new Usuario(3, "cocina1", "Marta", "COCINA");
    static final Usuario CAJERO = new Usuario(4, "cajero1", "Sofía", "CAJERO");

    static Pedido pedido(int id, String estado, String total) {
        Pedido p = new Pedido();
        p.setIdPedido(id);
        p.setEstado(estado);
        p.setTotal(new BigDecimal(total));
        return p;
    }

    /** PedidoDAO que guarda en una lista y anota los cambios de estado. */
    static class PedidoDAOFalso implements PedidoDAO {
        final List<String> cambios = new ArrayList<>();
        Pedido registrado;

        @Override
        public int registrar(Pedido pedido) {
            registrado = pedido;
            return 10;
        }

        @Override
        public List<Pedido> listarPorFecha(LocalDate fecha) {
            return new ArrayList<>();
        }

        @Override
        public List<Pedido> listarPorEstado(String... estados) {
            return new ArrayList<>();
        }

        @Override
        public List<DetallePedido> listarDetalle(int idPedido) {
            return new ArrayList<>();
        }

        @Override
        public boolean cambiarEstado(int idPedido, String actual, String nuevo) {
            cambios.add(idPedido + ":" + actual + "->" + nuevo);
            return true;
        }
    }
}
