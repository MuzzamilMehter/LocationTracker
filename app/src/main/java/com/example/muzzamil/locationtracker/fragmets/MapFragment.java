package com.example.muzzamil.locationtracker.fragmets;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.example.muzzamil.locationtracker.R;
import com.example.muzzamil.locationtracker.brodcastrecivers.UpdateLocationReciver;
import com.example.muzzamil.locationtracker.interfaces.LocationUpdateInterface;
import com.example.muzzamil.locationtracker.model.LocationTable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationUpdateInterface {

    private GoogleMap mMap;
    Handler handler = new Handler();
    int last_marker;
    private CountDownTimer timer;
    UpdateLocationReciver updateLocationReciver;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        updateLocationReciver = new UpdateLocationReciver(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        List<LocationTable> listArrayLocation = new Select().from(LocationTable.class).execute();
        last_marker = listArrayLocation.size();
        markersonmap(mMap, listArrayLocation);
    }

    public void markersonmap(GoogleMap mMap, List<LocationTable> listArrayLocation) {

        for (int i = 0; i < listArrayLocation.size(); i++) {
            Double lat = Double.parseDouble(listArrayLocation.get(i).Latitude);
            Double lang = Double.parseDouble(listArrayLocation.get(i).Longitude);
            LatLng latLang = new LatLng(lat, lang);
            Log.d("LAtitude", lat.toString());
            Log.d("Longtitude", lang.toString());

            mMap.addMarker(new MarkerOptions().position(latLang).title(listArrayLocation.get(i).Time));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 12.0f));
        }
    }

    /*@Override
    public void onUpdate() {
        List<LocationTable> listArrayLocation = new Select().from(LocationTable.class).execute();
        if (last_marker < listArrayLocation.size()) {
            int diff = listArrayLocation.size() - last_marker;
            List<LocationTable> listArrayLocation_new = listArrayLocation.subList(listArrayLocation.size() - diff, listArrayLocation.size());
            markersonmap(mMap, listArrayLocation_new);
            last_marker = listArrayLocation.size();
        }
    }

*/
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOCATION_UPDATE");
        getActivity().registerReceiver(updateLocationReciver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(updateLocationReciver);
    }


    @Override
    public void onUpdateLocation(Double latitude, Double longitude, String Date, float speed) {
        LatLng latLang = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLang).title(Date));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 12.0f));
    }
}
