package com.satellite;

import com.satellite.annotation.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import static org.junit.Assert.*;

public class SatelliteTest {

    Satellite satellite;

    @Before
    public void setUp() throws Exception {
        satellite = new Satellite();
    }

    @After
    public void tearDown() throws Exception {
        satellite = null;
    }

    @Test
    public void testInsert() throws Exception {
        satellite.insert(new String("what"));
        assertThat(satellite.getPendingList().get(0)).isEqualTo("what");
    }

    @Test
    public void connect() throws Exception {
    }

    @Test
    public void closeConnection() throws Exception {
    }

    @Test
    public void findById() throws Exception {
    }

    @Test
    public void findAll() throws Exception {
    }

    @Test
    public void remove() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void fetchAllByClass() throws Exception {
    }

    @Test
    public void fetchAllByCondition() throws Exception {
    }

    @Test
    public void fetchById() throws Exception {
    }

    @Test
    public void fetchByQuery() throws Exception {
    }

    @Test
    public void push() throws Exception {
    }

}