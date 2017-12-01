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

        satellite.insert(new User(13,"Gabriel", 90));
        satellite.insert(new User(14,"Karla", 34));
        satellite.insert(new User(15,"Tyrion", 56));
        satellite.insert(new User(16,"Alec", 78));

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");
        satellite.push();
        List<User> users = (List<User>) satellite.fetchAllByClass(User.class).findAll();

        List<User> user01 = (List<User>) satellite.fetchAllByCondition(User.class, "name = 'Karla'").findAll();
        List<User> user02 = (List<User>) satellite.fetchById(User.class, 4).findAll();

        for(User user : users){
            System.out.println(user.toString());
        }
        System.out.println(user01);
        System.out.println(user02);
        satellite.closeConnection();
        //satellite.findById(1);
    }
}
