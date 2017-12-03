package com.satellite;

import com.satellite.annotation.Id;
import com.satellite.exception.ConnectionFailedException;
import com.satellite.exception.NoConnectionOpenedException;
import com.satellite.exception.NoEmptyConstructorException;
import com.satellite.exception.NoIdAnnotationException;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage classes entities and perform various database-related operations
 */
public class Satellite {
    // Liste de la BD
    private List<Object> fetchList;
    // Liste des modification qui ne sont pas dans la BD
    private List<Object> pendingList;
    // Liste des éléments supprimés du fetch
    private List<Object> removeList;
    // Liste des éléments mis à jour du fetch
    private List<Object> updateList;
    // Class du generic
    //private Class beanClass;
    private static Satellite satellite;

    private TransferDataService transferDataService;

    private ConnectionManager connectionManager;

    private Satellite(){
        pendingList = new ArrayList<Object>();
        fetchList = new ArrayList<Object>();
        removeList = new ArrayList<Object>();
        updateList = new ArrayList<Object>();
        connectionManager = new ConnectionManager();
        transferDataService = new TransferDataService();
    }

    /**
     * Get the instance of Satellite
     *
     * @return satellite an instance of the Satellite class
     */
    public static Satellite getInstance() {
        if(null == satellite){
            satellite = new Satellite();
        }
        return satellite;
    }

    /* CONNECTION METHODS */

    /**
     * Connect to the database
     *
     * @param hostName
     * @param port
     * @param database
     * @param user
     * @param password
     * @return true if satellite was able to connect to the database, false otherwise
     * @throws ConnectionFailedException
     */
    public Boolean connect(String hostName, String port, String database, String user, String password) throws ConnectionFailedException {
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;
        if (null == connectionManager.connect(url, user, password)) {
            throw new ConnectionFailedException();
        }
        transferDataService.setConnection(connectionManager.getConnection());
        return isConnected();
    }

    /**
     * Checks if the satellite is still connected to the database
     *
     * @return true if satellite is still connected to the database, false otherwise
     */
    public Boolean isConnected(){
        return null != connectionManager.getConnection();
    }

    public void closeConnection() throws SQLException {
        connectionManager.close();
    }

    /*  PENDING LIST METHODS */

    /**
     *
     * @return List<?> satellite's pendgin list
     */
    public List<?> getPendingList() {
        return pendingList;
    }

    /**
     * Insert an objet into satellite before sending to database
     *
     * @param obj object to insert
     * @return true if there's no id duplication in the object's class and satellite could add it successfully, false otherwise
     */
    public boolean insert(Object obj) {
        if (listContainsDuplicateId(obj, pendingList) || listContainsDuplicateId(obj, fetchList)){
            return false;
        }
        return pendingList.add(obj);
    }

    private boolean listContainsDuplicateId(Object obj, List list) {
        for (Object entity : list)
            if (getIdValue(entity).equals(getIdValue(obj)) && entity.getClass() == obj.getClass()) {
                System.out.println("pendingList : Un objet avec le meme id existe deja !");
                return true;
            }
        return false;
    }

    /* FETCH LIST METHODS */

    /**
     * Remove an object from satellite latest fetch to remove it from database later
     *
     * @param obj the object to remove from the fetch
     * @return true if the object could be removed successfully from the fetch, false otherwise
     */
    public boolean remove(@NotNull Object obj) {
        Object id = getIdValue(obj);
        return removeById(obj.getClass(), id);
    }

    /**
     * Remove an object from satellite by providing its class and id
     *
     * @param classType the type of class of the object to be removed
     * @param id the id of the object
     * @return true if the object could be removed successfully from the fetch, false otherwise
     */
    public boolean removeById(Class classType, Object id){

        for (Object entity : fetchList) {
            if (id.equals(getIdValue(entity)) && classType == entity.getClass()) {
                removeList.add(entity);
                fetchList.remove(entity);
                return true;
            }
        }

        for (Object entity : pendingList) {
            if (id.equals(getIdValue(entity)) && classType == entity.getClass()) {
                pendingList.remove(entity);
                return true;
            }
        }
        return false;
    }

