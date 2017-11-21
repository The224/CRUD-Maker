package com.test;

import com.crudmaker.CRUDMaker;
import com.crudmaker.annotation.Id;

public class User {

    @Id(name="PK_NAME")
    private String name;

    private CRUDMaker<User> test;


}
