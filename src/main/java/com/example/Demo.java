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

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");
        /*satellite.insert(new User(ID_DEMO*4+1,"Gabriel", 90*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+2,"Karla", 34*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+3,"Tyrion", 56*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+4,"Alec", 78*ID_DEMO));

        satellite.insert(new Pants(ID_DEMO*4+1, "yellow", 3, "gap"));
        satellite.insert(new Pants(ID_DEMO*4+2, "red", 2, "point zero"));
        satellite.insert(new Pants(ID_DEMO*4+8, "blue", 1, "gap"));

        satellite.insert(new Country("Canada", 36048521, "A mari usque ad mare", "Ottawa"));
        satellite.insert(new Country("France", 67595000, "Liberté, Égalité, Fraternité", "Paris"));
        satellite.insert(new Country("Angleterre", 55012456, "Dieu et mon droit", "London"));

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");

        satellite.push();*/

        List databaseData = satellite.fetchAllDatabase("com.example").findAll();
        List multipleObjectsWithIds = satellite.fetchAllDatabase("com.example").findById(2);
        List userData = satellite.fetchAllByClass(User.class).findAll();
        List userDataWithName = satellite.fetchAllByCondition(User.class, "name = 'Karla'").findAll();

        satellite.remove(userData.get(1));
        satellite.push();

        List databaseData2 = satellite.fetchAllDatabase("com.example").findAll();

        satellite.closeConnection();

        System.out.println(databaseData);
        System.out.println(databaseData2);
        System.out.println(multipleObjectsWithIds);
        System.out.println(userData);
        System.out.println(userDataWithName);
    }
}
