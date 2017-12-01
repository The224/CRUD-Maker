package com.satellite;

import com.satellite.annotation.Id;
import com.satellite.exception.ConnectionFailedException;
import com.satellite.exception.NoConnectionOpenedException;
import com.satellite.exception.NoEmptyConstructorException;
import com.satellite.exception.NoIdAnnotationException;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Satellite {
    // Liste de la BD
    private List<Object> fetchList;
    // Liste des modification qui ne sont pas dans la BD
    private List<Object> pendingList;
    // Class du generic
    //private Class beanClass;
    private static Satellite satellite;

    private TransferDataService transferDataService;

    private ConnectionManager connectionManager;

    public Satellite() throws NoIdAnnotationException {
        pendingList = new ArrayList();
        fetchList = new ArrayList();
        connectionManager = new ConnectionManager();
        transferDataService = new TransferDataService(connectionManager.getConnection());
    }

    public static Satellite getInstance() throws Exception{
        if(null == satellite){
            satellite = new Satellite();
        }
        return satellite;
    }

    public boolean insert(Object obj) {
        for (Object t : fetchList)
            if (getIdValue(t).equals(getIdValue(obj))) {
                System.out.println("fetchList : Un objet avec le meme id existe deja !");
                return false;
            }

        for (Object t : pendingList)
            if (getIdValue(t).equals(getIdValue(obj))) {
                System.out.println("pendingList : Un objet avec le meme id existe deja !");
                return false;
            }
        pendingList.add(obj);
        return true;
    }

    public Boolean connect(String hostName, String port, String database, String user, String password) throws ConnectionFailedException {
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;
        if (null == connectionManager.connect(url, user, password)) {
            throw new ConnectionFailedException();
        }
        return true;
    }

    public Boolean isConnected(){
        return null != connectionManager.getConnection();
    }

    public void closeConnection() throws SQLException {
        connectionManager.close();
    }

    public Object findById(@NotNull Object id) {
        for (Object t : fetchList) {
            if (getIdValue(t).equals(id)) {
                return t;
            }
        }
        return null;
    }

    public List<?> getPendingList() {
        return pendingList;
    }

    public List<?> findAll() {
        return fetchList;
    }

    public boolean remove(@NotNull Object id) {
        for (Object t : fetchList) {
            if (getIdValue(t).equals(id)) {
                fetchList.remove(t);
                return true;
            }
        }
        // TODO : A voir !!!!
        for (Object t : pendingList) {
            if (getIdValue(t).equals(id)) {
                pendingList.remove(t);
                return true;
            }
        }
        return false;
    }

    public boolean update(@NotNull Object id, Object t) {
        remove(id); // The lazy way !
        return insert(t);
    }

    public Satellite fetchAllByClass(Class classType) throws NoEmptyConstructorException{
        fetchList = (List<Object>) transferDataService.fetchAllByClass(classType, connectionManager.getConnection());
        return this;
    }

    public Satellite fetchAllByCondition(Class classType, String condition) {
        String sql = "select * from " + classType.getSimpleName() + " where " + condition + ";";
        fetchList = (List<Object>) transferDataService.fetchEntitiesByQuery(classType, connectionManager.getConnection(), sql);
        return this;
    }

    public Satellite fetchById(Class classType, int id){
        String sql = "select * from " + classType.getSimpleName() + " where id = " + id + ";";
        fetchList = (List<Object>) transferDataService.fetchEntitiesByQuery(classType, connectionManager.getConnection(), sql);
        return this;
    }

    public Satellite fetchByQuery(){
        return null;
    }

    /**
     * Envoie les modifications de pendingList dans fetchList et la BD
     */
    public void push() throws Exception {
        Connection connection = connectionManager.getConnection();

        if (null != connection) {
            transferDataService.push(pendingList, connectionManager.getConnection());
        } else {
            throw new NoConnectionOpenedException();
        }

        fetchList.addAll(pendingList);
        pendingList = new ArrayList();
    }

    private Object getIdValue(Object t) {

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
