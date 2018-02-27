package com.example.muzzamil.locationtracker.services;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.activeandroid.query.Select;
import com.example.muzzamil.locationtracker.R;
import com.example.muzzamil.locationtracker.activity.MainActivity;
import com.example.muzzamil.locationtracker.asynctask.CheckIfForeground;
import com.example.muzzamil.locationtracker.model.LocationTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.provider.ContactsContract.Intents.Insert.ACTION;
import static com.activeandroid.Cache.getContext;

public class LocationService extends Service {
    private static final String TAG = "Location Service";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10;
    final static int myID = 1234;

    /*
    * Chaecking on location change listner.
    * */
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            updateTable(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");


        startForegroundMethod();
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /*
    * Foreground service implementation
    * */
    private void startForegroundMethod() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "chanal_id")
                .setContentTitle("Locaiton Tracker")
                .setContentText("Location Tracker Service Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(111, notification);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /*
    * Update Location in SQLite and send broad cast to update the map and the list view
    * */
    private void updateTable(Location location) {
        LocationTable locationTable = new LocationTable();
        locationTable.Latitude = Double.toString(location.getLatitude());
        locationTable.Longitude = Double.toString(location.getLongitude());
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(location.getTime());
        String formatted_time = format.format(date);
        locationTable.Time = formatted_time;
        locationTable.Speed = Float.toString(location.getSpeed());
        /*
        * Save to SQLite
        * */
        locationTable.save();
        Log.d("Speed", Float.toString(location.getSpeed()));
        String a = Float.toString(location.getSpeed());
        int size = new Select().from(LocationTable.class).execute().size();
        Log.d(TAG, "size =" + Integer.toString(size));
        Log.d(TAG, "Data Saved");
        try {
            boolean foregroud = new CheckIfForeground().execute(getApplicationContext()).get();

            if (foregroud) {
                Intent intent = new Intent();
                intent.setAction("LOCATION_UPDATE");
                intent.putExtra("Latitude", location.getLatitude());
                intent.putExtra("Longitude", location.getLongitude());
                intent.putExtra("Time", formatted_time);
                intent.putExtra("Speed", location.getSpeed());
                /*
                * Update the UI
                * */
                sendBroadcast(intent);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
