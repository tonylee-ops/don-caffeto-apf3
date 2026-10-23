package com.doncaffeto.util;

import com.doncaffeto.model.Pedido;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Exporta la lista de pedidos a un archivo Excel (usa Apache POI).
 *
 * @author Equipo Don Caffeto
 */
public final class ExportarExcel {

    private ExportarExcel() {
    }

    /**
     * @param pedidos pedidos a exportar
     * @param archivo archivo .xlsx de destino
     * @throws IOException si no se puede escribir el archivo
     */
    public static void pedidos(List<Pedido> pedidos, File archivo) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(); FileOutputStream salida = new FileOutputStream(archivo)) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            Sheet hoja = libro.createSheet("Pedidos");
            String[] titulos = {"N°", "Fecha", "Modalidad", "Mesa", "Mesero", "Estado", "Total"};
            Font negrita = libro.createFont();
            negrita.setBold(true);
            negrita.setColor(IndexedColors.WHITE.getIndex());
            CellStyle estiloCabecera = libro.createCellStyle();
            estiloCabecera.setFont(negrita);
            estiloCabecera.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            estiloCabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle estiloSoles = libro.createCellStyle();
            estiloSoles.setDataFormat(libro.createDataFormat().getFormat("\"S/ \"#,##0.00"));

            Row cabecera = hoja.createRow(0);
            for (int i = 0; i < titulos.length; i++) {
                cabecera.createCell(i).setCellValue(titulos[i]);
                cabecera.getCell(i).setCellStyle(estiloCabecera);
            }
            int fila = 1;
            for (Pedido p : pedidos) {
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(p.getIdPedido());
                r.createCell(1).setCellValue(p.getFecha().format(formato));
                r.createCell(2).setCellValue(p.getModalidad());
                r.createCell(3).setCellValue(p.getNumeroMesa() == null ? "-" : String.valueOf(p.getNumeroMesa()));
                r.createCell(4).setCellValue(p.getMesero());
                r.createCell(5).setCellValue(p.getEstado());
                r.createCell(6).setCellValue(p.getTotal().doubleValue());
                r.getCell(6).setCellStyle(estiloSoles);
            }
            for (int i = 0; i < titulos.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(salida);
        }
    }
}
