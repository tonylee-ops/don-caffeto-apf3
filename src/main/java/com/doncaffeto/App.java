package com.doncaffeto;

import com.doncaffeto.controller.LoginController;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmLogin;
import java.awt.EventQueue;

public class App {
    public static void main(String[] args) {
        Tema.instalar();
        EventQueue.invokeLater(() -> new LoginController(new FrmLogin(), new Servicios()).iniciar());
    }
}
