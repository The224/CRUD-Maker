package com.satellite.exception;

public class NoIdAnnotation extends Exception{
    public NoIdAnnotation(){
        System.out.println("Votre Class na pas l'annotation @Id sur un de vos parametre.");
    }
}