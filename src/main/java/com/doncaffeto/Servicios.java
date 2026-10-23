package com.doncaffeto;

import com.doncaffeto.dao.PedidoDAO;
import com.doncaffeto.dao.impl.CategoriaDAOImpl;
import com.doncaffeto.dao.impl.MesaDAOImpl;
import com.doncaffeto.dao.impl.PagoDAOImpl;
import com.doncaffeto.dao.impl.PedidoDAOImpl;
import com.doncaffeto.dao.impl.ProductoDAOImpl;
import com.doncaffeto.dao.impl.ReporteDAOImpl;
import com.doncaffeto.dao.impl.UsuarioDAOImpl;
import com.doncaffeto.service.CocinaService;
import com.doncaffeto.service.LoginService;
import com.doncaffeto.service.PagoService;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.service.ProductoService;
import com.doncaffeto.service.ReporteService;
import com.doncaffeto.service.UsuarioService;

/**
 * Único lugar donde se crean las clases MySQL (DAOImpl) y se entregan a los servicios.
 * El resto del programa solo conoce las interfaces DAO (principio de inversión de dependencias).
 *
 * @author Equipo Don Caffeto
 */
public class Servicios {

    /** Inicio de sesión. */
    public final LoginService login;
    /** Pedidos del mesero. */
    public final PedidoService pedidos;
    /** Cocina. */
    public final CocinaService cocina;
    /** Caja. */
    public final PagoService pagos;
    /** Productos. */
    public final ProductoService productos;
    /** Usuarios. */
    public final UsuarioService usuarios;
    /** Reportes. */
    public final ReporteService reportes;

    /** Crea los servicios conectados a MySQL. */
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

    /**
     * Arma la aplicación con servicios ya creados (por ejemplo, con DAOs de prueba).
     *
     * @param login     inicio de sesión
     * @param pedidos   pedidos
     * @param cocina    cocina
     * @param pagos     caja
     * @param productos productos
     * @param usuarios  usuarios
     * @param reportes  reportes
     */
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
