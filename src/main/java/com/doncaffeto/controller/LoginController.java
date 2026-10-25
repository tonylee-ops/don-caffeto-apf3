package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.view.FrmLogin;
import com.doncaffeto.view.FrmPrincipal;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class LoginController {
    private final FrmLogin vista;
    private final Servicios servicios;

    public LoginController(FrmLogin vista, Servicios servicios) {
        this.vista = vista;
        this.servicios = servicios;
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
            Usuario u = servicios.login.ingresar(usuario, clave);
            vista.dispose();
            new PrincipalController(new FrmPrincipal(), u, servicios).iniciar();
        } catch (IllegalArgumentException e) {
            vista.getLblMensaje().setText(e.getMessage());
            vista.getPwdPassword().requestFocusInWindow();
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo conectar con MySQL.\n" + e.getMessage());
        }
    }
}
