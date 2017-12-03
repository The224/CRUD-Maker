package com.example;

import com.satellite.Satellite;

import java.util.List;

public class UpdateDemo {

    public static void main(String... args) throws Exception{

        Satellite satellite;
        satellite = Satellite.getInstance();

        satellite.connect("localhost", "3306", "testSatellite", "root", "toor");

        List pantsData = satellite.fetchAllByClass(Pants.class).findAll();
        System.out.println(pantsData);

        satellite.update(pantsData.get(pantsData.size()-1), "color", "orange");
        satellite.push();

        List pantsDataWithUpdateColor = satellite.fetchAllByClass(Pants.class).findAll();
        System.out.println(pantsDataWithUpdateColor);

        satellite.update(pantsDataWithUpdateColor.get(0), "size", 12);
        satellite.push();

        List pantsDataWithUpdateId = satellite.fetchAllByClass(Pants.class).findAll();
        System.out.println(pantsDataWithUpdateId);
    }
}
