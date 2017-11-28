package com.satellite.exception;

public class ConnectionFailedException extends Exception{

    public ConnectionFailedException() {
        System.out.println("La connexion n\'a pas pu être établi.");
    }
}
