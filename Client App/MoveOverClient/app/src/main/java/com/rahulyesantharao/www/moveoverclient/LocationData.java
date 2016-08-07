package com.rahulyesantharao.www.moveoverclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.Manifest;

/**
 * Created by justi on 8/6/2016.
 */
public class LocationData implements LocationListener {

    LocationManager locationManager = null;
    Activity contextActivity;
    Firebase firebase;

    public LocationData(Activity context, Firebase firebase)
    {
        contextActivity = context;
        this.firebase = firebase;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        //TODO add check for permission
        permission();
    }

    @Override
    public void onLocationChanged(Location loc) {
        //lattitude = loc.getLatitude()
        //longitude = loc.getLongitude()
        firebase.addLocListener(loc.getLatitude(),loc.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
    }

    public void permission()
    {

        Log.d("test","permission start");
        if (ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(contextActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.v("test", "permission fail");

            if (ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission();
            }

        }
        else{
            Log.v("test","gps start");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);

        }
    }
}
