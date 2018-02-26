package com.example.muzzamil.locationtracker.brodcastrecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzzamil.locationtracker.interfaces.LocationUpdateInterface;

/**
 * Created by muzza on 26-02-2018.
 */

public class UpdateLocationReciver extends BroadcastReceiver {
    private final String TAG = "UpdateLocationReciver";
    //    private OnLocationUpdate onLocationUpdate;
    private LocationUpdateInterface locationUpdateInterface;

    public UpdateLocationReciver(LocationUpdateInterface locationUpdateInterface) {
        this.locationUpdateInterface = locationUpdateInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Reciver");
        Double latitude = intent.getDoubleExtra("Latitude", 0);
        Double longitude = intent.getDoubleExtra("Longitude", 0);
        String time = intent.getStringExtra("Time");
        Float speed = intent.getFloatExtra("Speed", 0.0f);
//        locationUpdateInterface.onUpdate(latitude, longitude, time, speed);
        locationUpdateInterface.onUpdateLocation(latitude, longitude, time, speed);
    }

   /* public interface OnLocationUpdate {
        public void onUpdate();
    }
*/

}
