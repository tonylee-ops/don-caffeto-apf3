package com.doncaffeto.controller;

import com.doncaffeto.dao.impl.MesaDAOImpl;
import com.doncaffeto.dao.impl.PedidoDAOImpl;
import com.doncaffeto.dao.impl.ProductoDAOImpl;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.LoginService;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.view.FrmLogin;
import com.doncaffeto.view.FrmPedido;
import com.doncaffeto.view.FrmPedidos;
import com.doncaffeto.view.FrmPrincipal;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Controla el menú principal. Muestra solo las opciones permitidas para el rol.
 *
 * @author Equipo Don Caffeto
 */
public class PrincipalController {

    private final FrmPrincipal vista;
    private final Usuario usuario;
    private final LoginService loginService;
    private final PedidoService pedidoService = new PedidoService(new PedidoDAOImpl());
    private JFrame ventanaPedido;
    private JFrame ventanaPedidos;

    /**
     * @param vista        menú principal
     * @param usuario      usuario que inició sesión
     * @param loginService servicio de login (se reutiliza al cerrar sesión)
     */
    public PrincipalController(FrmPrincipal vista, Usuario usuario, LoginService loginService) {
        this.vista = vista;
        this.usuario = usuario;
        this.loginService = loginService;
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "PE")));
        vista.getLblUsuario().setText(usuario.getNombres() + "  ·  " + usuario.getRol());
        vista.getLblSaludo().setText("¡Hola, " + usuario.getNombres() + "!");
        vista.getLblFecha().setText(fecha.substring(0, 1).toUpperCase() + fecha.substring(1));

        boolean puedeRegistrar = LoginService.puedeRegistrarPedidos(usuario.getRol());
        vista.getBtnMenuNuevoPedido().setVisible(puedeRegistrar);
        vista.getPnlCardNuevo().setVisible(puedeRegistrar);

        vista.getBtnMenuNuevoPedido().addActionListener(e -> abrirNuevoPedido());
        vista.getBtnNuevoPedido().addActionListener(e -> abrirNuevoPedido());
        vista.getBtnMenuPedidos().addActionListener(e -> abrirPedidos());
        vista.getBtnPedidos().addActionListener(e -> abrirPedidos());
        vista.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Mensajes.confirmar(vista, "¿Desea salir de Don Caffeto?")) {
                    System.exit(0);
                }
            }
        });
        // atajos de teclado: Ctrl+N nuevo pedido, Ctrl+P pedidos
        atajo(KeyEvent.VK_N, "nuevo", () -> { if (puedeRegistrar) { abrirNuevoPedido(); } });
        atajo(KeyEvent.VK_P, "pedidos", this::abrirPedidos);
    }

    /** Muestra la pantalla. */
    public void iniciar() {
        vista.setVisible(true);
    }

    private void atajo(int tecla, String nombre, Runnable accion) {
        vista.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla, KeyEvent.CTRL_DOWN_MASK), nombre);
        vista.getRootPane().getActionMap().put(nombre, new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                accion.run();
            }
        });
    }

    /** Si la ventana ya está abierta la trae al frente en lugar de abrir otra. */
    private boolean traerAlFrente(JFrame ventana) {
        if (ventana != null && ventana.isDisplayable()) {
            ventana.setExtendedState(Frame.NORMAL);
            ventana.toFront();
            return true;
        }
        return false;
    }

    private void abrirNuevoPedido() {
        if (!traerAlFrente(ventanaPedido)) {
            FrmPedido frm = new FrmPedido();
            new PedidoController(frm, pedidoService, new ProductoDAOImpl(), new MesaDAOImpl(), usuario).iniciar();
            ventanaPedido = frm;
        }
    }

    private void abrirPedidos() {
        if (!traerAlFrente(ventanaPedidos)) {
            FrmPedidos frm = new FrmPedidos();
            new PedidosController(frm, pedidoService, usuario).iniciar();
            ventanaPedidos = frm;
        }
    }

    private void cerrarSesion() {
        if (!Mensajes.confirmar(vista, "¿Cerrar la sesión de " + usuario.getNombres() + "?")) {
            return;
        }
        for (Window w : Window.getWindows()) {
            w.dispose();
        }
        new LoginController(new FrmLogin(), loginService).iniciar();
    }
}
