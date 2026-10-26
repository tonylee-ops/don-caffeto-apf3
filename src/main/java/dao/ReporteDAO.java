package dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReporteDAO {
    List<Object[]> ventasPorDia(LocalDate desde, LocalDate hasta) throws SQLException;

    List<Object[]> productosMasVendidos(LocalDate desde, LocalDate hasta) throws SQLException;

    List<Object[]> ventasPorMetodo(LocalDate desde, LocalDate hasta) throws SQLException;
}
