package com.example.arunr.moviesapp.retrofit;

import android.util.Log;
import android.widget.Toast;

import com.example.arunr.moviesapp.BuildConfig;
import com.example.arunr.moviesapp.MainActivity;
import com.example.arunr.moviesapp.RetrofitMovieResponseListener;
import com.example.arunr.moviesapp.adapter.MoviesAdapter;
import com.example.arunr.moviesapp.api.Client;
import com.example.arunr.moviesapp.api.Service;
import com.example.arunr.moviesapp.model.Movie;
import com.example.arunr.moviesapp.model.MoviesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitManager {

    // calls the api and loads the data
    public void loadJson(final RetrofitMovieResponseListener callbackListener) {
        try {

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    MoviesResponse model = response.body();
                    if (model != null) {
                        callbackListener.onDataReceived(model);
                    } else {
                        callbackListener.onDataFailed("null");
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    callbackListener.onDataFailed(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            callbackListener.onDataFailed(e.getMessage());
        }
    }

    // calls the top rated movies api
    public void loadJson1(final RetrofitMovieResponseListener callbackListener) {
        try {

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    MoviesResponse model = response.body();
                    if (model != null) {
                        callbackListener.onDataReceived(model);
                    } else {
                        callbackListener.onDataFailed("null");
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    callbackListener.onDataFailed(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            callbackListener.onDataFailed(e.getMessage());
        }
    }

}
