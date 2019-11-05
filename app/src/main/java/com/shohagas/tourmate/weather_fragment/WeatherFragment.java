package com.shohagas.tourmate.weather_fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.shohagas.tourmate.MainActivity;
import com.shohagas.tourmate.R;
import com.shohagas.tourmate.model.CurrentWeatherResponds;
import com.shohagas.tourmate.services.WeatherService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private Context context;

    private static final int LOCATION_PERMISION_CODE = 10000;

    private WeatherService service;
    private TextView dateTextView, weatherTextView, cityTextView, tempTextView;


    private FusedLocationProviderClient client;
    private Location lastLocation;

    private PlacesClient placesClient;
    private String apiKey = "AIzaSyD70axDZsvWyy9pliL_HtlTjELjJ6fjdyM";

    double latitude = 0.0;
    double longitude = 0.0;
    String temperatureUnit = "metric";
    private static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private String weatherApiKey = "78d2756c894c2b50fe953ccb32f44001";
    //String apiKey = getString(R.string.weather_api_key);
    private String urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", latitude, longitude, temperatureUnit, weatherApiKey);
    //private String urlString = "weather?lat=35&lon=139&unit=imperial&appid=f1844da160aa473d54b03ea4f11de187";


    private SimpleDateFormat dateFormat;
    private Calendar calendar;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        dateTextView = view.findViewById(R.id.text_view_date);
        weatherTextView = view.findViewById(R.id.text_view_weather);
        cityTextView = view.findViewById(R.id.city_text_view);
        tempTextView = view.findViewById(R.id.temperature_text_view);

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        client = LocationServices.getFusedLocationProviderClient(context);

        //Retrofit Initiation
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherService.class);

        getLastLocation();
        return view;
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
                    long unixDate = responds.getDt();
                    Date date1 = new Date(unixDate*1000);
                    String date = dateFormat.format(date1);

                    cityTextView.setText(city);
                    tempTextView.setText(tem);
                    dateTextView.setText(date);
                    weatherTextView.setText(String.valueOf(responds.getWeather().get(0).getMain()));

                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponds> call, Throwable t) {
                Toast.makeText(context, "Invalide Url!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getLastLocation() {
        checkPermission();
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isComplete() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    latitude = lastLocation.getLatitude();
                    longitude = lastLocation.getLongitude();
                    urlString = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", latitude, longitude, temperatureUnit, weatherApiKey);

                    //Retrofit Enqeue
                    enqueApiCall();


                }
            }
        });
    }

    //location permission
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_RESOLVED_FILTER
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISION_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISION_CODE) {
            if (grantResults[0] == Activity.RESULT_OK) {

            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }



}
