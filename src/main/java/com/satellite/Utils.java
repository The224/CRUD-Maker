package com.satellite;

public class Utils {






    private String writeCreateTable(Object entity) {
        return "create table " + entity.getClass().getSimpleName() + "(";
    }

    private String writePrimaryKey(String idName) {
        return "PRIMARY KEY(" + idName + "));";
    }



}
