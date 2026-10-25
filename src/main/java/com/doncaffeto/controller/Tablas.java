package com.doncaffeto.controller;

import com.doncaffeto.model.DetallePedido;
import com.doncaffeto.model.Pedido;
import com.doncaffeto.util.ExportarExcel;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public final class Tablas {
    public static final Map<String, String> MODALIDAD = Map.of(
            "SALON", "Salón", "PARA_LLEVAR", "Para llevar", "DELIVERY", "Delivery");

    public static final Map<String, String> ESTADO = Map.of(
            "PENDIENTE", "Pendiente", "EN_PREPARACION", "En preparación", "LISTO", "Listo",
            "PAGADO", "Pagado", "CANCELADO", "Cancelado");

    private Tablas() {
    }

    public static void llenar(JTable tabla, List<Object[]> filas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Object[] f : filas) {
            modelo.addRow(f);
        }
    }

    public static String destino(Pedido p) {
        if (p.getNumeroMesa() != null) {
            return "Mesa " + p.getNumeroMesa();
        }
        return "DELIVERY".equals(p.getModalidad()) ? "Delivery · " + p.getDireccion() : "Para llevar";
    }

    public static String nota(DetallePedido d) {
        return d.getNota() == null ? "—" : d.getNota();
    }

    public static void exportar(Component padre, String titulo, String[] columnas, List<Object[]> filas,
                                String nombre) {
        if (filas.isEmpty()) {
            Mensajes.aviso(padre, "No hay datos para exportar.");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx"));
        selector.setSelectedFile(new File(nombre + ".xlsx"));
        if (selector.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".xlsx");
        }
        try {
            ExportarExcel.exportar(titulo, columnas, filas, archivo);
            if (Mensajes.confirmar(padre, "Excel guardado en:\n" + archivo.getAbsolutePath() + "\n\n¿Abrirlo ahora?")
                    && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (IOException e) {
            Mensajes.error(padre, "No se pudo guardar el archivo:\n" + e.getMessage());
        }
    }
}
