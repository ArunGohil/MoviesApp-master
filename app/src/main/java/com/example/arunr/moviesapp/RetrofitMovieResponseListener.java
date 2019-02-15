package com.example.arunr.moviesapp;

import com.example.arunr.moviesapp.model.MoviesResponse;

public interface RetrofitMovieResponseListener {

    public void onDataReceived(MoviesResponse response);

    public void onDataFailed(String errorMessage);
}
