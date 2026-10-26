package dao;

import modelo.Mesa;
import java.sql.SQLException;
import java.util.List;

public interface MesaDAO {
    List<Mesa> listar() throws SQLException;

    boolean pedirCuenta(int idMesa) throws SQLException;
}
