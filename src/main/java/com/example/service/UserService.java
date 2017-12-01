package com.example.service;

import com.satellite.Satellite;

public class UserService extends Service {

    public UserService() {
        try {
            this.satellite = Satellite.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
