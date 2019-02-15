package com.example.arunr.moviesapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.arunr.moviesapp.adapter.MoviesAdapter;
import com.example.arunr.moviesapp.api.Client;
import com.example.arunr.moviesapp.api.Service;
import com.example.arunr.moviesapp.model.Movie;
import com.example.arunr.moviesapp.model.MoviesResponse;
import com.example.arunr.moviesapp.retrofit.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        RetrofitMovieResponseListener {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;
    public static final String LOG_TAG = MoviesAdapter.class.getName();
    private RetrofitManager retrofitManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Movies...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        retrofitManager = new RetrofitManager();

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        // Checks the orientation of the screen and sets layout accordingly
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        checkSortOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preferences updated");
        checkSortOrder();
    }

    private void checkSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, "Sorting by most popular");
            retrofitManager.loadJson(this);
        } else {
            Log.d(LOG_TAG, "Sorting by vote average");
            retrofitManager.loadJson1(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieList.isEmpty()) {
            checkSortOrder();
        } else {

        }
    }

    @Override
    public void onDataReceived(MoviesResponse response) {
        if (BuildConfig.THE_MOVIE_API_TOKEN.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain API Key", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        List<Movie> movies = ((MoviesResponse) response).getResults();
        recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
        recyclerView.smoothScrollToPosition(0);
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
        progressDialog.dismiss();
    }

    @Override
    public void onDataFailed(String errorMessage) {
        Log.d("Error", errorMessage);
        Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
    }

}
