package mvc;

import controlador.LoginController;
import modelo.Tema;
import vista.FrmLogin;
import java.awt.EventQueue;

public class App {
    public static void main(String[] args) {
        Tema.instalar();
        EventQueue.invokeLater(() -> new LoginController(new FrmLogin(), new Servicios()).iniciar());
    }
}
