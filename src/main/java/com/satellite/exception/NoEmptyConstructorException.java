package com.satellite.exception;

public class NoEmptyConstructorException extends Exception{
    public NoEmptyConstructorException() {
        System.out.println("No empty constructor found in class.");
    }
}
