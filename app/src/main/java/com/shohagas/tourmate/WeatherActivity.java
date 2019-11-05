package com.shohagas.tourmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shohagas.tourmate.model.CurrentWeatherResponds;
import com.shohagas.tourmate.profile_fragment.ProfileFragment;
import com.shohagas.tourmate.services.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity{
    private static final int LOCATION_PERMISION_CODE = 10000;

    private FusedLocationProviderClient client;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private WeatherService service;
    private TextView latiTextView, longTextView, cityTextView, tempTextView;

    double latitude = 0.0;
    double longitude = 0.0;
    String temperatureUnit = "imperial";
    private static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private String weatherApiKey = "78d2756c894c2b50fe953ccb32f44001";
    //String apiKey = getString(R.string.weather_api_key);
    private String urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", latitude, longitude, temperatureUnit, weatherApiKey);
    //private String urlString = "weather?lat=35&lon=139&unit=imperial&appid=f1844da160aa473d54b03ea4f11de187";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(bottomItemClickListener);

        latiTextView = findViewById(R.id.text_view_date);
        longTextView = findViewById(R.id.long_text_view);
        cityTextView = findViewById(R.id.city_text_view);
        tempTextView = findViewById(R.id.temperature_text_view);

        client = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30000)
                .setFastestInterval(1000);

        //Retrofit Initiation

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherService.class);
        ///////////////////

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", latitude, longitude, temperatureUnit, weatherApiKey);

                    Toast.makeText(WeatherActivity.this, "Lat: " + latitude + "\n Long: " + longitude, Toast.LENGTH_SHORT).show();

                    //Retrofit Enqeue
                    enqueApiCall();

                }
            }
        };

        //Requesting for Location
        requestForLocationUpdate();
    }

    private void enqueApiCall() {
        Call<CurrentWeatherResponds> currentWeatherRespondsCall = service.getCurrentWeatherResponds(urlString);
        currentWeatherRespondsCall.enqueue(new Callback<CurrentWeatherResponds>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponds> call, Response<CurrentWeatherResponds> response) {
                if (response.code() == 200) {
                    CurrentWeatherResponds responds = response.body();
                    String city = responds.getName();
                    String tem = responds.getMain().getTemp().toString();
                    cityTextView.setText(city);
                    tempTextView.setText(tem);
                    latiTextView.setText(String.valueOf(responds.getCoord().getLat()));
                    longTextView.setText(String.valueOf(responds.getCoord().getLon()));

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponds> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, "Invalide Url!", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Request for Location Update and Permission
    private void requestForLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISION_CODE);
                return;
            }
        }
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISION_CODE) {
            if (grantResults[0] == RESULT_OK) {
                requestForLocationUpdate();
            }

        }
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
                    startActivity(new Intent(WeatherActivity.this, MainActivity.class));
                    //finish();
                    break;
                case R.id.nav_nearby_place:
                    //selectedFragment = new NearByPlacesFragment();
                    startActivity(new Intent(WeatherActivity.this, NearByPlaceActivity.class));
                    break;
                case R.id.nav_weather:
                   /* selectedFragment = new WeatherFragment();
                    initFragment(selectedFragment);*/
                    startActivity(new Intent(WeatherActivity.this, WeatherActivity.class));
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
