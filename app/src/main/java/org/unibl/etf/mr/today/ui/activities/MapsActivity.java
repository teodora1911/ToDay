package org.unibl.etf.mr.today.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.unibl.etf.mr.today.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    Geocoder geocoder;
    private MapView map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        geocoder = new Geocoder(this);
        showOnMap();
    }

    private boolean hasPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    private void showOnMap() {
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        String location = getIntent().getStringExtra("location");
        if(location != null || !location.isBlank()){
            Address address = getAddress(location);
            if(address != null){
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                IMapController mapController = map.getController();
                mapController.setZoom(19.0);
                GeoPoint startPoint = new GeoPoint(latitude, longitude);
                mapController.setCenter(startPoint);
                mapController.animateTo(startPoint);
            } else {
                System.out.println("UNABLE TO FIND LOCATION.");
                finish();
            }
        } else {
            System.out.println("DATA FROM INTENT IS NULL OR BLANK.");
            finish();
        }
    }

    private Address getAddress(String location){
        Address address = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if(addresses.size() > 0){
                address = addresses.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return address;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(map != null)
            map.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(map != null)
            map.onPause();

    }
    @Override
    public void onStop() {
        super.onStop();
        if(map != null)
            map.onDetach();

    }
}