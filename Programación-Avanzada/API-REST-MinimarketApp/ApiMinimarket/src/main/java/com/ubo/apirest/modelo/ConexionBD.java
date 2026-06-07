package com.ubo.apirest.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL  = "jdbc:mysql://localhost:3306/minimarket_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "adminMinimarket";
    private static final String PASS = "JL0*_dbAdmin10";

    public static Connection conectar() throws SQLException {
        try {
            // Forzar carga del driver de MySQL (con mysql-connector-j 8.x)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Si llegas aquí, el jar de MySQL NO está en el WAR
            throw new SQLException("Driver MySQL no encontrado en el classpath", e);
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
