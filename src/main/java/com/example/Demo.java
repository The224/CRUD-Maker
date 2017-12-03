package com.example;

import com.satellite.Satellite;

import java.util.List;

public class Demo {

    private static final int ID_DEMO = 0;

    public static void main(String... args) throws Exception{
        Satellite satellite = null;

        try {
            satellite = Satellite.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        satellite.insert(new User(ID_DEMO*4+1,"Gabriel", 90*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+2,"Karla", 34*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+3,"Tyrion", 56*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+4,"Alec", 78*ID_DEMO));

        satellite.insert(new Country("Canada", 36048521, "A mari usque ad mare", "Ottawa"));
        satellite.insert(new Country("France", 67595000, "Liberté, Égalité, Fraternité", "Paris"));
        satellite.insert(new Country("Angleterre", 55012456, "Dieu et mon droit", "London"));
        */
        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");


        /*
        satellite.push();
        */
        List<Object> databaseList = (List<Object>) satellite.fetchAllDatabase("com.example").findAll();

        
        List fetchData = satellite.fetchAllByClass(User.class).findAll();

        List user01 = satellite.fetchAllByCondition(User.class, "name = 'Karla'").findAll();

        satellite.closeConnection();

        for(Object user : fetchData){
            System.out.println(user);
        }
        System.out.println(user01);
        System.out.println(databaseList);
    }
}
