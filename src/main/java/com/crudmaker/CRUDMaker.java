package com.crudmaker;

import java.util.List;

public class CRUDMaker<T> {

    private List<T> pendingList;

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

    public void push() {



    }





}
