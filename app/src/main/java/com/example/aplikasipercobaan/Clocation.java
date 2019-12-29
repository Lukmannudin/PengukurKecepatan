package com.example.aplikasipercobaan;

import android.location.Location;

public class Clocation extends Location {

    private boolean bUseMetricUnits = false;

    public Clocation(Location location) {

        this(location, true);
    }

    public Clocation(Location location, boolean bUseMetricUnits){
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;

    }
    public boolean getUseMetricUnits() {
        return this.bUseMetricUnits;
            }

            public void setbUseMetricUnits(boolean bUseMetricUnits){
                this.bUseMetricUnits = bUseMetricUnits;

            }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);

        if(!this.getUseMetricUnits()){
            // konversi meter ke kaki
            nDistance = nDistance*3.28083989501312f;

        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();

        if(!this.getUseMetricUnits()){
            // konversi meter ke kaki
            nAltitude = nAltitude*3.28083989501312d;

        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;

        if(!this.getUseMetricUnits()){
            // konversi meter/detik mill/jam
            nSpeed = super.getSpeed()*2.236936f;

        }
        return nSpeed;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();

        if(!this.getUseMetricUnits()){
            // konversi meter ke kaki
            nAccuracy = nAccuracy*3.28083989501312f;

        }
        return nAccuracy;
    }
}
