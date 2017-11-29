package com.satellite;

import com.satellite.annotation.Id;
import com.satellite.exception.ConnectionFailedException;
import com.satellite.exception.NoConnectionOpenedException;
import com.satellite.exception.NoIdAnnotationException;
import com.sun.istack.internal.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Satellite<T> {
    // Liste de la BD
    private List<T> fetchList;
    // Liste des modification qui ne sont pas dans la BD
    private List<T> pendingList;
    // Class du generic
    private Class beanClass;

    private TransferService transferService = new TransferService();

    private ConnectionManager connectionManager;

    public Satellite(Class beanClass) throws NoIdAnnotationException {
        pendingList = new ArrayList<T>();
        fetchList = new ArrayList<T>();
        this.beanClass = beanClass;
        connectionManager = new ConnectionManager();
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

    public void connect(String url, String user, String password) throws ConnectionFailedException{
        if(null == connectionManager.connect(url, user, password)){
            throw new ConnectionFailedException();
        }
    }

    public void closeConnection() throws SQLException{
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

    /**
     * Recois toutes les informations de la BD
     */
    public void fetchAll() {
        // TODO : recois de la BD
    }

    /**
     * Recois les informations specifier dans la query
     */
    public void fetchByQuery(String query) {
        // TODO : recois de la BD
    }

    /**
     * Envoie les modifications de pendingList dans fetchList et la BD
     */
    public void push() throws Exception {
        Connection connection = connectionManager.getConnection();

        if(null != connection){
            transferService.push(pendingList, connectionManager.getConnection());
        }
        else{
            throw new NoConnectionOpenedException();
        }

        fetchList.addAll(pendingList);
        pendingList = new ArrayList<T>();
    }

    private Object getIdValue(T t) {

        try {
            for(Field field : t.getClass().getDeclaredFields()){
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation:annotations) {
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

    public void printClassInformation() {
        try {
            for(Field field : beanClass.getDeclaredFields()){
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
    }
}
