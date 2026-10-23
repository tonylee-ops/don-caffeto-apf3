package com.doncaffeto.controller;

import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.LoginService;
import com.doncaffeto.view.FrmLogin;
import com.doncaffeto.view.FrmPrincipal;
import java.sql.SQLException;
import javax.swing.JOptionPane;

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
    }

    /** Muestra la pantalla. */
    public void iniciar() {
        vista.setVisible(true);
    }

    private void ingresar() {
        String usuario = vista.getTxtUsuario().getText();
        String clave = new String(vista.getPwdPassword().getPassword());
        vista.getPwdPassword().setText("");
        try {
            Usuario u = loginService.ingresar(usuario, clave);
            vista.dispose();
            new PrincipalController(new FrmPrincipal(), u).iniciar();
        } catch (IllegalArgumentException e) {
            vista.getLblMensaje().setText(e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "No se pudo conectar con la base de datos.\n" + e.getMessage());
        }
    }
}
