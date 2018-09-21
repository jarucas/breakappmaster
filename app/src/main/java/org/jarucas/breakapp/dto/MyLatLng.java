package org.jarucas.breakapp.dto;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Javier on 01/09/2018.
 */

public final class MyLatLng  {

    private double latitude;

    private double longitude;

    public MyLatLng() {
    }

    public MyLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(this.latitude, this.longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
