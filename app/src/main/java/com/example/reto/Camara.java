package com.example.reto;

public class Camara {

    private double latitude;
    private double longitude;
    private String title;
    private String cameraId;
    private String cameraRoad;
    private String kilometer;
    private String address;

    public Camara(double latitude, double longitude, String title, String cameraId,
                      String cameraRoad, String kilometer, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.cameraId = cameraId;
        this.cameraRoad = cameraRoad;
        this.kilometer = kilometer;
        this.address = address;
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
}
