package com.shohagas.tourmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.shohagas.tourmate.adapter.EventViewAdapter;
import com.shohagas.tourmate.event_fragment.EventListFragment;
import com.shohagas.tourmate.model.Event;
import com.shohagas.tourmate.model.MarkerItem;
import com.shohagas.tourmate.nearby_places_fragment.NearByPlacesFragment;
import com.shohagas.tourmate.profile_fragment.ProfileFragment;
import com.shohagas.tourmate.weather_fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ProgressBar progressBar;
    private FloatingActionButton searchLocationFab;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;

    private GoogleMap mMap;
    private GoogleMapOptions options;

    private List<MarkerItem> mMarkerItems;
    private ClusterManager<MarkerItem> clusterManager;

    private FusedLocationProviderClient client;
    private Location lastLocation;

    private String apiKey = "AIzaSyD70axDZsvWyy9pliL_HtlTjELjJ6fjdyM";
    private FragmentTransaction fragmentTransaction;
    private SupportMapFragment mapFragment;

    private final int  AUTOCOMPLETE_REQUEST_CODE = 100001;
    private List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Event List");

        progressBar = findViewById(R.id.progressBar_mainActivity);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(bottomItemClickListener);
        searchLocationFab = findViewById(R.id.searchLocation_fab);
        searchLocationFab.setOnClickListener(floatBtnOnClickListener);

        // For Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Events");

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_fragment_host,
                new EventListFragment()).commit();

        /*Google Map Api
         * */

        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);


        client = LocationServices.getFusedLocationProviderClient(this);

        mMarkerItems = new ArrayList<>();

        //Initializing Google Map Option
        options = new GoogleMapOptions();

        options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .zoomControlsEnabled(true)
                .compassEnabled(true);

    }

    /* Floating Button OnClick Listener*/
    private View.OnClickListener floatBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(MainActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }
    };


    /*Bottom Navigation*/
    private BottomNavigationView.OnNavigationItemSelectedListener bottomItemClickListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_event_list:
                    searchLocationFab.hide();
                    selectedFragment = new EventListFragment();
                    initFragment(selectedFragment);
                    break;
                case R.id.nav_weather:
                    searchLocationFab.hide();
                    selectedFragment = new WeatherFragment();
                    initFragment(selectedFragment);
                    break;
                case R.id.nav_nearby_place:
                    //For getting Google Map Fragment
                    mapFragment = SupportMapFragment.newInstance(options);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_fragment_host, mapFragment);
                    fragmentTransaction.commit();
                    //For Loading Google map in Async
                    mapFragment.getMapAsync(MainActivity.this);
                    searchLocationFab.show();
                    break;
                case R.id.nav_profile:
                    searchLocationFab.hide();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initializing Cluster Manager
        clusterManager = new ClusterManager<MarkerItem>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        getLastLocation();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_signout) {
            //User Sign out Code
            mFirebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }*/



}

