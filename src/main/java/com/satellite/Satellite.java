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

public class Satellite {
    // Liste de la BD
    private List<Object> fetchList;
    // Liste des modification qui ne sont pas dans la BD
    private List<Object> pendingList;
    // Liste des éléments supprimés du fetch
    private List<Object> removeList;
    // Class du generic
    //private Class beanClass;
    private static Satellite satellite;

    private TransferDataService transferDataService;

    private ConnectionManager connectionManager;

    public Satellite() throws NoIdAnnotationException {
        pendingList = new ArrayList<Object>();
        fetchList = new ArrayList<Object>();
        removeList = new ArrayList<Object>();
        connectionManager = new ConnectionManager();
        transferDataService = new TransferDataService();
    }

    //singleton
    public static Satellite getInstance() throws Exception{
        if(null == satellite){
            satellite = new Satellite();
        }
        return satellite;
    }

    /* CONNECTION METHODS */

    public Boolean connect(String hostName, String port, String database, String user, String password) throws ConnectionFailedException {
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;
        if (null == connectionManager.connect(url, user, password)) {
            throw new ConnectionFailedException();
        }
        transferDataService.setConnection(connectionManager.getConnection());
        return true;
    }

    public Boolean isConnected(){
        return null != connectionManager.getConnection();
    }

    public void closeConnection() throws SQLException {
        connectionManager.close();
    }

    /*  PENDING LIST METHODS */

    public List<?> getPendingList() {
        return pendingList;
    }

    public boolean insert(Object obj) {
        if (listContainsDuplicateId(obj, pendingList) || listContainsDuplicateId(obj, fetchList)){
            return false;
        }
        pendingList.add(obj);
        return true;
    }

    private boolean listContainsDuplicateId(Object obj, List list) {
        for (Object entity : list)
            if (getIdValue(entity).equals(getIdValue(obj)) && entity.getClass() == obj.getClass()) {
                System.out.println("pendingList : Un objet avec le meme id existe deja !");
                return true;
            }
        return false;
    }

    /* FIND METHODS -- SELECT FROM FETCHLIST */

    public List<?> findAll() {
        return fetchList;
    }

    public List findById(@NotNull Object id) {
        List entitiesList = new ArrayList();

        for (Object entity : fetchList) {
            if (getIdValue(entity).equals(id)) {
                entitiesList.add(entity);
            }
        }
        return entitiesList;
    }

    public Object findById(@NotNull Object id, Class classtype) {

        for (Object entity : fetchList) {
            if (getIdValue(entity).equals(id) && classtype == entity.getClass()) {
                return entity;
            }
        }
        return null;
    }

    public boolean remove(@NotNull Object obj) {
        for (Object entity : fetchList) {
            if (getIdValue(entity) == getIdValue(obj)) {
                removeList.add(entity);
                fetchList.remove(entity);
                return true;
            }
        }

        for (Object entity : pendingList) {
            if (getIdValue(entity) == getIdValue(obj)) {
                pendingList.remove(entity);
                return true;
            }
        }
        return false;
    }

    public boolean update(@NotNull Object id, Object t) {
        remove(id); // The lazy way !
        return insert(t);
    }


    /* FETCH METHODS -- GET FROM DATABASE TO FETCHLIST */

    public Satellite fetchAllDatabase(String classesPathUrl){
        fetchList = (List<Object>) transferDataService.fetchAllDatabase(classesPathUrl);
        return this;
    }

    public Satellite fetchAllByClass(Class... classes) throws NoEmptyConstructorException{
        fetchList = new ArrayList();
        for (Class classe : classes) {
            fetchList.addAll(transferDataService.fetchAllByClass(classe));
        }
        return this;
    }

    public Satellite fetchAllByCondition(Class classType, String condition) {
        String sql = "select * from " + classType.getSimpleName() + " where " + condition + ";";
        fetchList = (List<Object>) transferDataService.fetchEntitiesByQuery(classType, sql);
        return this;
    }

    /**
     * Envoie les modifications de pendingList dans fetchList et la BD
     */
    public void push() throws Exception {

        if (isConnected()) {
            // gère les nouveaux éléments et les éléments supprimés
            transferDataService.push(pendingList, removeList);
        } else {
            throw new NoConnectionOpenedException();
        }

        fetchList.addAll(pendingList);
        pendingList = new ArrayList<Object>();
    }

    public static Object getIdValue(Object t) {

        try {
            for (Field field : t.getClass().getDeclaredFields()) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == Id.class) {
                        field.setAccessible(true);
                        return field.get(t);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
