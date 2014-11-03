package com.yng.partyhunt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;
import com.yng.partyhunt.utilities.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class MainScreen extends Fragment {


    String text,Uid;
    public TextView txtText;
    public Button btnJoinParty;
    private Double distance = 0.004;

    private ArrayList<HashMap<String, String>> mylist;
    private WebServiceTasks wst = new WebServiceTasks();
    private GPSTracker gps;

    private Double latitude,longitude;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        Uid = getActivity().getIntent().getStringExtra("Id");

        gps = new GPSTracker(getActivity());


        text = "Welcome to Party Hunt" +
                " " + getActivity().getIntent().getStringExtra("Name") +
                " " + getActivity().getIntent().getStringExtra("Surname");
        txtText = (TextView) rootView.findViewById(R.id.txtText);
        txtText.setText(text);

        btnJoinParty = (Button) rootView.findViewById(R.id.btnJoinParty);
        btnJoinParty.setOnClickListener(btnJoinPartyClick);
        return rootView;
    }


   protected View.OnClickListener btnJoinPartyClick = new View.OnClickListener() {

        public void onClick(View v) {
            gps.getLocation();
            if(!gps.canGetLocation())
            {
                Toast.makeText(getActivity().getApplicationContext(),"Location is not reachable, please try again later!",Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                new HttpAsyncTask(getActivity(), "Checking for Parties...").execute("");
            }
        }
    };

    private void buttonResult()
    {
        if(mylist.size() == 0)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

            // set title
            alertDialogBuilder.setTitle("You are about to be mayor!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("No party found at this location! Do you want to share party?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Intent myIntent;
                            myIntent = new Intent(getActivity(), AddPartyScreen.class);
                            myIntent.putExtra("Id", Uid);
                            startActivityForResult(myIntent, 1);

                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show(); 
            

        }
        else
        {
            Intent myIntent;
            myIntent = new Intent(getActivity(), PartyDetailScreen.class);
            myIntent.putExtra("PartyId",mylist.get(0).get("Id").toString());
            myIntent.putExtra("ScreenMode","Check-in");
            myIntent.putExtra("UserId", Uid);
            startActivityForResult(myIntent, 1);
        }
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

            progress.show();

        }
        @Override
        protected String doInBackground(String... urls) {
            mylist = wst.receiveParties(latitude,longitude,distance);
            return "";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            progress.dismiss();
            buttonResult();
        }




    }

}
