package com.satellite;

import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Session;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private Connection connection;

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

    public void close() throws SQLException{
        if(null != connection){
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
