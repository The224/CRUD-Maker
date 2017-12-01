package com.example;

import com.satellite.annotation.Id;

public class Pays {

    @Id
    private String name;
    private long population;
    private String devise;
    private String capital;

    public Pays() {}

    public Pays(String name, long population, String devise, String capital) {
        this.name = name;
        this.population = population;
        this.devise = devise;
        this.capital = capital;
    }

    public String getName() {
        return name;
    }

    public long getPopulation() {
        return population;
    }

    public String getDevise() {
        return devise;
    }

    public String getCapital() {
        return capital;
    }
}
