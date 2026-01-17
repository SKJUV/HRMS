package com.hrms.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe singleton pour gérer la connexion à la base de données
 * Utilise le pattern Singleton pour avoir une seule instance
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    
    // Constructeur privé pour empêcher l'instanciation directe
    private DatabaseConnection() {
        loadDatabaseProperties();
    }
    
    /**
     * Charge les propriétés de connexion depuis database.properties ou utilise des valeurs par défaut
     */
    private void loadDatabaseProperties() {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/hrms");
                this.username = props.getProperty("db.username", "postgres");
                this.password = props.getProperty("db.password", "postgres");
            } else {
                // Valeurs par défaut si le fichier n'existe pas
                System.out.println("Fichier database.properties non trouvé. Utilisation des valeurs par défaut.");
                this.url = "jdbc:mysql://localhost:3306/hrms?useSSL=false&serverTimezone=UTC";
                this.username = "root";
                this.password = "";
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de database.properties: " + e.getMessage());
            // Valeurs par défaut
            this.url = "jdbc:mysql://localhost:3306/hrms?useSSL=false&serverTimezone=UTC";
            this.username = "root";
            this.password = "";
        }
        
        System.out.println("Configuration DB: " + url);
    }
    
    /**
     * Retourne l'instance unique de DatabaseConnection
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Retourne une connexion active à la base de données
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Chargement du driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connexion à la base de données établie avec succès !");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL non trouvé: " + e.getMessage());
            }
        }
        return connection;
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Échec du test de connexion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}
