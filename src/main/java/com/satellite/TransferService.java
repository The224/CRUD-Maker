package com.satellite;

import com.satellite.annotation.Id;

import javax.xml.transform.Result;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransferService<T> {

    public void push(List<T> pendingList, Connection connection) throws IllegalAccessException {

        List<Field> fieldsList = new ArrayList<Field>();

        for(T entity : pendingList){
            Field[] fields = entity.getClass().getDeclaredFields();

            for(Field field : fields){

                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation:annotations) {
                    if (annotation.annotationType() == Id.class) {
                        field.setAccessible(true);
                        fieldsList.add(0, field);
                    }
                    fieldsList.add(field);
                }
            }

            String query = "insert into " + entity.getClass().getSimpleName() + " values(";

            for(Field field : fieldsList){
                if(fieldsList.size()-1 != fieldsList.indexOf(field)){
                    query += field.get(entity).toString() + ",";
                }
                else{
                    query += field.get(entity).toString() + ");";
                }
            }
            System.out.println(query);

            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void fetchAll() {

    }

    public void fetchByQuery(String query) {

    }
}
