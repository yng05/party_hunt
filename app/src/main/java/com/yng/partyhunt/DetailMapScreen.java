package com.yng.partyhunt;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.app.partyhunt.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.WeakHashMap;

/**
 * Created by yng1905 on 6/26/14.
 */
public class DetailMapScreen extends ActionBarActivity {
    private LatLng latlng;
    private String name;
    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;


    WeakHashMap<GoogleMap, Integer> haspMapMarker = new WeakHashMap<GoogleMap, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmap_screen);

        name = getIntent().getStringExtra("Name");
        latlng = new LatLng(getIntent().getDoubleExtra("Latitude",0), getIntent().getDoubleExtra("Longitude",0));


        MapsInitializer.initialize(this);
        try{
                mMapView = (MapView) findViewById(R.id.mapView);
                mMapView.onCreate(mBundle);
                setUpMapIfNeeded(mMapView);}
        catch(Exception ex)
        {
            Log.d("Error:", ex.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.acti, menu);
        return true;
    }

    private void setUpMapIfNeeded(View rootView) {
        if (mMap == null) {
            mMap = ((MapView) mMapView).getMap();

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                }
            });

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.addMarker(new MarkerOptions().position(latlng).title(name));
        CameraUpdate center = CameraUpdateFactory.newLatLng(latlng);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }


}
