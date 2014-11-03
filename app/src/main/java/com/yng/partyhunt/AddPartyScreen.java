package com.yng.partyhunt;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;
import com.yng.partyhunt.utilities.GPSTracker;
import com.yng.partyhunt.utilities.UtilTextNum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Created by yng1905 on 6/17/14.
 */
public class AddPartyScreen  extends ActionBarActivity {

    private WebServiceTasks wst = new WebServiceTasks();
    private String text,Uid,longitude,latitude,endDate;
    private EditText txtName,txtDate,txtStart,txtEnd,txtDetails;
    private TextView txtText;
    private Button btnAddParty;
    private ImageView img1, img2, img3;
    private boolean[] imageExist = {false,false,false};
    private int clickedImage;
    private static final int CAMERA_REQUEST = 1888;
    private GPSTracker gps;
    private boolean isFirst = true;

    private int mYear, mMonth, mDay,sHour, eHour, sMinute, eMinute;
    String sAMPM, eAMPM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addparty_screen);

        Uid = getIntent().getStringExtra("Id");

        txtName = (EditText) findViewById(R.id.txtName);

        txtDate = (EditText) findViewById(R.id.txtDate);
        txtDate.setText(UtilTextNum.getDate());
        txtDate.setEnabled(false);


        txtStart  = (EditText) findViewById(R.id.txtStartTime);
        txtStart.setText(UtilTextNum.getTime());
        txtStart.setEnabled(false);

        txtEnd  = (EditText) findViewById(R.id.txtEndTime);
        UtilTextNum.disableSoftInputFromAppearing(txtEnd);
        txtEnd.setOnTouchListener(txtEndClick);

        txtDetails  = (EditText) findViewById(R.id.txtDetails);

        btnAddParty = (Button) findViewById(R.id.btnAdd);
        btnAddParty.setOnClickListener(btnAddPartyClick);

        img1 = (ImageView) findViewById(R.id.imageView);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);

        img1.setOnClickListener(img1Click);
        img2.setOnClickListener(img2Click);
        img3.setOnClickListener(img3Click);

    }



    private View.OnTouchListener txtEndClick = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if(isFirst){
            isFirst = false;
            final Calendar c = Calendar.getInstance();
            eHour = c.get(Calendar.HOUR_OF_DAY);
            eMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(v.getContext(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            // Display Selected time in textbox
                            txtEnd.setText(UtilTextNum.formatTime(hourOfDay, minute));
                            isFirst = true;
                        }
                    }, eHour, eMinute, false);
            tpd.show();

            }

            return true;
        }
    };

    private View.OnClickListener btnAddPartyClick = new View.OnClickListener() {

        public void onClick(View v) {

            if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter a name before you upload!", Toast.LENGTH_LONG).show();
                return;
            }
            if(txtEnd.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter end time of the party!", Toast.LENGTH_LONG).show();
                return;
            }
            if(txtDetails.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please write short explanation for the party!", Toast.LENGTH_LONG).show();
                return;
            }

            gps = new GPSTracker(AddPartyScreen.this);

            // check if GPS enabled
            if(gps.canGetLocation()){

                double lat = gps.getLatitude();
                double longt = gps.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longt);
                // \n is for new line
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
                return;
            }
            if(UtilTextNum.isTimeOneBeforeTwo(txtEnd.getText().toString(),txtStart.getText().toString()))
                endDate = UtilTextNum.addDay(txtDate.getText().toString());
            else
                endDate = txtDate.getText().toString();

            new HttpAsyncTask(AddPartyScreen.this, "Sending...").execute("");
        }
    };

    private View.OnClickListener img1Click = new View.OnClickListener() {

        public void onClick(View v) {
            clickedImage = 1;
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "PartyImage"+Integer.toString(clickedImage)+ Uid + ".jpeg");
            Uri imageFileUri = Uri.fromFile(file);
            Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(it, CAMERA_REQUEST);

        }
    };

    private View.OnClickListener img2Click = new View.OnClickListener() {

        public void onClick(View v) {
            clickedImage = 2;
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "PartyImage"+Integer.toString(clickedImage)+ Uid + ".jpeg");
            Uri imageFileUri = Uri.fromFile(file);
            Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(it, CAMERA_REQUEST);

        }
    };

    private View.OnClickListener img3Click = new View.OnClickListener() {

        public void onClick(View v) {
            clickedImage = 3;
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "PartyImage"+Integer.toString(clickedImage)+ Uid + ".jpeg");
            Uri imageFileUri = Uri.fromFile(file);
            Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(it, CAMERA_REQUEST);

        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;
            bmpFactoryOptions.inSampleSize = 5;
//imageFilePath image path which you pass with intent
            Bitmap photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()
                                                 + "/PartyImage"+Integer.toString(clickedImage)+ Uid + ".jpeg", bmpFactoryOptions);



            int sdk = android.os.Build.VERSION.SDK_INT;
            if(clickedImage == 1){
                    img1.setImageBitmap(photo);
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img1.setBackgroundDrawable(null);
                    } else {
                        img1.setBackground(null);
                    }
                    imageExist[0] = true;

            }
            if(clickedImage == 2){
                    img2.setImageBitmap(photo);
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img2.setBackgroundDrawable(null);
                    } else {
                        img2.setBackground(null);
                    }
                    imageExist[1] = true;
            }
            if(clickedImage == 3){
                    img3.setImageBitmap(photo);
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        img3.setBackgroundDrawable(null);
                    } else {
                        img3.setBackground(null);
                    }
                    imageExist[2] = true;
            }
            try {
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, "PartyImage"+Integer.toString(clickedImage)+ Uid + ".jpeg");

                fOut = new FileOutputStream(file);


                photo.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
                fOut.flush();
                fOut.close();


                MediaStore.Images.Media.insertImage(this.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());


            } catch (FileNotFoundException e) {
                if(clickedImage == 1){
                    img1.setImageBitmap(null);
                    img1.setBackground(getResources().getDrawable(R.drawable.photo));
                }
                if(clickedImage == 2){
                    img2.setImageBitmap(null);
                    img2.setBackground(getResources().getDrawable(R.drawable.photo));
                }
                if(clickedImage == 3){
                    img3.setImageBitmap(null);
                    img3.setBackground(getResources().getDrawable(R.drawable.photo));
                }
            } catch (IOException e) {
                if(clickedImage == 1){
                    img1.setImageBitmap(null);
                    img1.setBackground(getResources().getDrawable(R.drawable.photo));
                }
                if(clickedImage == 2){
                    img2.setImageBitmap(null);
                    img2.setBackground(getResources().getDrawable(R.drawable.photo));
                }
                if(clickedImage == 3){
                    img3.setImageBitmap(null);
                    img3.setBackground(getResources().getDrawable(R.drawable.photo));
                }
            }

        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

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

            return wst.sendPartyInfo(txtName.getText().toString(),txtDate.getText().toString(),endDate,txtStart.getText().toString()
                              ,txtEnd.getText().toString(), longitude, latitude ,txtDetails.getText().toString(), Uid,imageExist);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();


            if(!result.toString().equals("Failed"))
            {
                Intent myIntent = new Intent(AddPartyScreen.this, PartyDetailScreen.class);
                myIntent.putExtra("PartyId",result);
                myIntent.putExtra("ScreenMode","Preview");
                startActivity(myIntent);

                finish();
            }


        }
    }



}

/*
private View.OnClickListener txtDateClick = new View.OnClickListener() {
        public void onClick(View v) {

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(v.getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            txtDate.setText(dayOfMonth + "."
                                    + (monthOfYear + 1) + "." + year);
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();

        }

    };

    private View.OnClickListener txtStartClick = new View.OnClickListener() {
        public void onClick(View v) {

            // Process to get Current Time
            final Calendar c = Calendar.getInstance();
            sHour = c.get(Calendar.HOUR_OF_DAY);
            sMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog tpd = new TimePickerDialog(v.getContext(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            // Display Selected time in textbox

                            if(hourOfDay >= 12)
                            {
                                if(hourOfDay >12)
                                    hourOfDay -= 12;

                                sAMPM = "PM";
                            }
                            else
                            {
                                sAMPM = "AM";
                            }

                            sHour = hourOfDay;



                            if(minute < 10)
                                sMinute = Integer.getInteger("0" + String.valueOf(minute));
                            else
                                sMinute = minute;

                            txtStart.setText(sHour + ":" + sMinute + " " + sAMPM);
                        }
                    }, sHour, sMinute, false);
            tpd.show();

        }

    };

 */

