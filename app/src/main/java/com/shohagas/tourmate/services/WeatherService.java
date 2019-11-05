package com.shohagas.tourmate.services;

import com.shohagas.tourmate.model.CurrentWeatherResponds;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherService {
    @GET()
    Call<CurrentWeatherResponds> getCurrentWeatherResponds(@Url String urlString);
}