    /**
     * Update an entity from satellite's latest fetch
     *
     * @param object
     * @param fieldName the name of the field we want to update
     * @param newValue the new value of the field we want to update
     * @return true if the satellite could find and update the entity, false otherwise
     */
    public boolean update(@NotNull Object object, String fieldName, Object newValue) {

        boolean flag = false;

        for(Object entity: fetchList){

            if(getIdValue(object).equals(getIdValue(entity)) && object.getClass() == entity.getClass()){
                Field[] fields = object.getClass().getDeclaredFields();

                for(Field field : fields){
                    if(fieldName.equals(field.getName())){
                        field.setAccessible(true);
                        try {
                            field.set(entity, newValue);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                updateList.add(object);
                fetchList.remove(object);
                flag = true;
                break;
            }
        }
        return flag;
    }

    /* FIND METHODS -- SELECT FROM FETCH LIST */

    /**
     * Get all the entities from satellite's latest fetch
     *
     * @return List<?> all the entities fetched from the database
     */
    public List<?> findAll() {
        return fetchList;
    }

    /**
     * Get all entities from satellite's latest fetch corresponding to a specific id (search in all classes)
     *
     * @param id
     * @return List the entities from all classes corresponding to the id
     */
    public List findById(@NotNull Object id) {
        List entitiesList = new ArrayList();

        for (Object entity : fetchList) {
            if (getIdValue(entity).equals(id)) {
                entitiesList.add(entity);
            }
        }
        return entitiesList;
    }

    /**
     * Get a single entity from satellite's latest fetch corresponding to a specific class and id
     *
     * @param id
     * @param classtype
     * @return the entity corresponding to the class and id
     */
    public Object findById(@NotNull Object id, Class classtype) {

        for (Object entity : fetchList) {
            if (getIdValue(entity).equals(id) && classtype == entity.getClass()) {
                return entity;
            }
        }
        return null;
    }

    /* FETCH METHODS -- GET FROM DATABASE TO FETCH LIST */

    /**
     * Fetch all the entities from the database and build them into Java objects
     *
     * @param classesPathUrl the url of the package containing all the classes in the database
     * @return the satellite
     */
    public Satellite fetchAllDatabase(String classesPathUrl){
        fetchList = (List<Object>) transferDataService.fetchAllDatabase(classesPathUrl);
        return this;
    }

    /**
     * Fetch all the entities from one or various classes contained in the database and build them into Java objects
     *
     * @param classes
     * @return the satellite
     * @throws NoEmptyConstructorException
     */
    public Satellite fetchAllByClass(Class... classes) throws NoEmptyConstructorException{
        fetchList = new ArrayList();
        for (Class classe : classes) {
            fetchList.addAll(transferDataService.fetchAllByClass(classe));
        }
        return this;
    }

    /**
     * Fetch all the entities from a class contained in the database corresponding to a particular condition
     *
     * @param classType
     * @param condition
     * @return the satellite
     */
    public Satellite fetchAllByCondition(Class classType, String condition) {
        String sql = "select * from " + classType.getSimpleName() + " where " + condition + ";";
        fetchList = (List<Object>) transferDataService.fetchEntitiesByQuery(classType, sql);
        return this;
    }

    /* OTHER METHODS */

    /**
     * Send and persist into the connected database all the entities inserted previously into Satellite
     *
     * @throws Exception
     */
    public void push() throws Exception {

        if (isConnected()) {
            // gère les nouveaux éléments et les éléments supprimés
            transferDataService.push(pendingList, removeList, updateList);
        } else {
            throw new NoConnectionOpenedException();
        }

        fetchList.addAll(pendingList);
        pendingList = new ArrayList<Object>();
    }

    /**
     * Get the id of an object from the id annotation written in the class
     *
     * @param object
     * @return the id from the object if found, null otherwise
     */
    public static Object getIdValue(Object object) {

        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == Id.class) {
                        field.setAccessible(true);
                        return field.get(object);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
