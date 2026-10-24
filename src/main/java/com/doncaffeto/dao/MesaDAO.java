package com.doncaffeto.dao;

import com.doncaffeto.model.Mesa;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos de la tabla mesas.
 *
 * @author Equipo Don Caffeto
 */
public interface MesaDAO {

    /**
     * @return las mesas del salón con su estado y el tiempo que llevan ocupadas
     * @throws SQLException si falla la consulta
     */
    List<Mesa> listar() throws SQLException;

    /**
     * Pone la mesa en CUENTA (verde) si estaba OCUPADA.
     *
     * @param idMesa código de la mesa
     * @return true si se cambió
     * @throws SQLException si falla la actualización
     */
    boolean pedirCuenta(int idMesa) throws SQLException;
}
