package controlador;

import mvc.Servicios;
import modelo.Mesa;
import modelo.Usuario;
import vista.FrmMesa;
import vista.FrmMesas;
import vista.FrmPedido;
import vista.TarjetaMesa;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class MesasController {
    private final FrmMesas vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final List<TarjetaMesa> tarjetas = new ArrayList<>();
    private final Timer cronometros;
    private final Timer recarga;

    public MesasController(FrmMesas vista, Servicios servicios, Usuario usuario) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        cronometros = new Timer(1000, e -> tarjetas.forEach(TarjetaMesa::repaint));
        recarga = new Timer(10_000, e -> cargar());
        vista.getBtnParaLlevar().addActionListener(e -> nuevoPedido(null, "PARA_LLEVAR"));
        vista.getBtnDelivery().addActionListener(e -> nuevoPedido(null, "DELIVERY"));
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargar();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                cronometros.stop();
                recarga.stop();
            }
        });
    }

    public void iniciar() {
        cargar();
        cronometros.start();
        recarga.start();
        vista.setVisible(true);
    }

    private void cargar() {
        List<Mesa> mesas;
        try {
            mesas = servicios.pedidos.mesas(usuario);
        } catch (SQLException | SecurityException e) {
            recarga.stop();
            Mensajes.de(vista, e);
            return;
        }
        if (tarjetas.size() != mesas.size()) {
            vista.getPnlMesas().removeAll();
            tarjetas.clear();
            for (Mesa m : mesas) {
                TarjetaMesa t = new TarjetaMesa(m);
                t.addActionListener(e -> abrir(t.getMesa()));
                tarjetas.add(t);
                vista.getPnlMesas().add(t);
            }
            vista.getPnlMesas().revalidate();
        } else {
            for (int i = 0; i < mesas.size(); i++) {
                tarjetas.get(i).setMesa(mesas.get(i));
            }
        }
        long libres = mesas.stream().filter(Mesa::isLibre).count();
        long cuenta = mesas.stream().filter(m -> "CUENTA".equals(m.getEstado())).count();
        vista.getLblResumen().setText(libres + " libres  ·  " + (mesas.size() - libres - cuenta) + " en cocina  ·  "
                + cuenta + " con cuenta pedida");
    }

    private void abrir(Mesa mesa) {
        new MesaController(new FrmMesa(), servicios, usuario, mesa, this::cargar).iniciar();
    }

    private void nuevoPedido(Mesa mesa, String modalidad) {
        new PedidoController(new FrmPedido(), servicios, usuario, mesa, modalidad, this::cargar).iniciar();
    }
}
