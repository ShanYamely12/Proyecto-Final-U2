package pe.edu.upeu.sysdenuncias.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseConnection {

    private static Connection connection;

    // Método principal para obtener la conexión (Estático)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                loadAndConnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void loadAndConnect() {
        try {
            // Cargar propiedades
            Properties prop = new Properties();
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) prop.load(input);
            }

            String url = prop.getProperty("db.url", "jdbc:h2:file:./data/denuncias;AUTO_SERVER=TRUE");
            String user = prop.getProperty("db.username", "sa");
            String password = prop.getProperty("db.password", "");

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión a H2 exitosa.");

            // Ejecutar script si es necesario
            if (Boolean.parseBoolean(prop.getProperty("db.ddl.auto", "true"))) {
                runDdlScript(prop.getProperty("db.ddl.script", "schema.sql"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar: " + e.getMessage());
        }
    }

    private static void runDdlScript(String scriptName) {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream(scriptName)) {
            if (is == null) return;
            Scanner scanner = new Scanner(is, "UTF-8");
            scanner.useDelimiter(";");
            try (Statement stmt = connection.createStatement()) {
                while (scanner.hasNext()) {
                    String sql = scanner.next().trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) stmt.execute(sql);
                }
            }
        } catch (Exception e) {
            System.err.println("Error ejecutando DDL: " + e.getMessage());
        }
    }
}