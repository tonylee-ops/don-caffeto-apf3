package controlador;

import modelo.Categoria;
import modelo.Producto;
import modelo.Usuario;
import modelo.Validador;
import mvc.Servicios;
import vista.FrmProductos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ProductosController {
    private final FrmProductos vista;
    private final Servicios servicios;
    private final Usuario usuario;
    private List<Producto> productos = new ArrayList<>();
    private TableRowSorter<DefaultTableModel> filtro;
    private Producto editando;

    public ProductosController(FrmProductos vista, Servicios servicios, Usuario usuario) {
        this.vista = vista;
        this.servicios = servicios;
        this.usuario = usuario;
        vista.getBtnNuevo().addActionListener(e -> nuevo());
        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getTblProductos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editar();
            }
        });
        vista.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
    }

    public void iniciar() {
        try {
            DefaultComboBoxModel<Object> cats = new DefaultComboBoxModel<>();
            for (Categoria c : servicios.productos.categorias()) {
                cats.addElement(c);
            }
            vista.getCmbCategoria().setModel(cats);
            cargar();
        } catch (SQLException | SecurityException e) {
            Mensajes.de(vista, e);
        }
        nuevo();
        vista.setVisible(true);
    }

    private void cargar() throws SQLException {
        productos = servicios.productos.todos(usuario);
        List<Object[]> filas = new ArrayList<>();
        for (Producto p : productos) {
            filas.add(new Object[]{p.getNombre(), p.getCategoria(), p.getPrecio(), p.isDisponible() ? "Sí" : "No"});
        }
        Tablas.llenar(vista.getTblProductos(), filas);
        filtro = new TableRowSorter<>((DefaultTableModel) vista.getTblProductos().getModel());
        vista.getTblProductos().setRowSorter(filtro);
        filtrar();
    }

    private void filtrar() {
        if (filtro != null) {
            filtro.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(vista.getTxtBuscar().getText().trim()), 0));
        }
    }

    private void nuevo() {
        editando = new Producto();
        vista.getTblProductos().clearSelection();
        vista.getLblModo().setText("Nuevo producto");
        vista.getTxtNombre().setText("");
        vista.getTxtPrecio().setText("");
        vista.getChkDisponible().setSelected(true);
        vista.getTxtNombre().requestFocusInWindow();
    }

    private void editar() {
        int fila = vista.getTblProductos().getSelectedRow();
        if (fila < 0) {
            return;
        }
        editando = productos.get(vista.getTblProductos().convertRowIndexToModel(fila));
        vista.getLblModo().setText("Editando: " + editando.getNombre());
        vista.getTxtNombre().setText(editando.getNombre());
        vista.getTxtPrecio().setText(editando.getPrecio().toPlainString());
        vista.getChkDisponible().setSelected(editando.isDisponible());
        for (int i = 0; i < vista.getCmbCategoria().getItemCount(); i++) {
            Categoria c = (Categoria) vista.getCmbCategoria().getItemAt(i);
            if (c.getIdCategoria() == editando.getIdCategoria()) {
                vista.getCmbCategoria().setSelectedIndex(i);
            }
        }
    }

    private void guardar() {
        Categoria categoria = (Categoria) vista.getCmbCategoria().getSelectedItem();
        Producto p = new Producto(editando.getIdProducto(), vista.getTxtNombre().getText(), null,
                Validador.monto(vista.getTxtPrecio().getText()));
        p.setIdCategoria(categoria == null ? 0 : categoria.getIdCategoria());
        p.setDisponible(vista.getChkDisponible().isSelected());
        try {
            servicios.productos.guardar(p, usuario);
            Mensajes.info(vista, "Producto «" + p.getNombre() + "» guardado.");
            cargar();
            nuevo();
        } catch (IllegalArgumentException | SecurityException | SQLException e) {
            Mensajes.de(vista, e);
        }
    }
}
