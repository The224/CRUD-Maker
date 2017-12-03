package com.example;

import com.satellite.Satellite;

import java.util.List;

public class DeleteDemo {

    public static void main(String... args) throws Exception{

        Satellite satellite;
        satellite = Satellite.getInstance();

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");

        //initial user data
        List userData = satellite.fetchAllByClass(User.class).findAll();
        System.out.println(userData);

        //remove the entity in the satellite corresponding to the first element in the user data
        satellite.remove(userData.get(0));

        //user gabriel will cease to exist in database after this
        satellite.push();

        //get all user data and print result with first user removed
        List userDataWithOneRemoval = satellite.fetchAllByClass(User.class).findAll();
        System.out.println(userDataWithOneRemoval);

        //remove the user entity having the id 4
        satellite.removeById(User.class, 4);

        //user with id 4 in user class will cease to exist in database after this
        satellite.push();

        List userDataWithTwoRemovals = satellite.fetchAllByClass(User.class).findAll();
        System.out.println(userDataWithTwoRemovals);

        satellite.closeConnection();
    }
}
