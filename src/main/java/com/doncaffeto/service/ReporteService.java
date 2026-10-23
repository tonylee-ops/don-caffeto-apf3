package com.doncaffeto.service;

import com.doncaffeto.dao.ReporteDAO;
import com.doncaffeto.model.Usuario;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reportes de ventas (solo administrador).
 *
 * @author Equipo Don Caffeto
 */
public class ReporteService {

    /** Reportes disponibles, en el mismo orden que el combo de la pantalla. */
    public static final String[] TIPOS = {"Ventas por día", "Productos más vendidos", "Ventas por método de pago"};

    /** Columnas de cada reporte. */
    public static final String[][] COLUMNAS = {
        {"Fecha", "N° de ventas", "Total"},
        {"Producto", "Unidades", "Importe"},
        {"Método de pago", "N° de pagos", "Total"}
    };

    private final ReporteDAO reporteDAO;

    /**
     * @param reporteDAO acceso a reportes
     */
    public ReporteService(ReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    /**
     * @param tipo  posición del reporte en {@link #TIPOS}
     * @param desde fecha inicial
     * @param hasta fecha final
     * @param admin administrador en sesión
     * @return filas del reporte
     * @throws SQLException si falla la base de datos
     */
    public List<Object[]> generar(int tipo, LocalDate desde, LocalDate hasta, Usuario admin) throws SQLException {
        Autorizacion.exigir(admin, Autorizacion.ADMIN);
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha «Desde» no puede ser mayor que «Hasta».");
        }
        switch (tipo) {
            case 0:
                return reporteDAO.ventasPorDia(desde, hasta);
            case 1:
                return reporteDAO.productosMasVendidos(desde, hasta);
            default:
                return reporteDAO.ventasPorMetodo(desde, hasta);
        }
    }
}
