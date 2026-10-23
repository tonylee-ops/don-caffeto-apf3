package com.doncaffeto.controller;

import com.doncaffeto.dao.MesaDAO;
import com.doncaffeto.dao.ProductoDAO;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.service.PedidoService;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmPedido;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Controla el registro de un pedido nuevo.
 *
 * @author Equipo Don Caffeto
 */
public class PedidoController {

    /** Valores que se guardan en la base, en el mismo orden que el combo (Salón, Para llevar, Delivery). */
    private static final String[] MODALIDADES = {"SALON", "PARA_LLEVAR", "DELIVERY"};

    private final FrmPedido vista;
    private final PedidoService pedidoService;
    private final ProductoDAO productoDAO;
    private final MesaDAO mesaDAO;
    private final Usuario usuario;
    private List<Producto> productos;
    private TableRowSorter<DefaultTableModel> filtro;
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
        vista.getLblMesero().setText("Mesero: " + usuario.getNombres());
        vista.getBtnAgregar().addActionListener(e -> agregar());
        vista.getBtnMas().addActionListener(e -> cambiarCantidad(1));
        vista.getBtnMenos().addActionListener(e -> cambiarCantidad(-1));
        vista.getBtnQuitar().addActionListener(e -> quitar());
        vista.getBtnLimpiar().addActionListener(e -> limpiar());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getCmbModalidad().addActionListener(e -> cambiarModalidad());
        vista.getCmbCategoria().addActionListener(e -> filtrar());
        vista.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        vista.getTblProductos().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    agregar();
                }
            }
        });
        vista.getTblDetalle().getSelectionModel().addListSelectionListener(e -> actualizarBotones());
    }

    /** Carga la carta y las mesas libres, y muestra la pantalla. */
    public void iniciar() {
        try {
            productos = productoDAO.listarDisponibles();
            DefaultTableModel modelo = (DefaultTableModel) vista.getTblProductos().getModel();
            TreeSet<String> categorias = new TreeSet<>();
            for (Producto p : productos) {
                modelo.addRow(new Object[]{p.getNombre(), p.getCategoria(), p.getPrecio()});
                categorias.add(p.getCategoria());
            }
            filtro = new TableRowSorter<>(modelo);
            vista.getTblProductos().setRowSorter(filtro);
            DefaultComboBoxModel<Object> cats = new DefaultComboBoxModel<>();
            cats.addElement("Todas las categorías");
            categorias.forEach(cats::addElement);
            vista.getCmbCategoria().setModel(cats);
            cargarMesas();
            cambiarModalidad();
            mostrarDetalle();
            vista.setVisible(true);
            vista.getTxtBuscar().requestFocusInWindow();
        } catch (SQLException e) {
            Mensajes.error(null, "No se pudo cargar la carta. Revise la conexión con MySQL.");
        }
    }

    private void cargarMesas() throws SQLException {
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        List<Mesa> libres = mesaDAO.listarLibres();
        if (libres.isEmpty()) {
            modelo.addElement("Sin mesas libres");
        }
        libres.forEach(modelo::addElement);
        vista.getCmbMesa().setModel(modelo);
    }

    private String modalidad() {
        return MODALIDADES[vista.getCmbModalidad().getSelectedIndex()];
    }

    /** Activa solo los campos que pide la modalidad elegida. */
    private void cambiarModalidad() {
        boolean salon = "SALON".equals(modalidad());
        boolean delivery = "DELIVERY".equals(modalidad());
        vista.getCmbMesa().setEnabled(salon);
        vista.getTxtDireccion().setEnabled(delivery);
        vista.getTxtTelefono().setEnabled(delivery);
        if (!delivery) {
            vista.getTxtDireccion().setText("");
            vista.getTxtTelefono().setText("");
        }
    }

    /** Filtra la carta por texto y categoría mientras el usuario escribe. */
    private void filtrar() {
        if (filtro == null) {
            return;
        }
        String texto = vista.getTxtBuscar().getText().trim();
        RowFilter<DefaultTableModel, Integer> porNombre = RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0);
        if (vista.getCmbCategoria().getSelectedIndex() <= 0) {
            filtro.setRowFilter(porNombre);
        } else {
            String categoria = String.valueOf(vista.getCmbCategoria().getSelectedItem());
            RowFilter<DefaultTableModel, Integer> porCategoria = RowFilter.regexFilter(
                    "^" + java.util.regex.Pattern.quote(categoria) + "$", 1);
            filtro.setRowFilter(RowFilter.andFilter(List.of(porNombre, porCategoria)));
        }
    }

    private void agregar() {
        int fila = vista.getTblProductos().getSelectedRow();
        if (fila < 0) {
            Mensajes.aviso(vista, "Selecciona un producto de la carta.");
            return;
        }
        Producto p = productos.get(vista.getTblProductos().convertRowIndexToModel(fila));
        try {
            pedidoService.agregarProducto(pedido, p, (Integer) vista.getSpnCantidad().getValue());
            vista.getSpnCantidad().setValue(1);
            mostrarDetalle();
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        }
    }

    private void cambiarCantidad(int cambio) {
        int fila = vista.getTblDetalle().getSelectedRow();
        if (fila >= 0) {
            pedidoService.cambiarCantidad(pedido, fila, cambio);
            mostrarDetalle();
            if (fila < pedido.getDetalles().size()) {
                vista.getTblDetalle().setRowSelectionInterval(fila, fila);
            }
        }
    }

    private void quitar() {
        int fila = vista.getTblDetalle().getSelectedRow();
        if (fila >= 0) {
            pedido.getDetalles().remove(fila);
            mostrarDetalle();
        }
    }

    private void limpiar() {
        if (!pedido.getDetalles().isEmpty() && !Mensajes.confirmar(vista, "¿Borrar todos los productos del pedido?")) {
            return;
        }
        pedido = new Pedido();
        vista.getCmbModalidad().setSelectedIndex(0);
        mostrarDetalle();
    }

    private void mostrarDetalle() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetalle().getModel();
        modelo.setRowCount(0);
        int unidades = 0;
        for (DetallePedido d : pedido.getDetalles()) {
            modelo.addRow(new Object[]{d.getProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getImporte()});
            unidades += d.getCantidad();
        }
        vista.getLblItems().setText(unidades == 1 ? "1 producto" : unidades + " productos");
        vista.getLblTotal().setText(Tema.soles(pedidoService.calcularTotal(pedido)));
        actualizarBotones();
    }

    /** Los botones solo se activan cuando tienen sentido. */
    private void actualizarBotones() {
        boolean hayLinea = vista.getTblDetalle().getSelectedRow() >= 0;
        vista.getBtnMas().setEnabled(hayLinea);
        vista.getBtnMenos().setEnabled(hayLinea);
        vista.getBtnQuitar().setEnabled(hayLinea);
        vista.getBtnGuardar().setEnabled(!pedido.getDetalles().isEmpty());
        vista.getBtnLimpiar().setEnabled(!pedido.getDetalles().isEmpty());
    }

    private void guardar() {
        String modalidad = modalidad();
        Object mesa = vista.getCmbMesa().getSelectedItem();
        pedido.setModalidad(modalidad);
        pedido.setIdUsuario(usuario.getIdUsuario());
        pedido.setIdMesa("SALON".equals(modalidad) && mesa instanceof Mesa ? ((Mesa) mesa).getIdMesa() : null);
        pedido.setDireccion("DELIVERY".equals(modalidad) ? vista.getTxtDireccion().getText().trim() : null);
        pedido.setTelefono("DELIVERY".equals(modalidad) ? vista.getTxtTelefono().getText().trim() : null);
        try {
            int id = pedidoService.guardar(pedido);
            Mensajes.info(vista, "Pedido N° " + id + " registrado.\nPuedes confirmarlo en «Pedidos del día».");
            pedido = new Pedido();
            vista.getCmbModalidad().setSelectedIndex(0);
            cargarMesas();
            mostrarDetalle();
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        } catch (SQLException e) {
            Mensajes.error(vista, "No se pudo guardar el pedido:\n" + e.getMessage());
        }
    }
}
