package com.doncaffeto.dao;

import com.doncaffeto.model.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {
    List<Producto> listar(boolean soloDisponibles) throws SQLException;

    void guardar(Producto producto) throws SQLException;
}
