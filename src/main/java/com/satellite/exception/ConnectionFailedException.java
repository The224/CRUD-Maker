package com.satellite.exception;

public class ConnectionFailedException extends Exception{

    public ConnectionFailedException() {
        System.out.println("The connection couldn\'t be established.");
    }
}
