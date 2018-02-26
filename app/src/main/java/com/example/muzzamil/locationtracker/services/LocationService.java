package com.example.muzzamil.locationtracker.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class LocationService extends IntentService implements LocationListener {

    final String TAG = "LocationService";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000;

    LocationManager locationManager;
    Location loc;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    public LocationService() {
        super("LocationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            canGetLocation = intent.getBooleanExtra("CAN_GET_LOCATION", true);
        }
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            getLocation();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public void onLocationChanged(Location location) {
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
       /* tvLatitude.setText(Double.toString(loc.getLatitude()));
        tvLongitude.setText(Double.toString(loc.getLongitude()));
        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));*/
        Log.d(TAG, loc.getLatitude() + "" + loc.getLongitude());
    }

   /* @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"OnDistory");
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
