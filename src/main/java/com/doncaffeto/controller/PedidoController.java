package com.doncaffeto.controller;

import com.doncaffeto.dao.MesaDAO;
import com.doncaffeto.dao.ProductoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.view.FrmPedido;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controla el registro de un pedido nuevo.
 *
 * @author Equipo Don Caffeto
 */
public class PedidoController {

    private final FrmPedido vista;
    private final PedidoService pedidoService;
    private final ProductoDAO productoDAO;
    private final MesaDAO mesaDAO;
    private final Usuario usuario;
    private List<Producto> productos;
    private Pedido pedido = new Pedido();

    /**
     * @param vista         pantalla de pedido
     * @param pedidoService servicio de pedidos
     * @param productoDAO   acceso a productos
     * @param mesaDAO       acceso a mesas
     * @param usuario       mesero que registra
     */
    public PedidoController(FrmPedido vista, PedidoService pedidoService, ProductoDAO productoDAO,
                            MesaDAO mesaDAO, Usuario usuario) {
        this.vista = vista;
        this.pedidoService = pedidoService;
        this.productoDAO = productoDAO;
        this.mesaDAO = mesaDAO;
        this.usuario = usuario;
        vista.getBtnAgregar().addActionListener(e -> agregar());
        vista.getBtnQuitar().addActionListener(e -> quitar());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getCmbModalidad().addActionListener(e -> cambiarModalidad());
    }

    /** Carga la carta y las mesas libres, y muestra la pantalla. */
    public void iniciar() {
        try {
            productos = productoDAO.listarDisponibles();
            DefaultTableModel modelo = (DefaultTableModel) vista.getTblProductos().getModel();
            for (Producto p : productos) {
                modelo.addRow(new Object[]{p.getIdProducto(), p.getNombre(), p.getCategoria(), p.getPrecio()});
            }
            cargarMesas();
            cambiarModalidad();
            vista.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la carta: " + e.getMessage());
        }
    }

    private void cargarMesas() throws SQLException {
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        for (Mesa m : mesaDAO.listarLibres()) {
            modelo.addElement(m);
        }
        vista.getCmbMesa().setModel(modelo);
    }

    private void cambiarModalidad() {
        String modalidad = String.valueOf(vista.getCmbModalidad().getSelectedItem());
        vista.getCmbMesa().setEnabled("SALON".equals(modalidad));
        vista.getTxtDireccion().setEnabled("DELIVERY".equals(modalidad));
        vista.getTxtTelefono().setEnabled("DELIVERY".equals(modalidad));
    }

    private void agregar() {
        int fila = vista.getTblProductos().getSelectedRow();
        try {
            Producto p = fila < 0 ? null : productos.get(vista.getTblProductos().convertRowIndexToModel(fila));
            pedidoService.agregarProducto(pedido, p, (Integer) vista.getSpnCantidad().getValue());
            mostrarDetalle();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage());
        }
    }

    private void quitar() {
        int fila = vista.getTblDetalle().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione una línea del detalle.");
            return;
        }
        pedido.getDetalles().remove(fila);
        mostrarDetalle();
    }

    private void mostrarDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        modelo.setRowCount(0);
        for (DetallePedido d : pedido.getDetalles()) {
            modelo.addRow(new Object[]{d.getProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getImporte()});
        }
        vista.getLblTotal().setText("Total: S/ " + pedidoService.calcularTotal(pedido));
    }

    private void guardar() {
        String modalidad = String.valueOf(vista.getCmbModalidad().getSelectedItem());
        Mesa mesa = (Mesa) vista.getCmbMesa().getSelectedItem();
        pedido.setModalidad(modalidad);
        pedido.setIdUsuario(usuario.getIdUsuario());
        pedido.setIdMesa("SALON".equals(modalidad) && mesa != null ? mesa.getIdMesa() : null);
        pedido.setDireccion("DELIVERY".equals(modalidad) ? vista.getTxtDireccion().getText().trim() : null);
        pedido.setTelefono("DELIVERY".equals(modalidad) ? vista.getTxtTelefono().getText().trim() : null);
        try {
            int id = pedidoService.guardar(pedido);
            JOptionPane.showMessageDialog(vista, "Pedido N° " + id + " registrado.");
            pedido = new Pedido();
            mostrarDetalle();
            cargarMesas();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "No se pudo guardar: " + e.getMessage());
        }
    }
}
