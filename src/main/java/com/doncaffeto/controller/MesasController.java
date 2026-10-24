package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.view.FrmMesa;
import com.doncaffeto.view.FrmMesas;
import com.doncaffeto.view.FrmPedido;
import com.doncaffeto.view.componentes.TarjetaMesa;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * Mapa de mesas del mesero. Los cronómetros avanzan cada segundo y los colores se vuelven a leer de
 * MySQL cada 10 segundos (así se ve cuando la caja cobra y la mesa vuelve a gris).
 *
 * @author Equipo Don Caffeto
 */
public class MesasController {

    private final FrmMesas vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final List<TarjetaMesa> tarjetas = new ArrayList<>();
    private final Timer cronometros;
    private final Timer recarga;

    /**
     * @param vista     mapa de mesas
     * @param servicios servicios de la aplicación
     * @param usuario   mesero o administrador
     */
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
                cargar(); // al volver de otra ventana, el mapa se pone al día
            }

            @Override
            public void windowClosed(WindowEvent e) {
                cronometros.stop();
                recarga.stop();
            }
        });
    }

    /** Dibuja las mesas, inicia los cronómetros y muestra la pantalla. */
    public void iniciar() {
        cargar();
        cronometros.start();
        recarga.start();
        vista.setVisible(true);
    }

    /** Lee las mesas de MySQL y actualiza las tarjetas. */
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

    /** Mesa gris: toma el pedido. Mesa naranja o verde: muestra su pedido. */
    private void abrir(Mesa mesa) {
        if (mesa.isLibre()) {
            nuevoPedido(mesa, "SALON");
        } else {
            new MesaController(new FrmMesa(), servicios, usuario, mesa, this::cargar).iniciar();
        }
    }

    private void nuevoPedido(Mesa mesa, String modalidad) {
        new PedidoController(new FrmPedido(), servicios, usuario, mesa, modalidad, this::cargar).iniciar();
    }
}
