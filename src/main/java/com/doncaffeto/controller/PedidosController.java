package com.doncaffeto.controller;

import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.util.ExportarExcel;
import com.doncaffeto.view.FrmPedidos;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controla la lista de pedidos: búsqueda por fecha, confirmación y exportación a Excel.
 *
 * @author Equipo Don Caffeto
 */
public class PedidosController {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final FrmPedidos vista;
    private final PedidoService pedidoService;
    private final Usuario usuario;
    private List<Pedido> pedidos = new ArrayList<>();

    /**
     * @param vista         pantalla de pedidos
     * @param pedidoService servicio de pedidos
     * @param usuario       usuario en sesión
     */
    public PedidosController(FrmPedidos vista, PedidoService pedidoService, Usuario usuario) {
        this.vista = vista;
        this.pedidoService = pedidoService;
        this.usuario = usuario;
        vista.getTxtFecha().setText(LocalDate.now().format(FECHA));
        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getBtnConfirmar().addActionListener(e -> confirmar());
        vista.getBtnExportar().addActionListener(e -> exportar());
        vista.getTblPedidos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
    }

    /** Busca los pedidos de hoy y muestra la pantalla. */
    public void iniciar() {
        buscar();
        vista.setVisible(true);
    }

    private void buscar() {
        try {
            LocalDate fecha = LocalDate.parse(vista.getTxtFecha().getText().trim(), FECHA);
            pedidos = pedidoService.listar(fecha);
            DefaultTableModel modelo = (DefaultTableModel) vista.getTblPedidos().getModel();
            modelo.setRowCount(0);
            for (Pedido p : pedidos) {
                modelo.addRow(new Object[]{p.getIdPedido(), p.getFecha().format(HORA), p.getModalidad(),
                        p.getNumeroMesa() == null ? "-" : p.getNumeroMesa(), p.getMesero(), p.getEstado(),
                        p.getTotal()});
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista, "Escriba la fecha como dd/mm/aaaa.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "No se pudo consultar: " + e.getMessage());
        }
    }

    private Pedido seleccionado() {
        int fila = vista.getTblPedidos().getSelectedRow();
        return fila < 0 ? null : pedidos.get(vista.getTblPedidos().convertRowIndexToModel(fila));
    }

    private void mostrarDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        modelo.setRowCount(0);
        Pedido p = seleccionado();
        if (p == null) {
            return;
        }
        try {
            for (DetallePedido d : pedidoService.detalle(p.getIdPedido())) {
                modelo.addRow(new Object[]{d.getProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getImporte()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "No se pudo cargar el detalle: " + e.getMessage());
        }
    }

    private void confirmar() {
        Pedido p = seleccionado();
        if (p == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un pedido.");
            return;
        }
        try {
            pedidoService.confirmar(p, usuario.getIdUsuario());
            JOptionPane.showMessageDialog(vista, "Pedido N° " + p.getIdPedido() + " enviado a cocina.");
            buscar();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "No se pudo confirmar: " + e.getMessage());
        }
    }

    private void exportar() {
        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay pedidos para exportar.");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("pedidos.xlsx"));
        if (selector.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            try {
                ExportarExcel.pedidos(pedidos, selector.getSelectedFile());
                JOptionPane.showMessageDialog(vista, "Archivo Excel guardado.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(vista, "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }
}
