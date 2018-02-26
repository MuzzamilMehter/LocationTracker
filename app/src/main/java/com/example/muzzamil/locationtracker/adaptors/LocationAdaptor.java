package com.example.muzzamil.locationtracker.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.muzzamil.locationtracker.R;
import com.example.muzzamil.locationtracker.model.LocationTable;

import java.util.List;

/**
 * Created by muzza on 25-02-2018.
 */

public class LocationAdaptor extends BaseAdapter {

    List<LocationTable> locationList;
    Context context;

    public LocationAdaptor(Context context, List<LocationTable> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int i) {
        return locationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.location_list_item, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtLatitude.setText(locationList.get(i).Latitude);
        holder.txtLongitude.setText(locationList.get(i).Longitude);
        holder.txtTime.setText(locationList.get(i).Time);
        holder.txtSpeed.setText(locationList.get(i).Speed);
        return convertView;
    }

    private class ViewHolder {
        TextView txtLatitude, txtLongitude, txtTime, txtSpeed;

        public ViewHolder(View item) {
            txtLatitude = (TextView) item.findViewById(R.id.latitude_txt);
            txtLongitude = (TextView) item.findViewById(R.id.longitude_txt);
            txtTime = (TextView) item.findViewById(R.id.time_txt);
            txtSpeed = (TextView) item.findViewById(R.id.speed);
        }
    }
}
