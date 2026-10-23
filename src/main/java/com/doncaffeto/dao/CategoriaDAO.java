package com.doncaffeto.dao;

import com.doncaffeto.model.Categoria;
import java.sql.SQLException;
import java.util.List;

/**
 * Acceso a datos de la tabla categorias.
 *
 * @author Equipo Don Caffeto
 */
public interface CategoriaDAO {

    /**
     * @return categorías ordenadas por nombre
     * @throws SQLException si falla la consulta
     */
    List<Categoria> listar() throws SQLException;
}
