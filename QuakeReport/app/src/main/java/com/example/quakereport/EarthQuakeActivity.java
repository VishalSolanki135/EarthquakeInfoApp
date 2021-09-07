package com.example.quakereport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthQuakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG=EarthQuakeActivity.class.getName();
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=3&limit=30";
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        mAdapter = new EarthquakeAdapter(this, new ArrayList<>());
        //set the adapter on the list view
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = mAdapter.getItem(position);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmUrl());
                Intent websiteIntent  = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(websiteIntent);
            }
        });

        Bundle queryBundle = new Bundle();
        queryBundle.putString("url", USGS_REQUEST_URL);
        getSupportLoaderManager().initLoader(0, queryBundle, this).forceLoad();

        mEmptyStateView = (TextView)findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateView);
    }


    //These all methods are the signs that these will perform an asynchronous behaviour.
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this);//created a async task Loader
    }//this is called when we need to create and return a new loader object

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished( Loader<List<Earthquake>> loader, List<Earthquake> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateView.setText(R.string.no_earthquakes);
        mAdapter.clear();
        if(data!=null && !data.isEmpty()) {
//            mAdapter.addAll(data);
        }
    }//this is called when loader is finished loading the data in the background thread.

    @Override
    public void onLoaderReset( Loader<List<Earthquake>> loader) {
        mAdapter.addAll(new ArrayList<>());
    }//this is called when the previous created loader is reset.(means data from the last loader is now invalid)

    private static class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
        public EarthquakeLoader(Context context) {
            super(context);
        }


        @Override
        public List<Earthquake> loadInBackground() {
            List<Earthquake> result = QueryUtils.fetchQueryData(USGS_REQUEST_URL);
            return result;
        }
    }
}
