package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.view.FrmCocina;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class CocinaController {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final FrmCocina vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final Timer reloj;
    private List<Pedido> pedidos = new ArrayList<>();
    private int ultimoPedido = -1;

    public CocinaController(FrmCocina vista, Servicios servicios, Usuario usuario) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        reloj = new Timer(15_000, e -> actualizar());
        vista.getBtnActualizar().addActionListener(e -> actualizar());
        vista.getBtnPreparar().addActionListener(e -> avanzar());
        vista.getBtnListo().addActionListener(e -> avanzar());
        vista.getTblCocina().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        vista.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                reloj.stop();
            }
        });
    }

    public void iniciar() {
        actualizar();
        reloj.start();
        vista.setVisible(true);
    }

    private void actualizar() {
        Pedido antes = seleccionado();
        try {
            pedidos = servicios.cocina.pedidosEnCocina(usuario);
        } catch (SQLException | SecurityException e) {
            reloj.stop();
            Mensajes.de(vista, e);
            return;
        }
        List<Object[]> filas = new ArrayList<>();
        int pendientes = 0;
        int mayor = ultimoPedido;
        for (Pedido p : pedidos) {
            long minutos = Duration.between(p.getFecha(), LocalDateTime.now()).toMinutes();
            filas.add(new Object[]{p.getIdPedido(), p.getFecha().format(HORA), Tablas.destino(p), Tablas.ESTADO.get(p.getEstado()),
                    minutos + " min"});
            if ("PENDIENTE".equals(p.getEstado())) {
                pendientes++;
            }
            mayor = Math.max(mayor, p.getIdPedido());
        }
        if (ultimoPedido >= 0 && mayor > ultimoPedido) {
            Toolkit.getDefaultToolkit().beep();
        }
        ultimoPedido = mayor;
        Tablas.llenar(vista.getTblCocina(), filas);
        vista.getLblResumen().setText(pendientes + (pendientes == 1 ? " pendiente" : " pendientes") + "  ·  "
                + (pedidos.size() - pendientes) + " en preparación");
        vista.getLblActualizado().setText("Actualizado " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        for (int i = 0; antes != null && i < pedidos.size(); i++) {
            if (pedidos.get(i).getIdPedido() == antes.getIdPedido()) {
                vista.getTblCocina().setRowSelectionInterval(i, i);
            }
        }
        mostrarDetalle();
    }

    private Pedido seleccionado() {
        int fila = vista.getTblCocina().getSelectedRow();
        return fila < 0 || fila >= pedidos.size() ? null : pedidos.get(fila);
    }

    private void mostrarDetalle() {
        Pedido p = seleccionado();
        vista.getBtnPreparar().setEnabled(p != null && "PENDIENTE".equals(p.getEstado()));
        vista.getBtnListo().setEnabled(p != null && "EN_PREPARACION".equals(p.getEstado()));
        List<Object[]> filas = new ArrayList<>();
        if (p == null) {
            vista.getLblSeleccion().setText("Selecciona un pedido");
            vista.getLblObservacion().setText(" ");
        } else {
            vista.getLblSeleccion().setText("Pedido N° " + p.getIdPedido() + "  ·  " + Tablas.destino(p));
            vista.getLblObservacion().setText(p.getObservacion() == null ? " " : "Observación: " + p.getObservacion());
            try {
                for (DetallePedido d : servicios.pedidos.detalle(p.getIdPedido())) {
                    filas.add(new Object[]{d.getProducto(), d.getCantidad(), Tablas.nota(d)});
                }
            } catch (SQLException e) {
                Mensajes.de(vista, e);
            }
        }
        Tablas.llenar(vista.getTblDetalle(), filas);
    }

    private void avanzar() {
        Pedido p = seleccionado();
        if (p == null) {
            return;
        }
        try {
            String nuevo = servicios.cocina.avanzar(p, usuario);
            if ("LISTO".equals(nuevo)) {
                Mensajes.info(vista, "Pedido N° " + p.getIdPedido() + " listo. Ya aparece en caja.");
            }
            actualizar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
            actualizar();
        }
    }
}
