package com.satellite;

import com.satellite.annotation.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferDataService<T> {

    public static final String SQL_INTEGER_TYPE = "INT";
    public static final String SQL_STRING_TYPE = "VARCHAR";
    private static final String TABLE_NAME = "test";
    private Class classType;

    public TransferDataService(Class classType) {
        this.classType = classType;
    }

    public void push(List<T> pendingList, Connection connection) throws Exception{

        for (T entity : pendingList) {

            if(!entityTableExists(connection, entity)){
                createSQLTableFromEntity(connection, entity);
            }
            persistEntityValues(connection, entity);
        }
    }

    private void persistEntityValues(Connection connection, T entity) throws IllegalAccessException {

        List<Field> fieldsList = getOrderedFieldsList(entity);
        String sql = "insert into " + entity.getClass().getSimpleName() + " values(";

        for(Field field : fieldsList){
            field.setAccessible(true);

            String fieldValue = (String.class == field.getType()) ? "'" + field.get(entity).toString() + "'" : field.get(entity).toString();
            sql += (fieldsList.size()-1 != fieldsList.indexOf(field)) ? fieldValue + ", " : fieldValue + ");";
        }
        executeSQLUpdate(connection, sql);
    }

    private Boolean entityTableExists(Connection connection, T entity) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, entity.getClass().getSimpleName(), null);

        if(!tables.next()){
            return false;
        }
        return true;
    }

    private void createSQLTableFromEntity(Connection connection, T entity) throws SQLException {

        List<Field> fieldsList = getOrderedFieldsList(entity);
        String sql = "create table " + entity.getClass().getSimpleName() + "(";
        String idName = "";

        for(Field field : fieldsList) {

            if (0 == fieldsList.indexOf(field)) {
                idName = field.getName();
            }

            sql += field.getName();

            if (Integer.class == field.getType() || int.class == field.getType()) {
                sql += " " + SQL_INTEGER_TYPE+ ", ";
            } else if (String.class == field.getType()) {
                sql += " " + SQL_STRING_TYPE + "(40), ";
            }
        }
        sql += "PRIMARY KEY(" + idName + "));";
        executeSQLUpdate(connection, sql);
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

            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == Id.class) {
                    orderedList.add(0, field);
                }
                else{
                    orderedList.add(field);
                }
            }
        }
        return orderedList;
    }

    public List<T> fetchAll(Statement statement) {
        String sql = "SELECT * FROM "+TABLE_NAME;
        List<T> liste = new ArrayList<T>();

        try {
            ResultSet rs = statement.executeQuery(sql);

            Field[] fields = classType.getDeclaredFields();

            while(rs.next()){
                Object[] variables = new Object[fields.length];

                for (int i = 0; i < fields.length; i++) {

                    variables[i] = rs.getObject(fields[i].getName());

                    T t = (T) buildOne();

                    //fields[i].set(t ,);

                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    public Object buildOne() throws InstantiationException, IllegalAccessException {
        return classType.newInstance();
    }

    public void fetchByQuery(String query) {

    }

    private Object test(T t) {
        try {
            for(Field field : t.getClass().getDeclaredFields()){
                field.get(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
