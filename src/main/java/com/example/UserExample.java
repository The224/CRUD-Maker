package com.example;

import com.satellite.annotation.Id;

import javax.persistence.Column;

public class UserExample {

    @Id(name="PK_ID")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "AGE")
    private int age;

    public UserExample(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserExample{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
