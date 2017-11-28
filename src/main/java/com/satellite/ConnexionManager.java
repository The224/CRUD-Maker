package com.satellite;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionManager {

    private String hostIp;
    private String hostName;
    private String hostPassword;
    private Integer hostPort;

    private String ssh_username;
    private String ssh_hostname;
    private int ssh_localport;
    private String ssh_localadress;
    private int ssh_remoteport;
    private String ssh_pathkey;


    public ConnexionManager(String hostIp, String hostName, String hostPassword) {
        this.hostIp = hostIp;
        this.hostName = hostName;
        this.hostPassword = hostPassword;
        this.hostPort = hostPort;
    }

    private Session sshSession;

    public Connection connectJDBC(String url, String user, String password) {

        openSSHSession();
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

    public void openSSHSession() {

        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(ssh_username, ssh_hostname, 22);
            jsch.addIdentity(ssh_pathkey);

            configureSSH(sshSession);
            sshSession.connect();
            sshSession.setPortForwardingL(ssh_localport, ssh_localadress, ssh_remoteport);
            System.out.println("Session créée");
        } catch (Exception e) {
            System.out.println("Problème de création de session: ");
            e.printStackTrace();
        }

    }

    public void configureSSH(Session session) {
        java.util.Properties configurations = new java.util.Properties();
        configurations.put("StrictHostKeyChecking", "no");
        session.setConfig(configurations);
    }

    public void closeSSHSession(){
        sshSession.disconnect();
    }

    public void push() {

    }

    public void fetchAll() {

    }

    public void fetchByQuery(String query) {

    }

}
