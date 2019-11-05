package com.shohagas.tourmate.nearby_places_fragment;


import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.clustering.ClusterManager;
import com.shohagas.tourmate.R;
import com.shohagas.tourmate.model.MarkerItem;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByPlacesFragment extends Fragment {

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


        // Inflate the layout for this fragment
        return view;
    }



}
