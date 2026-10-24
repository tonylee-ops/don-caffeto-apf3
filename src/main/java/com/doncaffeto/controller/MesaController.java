package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
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
 * Pantalla de una mesa: a la izquierda su pedido (lo ya enviado a cocina, total y botón para pedir
 * la cuenta) y en medio la carta para agregar más productos. Es el único lugar donde el mesero ve
 * el pedido de salón.
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
    private final Carta carta;
    /** Productos elegidos que todavía no se envían a cocina. */
    private Pedido nuevos = new Pedido();
    private Pedido pedido;

    /**
     * @param vista     pantalla de la mesa
     * @param servicios servicios de la aplicación
     * @param usuario   mesero o administrador
     * @param mesa      mesa elegida en el mapa
     * @param alCambiar se ejecuta cuando la mesa cambia (para refrescar el mapa)
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
        carta = new Carta(vista.getTblProductos(), vista.getTxtBuscar(), vista.getCmbCategoria(), this::agregar);
        vista.getLblTitulo().setText("Mesa " + mesa.getNumero());
        vista.setTitle("Don Caffeto - Mesa " + mesa.getNumero());
        vista.getBtnAgregar().addActionListener(e -> agregar());
        vista.getTxtNota().addActionListener(e -> agregar());
        vista.getBtnQuitarNuevo().addActionListener(e -> quitarNuevo());
        vista.getBtnEnviar().addActionListener(e -> enviar());
        vista.getBtnPedirCuenta().addActionListener(e -> pedirCuenta());
        vista.getBtnAnular().addActionListener(e -> anular());
        vista.getBtnCerrar().addActionListener(e -> cerrar());
        vista.getTblNuevos().getSelectionModel().addListSelectionListener(e -> mostrarNuevos(false));
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cronometro.stop();
                recarga.stop();
            }
        });
    }

    /** Carga el pedido y la carta, y muestra la pantalla. */
    public void iniciar() {
        if (cargar()) {
            try {
                carta.mostrar(servicios.productos.carta());
            } catch (SQLException e) {
                Mensajes.de(vista, e);
            }
            mostrarNuevos(true);
            cronometro.start();
            recarga.start();
            vista.setVisible(true);
            vista.getTxtBuscar().requestFocusInWindow();
        }
    }

    /**
     * Vuelve a leer el pedido de la izquierda (la cocina pudo cambiar su estado).
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
        vista.getLblEstadoMesa().setText(cuenta ? "Cuenta pedida · esperando el pago" : listo ? "Pedido listo" : "En cocina");
        vista.getLblEstadoMesa().setForeground(Tema.colorMesa(pedido.getEstadoMesa()));
        vista.getLblCronometro().setForeground(Tema.colorMesa(pedido.getEstadoMesa()));
        vista.getLblCronometro().setText(TarjetaMesa.cronometro(mesa.getSegundosOcupada()));
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

        // pedir la cuenta: cuando la cocina terminó; anular: solo si la cocina aún no empezó
        vista.getBtnPedirCuenta().setVisible(!cuenta);
        vista.getBtnPedirCuenta().setEnabled(listo && nuevos.getDetalles().isEmpty());
        vista.getBtnPedirCuenta().setToolTipText(listo ? null : "Disponible cuando la cocina marque el pedido como listo");
        vista.getBtnAnular().setVisible("PENDIENTE".equals(pedido.getEstado()));
        // con la cuenta pedida ya no se agregan productos
        vista.getPnlCardAgregar().setVisible(!cuenta);
        return true;
    }

    // ------------------------- agregar productos (en medio) -------------------------

    private void agregar() {
        Producto p = carta.seleccionado();
        if (p == null) {
            Mensajes.aviso(vista, "Selecciona un producto de la carta.");
            return;
        }
        try {
            servicios.pedidos.agregarProducto(nuevos, p, (Integer) vista.getSpnCantidad().getValue(),
                    vista.getTxtNota().getText());
            vista.getSpnCantidad().setValue(1);
            vista.getTxtNota().setText("");
            mostrarNuevos(true);
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        }
    }

    private void quitarNuevo() {
        int fila = vista.getTblNuevos().getSelectedRow();
        if (fila >= 0) {
            nuevos.getDetalles().remove(fila);
            mostrarNuevos(true);
        }
    }

    /**
     * @param recargarTabla true para volver a llenar la tabla de "Por enviar"
     */
    private void mostrarNuevos(boolean recargarTabla) {
        if (recargarTabla) {
            List<Object[]> filas = new ArrayList<>();
            for (DetallePedido d : nuevos.getDetalles()) {
                filas.add(new Object[]{d.getProducto(), Tablas.nota(d), d.getCantidad(), d.getImporte()});
            }
            Tablas.llenar(vista.getTblNuevos(), filas);
        }
        boolean hay = !nuevos.getDetalles().isEmpty();
        vista.getLblTotalNuevos().setText(hay ? "+ " + Tema.soles(servicios.pedidos.calcularTotal(nuevos)) : " ");
        vista.getBtnEnviar().setEnabled(hay);
        vista.getBtnQuitarNuevo().setEnabled(vista.getTblNuevos().getSelectedRow() >= 0);
        vista.getBtnPedirCuenta().setEnabled(pedido != null && "LISTO".equals(pedido.getEstado()) && !hay);
    }

    private void enviar() {
        try {
            servicios.pedidos.agregarAPedido(pedido, nuevos.getDetalles(), usuario);
            Mensajes.info(vista, "Productos enviados a cocina. Aparecen con la nota «Agregado».");
            nuevos = new Pedido();
            mostrarNuevos(true);
            alCambiar.run();
            cargar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
            cargar();
        }
    }

    // ------------------------- acciones del pedido (izquierda) -------------------------

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

    private void cerrar() {
        if (!nuevos.getDetalles().isEmpty()
                && !Mensajes.confirmar(vista, "Hay productos sin enviar a cocina. ¿Salir sin enviarlos?")) {
            return;
        }
        vista.dispose();
    }
}
