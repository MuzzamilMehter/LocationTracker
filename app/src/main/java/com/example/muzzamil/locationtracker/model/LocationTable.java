package com.example.muzzamil.locationtracker.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by muzza on 25-02-2018.
 */

@Table(name = "LocationTable")
public class LocationTable extends Model {

    @Column(name = "Latitude")
    public String Latitude;

    @Column(name = "Longitude")
    public String Longitude;

    @Column(name = "Time")
    public String Time;

    @Column(name = "Speed")
    public String Speed;

}

