package com.example;

public class Main {
    public static void main(String... args) {
        UserServiceExample service = new UserServiceExample();

        service.safeAddUser("Gabriel", 90);
        service.safeAddUser("Karla", 34);
        service.safeAddUser("Tyrion", 56);
        service.safeAddUser("Alec", 78);


        service.saveCreateUserOnDB();

















    }
}
