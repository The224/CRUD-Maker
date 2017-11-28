package com.satellite;

import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Session;


import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

    private String hostIp;
    private String hostName;
    private String hostPassword;
    private Integer hostPort;

    public Connection connect(String url, String user, String password) {

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            System.out.println("Problème de connexion: ");
            e.printStackTrace();
        }
        return connection;
    }

    public void push() {

    }

    public void fetchAll() {

    }

    public void fetchByQuery(String query) {

    }

}
