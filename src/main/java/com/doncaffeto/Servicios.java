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
