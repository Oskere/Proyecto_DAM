package com.example.reto;

public class camaras {
    private String cameraId;
    private String sourceId;
    private String cameraName;
    private String latitude;
    private String longitude;
    private String road;
    private String kilometer;
    private String address;
    private String urlImage;

    public camaras(String cameraId, String sourceId, String cameraName, String latitude, String longitude,
                  String road, String kilometer, String address, String urlImage) {
        this.cameraId = cameraId;
        this.sourceId = sourceId;
        this.cameraName = cameraName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.road = road;
        this.kilometer = kilometer;
        this.address = address;
        this.urlImage = urlImage;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRoad() {
        return road;
    }

    public String getKilometer() {
        return kilometer;
    }

    public String getAddress() {
        return address;
    }

    public String getUrlImage() {
        return urlImage;
    }
// Agrega getters y setters según sea necesario
}


