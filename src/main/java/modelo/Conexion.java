package modelo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Conexion {
    private static Properties config;

    private Conexion() {
    }

    private static synchronized Properties config() throws SQLException {
        if (config == null) {
            Path archivo = Path.of("config.properties");
            if (!Files.exists(archivo)) {
                throw new SQLException("No existe config.properties. Copie config.example.properties "
                        + "con ese nombre y escriba la clave de MySQL.");
            }
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(archivo)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("No se pudo leer config.properties", e);
            }
            config = p;
        }
        return config;
    }

    public static Connection obtener() throws SQLException {
        Properties p = config();
        return DriverManager.getConnection(p.getProperty("db.url"), p.getProperty("db.user"),
                p.getProperty("db.password"));
    }
}
