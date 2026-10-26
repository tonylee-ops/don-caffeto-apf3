package services;

import dao.MesaDAO;
import dao.PedidoDAO;
import modelo.DetallePedido;
import modelo.Mesa;
import modelo.Pedido;
import modelo.Usuario;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    static Pedido pedidoDeMesa(int id, int mesa, String estado, String estadoMesa, String total) {
        Pedido p = pedido(id, estado, total);
        p.setIdMesa(mesa);
        p.setNumeroMesa(mesa);
        p.setEstadoMesa(estadoMesa);
        return p;
    }

    static class MesaDAOFalso implements dao.MesaDAO {
        final List<Integer> cuentas = new ArrayList<>();

        @Override
        public List<modelo.Mesa> listar() {
            return new ArrayList<>();
        }

        @Override
        public boolean pedirCuenta(int idMesa) {
            cuentas.add(idMesa);
            return true;
        }
    }

    static class PedidoDAOFalso implements PedidoDAO {
        final List<String> cambios = new ArrayList<>();
        Pedido registrado;
        BigDecimal importeAgregado;

        @Override
        public int registrar(Pedido pedido) {
            registrado = pedido;
            return 10;
        }

        @Override
        public boolean agregarProductos(int idPedido, List<DetallePedido> nuevos, BigDecimal importe) {
            importeAgregado = importe;
            return true;
        }

        @Override
        public void agregarObservacion(int idPedido, int idUsuario, String texto) {
            cambios.add(idPedido + ":obs:" + texto);
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
        public Pedido pedidoDeMesa(int idMesa) {
            return null;
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
