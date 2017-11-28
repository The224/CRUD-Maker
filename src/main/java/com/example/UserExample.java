package com.example;

import com.satellite.annotation.Id;

import javax.persistence.Column;

public class UserExample {

    @Id
    private Integer id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "AGE")
    private int age;

    public UserExample(int id, String name, int age) {
        this.name = name;
        this.age = age;
        this.id = id;
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
