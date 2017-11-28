package com.satellite.exception;

public class NoIdAnnotationException extends Exception{

    public NoIdAnnotationException(){
        System.out.println("Votre Class na pas l'annotation @Id sur un de vos parametre.");
    }
}