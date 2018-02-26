package com.example.muzzamil.locationtracker.fragmets;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.example.muzzamil.locationtracker.R;
import com.example.muzzamil.locationtracker.adaptors.LocationAdaptor;
import com.example.muzzamil.locationtracker.brodcastrecivers.UpdateLocationReciver;
import com.example.muzzamil.locationtracker.interfaces.LocationUpdateInterface;
import com.example.muzzamil.locationtracker.model.LocationTable;

import java.util.Collections;
import java.util.List;


public class ListFragment extends Fragment implements LocationUpdateInterface {

    private ListView locationListView;
    private LocationAdaptor locationAdaptor;
    int last_marker;
    UpdateLocationReciver updateLocationReciver;
    List<LocationTable> listArrayLocation;
    private CountDownTimer timer;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listArrayLocation = new Select().from(LocationTable.class).execute();
        Collections.reverse(listArrayLocation);
        locationAdaptor = new LocationAdaptor(getActivity(), listArrayLocation);
        locationListView = (ListView) view.findViewById(R.id.location_list);
        locationListView.setAdapter(locationAdaptor);
        last_marker = listArrayLocation.size();
        updateLocationReciver = new UpdateLocationReciver(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LOCATION_UPDATE");
        getActivity().registerReceiver(updateLocationReciver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(updateLocationReciver);
    }

   /* @Override
    public void onUpdate() {
        Log.d("ListFragment", "OnUpdateListner");
        listArrayLocation = new Select().from(LocationTable.class).execute();
        Collections.reverse(listArrayLocation);
        locationAdaptor = new LocationAdaptor(getActivity(), listArrayLocation);
        locationListView.setAdapter(locationAdaptor);
    }*/


    @Override
    public void onUpdateLocation(Double latitude, Double longitude, String Date, float speed) {
        LocationTable locationTable = new LocationTable();
        locationTable.Latitude = Double.toString(latitude);
        locationTable.Longitude = Double.toString(longitude);
        locationTable.Time = Date;
        locationTable.Speed = Float.toString(speed);

        listArrayLocation.add(0, locationTable);
        Log.i("", "");
        locationAdaptor.notifyDataSetChanged();
        locationAdaptor.notifyDataSetInvalidated();
    }
}
