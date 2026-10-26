package controlador;

import modelo.Producto;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Carta {
    private final JTable tabla;
    private final JTextField buscar;
    private final JComboBox<Object> categoria;
    private List<Producto> productos = new ArrayList<>();
    private TableRowSorter<DefaultTableModel> filtro;

    public Carta(JTable tabla, JTextField buscar, JComboBox<Object> categoria, Runnable alElegir) {
        this.tabla = tabla;
        this.buscar = buscar;
        this.categoria = categoria;
        categoria.addActionListener(e -> filtrar());
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    alElegir.run();
                }
            }
        });
    }

    public void mostrar(List<Producto> lista) {
        productos = lista;
        List<Object[]> filas = new ArrayList<>();
        TreeSet<String> categorias = new TreeSet<>();
        for (Producto p : lista) {
            filas.add(new Object[]{p.getNombre(), p.getCategoria(), p.getPrecio()});
            categorias.add(p.getCategoria());
        }
        Tablas.llenar(tabla, filas);
        filtro = new TableRowSorter<>((DefaultTableModel) tabla.getModel());
        tabla.setRowSorter(filtro);
        DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
        modelo.addElement("Todas las categorías");
        categorias.forEach(modelo::addElement);
        categoria.setModel(modelo);
    }

    public Producto seleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : productos.get(tabla.convertRowIndexToModel(fila));
    }

    private void filtrar() {
        if (filtro == null) {
            return;
        }
        List<RowFilter<DefaultTableModel, Integer>> filtros = new ArrayList<>();
        filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(buscar.getText().trim()), 0));
        if (categoria.getSelectedIndex() > 0) {
            filtros.add(RowFilter.regexFilter("^" + Pattern.quote(String.valueOf(categoria.getSelectedItem())) + "$", 1));
        }
        filtro.setRowFilter(RowFilter.andFilter(filtros));
    }
}
