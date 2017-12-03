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
    private static final int AUCUN_ARGUMENT = 0;
    private Connection connection;

    /**
     *
     * Send all new entities, modifications and removals to the database
     *
     * @param pendingList
     * @param removeList
     * @throws Exception
     */
    public void push(List<?> pendingList, List<?> removeList, List<?> updateList) throws Exception {
        for (Object entity : pendingList) {
            // Verifier que la table existe dans la DB
            // Dans la boucle pour chaque type different
            if (!entityTableExists(entity.getClass())) {
                createSQLTableFromEntity(entity);
            }
            persistEntityValues(entity);
        }
        for(Object entity : removeList){
            deleteEntity(entity);
        }
        for(Object entity : updateList){
            update(entity);
        }
    }

    /**
     * Delete an entity from the database by its id
     *
     * @param entity
     */
    private void deleteEntity(Object entity){
        String sql = "DELETE FROM " + entity.getClass().getSimpleName() + " WHERE id = " + Satellite.getIdValue(entity) + ";";
        executeSQLUpdate(sql);
    }

    /**
     * Update the row corresponding to the entity in the database
     *
     * @param entity
     * @throws IllegalAccessException
     */
    public void update(Object entity) throws IllegalAccessException {
        List<Field> fieldsList = getOrderedFieldsList(entity);

        StringBuilder sql = new StringBuilder(SQLUtils.writeUpdate(entity));

        for (Field field : fieldsList) {
            field.setAccessible(true);
            String fieldValue = (String.class == field.getType()) ? "'" + field.get(entity).toString() + "'" : " " + field.get(entity).toString();
            sql.append(field.getName() + " = ");
            sql.append((fieldsList.size() - 1 != fieldsList.indexOf(field)) ? fieldValue + ", " : fieldValue);
        }
        sql.append(" WHERE id = " + Satellite.getIdValue(entity));
        executeSQLUpdate(sql.toString());
    }

    /**
     * Insert all the entity values in the database
     *
     * @param entity
     * @throws IllegalAccessException
     */
    private void persistEntityValues(Object entity) throws IllegalAccessException {
        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(SQLUtils.writeInsertInto(entity));

        for (Field field : fieldsList) {
            field.setAccessible(true);
            String fieldValue = (String.class == field.getType()) ? "'" + field.get(entity).toString() + "'" : field.get(entity).toString();

            // Detecte le dernier element
            sql.append((fieldsList.size() - 1 != fieldsList.indexOf(field)) ? fieldValue + ", " : fieldValue + ");");
        }
        executeSQLUpdate(sql.toString());
    }

    /**
     * Checks if the table corresponding to a particular class exists already
     *
     * @param entityClass
     * @return
     * @throws SQLException
     */
    private Boolean entityTableExists(Class entityClass) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, entityClass.getSimpleName(), null);
        return tables.next();
    }

    /**
     * Create an entity class table in the database
     *
     * @param entity
     */
    private void createSQLTableFromEntity(Object entity){

        List<Field> fieldsList = getOrderedFieldsList(entity);
        StringBuilder sql = new StringBuilder(SQLUtils.writeCreateTable(entity));
        String idName = "";

        for (Field field : fieldsList) {

            if (0 == fieldsList.indexOf(field)) {
                idName = field.getName();
            }
            sql.append(field.getName());

            // BUG IF OTHER THEN INT OR STRING
            if (Integer.class == field.getType() || int.class == field.getType()) {
                sql.append(" " + SQLUtils.SQL_INTEGER_TYPE + ", ");
            } else if (String.class == field.getType()) {
                sql.append(" " + SQLUtils.SQL_STRING_TYPE + "(40), ");
            }
        }
        sql.append(SQLUtils.writePrimaryKey(idName));
        executeSQLUpdate(sql.toString());
    }

    /**
     * Delete  an entity class table from the database
     *
     * @param entity
     */
    private void dropTable(Class entity) {
        executeSQLUpdate("DROP TABLE" + entity.getSimpleName() + ";");
    }

    /**
     * Execute an update into the database
     *
     * @param sql
     */
    private void executeSQLUpdate(String sql) {
        System.out.println(sql);
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * List all the fields of an entity in order (beginnning by its id)
     *
     * @param entity
     * @return the ordered fields list
     */
    private List<Field> getOrderedFieldsList(Object entity) {

        Field[] fields = entity.getClass().getDeclaredFields();
        List<Field> orderedList = new ArrayList<Field>();

        for (Field field : fields) {
            addToFieldsListInOrder(orderedList, field);
        }
        return orderedList;
    }

    /**
     * Add to the list in order whether the field is an id or not
     *
     * @param orderedList
     * @param field
     */
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

    /**
     * Fetch all entities from the database and build them
     *
     * @param url
     * @return the list of entities
     */
    public List<?> fetchAllDatabase(String url){

        List listAllEntities = new ArrayList();

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                Class classValue = Class.forName(url + "." + tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase());
                listAllEntities.addAll(fetchAllByClass(classValue));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return listAllEntities;
    }

    /**
     * Fetch all the entities of a particular class in the database
     *
     * @param classType
     * @return the list of entities
     */
    public List<?> fetchAllByClass(Class classType){
        String sql = "SELECT * FROM " + classType.getSimpleName();
        return fetchEntitiesByQuery(classType, sql);
    }

    /**
     * Fetch all the entities that are the results of a particular sql query
     *
     * @param classType
     * @param sql the query
     * @return the list of results
     */
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
     * Get the empty constructor of a particular class
     *
     * @param classType
     * @return the empty constructor
     * @throws NoEmptyConstructorException
     */
    private Constructor getEmptyConstructor(Class classType) throws NoEmptyConstructorException {
        for(Constructor constructor: classType.getDeclaredConstructors()){
            if (AUCUN_ARGUMENT == constructor.getParameterTypes().length)
                return constructor;
        }
        throw new NoEmptyConstructorException();
    }

    /**
     * Get the results of an sql query
     *
     * @param sql the query
     * @return the results in the form of a resultset
     * @throws SQLException
     */
    private ResultSet getResultSetQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * Checks if a method is a setter or not
     *
     * @param method
     * @return true if the method is a setter
     */
    private boolean isSetterMethod(Method method) {
        return "set".equals(method.getName().substring(0, 3).toLowerCase());
    }

    /**
     * Checks if the setter method name corresponds to a field name
     *
     * @param method
     * @param field
     * @return true if the names correspond, false otherwise
     */
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
