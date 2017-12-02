package com.example;

import com.satellite.annotation.Id;

public class Pays {

    @Id
    private String name;
    private int population;
    private String devise;
    private String capital;

    public Pays() {}

    public Pays(String name, int population, String devise, String capital) {
        this.name = name;
        this.population = population;
        this.devise = devise;
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

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return "Pays{" +
                "name='" + name + '\'' +
                ", population=" + population +
                ", devise='" + devise + '\'' +
                ", capital='" + capital + '\'' +
                '}';
    }
}
