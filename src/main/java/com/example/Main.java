package com.example;

import com.satellite.Satellite;

public class Main {
    public static void main(String... args) throws Exception{
        Satellite<UserExample> satellite = null;

        try {
            satellite = new Satellite<UserExample>(UserExample.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        satellite.insert(new UserExample(1,"Gabriel", 90));
        satellite.insert(new UserExample(2,"Karla", 34));
        satellite.insert(new UserExample(3,"Tyrion", 56));
        satellite.insert(new UserExample(4,"Alec", 78));

        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        //satellite.push();
        satellite.closeConnection();
        //satellite.findById(1);

    }
}
