package com.doncaffeto.dao;

import com.doncaffeto.model.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos de la tabla productos.
 *
 * @author Equipo Don Caffeto
 */
public interface ProductoDAO {

    /**
     * @param soloDisponibles true para traer solo los que se pueden vender
     * @return productos con el nombre de su categoría
     * @throws SQLException si falla la consulta
     */
    List<Producto> listar(boolean soloDisponibles) throws SQLException;

    /**
     * Crea el producto si su código es 0; si no, lo actualiza.
     *
     * @param producto datos del producto
     * @throws SQLException si falla el registro (por ejemplo, nombre repetido)
     */
    void guardar(Producto producto) throws SQLException;
}
