package com.satellite;

import com.example.UserExample;
import com.satellite.annotation.Id;
import com.satellite.exception.ConnectionFailedException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

import static org.junit.Assert.*;

public class SatelliteTest {

    Satellite satellite;
    UserExample user01;
    UserExample user02;

    @Before
    public void setUp() throws Exception {
        satellite = new Satellite();
        user01 = new UserExample(60, "Maya", 28);
        user02 = new UserExample(65, "Nicolas", 34);

    }

    @After
    public void tearDown() throws Exception {
        satellite = null;
        user01 = null;
        user02 = null;
    }

    @Test
    public void testInsert() throws Exception {
        satellite.insert(user01);
        satellite.insert(user02);
        satellite.insert(new String("clown"));

        //valid cases
        assertThat(satellite.getPendingList().get(0)).isEqualTo(user01);
        assertThat(satellite.getPendingList().get(1)).isEqualTo(user02);

        //dubious case : should not allow object without annotation on field
        assertThat(satellite.getPendingList().get(2)).isEqualTo("clown");

        //invalid : id already exists in pendingList
        assertThat(satellite.insert(user01)).isFalse();
    }

    @Test(expected = ConnectionFailedException.class)
    public void testConnect() throws Exception {
        assertThat(satellite.connect("localhost", "3306", "testSatellite", "root", "")).isTrue();

        //invalid credentials : triggers exception
        satellite.connect("pirate", "ofsouthsea", "find", "the", "treasure");
    }

    @Test
    public void testCloseConnection() throws Exception {
        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        satellite.closeConnection();

        assertThat(satellite.isConnected()).isFalse();
    }

    @Test
    public void testFindById() throws Exception {
        satellite.insert(user01);

        assertThat(satellite.findById(user01.getId())).isEqualTo(user01);
        assertThatThrownBy((ThrowableAssert.ThrowingCallable) satellite.findById(4)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testFindAll() throws Exception {
        List<Object> emptyList = satellite.fetchAllByClass(Integer.class).findAll();
        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        satellite.insert(user02);
        satellite.push();
        List<Object> oneItemList = satellite.fetchById(UserExample.class, user02.getId()).findAll();

        assertThat(emptyList).isEmpty();
        assertThat(oneItemList).hasSize(1);
    }

    @Test
    public void testRemove() throws Exception {
        satellite.connect("localhost", "3306", "testSatellite", "root", "");
        satellite.fetchAllByClass(UserExample.class).findAll();

        assertThat(satellite.findById(65)).isNotNull();
        satellite.remove(65);
        assertThat(satellite.findById(65)).isNull();
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