package mvc;

import modelo.PedidoDAO;
import modelo.CategoriaDAOImpl;
import modelo.MesaDAOImpl;
import modelo.PagoDAOImpl;
import modelo.PedidoDAOImpl;
import modelo.ProductoDAOImpl;
import modelo.ReporteDAOImpl;
import modelo.UsuarioDAOImpl;
import modelo.CocinaService;
import modelo.LoginService;
import modelo.PagoService;
import modelo.PedidoService;
import modelo.ProductoService;
import modelo.ReporteService;
import modelo.UsuarioService;

public class Servicios {
    public final LoginService login;

    public final PedidoService pedidos;

    public final CocinaService cocina;

    public final PagoService pagos;

    public final ProductoService productos;

    public final UsuarioService usuarios;

    public final ReporteService reportes;

    public Servicios() {
        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
        login = new LoginService(usuarioDAO);
        pedidos = new PedidoService(pedidoDAO, new MesaDAOImpl());
        cocina = new CocinaService(pedidoDAO);
        pagos = new PagoService(new PagoDAOImpl(), pedidoDAO);
        productos = new ProductoService(new ProductoDAOImpl(), new CategoriaDAOImpl());
        usuarios = new UsuarioService(usuarioDAO);
        reportes = new ReporteService(new ReporteDAOImpl());
    }

    public Servicios(LoginService login, PedidoService pedidos, CocinaService cocina, PagoService pagos,
                     ProductoService productos, UsuarioService usuarios, ReporteService reportes) {
        this.login = login;
        this.pedidos = pedidos;
        this.cocina = cocina;
        this.pagos = pagos;
        this.productos = productos;
        this.usuarios = usuarios;
        this.reportes = reportes;
    }
}
