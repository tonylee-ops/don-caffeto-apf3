package services;

import dao.ReporteDAO;
import modelo.Usuario;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReporteService {
    public static final String[] TIPOS = {"Ventas por día", "Productos más vendidos", "Ventas por método de pago"};

    public static final String[][] COLUMNAS = {
        {"Fecha", "N° de ventas", "Total"},
        {"Producto", "Unidades", "Importe"},
        {"Método de pago", "N° de pagos", "Total"}
    };

    private final ReporteDAO reporteDAO;

    public ReporteService(ReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

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
