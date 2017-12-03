package com.example;

import com.satellite.annotation.Id;

public class Country {

    @Id
    private String name;
    private int population;
    private String motto;
    private String capital;

    public Country() {}

    public Country(String name, int population, String motto, String capital) {
        this.name = name;
        this.population = population;
        this.motto = motto;
        this.capital = capital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", population=" + population +
                ", motto='" + motto + '\'' +
                ", capital='" + capital + '\'' +
                '}';
    }
}
