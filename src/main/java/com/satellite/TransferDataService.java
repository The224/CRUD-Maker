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

public class TransferDataService {

    private static final String SQL_INTEGER_TYPE = "INT";
    private static final String SQL_STRING_TYPE = "VARCHAR";
    private static final int AUCUN_ARGUMENT = 0;

    private Connection connection;

    public TransferDataService() {
    }

    public void push(List<?> pendingList) throws Exception {
        for (Object entity : pendingList) {
            // Verifier que la table existe dans la DB
            // Dans la boucle pour chaque type different
            if (!entityTableExists(pendingList.get(0).getClass())) {
                createSQLTableFromEntity(pendingList.get(0));
            }
            persistEntityValues(entity);
        }
    }

    private void persistEntityValues(Object entity) throws IllegalAccessException {
        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(SQLUtils.writeInsertInto(entity));

        for (Field field : fieldsList) {
            field.setAccessible(true);
            // TODO : A voir !!!!
            String fieldValue = (String.class == field.getType()) ? "'" + field.get(entity).toString() + "'" : field.get(entity).toString();

            // Detecte le dernier element
            sql.append((fieldsList.size() - 1 != fieldsList.indexOf(field)) ? fieldValue + ", " : fieldValue + ");");
        }
        executeSQLUpdate(sql.toString());
    }



    private Boolean entityTableExists(Class entityClass) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, entityClass.getSimpleName(), null);
        return tables.next();
    }

    private void createSQLTableFromEntity(Object entity){

        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(SQLUtils.writeCreateTable(entity));
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
        sql.append(SQLUtils.writePrimaryKey(idName));
        executeSQLUpdate(sql.toString());
    }


    private void executeSQLUpdate(String sql) {
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Field> getOrderedFieldsList(Object entity) {

        Field[] fields = entity.getClass().getDeclaredFields();
        List<Field> orderedList = new ArrayList<Field>();

        for (Field field : fields) {
            addToFieldsListInOrder(orderedList, field);
        }
        return orderedList;
    }

    private void addToFieldsListInOrder(List<Field> orderedList, Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();

        if (annotations.length == 0)
            orderedList.add(field);

        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Id.class) {
                orderedList.add(0, field);
            } else {
                orderedList.add(field);
            }
        }
    }

    public List<?> fetchAllByClass(Class classType){
        String sql = "SELECT * FROM " + classType.getSimpleName();
        return fetchEntitiesByQuery(classType, sql);
    }

    public List<?> fetchEntitiesByQuery(Class classType, String sql) {
        Field[] fields = classType.getDeclaredFields();
        Method[] methods = classType.getDeclaredMethods();

        List<Object> list = new ArrayList();

        try {
            ResultSet rs = getResultSetQuery(sql);
            Constructor constructor = getEmptyConstructor(classType);

            while (rs.next()) {
                Object t = constructor.newInstance();

                for(Method method : methods){
                    if(isSetterMethod(method)){
                        for(Field field : fields){
                            if(isMethodNameIsEqualToFieldName(method, field)){
                                method.invoke(t,  rs.getObject(field.getName()));
                            }
                        }
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param classType
     * @return Le constructor sans paramettre
     * @throws NoEmptyConstructorException
     */
    private Constructor getEmptyConstructor(Class classType) throws NoEmptyConstructorException {
        for(Constructor constructor: classType.getDeclaredConstructors()){
            if (AUCUN_ARGUMENT == constructor.getParameterTypes().length)
                return constructor;
        }
        throw new NoEmptyConstructorException();
    }

    private ResultSet getResultSetQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    private boolean isSetterMethod(Method method) {
        return "set".equals(method.getName().substring(0, 3).toLowerCase());
    }

    private boolean isMethodNameIsEqualToFieldName(Method method, Field field) {
        return method.getName().substring(3).toLowerCase().equals(field.getName().toLowerCase());
    }


    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
