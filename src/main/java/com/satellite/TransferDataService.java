package com.satellite;

import com.satellite.annotation.Id;
import com.satellite.exception.NoEmptyConstructorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferDataService<T> {

    public static final String SQL_INTEGER_TYPE = "INT";
    public static final String SQL_STRING_TYPE = "VARCHAR";
    public static final int AUCUN_ARGUMENT = 0;

    public void push(List<T> pendingList, Connection connection) throws Exception {

        for (T entity : pendingList) {

            if (!entityTableExists(connection, entity)) {
                createSQLTableFromEntity(connection, entity);
            }
            persistEntityValues(connection, entity);
        }
    }

    private void persistEntityValues(Connection connection, T entity) throws IllegalAccessException {

        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(writeInsertInto(entity));

        for (Field field : fieldsList) {
            field.setAccessible(true);
            String fieldValue = (String.class == field.getType()) ? "'" + field.get(entity).toString() + "'" : field.get(entity).toString();
            sql.append((fieldsList.size() - 1 != fieldsList.indexOf(field)) ? fieldValue + ", " : fieldValue + ");");
        }
        executeSQLUpdate(connection, sql.toString());
    }

    private String writeInsertInto(T entity) {
        return "insert into " + entity.getClass().getSimpleName() + " values(";
    }

    private Boolean entityTableExists(Connection connection, T entity) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, entity.getClass().getSimpleName(), null);
        return tables.next();
    }

    private void createSQLTableFromEntity(Connection connection, T entity){

        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(writeCreateTable(entity));
        String idName = "";

        for (Field field : fieldsList) {

            if (0 == fieldsList.indexOf(field)) {
                idName = field.getName();
            }
            sql.append(field.getName());

            if (Integer.class == field.getType() || int.class == field.getType()) {
                sql.append(" " + SQL_INTEGER_TYPE + ", ");
            } else if (String.class == field.getType()) {
                sql.append(" " + SQL_STRING_TYPE + "(40), ");
            }
        }
        sql.append(writePrimaryKey(idName));
        executeSQLUpdate(connection, sql.toString());
    }

    private String writeCreateTable(T entity) {
        return "create table " + entity.getClass().getSimpleName() + "(";
    }

    private String writePrimaryKey(String idName) {
        return "PRIMARY KEY(" + idName + "));";
    }

    private void executeSQLUpdate(Connection connection, String sql) {
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Field> getOrderedFieldsList(T entity) {

        Field[] fields = entity.getClass().getDeclaredFields();
        List<Field> orderedList = new ArrayList<Field>();

        for (Field field : fields) {
            addToFieldsListInOrder(orderedList, field);
        }
        return orderedList;
    }

    private void addToFieldsListInOrder(List<Field> orderedList, Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Id.class) {
                orderedList.add(0, field);
            } else {
                orderedList.add(field);
            }
        }
    }

    public List<T> fetchAllByClass(Class classType, Connection connection){
        String sql = "SELECT * FROM " + classType.getSimpleName();
        return fetchEntitiesByQuery(classType, connection, sql);
    }

    public List<T> fetchEntitiesByQuery(Class classType, Connection connection, String sql) {
        Field[] fields = classType.getDeclaredFields();
        List<T> list = new ArrayList<T>();

        try {
            ResultSet rs = getResultSetQuery(connection, sql);

            while (rs.next()) {
                T t = null;
                Constructor[] constructors = classType.getDeclaredConstructors();
                Method[] methods = classType.getDeclaredMethods();

                for(Constructor constructor: constructors){

                    if(isEmptyConstructor(constructor)){
                        t = (T) constructor.newInstance();

                        for(Method method : methods){
                            if(isSetterMethod(method)){
                                for(Field field : fields){
                                        if(methodNameIsEqualToFieldName(method, field)){
                                        method.invoke(t,  rs.getObject(field.getName()));
                                    }
                                }
                            }
                        }
                        break;
                    }
                    //si aucun constructeur vide n'a été repéré pour instancier
                    if(constructor.equals(constructors[constructors.length-1])){
                        throw new NoEmptyConstructorException();
                    }
                }
                if(null != t) {
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private ResultSet getResultSetQuery(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    private boolean isEmptyConstructor(Constructor constructor) {
        return AUCUN_ARGUMENT == constructor.getParameterTypes().length;
    }

    private boolean isSetterMethod(Method method) {
        return "set".equals(method.getName().substring(0, 3).toLowerCase());
    }

    private boolean methodNameIsEqualToFieldName(Method method, Field field) {
        return method.getName().substring(3).toLowerCase().equals(field.getName().toLowerCase());
    }

    public Object buildOne(Class classType) throws InstantiationException, IllegalAccessException {
        return classType.newInstance();
    }

    public void fetchByQuery(String query) {

    }
}
