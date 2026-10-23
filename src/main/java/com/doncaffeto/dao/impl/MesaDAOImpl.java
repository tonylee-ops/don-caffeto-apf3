package com.doncaffeto.dao.impl;

import com.doncaffeto.dao.MesaDAO;
import com.doncaffeto.model.Mesa;
import com.doncaffeto.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación MySQL de {@link MesaDAO}.
 *
 * @author Equipo Don Caffeto
 */
public class MesaDAOImpl implements MesaDAO {

    @Override
    public List<Mesa> listarLibres() throws SQLException {
        List<Mesa> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id_mesa, numero, estado FROM mesas WHERE estado = 'LIBRE' ORDER BY numero");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mesa(rs.getInt("id_mesa"), rs.getInt("numero"), rs.getString("estado")));
            }
        }
        return lista;
    }
}
