package com.satellite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to manage the JDBC connection
 */
public class ConnectionManager {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    /**
     * Connect to the database via JDBC
     *
     * @param url
     * @param user
     * @param password
     * @return the connection
     */
    public Connection connect(String url, String user, String password) {

        connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            System.out.println("Problème de connexion: ");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close the connection to the database via JDBC
     *
     * @throws SQLException
     */
    public void close() throws SQLException{
        if(null != connection){
            connection.close();
            connection = null;
        }
    }
}
