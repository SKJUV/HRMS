module com.hrms {
    // Modules JavaFX requis
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // Module JDBC pour PostgreSQL
    requires java.sql;
    
    // Ouvrir les packages aux modules JavaFX pour la réflexion (nécessaire pour FXML)
    opens com.hrms to javafx.fxml;
    opens com.hrms.controller to javafx.fxml;
    opens com.hrms.model to javafx.base;
    
    // Exporter les packages principaux
    exports com.hrms;
    exports com.hrms.controller;
    exports com.hrms.model;
    exports com.hrms.service;
    exports com.hrms.dao;
    exports com.hrms.util;
}
