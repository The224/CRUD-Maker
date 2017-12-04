package com.example;

import com.satellite.Satellite;

import java.util.List;

public class InsertDemo {

    private static final int ID_DEMO = 0;

    public static void main(String... args) throws Exception{

        Satellite satellite;
        satellite = Satellite.getInstance();

        //connect to the database
        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");

        System.out.println(satellite.isConnected());

        //insert commands
        satellite.insert(new User(ID_DEMO*4+1,"Gabriel", 90*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+2,"Karla", 34*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+3,"Tyrion", 56*ID_DEMO));
        satellite.insert(new User(ID_DEMO*4+4,"Alec", 78*ID_DEMO));

        satellite.insert(new Pants(ID_DEMO*4+1, "yellow", 3, "gap"));
        satellite.insert(new Pants(ID_DEMO*4+2, "red", 2, "point zero"));
        satellite.insert(new Pants(ID_DEMO*4+8, "blue", 1, "gap"));

        satellite.insert(new Country("Canada", 36048521, "A mari usque ad mare", "Ottawa"));
        satellite.insert(new Country("France", 67595000, "Liberté, Égalité, Fraternité", "Paris"));
        satellite.insert(new Country("England", 55012456, "God and my right", "London"));

        //send the entities and info to the database
        satellite.push();

        //fetch and list everything in the database
        List databaseData = satellite.fetchAllDatabase("com.example").findAll();

        //fetch everything in the database and list any entity having the id 2
        List multipleObjectsWithIds = satellite.fetchAllDatabase("com.example").findById(2);

        //fetch everything in the class user and list every entity fetched

        List userData = satellite.fetchAllByClass(User.class).findAll();

        //fetch and list everything in the class user having Karla as its name
        List userDataWithName = satellite.fetchAllByCondition(User.class, "name = 'Karla'").findAll();

        //print the results
        System.out.println(databaseData);
        System.out.println(multipleObjectsWithIds);
        System.out.println(userData);
        System.out.println(userDataWithName);

        //close connection with the database
        satellite.closeConnection();
    }
}
