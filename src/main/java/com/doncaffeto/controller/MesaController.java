package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.PagoService;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmMesa;
import com.doncaffeto.view.componentes.TarjetaMesa;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * Pedido de una mesa: es el único lugar donde el mesero ve el pedido de salón. Desde aquí pide la
 * cuenta (cuando la cocina terminó) o anula el pedido (si la cocina aún no lo empezó).
 *
 * @author Equipo Don Caffeto
 */
public class MesaController {

    private final FrmMesa vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final Mesa mesa;
    private final Runnable alCambiar;
    private final Timer cronometro;
    private final Timer recarga;
    private Pedido pedido;

    /**
     * @param vista     pantalla de la mesa
     * @param servicios servicios de la aplicación
     * @param usuario   mesero o administrador
     * @param mesa      mesa elegida en el mapa
     * @param alCambiar se ejecuta cuando la mesa cambia de estado (para refrescar el mapa)
     */
    public MesaController(FrmMesa vista, Servicios servicios, Usuario usuario, Mesa mesa, Runnable alCambiar) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        this.mesa = mesa;
        this.alCambiar = alCambiar;
        cronometro = new Timer(1000, e -> vista.getLblCronometro().setText(
                TarjetaMesa.cronometro(mesa.getSegundosOcupada())));
        recarga = new Timer(10_000, e -> cargar());
        vista.getLblTitulo().setText("Mesa " + mesa.getNumero());
        vista.setTitle("Don Caffeto - Mesa " + mesa.getNumero());
        vista.getBtnPedirCuenta().addActionListener(e -> pedirCuenta());
        vista.getBtnAnular().addActionListener(e -> anular());
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cronometro.stop();
                recarga.stop();
            }
        });
    }

    /** Carga el pedido de la mesa y muestra la pantalla. */
    public void iniciar() {
        if (cargar()) {
            cronometro.start();
            recarga.start();
            vista.setVisible(true);
        }
    }

    /**
     * Vuelve a leer el pedido (la cocina pudo cambiar su estado).
     *
     * @return false si la mesa ya no tiene pedido
     */
    private boolean cargar() {
        try {
            pedido = servicios.pedidos.pedidoDeMesa(mesa, usuario);
        } catch (SQLException | SecurityException e) {
            Mensajes.de(vista, e);
            return false;
        }
        if (pedido == null) {
            Mensajes.info(vista, "La mesa " + mesa.getNumero() + " ya está libre.");
            alCambiar.run();
            vista.dispose();
            return false;
        }
        mesa.setEstado(pedido.getEstadoMesa());
        boolean cuenta = "CUENTA".equals(pedido.getEstadoMesa());
        boolean listo = "LISTO".equals(pedido.getEstado());
        vista.getLblEstadoMesa().setText(cuenta ? "Cuenta pedida · esperando el pago en caja"
                : listo ? "Pedido listo · pide la cuenta cuando el cliente termine" : "Pedido en cocina");
        vista.getLblEstadoMesa().setForeground(Tema.colorMesa(pedido.getEstadoMesa()));
        vista.getLblSeleccion().setText("Pedido N° " + pedido.getIdPedido() + "  ·  Mesero: " + pedido.getMesero());
        String estado = Tablas.ESTADO.get(pedido.getEstado());
        vista.getLblEstadoCocina().setText(listo ? "Listo para servir" : estado);
        vista.getLblEstadoCocina().setForeground(Tema.color(estado));
        vista.getLblObservacion().setText(pedido.getObservacion() == null ? " " : "Observación: " + pedido.getObservacion());
        List<Object[]> filas = new ArrayList<>();
        try {
            for (DetallePedido d : servicios.pedidos.detalle(pedido.getIdPedido())) {
                filas.add(new Object[]{d.getProducto(), Tablas.nota(d), d.getCantidad(), d.getImporte()});
            }
        } catch (SQLException e) {
            Mensajes.de(vista, e);
        }
        Tablas.llenar(vista.getTblDetalle(), filas);
        vista.getLblTotal().setText(Tema.soles(pedido.getTotal()));
        vista.getLblIgv().setText("Incluye IGV 18 %: " + Tema.soles(PagoService.igv(pedido.getTotal())));
        vista.getLblCronometro().setText(TarjetaMesa.cronometro(mesa.getSegundosOcupada()));
        vista.getLblCronometro().setForeground(Tema.colorMesa(pedido.getEstadoMesa()));

        // la cuenta se pide cuando la cocina terminó; anular solo si la cocina aún no empezó
        vista.getBtnPedirCuenta().setVisible(!cuenta);
        vista.getBtnPedirCuenta().setEnabled(listo);
        vista.getBtnPedirCuenta().setToolTipText(listo ? null : "Disponible cuando la cocina marque el pedido como listo");
        vista.getBtnAnular().setVisible("PENDIENTE".equals(pedido.getEstado()));
        return true;
    }

    private void pedirCuenta() {
        if (!Mensajes.confirmar(vista, "¿Pedir la cuenta de la mesa " + mesa.getNumero() + " por "
                + Tema.soles(pedido.getTotal()) + "?\nEl pedido pasará a caja para el cobro.")) {
            return;
        }
        try {
            servicios.pedidos.pedirCuenta(pedido, usuario);
            alCambiar.run();
            cargar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }

    private void anular() {
        if (!Mensajes.confirmar(vista, "¿Anular el pedido de la mesa " + mesa.getNumero() + "?\n"
                + "La cocina dejará de verlo y la mesa quedará libre.")) {
            return;
        }
        try {
            servicios.pedidos.anular(pedido, usuario);
            alCambiar.run();
            vista.dispose();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }
}
