package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.Autorizacion;
import com.doncaffeto.util.Tema;
import com.doncaffeto.util.Validador;
import com.doncaffeto.view.FrmPedidos;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PedidosController {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] COLUMNAS = {"N°", "Hora", "Modalidad", "Destino", "Mesero", "Estado", "Total"};

    private final FrmPedidos vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private List<Pedido> pedidos = new ArrayList<>();
    private final List<Object[]> filas = new ArrayList<>();
    private LocalDate fecha = LocalDate.now();

    public PedidosController(FrmPedidos vista, Servicios servicios, Usuario usuario) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        vista.getTxtFecha().setText(fecha.format(Validador.FECHA));
        vista.getBtnAnular().setVisible(Autorizacion.permite(usuario, Autorizacion.PEDIDOS));
        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getTxtFecha().addActionListener(e -> buscar());
        vista.getBtnHoy().addActionListener(e -> {
            vista.getTxtFecha().setText(LocalDate.now().format(Validador.FECHA));
            buscar();
        });
        vista.getBtnAnular().addActionListener(e -> anular());
        vista.getBtnExportar().addActionListener(e -> Tablas.exportar(vista, "Pedidos", COLUMNAS, filas,
                "pedidos_" + fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        vista.getTblPedidos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
    }

    public void iniciar() {
        buscar();
        vista.setVisible(true);
    }

    private void buscar() {
        LocalDate f = Validador.fecha(vista.getTxtFecha().getText());
        if (f == null) {
            Mensajes.aviso(vista, "Escribe la fecha como dd/mm/aaaa (por ejemplo 23/10/2026).");
            return;
        }
        fecha = f;
        try {
            pedidos = servicios.pedidos.listar(fecha);
        } catch (SQLException e) {
            Mensajes.de(vista, e);
            return;
        }
        filas.clear();
        BigDecimal suma = BigDecimal.ZERO;
        for (Pedido p : pedidos) {
            filas.add(new Object[]{p.getIdPedido(), p.getFecha().format(HORA), Tablas.MODALIDAD.get(p.getModalidad()),
                    Tablas.destino(p), p.getMesero(), Tablas.ESTADO.get(p.getEstado()), p.getTotal()});
            if (!"CANCELADO".equals(p.getEstado())) {
                suma = suma.add(p.getTotal());
            }
        }
        Tablas.llenar(vista.getTblPedidos(), filas);
        vista.getLblResumen().setText(pedidos.isEmpty() ? "No hay pedidos el " + fecha.format(Validador.FECHA)
                : pedidos.size() + (pedidos.size() == 1 ? " pedido" : " pedidos") + "  ·  Total " + Tema.soles(suma)
                + " (sin anulados)");
        vista.getBtnExportar().setEnabled(!pedidos.isEmpty());
        mostrarDetalle();
    }

    private Pedido seleccionado() {
        int fila = vista.getTblPedidos().getSelectedRow();
        return fila < 0 ? null : pedidos.get(vista.getTblPedidos().convertRowIndexToModel(fila));
    }

    private void mostrarDetalle() {
        Pedido p = seleccionado();
        vista.getBtnAnular().setEnabled(p != null && "PENDIENTE".equals(p.getEstado()));
        List<Object[]> detalle = new ArrayList<>();
        if (p == null) {
            vista.getLblSeleccion().setText("Selecciona un pedido para ver su detalle");
            vista.getLblObservacion().setText(" ");
        } else {
            vista.getLblSeleccion().setText("Pedido N° " + p.getIdPedido() + "  ·  " + Tablas.destino(p)
                    + "  ·  " + Tablas.ESTADO.get(p.getEstado()));
            vista.getLblObservacion().setText(p.getObservacion() == null ? " " : "Observación: " + p.getObservacion());
            try {
                for (DetallePedido d : servicios.pedidos.detalle(p.getIdPedido())) {
                    detalle.add(new Object[]{d.getProducto(), Tablas.nota(d), d.getCantidad(), d.getPrecioUnitario(),
                            d.getImporte()});
                }
            } catch (SQLException e) {
                Mensajes.de(vista, e);
            }
        }
        Tablas.llenar(vista.getTblDetalle(), detalle);
    }

    private void anular() {
        Pedido p = seleccionado();
        if (p == null || !Mensajes.confirmar(vista, "¿Anular el pedido N° " + p.getIdPedido() + "?\n"
                + "La cocina dejará de verlo.")) {
            return;
        }
        try {
            servicios.pedidos.anular(p, usuario);
            Mensajes.info(vista, "Pedido N° " + p.getIdPedido() + " anulado.");
            buscar();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }
}
