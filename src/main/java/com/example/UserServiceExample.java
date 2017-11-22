package com.example;

import com.satellite.Satellite;

public class UserServiceExample {

    private Satellite<UserExample> satellite;


    public void safeAddUser(String name, int age) {
        try {
            confirmAddUser(name, age);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void confirmAddUser(String name, int age) throws Exception {
        UserExample user = new UserExample(name,age);
        if (user != null)
            satellite.create(user);
        else
            throw new Exception();
    }

    public void saveCreateUserOnDB() {
        satellite.push();
    }






}
