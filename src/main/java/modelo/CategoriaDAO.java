package modelo;

import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {
    List<Categoria> listar() throws SQLException;
}
