package com.example;

import com.satellite.Satellite;

import java.util.List;

public class Demo {
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

        satellite.insert(new Pays("Canada", 36048521, "A mari usque ad mare", "Ottawa"));
        satellite.insert(new Pays("France", 67595000, "Liberté, Égalité, Fraternité", "Paris"));
        satellite.insert(new Pays("Angleterre", 55012456, "Dieu et mon droit", "Londres"));

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");
        satellite.push();



        List fetchData = satellite.fetchAllByClass(User.class).findAll();

        List user01 = satellite.fetchAllByCondition(User.class, "name = 'Karla'").findAll();

        satellite.closeConnection();


        for(Object user : fetchData){
            System.out.println(user);
        }
        System.out.println(user01);

        //satellite.findById(1);
    }
}
