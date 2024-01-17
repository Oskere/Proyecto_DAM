package com.example.reto.Entidades;

import java.io.Serializable;

public class Incidencia implements Serializable {

    private double latitude;
    private double longitude;
    private String title;
    private String id;
    private String province;
    private String carRegistration;
    private String incidenceLevel;
    private String road;
    private String incidenceType;

    public Incidencia(double latitude, double longitude, String title, String id,
                         String province, String carRegistration, String incidenceLevel,
                         String road, String incidenceType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.id = id;
        this.province = province;
        this.carRegistration = carRegistration;
        this.incidenceLevel = incidenceLevel;
        this.road = road;
        this.incidenceType = incidenceType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getProvince() {
        return province;
    }

    public String getCarRegistration() {
        return carRegistration;
    }

    public String getIncidenceLevel() {
        return incidenceLevel;
    }

    public String getRoad() {
        return road;
    }

    public String getIncidenceType() {
        return incidenceType;
    }
}

