package com.satellite;

import com.satellite.annotation.Id;
import com.satellite.exception.NoIdAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Satellite<T> {

    private List<T> pendingList;
    private Class beanClass;

    private Class idClass;

    public Satellite(Class beanClass) throws NoIdAnnotation{
        pendingList = new ArrayList<T>();
        this.beanClass = beanClass;

        setIdClass();
    }

    public void insert(T t) {
        pendingList.add(t);
    }

    public T findById(Object id) {
        for (T t : pendingList) {
            if (getIdValue(t).equals(id)) {
                return t;
            }
        }
        return null;
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

    private void setIdClass() throws NoIdAnnotation {
        try {
            for(Field field : beanClass.getDeclaredFields()){
                Annotation[] annotations = field.getDeclaredAnnotations();

                for (Annotation annotation:annotations) {
                    if (annotation.annotationType() == Id.class) { /// La variable qui est l'idClass

                        idClass = field.getType();
                        /*
                        Class type = field.getType();
                        String name = field.getName();
                        System.out.println(type);
                        System.out.println(name);
                        System.out.println(annotation);
                        */
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new NoIdAnnotation();
    }
}
