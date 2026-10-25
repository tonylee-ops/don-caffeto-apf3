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

public class MesaDAOImpl implements MesaDAO {
    @Override
    public List<Mesa> listar() throws SQLException {
        String sql = "SELECT id_mesa, numero, capacidad, estado, "
                + "COALESCE(TIMESTAMPDIFF(SECOND, ocupada_desde, NOW()), 0) AS segundos FROM mesas ORDER BY numero";
        List<Mesa> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Mesa m = new Mesa(rs.getInt("id_mesa"), rs.getInt("numero"), rs.getString("estado"));
                m.setCapacidad(rs.getInt("capacidad"));
                m.setSegundosOcupada(rs.getLong("segundos"));
                lista.add(m);
            }
        }
        return lista;
    }

    @Override
    public boolean pedirCuenta(int idMesa) throws SQLException {
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(
                "UPDATE mesas SET estado = 'CUENTA' WHERE id_mesa = ? AND estado = 'OCUPADA'")) {
            ps.setInt(1, idMesa);
            return ps.executeUpdate() == 1;
        }
    }
}
