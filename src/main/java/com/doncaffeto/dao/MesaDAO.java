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
     * @return mesas libres
     * @throws SQLException si falla la consulta
     */
    List<Mesa> listarLibres() throws SQLException;
}
