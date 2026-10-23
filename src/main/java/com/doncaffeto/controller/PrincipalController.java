package com.doncaffeto.controller;

import com.doncaffeto.dao.impl.MesaDAOImpl;
import com.doncaffeto.dao.impl.PedidoDAOImpl;
import com.doncaffeto.dao.impl.ProductoDAOImpl;
import com.doncaffeto.dao.impl.UsuarioDAOImpl;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.LoginService;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.view.FrmLogin;
import com.doncaffeto.view.FrmPedido;
import com.doncaffeto.view.FrmPedidos;
import com.doncaffeto.view.FrmPrincipal;

/**
 * Controla el menú principal. Muestra solo las opciones permitidas para el rol.
 *
 * @author Equipo Don Caffeto
 */
public class PrincipalController {

    private final FrmPrincipal vista;
    private final Usuario usuario;
    private final PedidoService pedidoService = new PedidoService(new PedidoDAOImpl());

    /**
     * @param vista   menú principal
     * @param usuario usuario que inició sesión
     */
    public PrincipalController(FrmPrincipal vista, Usuario usuario) {
        this.vista = vista;
        this.usuario = usuario;
        vista.getLblUsuario().setText(usuario.getNombres() + " (" + usuario.getRol() + ")");
        vista.getBtnNuevoPedido().setVisible(LoginService.puedeRegistrarPedidos(usuario.getRol()));
        vista.getBtnNuevoPedido().addActionListener(e -> new PedidoController(new FrmPedido(), pedidoService,
                new ProductoDAOImpl(), new MesaDAOImpl(), usuario).iniciar());
        vista.getBtnPedidos().addActionListener(e ->
                new PedidosController(new FrmPedidos(), pedidoService, usuario).iniciar());
        vista.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
    }

    /** Muestra la pantalla. */
    public void iniciar() {
        vista.setVisible(true);
    }

    private void cerrarSesion() {
        vista.dispose();
        new LoginController(new FrmLogin(), new LoginService(new UsuarioDAOImpl())).iniciar();
    }
}
