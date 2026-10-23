package com.doncaffeto.controller;

import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.util.ExportarExcel;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmPedidos;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Controla la lista de pedidos: búsqueda por fecha, confirmación y exportación a Excel.
 *
 * @author Equipo Don Caffeto
 */
public class PedidosController {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Map<String, String> MODALIDAD = Map.of(
            "SALON", "Salón", "PARA_LLEVAR", "Para llevar", "DELIVERY", "Delivery");

    private final FrmPedidos vista;
    private final PedidoService pedidoService;
    private final Usuario usuario;
    private List<Pedido> pedidos = new ArrayList<>();
    private LocalDate fecha = LocalDate.now();

    /**
     * @param vista         pantalla de pedidos
     * @param pedidoService servicio de pedidos
     * @param usuario       usuario en sesión
     */
    public PedidosController(FrmPedidos vista, PedidoService pedidoService, Usuario usuario) {
        this.vista = vista;
        this.pedidoService = pedidoService;
        this.usuario = usuario;
        vista.getTxtFecha().setText(fecha.format(FECHA));
        vista.getBtnBuscar().addActionListener(e -> buscar());
        vista.getTxtFecha().addActionListener(e -> buscar());
        vista.getBtnHoy().addActionListener(e -> {
            vista.getTxtFecha().setText(LocalDate.now().format(FECHA));
            buscar();
        });
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
            fecha = LocalDate.parse(vista.getTxtFecha().getText().trim(), FECHA);
        } catch (DateTimeParseException e) {
            Mensajes.aviso(vista, "Escribe la fecha como dd/mm/aaaa (por ejemplo 23/10/2026).");
            return;
        }
        try {
            pedidos = pedidoService.listar(fecha);
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo consultar los pedidos. Revise la conexión con MySQL.");
            return;
        }
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblPedidos().getModel();
        modelo.setRowCount(0);
        BigDecimal suma = BigDecimal.ZERO;
        for (Pedido p : pedidos) {
            modelo.addRow(new Object[]{p.getIdPedido(), p.getFecha().format(HORA), MODALIDAD.get(p.getModalidad()),
                    p.getNumeroMesa() == null ? "—" : "Mesa " + p.getNumeroMesa(), p.getMesero(), p.getEstado(),
                    p.getTotal()});
            suma = suma.add(p.getTotal());
        }
        vista.getLblResumen().setText(pedidos.isEmpty() ? "No hay pedidos el " + fecha.format(FECHA)
                : pedidos.size() + (pedidos.size() == 1 ? " pedido" : " pedidos") + "  ·  Total " + Tema.soles(suma));
        vista.getBtnExportar().setEnabled(!pedidos.isEmpty());
        mostrarDetalle();
    }

    private Pedido seleccionado() {
        int fila = vista.getTblPedidos().getSelectedRow();
        return fila < 0 ? null : pedidos.get(vista.getTblPedidos().convertRowIndexToModel(fila));
    }

    private void mostrarDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        modelo.setRowCount(0);
        Pedido p = seleccionado();
        vista.getBtnConfirmar().setEnabled(p != null && "REGISTRADO".equals(p.getEstado()));
        if (p == null) {
            vista.getLblSeleccion().setText("Selecciona un pedido para ver su detalle");
            return;
        }
        vista.getLblSeleccion().setText("Pedido N° " + p.getIdPedido() + "  ·  " + MODALIDAD.get(p.getModalidad())
                + "  ·  " + p.getEstado());
        try {
            for (DetallePedido d : pedidoService.detalle(p.getIdPedido())) {
                modelo.addRow(new Object[]{d.getProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getImporte()});
            }
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo cargar el detalle del pedido.");
        }
    }

    private void confirmar() {
        Pedido p = seleccionado();
        if (p == null || !Mensajes.confirmar(vista, "¿Enviar el pedido N° " + p.getIdPedido() + " a cocina?")) {
            return;
        }
        try {
            pedidoService.confirmar(p, usuario.getIdUsuario());
            Mensajes.info(vista, "Pedido N° " + p.getIdPedido() + " enviado a cocina.");
            buscar();
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo confirmar:\n" + e.getMessage());
        }
    }

    private void exportar() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx"));
        selector.setSelectedFile(new File("pedidos_" + fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx"));
        if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".xlsx");
        }
        try {
            ExportarExcel.pedidos(pedidos, archivo);
            if (Mensajes.confirmar(vista, "Excel guardado en:\n" + archivo.getAbsolutePath() + "\n\n¿Abrirlo ahora?")
                    && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (IOException e) {
            Mensajes.error(vista, "No se pudo guardar el archivo:\n" + e.getMessage());
        }
    }
}
