package com.example;

import com.satellite.Satellite;

import java.util.List;

public class Main {
    public static void main(String... args) throws Exception{
        Satellite<UserExample> satellite = null;

        try {
            satellite = new Satellite<UserExample>(UserExample.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        satellite.insert(new UserExample(9,"Gabriel", 90));
        satellite.insert(new UserExample(10,"Karla", 34));
        satellite.insert(new UserExample(11,"Tyrion", 56));
        satellite.insert(new UserExample(12,"Alec", 78));

        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        //satellite.push();
        List<UserExample> users = satellite.fetchAllByClass(UserExample.class).findAll();

        List<UserExample> user01 = satellite.fetchAllByCondition(UserExample.class, "id = 3").findAll();

        for(UserExample user : users){
            System.out.println(user.toString());
        }
        System.out.println(user01);
        satellite.closeConnection();
        //satellite.findById(1);
    }
}
