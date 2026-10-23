package com.doncaffeto.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Consultas de reportes de ventas.
 *
 * @author Equipo Don Caffeto
 */
public interface ReporteDAO {

    /**
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return filas {fecha, cantidad de ventas, total}
     * @throws SQLException si falla la consulta
     */
    List<Object[]> ventasPorDia(LocalDate desde, LocalDate hasta) throws SQLException;

    /**
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return filas {producto, unidades vendidas, importe}
     * @throws SQLException si falla la consulta
     */
    List<Object[]> productosMasVendidos(LocalDate desde, LocalDate hasta) throws SQLException;

    /**
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return filas {método de pago, cantidad de pagos, total}
     * @throws SQLException si falla la consulta
     */
    List<Object[]> ventasPorMetodo(LocalDate desde, LocalDate hasta) throws SQLException;
}
