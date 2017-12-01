package com.satellite;

/**
 * Cette classe permettra dans le futur l'implementation d'une panoplis de SGBDR et SGBD NoSQL
 */
public class SQLUtils {

    /**
     * @return insert into 'entity class name' values(
     */
    public static String writeInsertInto(Object entity) {
        return "insert into " + entity.getClass().getSimpleName() + " values(";
    }

    /**
     * @return create table 'entity class name' (
     */
    public static String writeCreateTable(Object entity) {
        return "create table " + entity.getClass().getSimpleName() + "(";
    }

    /**
     * @return PRIMARY KEY ('idName'));
     */
    public static String writePrimaryKey(String idName) {
        return "PRIMARY KEY(" + idName + "));";
    }

}
