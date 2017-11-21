package com.test;

import com.satellite.Satellite;
import com.satellite.annotation.Id;

public class User {

    @Id(name="PK_NAME")
    private String name;

    private Satellite<User> test;


}
