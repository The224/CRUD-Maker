package com.example;

import com.satellite.Satellite;

public class Main {
    public static void main(String... args) {
        Satellite<UserExample> satellite = new Satellite<UserExample>("","","");

        satellite.create(new UserExample("Gabriel", 90));
        satellite.create(new UserExample("Karla", 34));
        satellite.create(new UserExample("Tyrion", 56));
        satellite.create(new UserExample("Alec", 78));

        UserExample user = new UserExample("TestName", 100);

        satellite.printClassInformation(user);
    }
}
