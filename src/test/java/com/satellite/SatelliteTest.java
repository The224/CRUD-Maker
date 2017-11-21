package com.satellite;

import com.satellite.annotation.Id;

import static org.junit.Assert.*;

public class SatelliteTest {

    private class User {
        @Id(name="ID")
        public int id;
        public String name;
        public int age;

    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void create() throws Exception {
    }

    @org.junit.Test
    public void read() throws Exception {
    }

    @org.junit.Test
    public void update() throws Exception {
    }

    @org.junit.Test
    public void delete() throws Exception {
    }

    @org.junit.Test
    public void push() throws Exception {
    }

}