package com.satellite;

import org.jetbrains.annotations.NotNull;

/**
 * Cette classe permettra dans le futur l'implementation d'une panoplis de SGBDR et SGBD NoSQL
 */
public class SQLUtils {

    public static final String SQL_INTEGER_TYPE = "INT";
    public static final String SQL_STRING_TYPE = "VARCHAR";

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
     * @return primary key ('idName'));
     */
    public static String writePrimaryKey(String idName) {
        return "primary key(" + idName + "));";
    }

    /**
     *
     * @return update <tablename> set <column1>, <column2>, etc.
     */
    @NotNull
    static String writeUpdate(Object entity) {
        return "update " + entity.getClass().getSimpleName() + " set ";
    }
}