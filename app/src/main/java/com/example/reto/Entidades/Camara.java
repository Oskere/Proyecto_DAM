package com.example.reto.Entidades;

import java.io.Serializable;

public class Camara implements Serializable {

    private double latitude;
    private double longitude;
    private String title;
    private String cameraId;
    private String cameraRoad;
    private String kilometer;
    private String address;
    private String imageUrl;

    public Camara(double latitude, double longitude, String title, String cameraId,
                  String cameraRoad, String kilometer, String address, String imageUrl) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.cameraId = cameraId;
        this.cameraRoad = cameraRoad;
        this.kilometer = kilometer;
        this.address = address;
        this.imageUrl = imageUrl;
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

    public String getCameraId() {
        return cameraId;
    }

    public String getCameraRoad() {
        return cameraRoad;
    }

    public String getKilometer() {
        return kilometer;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
