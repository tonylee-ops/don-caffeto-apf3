package com.doncaffeto.controller;

import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.LoginService;
import com.doncaffeto.view.FrmLogin;
import com.doncaffeto.view.FrmPrincipal;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Controla la pantalla de inicio de sesión.
 *
 * @author Equipo Don Caffeto
 */
public class LoginController {

    private final FrmLogin vista;
    private final LoginService loginService;

    /**
     * @param vista        pantalla de login
     * @param loginService servicio de inicio de sesión
     */
    public LoginController(FrmLogin vista, LoginService loginService) {
        this.vista = vista;
        this.loginService = loginService;
        vista.getBtnIngresar().addActionListener(e -> ingresar());
        vista.getChkMostrar().addActionListener(e ->
                vista.getPwdPassword().setEchoChar(vista.getChkMostrar().isSelected() ? (char) 0 : '•'));
        KeyAdapter limpiarMensaje = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                vista.getLblMensaje().setText(" ");
            }
        };
        vista.getTxtUsuario().addKeyListener(limpiarMensaje);
        vista.getPwdPassword().addKeyListener(limpiarMensaje);
    }

    /** Muestra la pantalla con el cursor en el usuario. */
    public void iniciar() {
        vista.getPwdPassword().setEchoChar('•');
        vista.setVisible(true);
        vista.getTxtUsuario().requestFocusInWindow();
    }

    private void ingresar() {
        String usuario = vista.getTxtUsuario().getText();
        String clave = new String(vista.getPwdPassword().getPassword());
        vista.getPwdPassword().setText("");
        try {
            Usuario u = loginService.ingresar(usuario, clave);
            vista.dispose();
            new PrincipalController(new FrmPrincipal(), u, loginService).iniciar();
        } catch (IllegalArgumentException e) {
            vista.getLblMensaje().setText(e.getMessage());
            vista.getPwdPassword().requestFocusInWindow();
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo conectar con la base de datos.\nRevise que MySQL esté encendido.");
        }
    }
}
