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

public class Satellite<T> {
    // Liste de la BD
    private List<T> fetchList;
    // Liste des modification qui ne sont pas dans la BD
    private List<T> pendingList;
    // Class du generic
    //private Class beanClass;

    private TransferDataService transferDataService;

    private ConnectionManager connectionManager;

    public Satellite() throws NoIdAnnotationException {
        pendingList = new ArrayList<T>();
        fetchList = new ArrayList<T>();
        connectionManager = new ConnectionManager();
        transferDataService = new TransferDataService();
    }

    public boolean insert(T obj) {
        for (T t : pendingList)
            if (getIdValue(t).equals(getIdValue(obj))) {
                System.out.println("Un objet avec le meme id existe deja !");
                return false;
            }
        pendingList.add(obj);
        return true;
    }

    public void connect(String hostName, String port, String database, String user, String password) throws ConnectionFailedException {
        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;
        if (null == connectionManager.connect(url, user, password)) {
            throw new ConnectionFailedException();
        }
    }

    public void closeConnection() throws SQLException {
        connectionManager.close();
    }

    public T findById(@NotNull Object id) {
        for (T t : fetchList) {
            if (getIdValue(t).equals(id)) {
                return t;
            }
        }
        return null;
    }

    public List<T> getPendingList() {
        return pendingList;
    }

    public List<T> findAll() {
        return fetchList;
    }

    public boolean remove(@NotNull Object id) {
        for (T t : fetchList) {
            if (getIdValue(t).equals(id)) {
                fetchList.remove(t);
                return true;
            }
        }
        // TODO : A voir !!!!
        for (T t : pendingList) {
            if (getIdValue(t).equals(id)) {
                pendingList.remove(t);
                return true;
            }
        }
        return false;
    }

    public boolean update(@NotNull Object id, T t) {
        remove(id); // The lazy way !
        return insert(t);
    }

    public Satellite fetchAllByClass(Class classType) throws NoEmptyConstructorException{
        if(!fetchList.isEmpty()){
            fetchList = new ArrayList<T>();
        }
        fetchList = transferDataService.fetchAllByClass(classType, connectionManager.getConnection());
        return this;
    }

    public Satellite fetchAllByCondition(Class classType, String condition) {
        if(!fetchList.isEmpty()){
            fetchList = new ArrayList<T>();
        }
        String sql = "select * from " + classType.getSimpleName() + " where " + condition + ";";
        fetchList = transferDataService.fetchEntitiesByQuery(classType, connectionManager.getConnection(), sql);
        return this;
    }

    public Satellite fetchById(Class classType, int id){
        if(!fetchList.isEmpty()){
            fetchList = new ArrayList<T>();
        }
        String sql = "select * from " + classType.getSimpleName() + " where id = " + id + ";";
        fetchList = transferDataService.fetchEntitiesByQuery(classType, connectionManager.getConnection(), sql);
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
        pendingList = new ArrayList<T>();
    }

    private Object getIdValue(T t) {

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

    /*public void printClassInformation() {
        try {
            for (Field field : beanClass.getDeclaredFields()) {
                Class type = field.getType();
                String name = field.getName();
                Annotation[] annotations = field.getDeclaredAnnotations();

                System.out.println(type);
                System.out.println(name);

                System.out.println(Arrays.toString(annotations));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
