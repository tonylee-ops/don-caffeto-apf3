package com.doncaffeto;

import com.doncaffeto.controller.LoginController;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmLogin;
import java.awt.EventQueue;

/**
 * Inicio de la aplicación Don Caffeto.
 *
 * @author Equipo Don Caffeto
 */
public class App {

    /**
     * Aplica el tema naranja y abre el inicio de sesión.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        Tema.instalar();
        EventQueue.invokeLater(() -> new LoginController(new FrmLogin(), new Servicios()).iniciar());
    }
}
