package com.shohagas.tourmate.fragments_view;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.maps.android.clustering.ClusterManager;
import com.shohagas.tourmate.R;
import com.shohagas.tourmate.model.MarkerItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByPlacesFragment extends Fragment implements OnMapReadyCallback {
    private Context context;
    private GoogleMap mMap;
    private GoogleMapOptions options;

    private List<MarkerItem> mMarkerItems;
    private ClusterManager<MarkerItem> clusterManager;

    private FusedLocationProviderClient client;
    private Location lastLocation;

    private PlacesClient placesClient;
    private String apiKey = "AIzaSyD70axDZsvWyy9pliL_HtlTjELjJ6fjdyM";

    public NearByPlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_by_places, container, false);


        // Initialize the SDK
        Places.initialize(getContext().getApplicationContext(), apiKey);

        // Create a new Places client instance
        placesClient = Places.createClient(context);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        mMarkerItems = new ArrayList<>();

        //Initializing Google Map Option
        options = new GoogleMapOptions();

        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .zoomControlsEnabled(true)
                .compassEnabled(true);

        //For getting Google Map Fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment);

        fragmentTransaction.commit();
        //For Loading Google map in Async
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initializing Cluster Manager
        clusterManager = new ClusterManager<MarkerItem>(context, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        getLastLocation();

     /*   //Map Long on click listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //mMap.clear();
                *//*mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker in BUBT") );*//*
                //Add latlon to as marker array list
                mMarkerItems.add(new MarkerItem(latLng));
                clusterManager.addItems(mMarkerItems);
                clusterManager.cluster();
            }
        });
*/
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_RESOLVED_FILTER
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }


    private void initFragment(Fragment selectedFragment) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_host,
                selectedFragment).commit();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
