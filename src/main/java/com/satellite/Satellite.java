package com.satellite;

import java.util.List;

public class Satellite<T> extends Linker {

    private List<T> pendingList;

    public Satellite(String hostIp, String hostName, String hostPassword) {
        super(hostIp, hostName, hostPassword);
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

}
