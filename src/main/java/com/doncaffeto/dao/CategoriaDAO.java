package com.doncaffeto.dao;

import com.doncaffeto.model.Categoria;
import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {
    List<Categoria> listar() throws SQLException;
}
