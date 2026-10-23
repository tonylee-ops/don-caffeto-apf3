package com.doncaffeto.dao;

import com.doncaffeto.model.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos de productos.
 *
 * @author Equipo Don Caffeto
 */
public interface ProductoDAO {

    /**
     * @return productos disponibles para la venta
     * @throws SQLException si falla la consulta
     */
    List<Producto> listarDisponibles() throws SQLException;
}
