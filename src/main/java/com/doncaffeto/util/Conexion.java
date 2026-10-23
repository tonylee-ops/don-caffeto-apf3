package com.doncaffeto.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexiones a MySQL con los datos de config.properties.
 *
 * @author Equipo Don Caffeto
 */
public final class Conexion {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = Conexion.class.getResourceAsStream("/config.properties")) {
            CONFIG.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("No se encontró config.properties", e);
        }
    }

    private Conexion() {
    }

    /**
     * @return una conexión nueva (se cierra con try-with-resources)
     * @throws SQLException si MySQL no responde o los datos son incorrectos
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user"), CONFIG.getProperty("db.password"));
    }
}
