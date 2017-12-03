package com.example;

import com.satellite.annotation.Id;

public class Pants {

    public enum SIZE_TYPE{
        SMALL, MEDIUM, LARGE
    }

    @Id
    private int id;
    private String color;
    private int size;
    private String brandName;

    public Pants(){

    }

    public Pants(int id, String color, int size, String brandName) {
        this.id = id;
        this.color = color;
        this.size = size;
        this.brandName = brandName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public String toString() {
        return "Pants{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", size=" + size +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
