package com.satellite.exception;

public class NoConnectionOpenedException extends Exception{

    public NoConnectionOpenedException() {
        System.out.println("Attempting to do a Satellite without a connection to the database opened.");
    }
}
