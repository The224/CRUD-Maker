package com.satellite.exception;

public class NoIdAnnotationException extends Exception{

    public NoIdAnnotationException(){
        System.out.println("Your class has no @Id annotation.");
    }
}