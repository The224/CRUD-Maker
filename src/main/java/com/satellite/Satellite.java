package com.satellite;

import com.satellite.annotation.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Satellite<T> extends Linker {

    private List<T> pendingList;

    public Satellite(String hostIp, String hostName, String hostPassword) {
        super(hostIp, hostName, hostPassword);
        pendingList = new ArrayList<T>();

    }

    public void create(T t) {
        pendingList.add(t);
    }

    public T read(int i) {
        return pendingList.get(i);
    }

    public void update(int t) {

    }

    public void delete(T t) {
        pendingList.remove(t);
    }

    public void printClassInformation(T t) {
        try {
            Class loadedClass = t.getClass();
            System.out.println("Class " + loadedClass + " found successfully!");

            Field[] fields = t.getClass().getDeclaredFields();
            System.out.println(Arrays.toString(fields));

            System.out.println(t.getClass().isAnnotationPresent(Id.class));

            Annotation[] annotations = t.getClass().getDeclaredAnnotations();
            System.out.println(Arrays.toString(annotations));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
