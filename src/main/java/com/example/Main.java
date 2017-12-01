package com.example;

import com.satellite.Satellite;

import java.util.List;

public class Main {
    public static void main(String... args) throws Exception{
        Satellite satellite = null;

        try {
            satellite = Satellite.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        satellite.insert(new UserExample(13,"Gabriel", 90));
        satellite.insert(new UserExample(14,"Karla", 34));
        satellite.insert(new UserExample(15,"Tyrion", 56));
        satellite.insert(new UserExample(16,"Alec", 78));

        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        //satellite.push();
        List<UserExample> users = (List<UserExample>) satellite.fetchAllByClass(UserExample.class).findAll();

        List<UserExample> user01 = (List<UserExample>) satellite.fetchAllByCondition(UserExample.class, "name = 'Karla'").findAll();
        List<UserExample> user02 = (List<UserExample>) satellite.fetchById(UserExample.class, 4).findAll();

        for(UserExample user : users){
            System.out.println(user.toString());
        }
        System.out.println(user01);
        System.out.println(user02);
        satellite.closeConnection();
        //satellite.findById(1);
    }
}
