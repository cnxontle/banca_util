package com.example.bbva;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;

import com.example.bbva.MainActivity;
import com.example.bbva.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bbva.databinding.ActivityMapsBinding;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Mapa de Sucursales");

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng jalisco = new LatLng(20.650610, -103.348418);
        LatLng bbva1 = new LatLng(20.606579, -103.415613);
        LatLng bbva2 = new LatLng(20.628591, -103.416583);
        LatLng bbva3 = new LatLng(20.635432, -103.397005);
        LatLng bbva4 = new LatLng(20.645220, -103.386113);
        LatLng bbva5 = new LatLng(20.655138, -103.342748);
        LatLng bbva6 = new LatLng(20.637000, -103.311576);
        LatLng bbva7 = new LatLng(20.673608, -103.347147);
        LatLng bbva8 = new LatLng(20.684034, -103.326005);
        LatLng bbva9 = new LatLng(20.687176, -103.297073);
        LatLng bbva10 = new LatLng(20.630105, -103.266108);
        mMap.addMarker(new MarkerOptions().position(bbva1).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva2).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva3).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva4).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva5).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva6).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva7).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva8).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva9).title("Banca Útil"));
        mMap.addMarker(new MarkerOptions().position(bbva10).title("Banca Útil"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jalisco,11));
    }

}