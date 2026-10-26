package modelo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExportarExcel {
    private ExportarExcel() {
    }

    public static void exportar(String titulo, String[] columnas, List<Object[]> filas, File archivo)
            throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(); FileOutputStream salida = new FileOutputStream(archivo)) {
            Sheet hoja = libro.createSheet(titulo);

            Font negrita = libro.createFont();
            negrita.setBold(true);
            negrita.setColor(IndexedColors.WHITE.getIndex());
            CellStyle cabecera = libro.createCellStyle();
            cabecera.setFont(negrita);
            cabecera.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            cabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle soles = libro.createCellStyle();
            soles.setDataFormat(libro.createDataFormat().getFormat("\"S/ \"#,##0.00"));

            Row r0 = hoja.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell c = r0.createCell(i);
                c.setCellValue(columnas[i]);
                c.setCellStyle(cabecera);
            }
            int n = 1;
            for (Object[] fila : filas) {
                Row r = hoja.createRow(n++);
                for (int i = 0; i < fila.length; i++) {
                    Cell c = r.createCell(i);
                    if (fila[i] instanceof BigDecimal) {
                        c.setCellValue(((BigDecimal) fila[i]).doubleValue());
                        c.setCellStyle(soles);
                    } else if (fila[i] instanceof Number) {
                        c.setCellValue(((Number) fila[i]).doubleValue());
                    } else {
                        c.setCellValue(fila[i] == null ? "" : String.valueOf(fila[i]));
                    }
                }
            }
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(salida);
        }
    }
}
