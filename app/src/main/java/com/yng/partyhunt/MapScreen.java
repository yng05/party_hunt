package com.yng.partyhunt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yng.partyhunt.connection.WebServiceTasks;
import com.yng.partyhunt.utilities.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by yng1905 on 6/24/14.
 */
public class MapScreen extends Fragment implements GoogleMap.OnMarkerClickListener{

    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;
    private boolean isMapLoaded = false;

    private Spinner spnDistance;
    private Button btnRefresh;
    private Double distance = 10.0;

    private ArrayList<Marker> markers = new ArrayList<Marker>();


    private GPSTracker gps;
    double latitude = 0.0,longitude = 0.0;

    private ArrayList<HashMap<String, String>> mylist;
    private WebServiceTasks wst = new WebServiceTasks();

    WeakHashMap<GoogleMap, Integer> haspMapMarker = new WeakHashMap<GoogleMap, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map_screen, container, false);
        spnDistance = (Spinner) rootView.findViewById(R.id.spnDistance);
        btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(btnRefreshClick);

        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        new HttpAsyncTask(getActivity(), "Sending...").execute("");




        try {
        MapsInitializer.initialize(getActivity());
        }
        catch (Exception e) {
            Log.e("Error: ", "Have GoogleMap but then error", e);
        }
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(rootView);

        return rootView;
    }

    protected View.OnClickListener btnRefreshClick = new View.OnClickListener() {

        public void onClick(View v) {
            switch (spnDistance.getSelectedItemPosition())
            {
                case 0:
                    distance = 5.0;
                    break;
                case 1:
                    distance = 25.0;
                    break;
                case 2:
                    distance = 50.0;
                    break;
                case 3:
                    distance = 100.0;
                    break;

            }
            gps.getLocation();
            if(gps.canGetLocation()){

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // \n is for new line
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }

            new HttpAsyncTask(getActivity(), "Sending...").execute("");

        }
    };

    private void setUpMapIfNeeded(View rootView) {
        if (mMap == null) {
            mMap = ((MapView) rootView.findViewById(R.id.mapView)).getMap();


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Integer i = markers.indexOf(marker);
                    Intent myIntent;
                    myIntent = new Intent(getActivity(), PartyDetailScreen.class);
                    myIntent.putExtra("PartyId",mylist.get(i).get("Id").toString());
                    myIntent.putExtra("ScreenMode","MapDetail");
                    startActivityForResult(myIntent, 1);
                }
            });

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if(mylist == null)
            return;

        if(markers.size() > 0)
        {
            for(int i = markers.size()-1; i >= 0; i--)
            markers.remove(i);
        }

        for(int i = 0;i<mylist.size();i++){
            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(mylist.get(i).get("Latitude").toString()),
                Double.valueOf(mylist.get(i).get("Longitude").toString()))).title(mylist.get(i).get("Name").toString())
                .snippet("Check-in: " + mylist.get(i).get("AttCount").toString()));

            markers.add(m);

            if(i ==0)
            {
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(mylist.get(i).get("Latitude").toString()),
                                Double.valueOf(mylist.get(i).get("Longitude").toString())));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker theMarker)
    {


        return false;
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

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        Context context;
        String str_mesg;
        ProgressDialog progress;

        public HttpAsyncTask(Context context, String str_mesg){
            this.context=context;
            this.str_mesg=str_mesg;
        }
        @Override
        protected void onPreExecute() {
            //Show progress Dialog here
            super.onPreExecute();

            // create ProgressDialog here ...
            progress = new ProgressDialog(context);
            progress.setMessage(str_mesg);
            // set other progressbar attributes
            if(isMapLoaded){
            Toast.makeText(getActivity().getApplicationContext(), "Map is loading...", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        protected String doInBackground(String... urls) {
            mylist = wst.receiveParties(latitude,longitude,distance);
            return "";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            isMapLoaded = true;
            mMap.clear();
            setUpMap();
        }




    }
}
