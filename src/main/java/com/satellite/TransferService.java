package com.satellite;

import java.lang.reflect.Field;
import java.util.List;

public class TransferService<T> {

    public void push(List<T> pendingList, ConnectionManager connectionManager) {

        for(T entity : pendingList){
            Field[] fields = entity.getClass().getDeclaredFields();

        }

    }

    public void fetchAll() {

    }

    public void fetchByQuery(String query) {

    }
}
