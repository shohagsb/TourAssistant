package com.shohagas.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;
import com.shohagas.tourmate.event_fragment.EventListFragment;
import com.shohagas.tourmate.model.MarkerItem;
import com.shohagas.tourmate.profile_fragment.ProfileFragment;
import com.shohagas.tourmate.weather_fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.List;

public class NearByPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleMapOptions options;

    private List<MarkerItem> mMarkerItems;
    private ClusterManager<MarkerItem> clusterManager;

    private FusedLocationProviderClient client;
    private Location lastLocation;

    private PlacesClient placesClient;
    private String apiKey = "AIzaSyD70axDZsvWyy9pliL_HtlTjELjJ6fjdyM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_place);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(bottomItemClickListener);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        client = LocationServices.getFusedLocationProviderClient(this);

        mMarkerItems = new ArrayList<>();

        //Initializing Google Map Option
        options = new GoogleMapOptions();

        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .zoomControlsEnabled(true)
                .compassEnabled(true);

        //For getting Google Map Fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment);

        fragmentTransaction.commit();
        //For Loading Google map in Async
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initializing Cluster Manager
        clusterManager = new ClusterManager<MarkerItem>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        getLastLocation();

        //Map Long on click listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //mMap.clear();
                /*mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker in BUBT") );*/
                //Add latlon to as marker array list
                mMarkerItems.add(new MarkerItem(latLng));
                clusterManager.addItems(mMarkerItems);
                clusterManager.cluster();
            }
        });

        checkPermission();

        //For locating current location
        mMap.setMyLocationEnabled(true);

    }

    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isComplete() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15
                    ));
                }
            }
        });
    }


    //location permission
    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_RESOLVED_FILTER
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }


    private void showCurrentPlaces() {
    }



    /*Bottom Navigation*/
    private BottomNavigationView.OnNavigationItemSelectedListener bottomItemClickListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_event_list:
                    /*selectedFragment = new EventListFragment();
                    initFragment(selectedFragment);*/
                    startActivity(new Intent(NearByPlaceActivity.this, MainActivity.class));
                    //finish();
                    break;
                case R.id.nav_nearby_place:
                    //selectedFragment = new NearByPlacesFragment();
                    startActivity(new Intent(NearByPlaceActivity.this, NearByPlaceActivity.class));
                    break;
                case R.id.nav_weather:
                   /* selectedFragment = new WeatherFragment();
                    initFragment(selectedFragment);*/
                    startActivity(new Intent(NearByPlaceActivity.this, WeatherActivity.class));
                    finish();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    initFragment(selectedFragment);
                    break;
            }

            return true;
        }
    };

    private void initFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_host,
                selectedFragment).commit();
    }


}
