package com.satellite;

import com.satellite.annotation.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransferService<T> {

    private static final String TABLE_NAME = "test";
    private Class classType;

    public TransferService(Class classType) {
        this.classType = classType;
    }


    public void push(List<T> pendingList, Connection connection) {

        List<Field> fieldsList = new ArrayList<Field>();

        for (T entity : pendingList) {
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {

                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == Id.class) {
                        field.setAccessible(true);
                        fieldsList.add(0, field);
                    }
                    fieldsList.add(field);
                }
            }

            String query = "insert into" + entity.getClass().getName() + "values(";

            for (Field field : fieldsList) {
                if (fieldsList.size() - 1 != fieldsList.indexOf(field)) {
                    query += field + ",";
                } else {
                    query += field + ");";
                }
            }

            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

                    fields[i].set(t ,);

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
