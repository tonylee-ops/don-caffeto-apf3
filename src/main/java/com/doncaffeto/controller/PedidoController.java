package com.doncaffeto.controller;

import com.doncaffeto.Servicios;
import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Tema;
import com.doncaffeto.view.FrmPedido;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Controla el registro de un pedido nuevo (mesero). Se abre desde el mapa de mesas con la mesa
 * ya elegida, o con los botones "Para llevar" y "Delivery".
 *
 * @author Equipo Don Caffeto
 */
public class PedidoController {

    /** Valores de la base, en el mismo orden que el combo (Salón, Para llevar, Delivery). */
    private static final String[] MODALIDADES = {"SALON", "PARA_LLEVAR", "DELIVERY"};

    private final FrmPedido vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private final Mesa mesa;
    private final String modalidadFija;
    private final Runnable alGuardar;
    private List<Producto> productos = new ArrayList<>();
    private TableRowSorter<DefaultTableModel> filtro;
    private Pedido pedido = new Pedido();

    /**
     * @param vista     pantalla de pedido
     * @param servicios servicios de la aplicación
     * @param usuario   mesero que registra
     * @param mesa      mesa elegida en el mapa (null para llevar o delivery)
     * @param modalidad SALON, PARA_LLEVAR o DELIVERY
     * @param alGuardar se ejecuta después de guardar (para refrescar el mapa de mesas)
     */
    public PedidoController(FrmPedido vista, Servicios servicios, Usuario usuario, Mesa mesa, String modalidad,
                            Runnable alGuardar) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        this.mesa = mesa;
        this.modalidadFija = modalidad;
        this.alGuardar = alGuardar;
        vista.getLblMesero().setText("Mesero: " + usuario.getNombres());
        vista.getBtnAgregar().addActionListener(e -> agregar());
        vista.getTxtNota().addActionListener(e -> agregar());
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
            productos = servicios.productos.carta();
            List<Object[]> filas = new ArrayList<>();
            TreeSet<String> categorias = new TreeSet<>();
            for (Producto p : productos) {
                filas.add(new Object[]{p.getNombre(), p.getCategoria(), p.getPrecio()});
                categorias.add(p.getCategoria());
            }
            Tablas.llenar(vista.getTblProductos(), filas);
            filtro = new TableRowSorter<>((DefaultTableModel) vista.getTblProductos().getModel());
            vista.getTblProductos().setRowSorter(filtro);
            DefaultComboBoxModel<Object> cats = new DefaultComboBoxModel<>();
            cats.addElement("Todas las categorías");
            categorias.forEach(cats::addElement);
            vista.getCmbCategoria().setModel(cats);
        } catch (SQLException e) {
            Mensajes.de(vista, e);
        }
        fijarModalidad();
        cambiarModalidad();
        mostrarDetalle();
        vista.setVisible(true);
        vista.getTxtBuscar().requestFocusInWindow();
    }

    /** La mesa y la modalidad vienen del mapa de mesas y no se cambian aquí. */
    private void fijarModalidad() {
        int indice = java.util.Arrays.asList(MODALIDADES).indexOf(modalidadFija);
        vista.getCmbModalidad().setSelectedIndex(Math.max(indice, 0));
        vista.getCmbModalidad().setEnabled(false);
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        modelo.addElement(mesa == null ? "—" : mesa);
        vista.getCmbMesa().setModel(modelo);
        String titulo = mesa != null ? "Mesa " + mesa.getNumero() + " · Nuevo pedido"
                : "DELIVERY".equals(modalidadFija) ? "Nuevo pedido delivery" : "Nuevo pedido para llevar";
        vista.getLblTitulo().setText(titulo);
        vista.setTitle("Don Caffeto - " + titulo);
    }

    private String modalidad() {
        return MODALIDADES[vista.getCmbModalidad().getSelectedIndex()];
    }

    /** Activa solo los campos que pide la modalidad elegida. */
    private void cambiarModalidad() {
        boolean salon = "SALON".equals(modalidad());
        boolean delivery = "DELIVERY".equals(modalidad());
        // solo se muestran los datos que pide la modalidad
        vista.getCmbMesa().setEnabled(false);
        vista.getCmbMesa().setVisible(salon);
        vista.getLblEtqMesa().setVisible(salon);
        vista.getTxtDireccion().setVisible(delivery);
        vista.getLblEtqDireccion().setVisible(delivery);
        vista.getTxtTelefono().setVisible(delivery);
        vista.getLblEtqTelefono().setVisible(delivery);
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
        List<RowFilter<DefaultTableModel, Integer>> filtros = new ArrayList<>();
        filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(vista.getTxtBuscar().getText().trim()), 0));
        if (vista.getCmbCategoria().getSelectedIndex() > 0) {
            String categoria = String.valueOf(vista.getCmbCategoria().getSelectedItem());
            filtros.add(RowFilter.regexFilter("^" + Pattern.quote(categoria) + "$", 1));
        }
        filtro.setRowFilter(RowFilter.andFilter(filtros));
    }

    private void agregar() {
        int fila = vista.getTblProductos().getSelectedRow();
        if (fila < 0) {
            Mensajes.aviso(vista, "Selecciona un producto de la carta.");
            return;
        }
        Producto p = productos.get(vista.getTblProductos().convertRowIndexToModel(fila));
        try {
            servicios.pedidos.agregarProducto(pedido, p, (Integer) vista.getSpnCantidad().getValue(),
                    vista.getTxtNota().getText());
            vista.getSpnCantidad().setValue(1);
            vista.getTxtNota().setText("");
            mostrarDetalle();
        } catch (IllegalArgumentException e) {
            Mensajes.aviso(vista, e.getMessage());
        }
    }

    private void cambiarCantidad(int cambio) {
        int fila = vista.getTblDetalle().getSelectedRow();
        if (fila >= 0) {
            servicios.pedidos.cambiarCantidad(pedido, fila, cambio);
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
        reiniciar();
    }

    private void reiniciar() {
        pedido = new Pedido();
        vista.getTxtObservacion().setText("");
        mostrarDetalle();
    }

    private void mostrarDetalle() {
        List<Object[]> filas = new ArrayList<>();
        int unidades = 0;
        for (DetallePedido d : pedido.getDetalles()) {
            filas.add(new Object[]{d.getProducto(), Tablas.nota(d), d.getCantidad(), d.getImporte()});
            unidades += d.getCantidad();
        }
        Tablas.llenar(vista.getTblDetalle(), filas);
        vista.getLblItems().setText(unidades == 1 ? "1 producto" : unidades + " productos");
        vista.getLblTotal().setText(Tema.soles(servicios.pedidos.calcularTotal(pedido)));
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
        pedido.setModalidad(modalidad);
        pedido.setIdMesa(mesa == null ? null : mesa.getIdMesa());
        pedido.setDireccion("DELIVERY".equals(modalidad) ? vista.getTxtDireccion().getText().trim() : null);
        pedido.setTelefono("DELIVERY".equals(modalidad) ? vista.getTxtTelefono().getText().trim() : null);
        pedido.setObservacion(vista.getTxtObservacion().getText());
        try {
            int id = servicios.pedidos.guardar(pedido, usuario);
            Mensajes.info(vista, "Pedido N° " + id + " enviado a cocina."
                    + (mesa == null ? "" : "\nLa mesa " + mesa.getNumero() + " ahora aparece en naranja."));
            alGuardar.run();
            vista.dispose();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }
}
