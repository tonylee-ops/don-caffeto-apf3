package controlador;

import modelo.Usuario;
import mvc.Servicios;
import services.Autorizacion;
import vista.FrmCaja;
import vista.FrmCocina;
import vista.FrmLogin;
import vista.FrmMesas;
import vista.FrmPedidos;
import vista.FrmPrincipal;
import vista.FrmProductos;
import vista.FrmReportes;
import vista.FrmUsuarios;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrincipalController {
    private final FrmPrincipal vista;
    private final Usuario usuario;
    private final Servicios servicios;
    private final Map<String, JFrame> abiertas = new HashMap<>();

    public PrincipalController(FrmPrincipal vista, Usuario usuario, Servicios servicios) {
        this.vista = vista;
        this.usuario = usuario;
        this.servicios = servicios;
        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "PE")));
        vista.getLblUsuario().setText(usuario.getNombres() + "  ·  " + usuario.getRol());
        vista.getLblSaludo().setText("¡Hola, " + usuario.getNombres().split(" ")[0] + "!");
        vista.getLblFecha().setText(Character.toUpperCase(hoy.charAt(0)) + hoy.substring(1));

        modulo("mesas", Autorizacion.PEDIDOS, vista.getBtnMenuMesas(), vista.getBtnMesas(),
                vista.getPnlCardMesas(), () -> {
                    FrmMesas f = new FrmMesas();
                    new MesasController(f, servicios, usuario).iniciar();
                    return f;
                });

        modulo("pedidos", Autorizacion.ADMIN, vista.getBtnMenuPedidos(),
                vista.getBtnPedidos(), vista.getPnlCardPedidos(), () -> {
                    FrmPedidos f = new FrmPedidos();
                    new PedidosController(f, servicios, usuario).iniciar();
                    return f;
                });
        modulo("cocina", Autorizacion.COCINA, vista.getBtnMenuCocina(), vista.getBtnCocina(),
                vista.getPnlCardCocina(), () -> {
                    FrmCocina f = new FrmCocina();
                    new CocinaController(f, servicios, usuario).iniciar();
                    return f;
                });
        modulo("caja", Autorizacion.CAJA, vista.getBtnMenuCaja(), vista.getBtnCaja(), vista.getPnlCardCaja(), () -> {
            FrmCaja f = new FrmCaja();
            new CajaController(f, servicios, usuario).iniciar();
            return f;
        });
        modulo("productos", Autorizacion.ADMIN, vista.getBtnMenuProductos(), vista.getBtnProductos(),
                vista.getPnlCardProductos(), () -> {
                    FrmProductos f = new FrmProductos();
                    new ProductosController(f, servicios, usuario).iniciar();
                    return f;
                });
        modulo("usuarios", Autorizacion.ADMIN, vista.getBtnMenuUsuarios(), vista.getBtnUsuarios(),
                vista.getPnlCardUsuarios(), () -> {
                    FrmUsuarios f = new FrmUsuarios();
                    new UsuariosController(f, servicios, usuario).iniciar();
                    return f;
                });
        modulo("reportes", Autorizacion.ADMIN, vista.getBtnMenuReportes(), vista.getBtnReportes(),
                vista.getPnlCardReportes(), () -> {
                    FrmReportes f = new FrmReportes();
                    new ReportesController(f, servicios, usuario).iniciar();
                    return f;
                });

        vista.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Mensajes.confirmar(vista, "¿Deseas salir de Don Caffeto?")) {
                    System.exit(0);
                }
            }
        });
    }

    public void iniciar() {
        vista.setVisible(true);
    }

    private void modulo(String clave, String[] roles, JButton menu, JButton tarjeta, JPanel panel,
                        Supplier<JFrame> abrir) {
        boolean permitido = Autorizacion.permite(usuario, roles);
        menu.setVisible(permitido);
        panel.setVisible(permitido);
        Runnable accion = () -> {
            JFrame ventana = abiertas.get(clave);
            if (ventana != null && ventana.isDisplayable()) {
                ventana.setExtendedState(Frame.NORMAL);
                ventana.toFront();
            } else {
                abiertas.put(clave, abrir.get());
            }
        };
        menu.addActionListener(e -> accion.run());
        tarjeta.addActionListener(e -> accion.run());
    }

    private void cerrarSesion() {
        if (!Mensajes.confirmar(vista, "¿Cerrar la sesión de " + usuario.getNombres() + "?")) {
            return;
        }
        for (Window w : Window.getWindows()) {
            w.dispose();
        }
        new LoginController(new FrmLogin(), servicios).iniciar();
    }
}
