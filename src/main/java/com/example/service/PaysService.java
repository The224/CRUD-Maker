package com.example.service;

import com.satellite.Satellite;

public class PaysService extends Service {

    public PaysService() {
        try {
            this.satellite = Satellite.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
