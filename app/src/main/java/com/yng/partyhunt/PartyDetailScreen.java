package com.yng.partyhunt;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;
import com.yng.partyhunt.database.DBCParties;
import com.yng.partyhunt.database.DBCPartypics;
import com.yng.partyhunt.database.DBCPictures;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
  * Created by yng1905 on 6/17/14.
  */
 public class PartyDetailScreen  extends ActionBarActivity {

     private WebServiceTasks wst = new WebServiceTasks();
     private String partyId,imageSrc, screenMode,userId;
     private Bitmap toSaveBitmap;
     private TextView lblName,lblBeginDate,lblEndDate,lblMayor,lblDetail,lblCount;
     private Button btnAddList,btnDelete,btnMap,btnCheckin;
     private ImageView img1, img2, img3;
     private Double latitude,longitude;
     private boolean isCheckin = false;

     private ArrayList<HashMap<String, String>> mylist;

     private DBCParties dbParties;
     private DBCPictures dbPictures;
     private DBCPartypics dbPartypics;

     @TargetApi(Build.VERSION_CODES.GINGERBREAD)
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_partydetail_screen);

         partyId = getIntent().getStringExtra("PartyId");
         screenMode = getIntent().getStringExtra("ScreenMode");

         if(screenMode.equals("Check-in"))
            userId = getIntent().getStringExtra("UserId");

         if (android.os.Build.VERSION.SDK_INT > 9) {
             StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
             StrictMode.setThreadPolicy(policy);
         }

         lblName = (TextView) findViewById(R.id.lblName);
         lblBeginDate = (TextView) findViewById(R.id.lblBeginDate);
         lblEndDate = (TextView) findViewById(R.id.lblEndDate);
         lblMayor = (TextView) findViewById(R.id.lblMayor);
         lblDetail = (TextView) findViewById(R.id.lblDetail);
         lblCount = (TextView) findViewById(R.id.lblCount);


         btnAddList = (Button) findViewById(R.id.btnAdd);
         btnAddList.setOnClickListener(btnAddListClick);
         btnDelete = (Button) findViewById(R.id.btnDelete);
         btnDelete.setOnClickListener(btnDeleteClick);
         btnMap = (Button) findViewById(R.id.btnMap);
         btnMap.setOnClickListener(btnMapClick);
         btnCheckin = (Button) findViewById(R.id.btnCheckin);
         btnCheckin.setOnClickListener(btnCheckinClick);


         if(screenMode.equals("Preview"))
                   {
                      btnAddList.setVisibility(View.GONE);
                      btnDelete.setVisibility(View.GONE);
                      btnMap.setVisibility(View.GONE);
                      btnCheckin.setVisibility(View.GONE);
                   }else if (screenMode.equals("MapDetail"))
                   {
                      btnDelete.setVisibility(View.GONE);
                      btnMap.setVisibility(View.GONE);
                      btnCheckin.setVisibility(View.GONE);
                   }else if(screenMode.equals("ListDetail"))
                   {
                       btnAddList.setVisibility(View.GONE);
                       btnCheckin.setVisibility(View.GONE);
                   }else if(screenMode.equals("Check-in"))
                     {
                         btnAddList.setVisibility(View.GONE);
                         btnDelete.setVisibility(View.GONE);
                         btnMap.setVisibility(View.GONE);
                     }

         img1 = (ImageView) findViewById(R.id.imageView);
         img2 = (ImageView) findViewById(R.id.imageView2);
         img3 = (ImageView) findViewById(R.id.imageView3);

         img1.setOnClickListener(img1Click);
         img2.setOnClickListener(img2Click);
         img3.setOnClickListener(img3Click);


         dbParties = new DBCParties(this);
         dbPictures = new DBCPictures(this);
         dbPartypics = new DBCPartypics(this);

         if(screenMode.equals("ListDetail"))
         {
             BindItems();
         }
         else
            new HttpAsyncTask(PartyDetailScreen.this, "Sending...").execute("");
     }

     public void BindItems()
     {
         Cursor cParties = (Cursor) dbParties.Select(dbParties,"SELECT * FROM " + dbParties.tableName + " WHERE idNr=" + partyId);
         cParties.moveToFirst();

         lblName.setText(cParties.getString(2));
         lblBeginDate.setText(cParties.getString(3) + " " + cParties.getString(5));
         lblEndDate.setText(cParties.getString(4) + " " + cParties.getString(6));
         lblCount.setText(cParties.getString(7));
         latitude = Double.valueOf(cParties.getString(9));
         longitude = Double.valueOf(cParties.getString(8));
         lblDetail.setText(cParties.getString(10));
         lblMayor.setText(cParties.getString(11));

         Cursor cPhotos = (Cursor) dbPictures.Select(dbPictures,"SELECT URI FROM " + dbPictures.tableName + " WHERE idNr IN (SELECT picId FROM " + dbPartypics.tableName + " WHERE partyId = " + partyId +")");

         cPhotos.moveToFirst();
         int i = 0;
         File imgFile;
         if(cPhotos.getCount() == 0){
             img1.setVisibility(View.GONE);
             img2.setVisibility(View.GONE);
             img3.setVisibility(View.GONE);
             return;
         }
         do
         {
             i++;
             switch (i){
                 case 1:
                     imgFile = new  File(Environment.getExternalStorageDirectory().toString() + "/" + cPhotos.getString(0));
                     if(imgFile.exists()){
                      Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                      img1.setImageBitmap(myBitmap);
                     }
                     break;
                 case 2:
                     imgFile = new  File(Environment.getExternalStorageDirectory().toString() + "/" + cPhotos.getString(0));
                     if(imgFile.exists()){
                         Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                         img2.setImageBitmap(myBitmap);

                     }
                     break;
                 case 3:
                     imgFile = new  File(Environment.getExternalStorageDirectory().toString() + "/" + cPhotos.getString(0));
                     if(imgFile.exists()){
                         Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                         img3.setImageBitmap(myBitmap);
                     }
                     break;
             }
         }while(cPhotos.moveToNext());

         if(img1.getDrawable() == null)
             img1.setVisibility(View.GONE);
         if(img2.getDrawable() == null)
             img2.setVisibility(View.GONE);
         if(img3.getDrawable() == null)
             img3.setVisibility(View.GONE);
     }


     public void saveBitmap(String name) throws IOException {
         Bitmap photo = toSaveBitmap;

         String path = Environment.getExternalStorageDirectory().toString();
         OutputStream fOut = null;

         File file = new File(path, name);

         fOut = new FileOutputStream(file);


         photo.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
         try {
             fOut.flush();
         } catch (IOException e) {
             e.printStackTrace();
         }
         fOut.close();


         MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

     }




     private View.OnClickListener btnAddListClick = new View.OnClickListener() {

         public void onClick(View v) {

             Toast.makeText(getApplicationContext(),"Adding into list, please wait...",Toast.LENGTH_LONG).show();
             Cursor cursor = dbParties.Select(dbParties,"SELECT * FROM " + dbParties.tableName
                                                       +" WHERE serverId = " + partyId);
             boolean saved = false;
             if(cursor.getCount() > 0)
             {
                 Toast.makeText(v.getContext(),"This party is already saved!",Toast.LENGTH_LONG).show();
                 return;
             }

             btnAddList.setEnabled(false);
             Long pId = 0L;

             try{
             dbParties.setServerId(partyId);
             dbParties.setName(mylist.get(0).get("Name").toString());
             dbParties.setBeginDate(mylist.get(0).get("BeginDate").toString());
             dbParties.setEndDate(mylist.get(0).get("EndDate").toString());
             dbParties.setStartTime(mylist.get(0).get("Begin").toString());
             dbParties.setEndTime(mylist.get(0).get("End").toString());
             dbParties.setDescription(mylist.get(0).get("Description").toString());
             dbParties.setMayor(mylist.get(0).get("Mayor").toString());
             dbParties.setLatitude(mylist.get(0).get("Latitude").toString());
             dbParties.setLongitude(mylist.get(0).get("Longitude").toString());
             dbParties.setAttendersCount(mylist.get(0).get("AttCount").toString());

             pId = dbParties.Insert();
             saved = true;
             }catch(Exception ex)
             {
                 Toast.makeText(v.getContext(),"Party couldn't be saved into list. Please try again!",Toast.LENGTH_LONG).show();
                 Log.d("Error",ex.toString());
             }
             finally {
                     Long picId1 = -1L;
                     Long picId2 = -1L;
                     Long picId3 = -1L;
                     try{

                         imageSrc = "http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo1").toString();
                         try {
                             URL url = new URL(imageSrc);
                             HttpURLConnection connection = (HttpURLConnection) url
                                     .openConnection();
                             connection.setDoInput(true);
                             connection.connect();
                             InputStream input = connection.getInputStream();
                             toSaveBitmap = BitmapFactory.decodeStream(input);
                         } catch (IOException e) {
                             e.printStackTrace();

                         }saveBitmap(mylist.get(0).get("Photo1").toString().substring(7));

                         dbPictures.setURI(mylist.get(0).get("Photo1").toString().substring(7));
                         picId1 = dbPictures.Insert();

                         imageSrc = "http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo2").toString();
                         try {
                             URL url = new URL(imageSrc);
                             HttpURLConnection connection = (HttpURLConnection) url
                                     .openConnection();
                             connection.setDoInput(true);
                             connection.connect();
                             InputStream input = connection.getInputStream();
                             toSaveBitmap = BitmapFactory.decodeStream(input);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }saveBitmap(mylist.get(0).get("Photo2").toString().substring(7));

                         dbPictures.setURI(mylist.get(0).get("Photo2").toString().substring(7));
                         picId2 = dbPictures.Insert();

                         imageSrc = "http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo3").toString();
                         try {
                             URL url = new URL(imageSrc);
                             HttpURLConnection connection = (HttpURLConnection) url
                                     .openConnection();
                             connection.setDoInput(true);
                             connection.connect();
                             InputStream input = connection.getInputStream();
                             toSaveBitmap = BitmapFactory.decodeStream(input);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }saveBitmap(mylist.get(0).get("Photo3").toString().substring(7));

                         dbPictures.setURI(mylist.get(0).get("Photo3").toString().substring(7));
                         picId3 = dbPictures.Insert();

                     }catch(Exception ex)
                     {
                         Log.d("Error",ex.toString());
                     }

                     if(saved){
                         try{
                         if(picId1 != -1L){
                             dbPartypics.setPartyId(pId);
                             dbPartypics.setPicId(picId1);
                             dbPartypics.Insert();
                         }
                         if(picId2 != -1L){
                             dbPartypics.setPartyId(pId);
                             dbPartypics.setPicId(picId2);
                             dbPartypics.Insert();
                         }
                         if(picId3 != -1L){
                             dbPartypics.setPartyId(pId);
                             dbPartypics.setPicId(picId3);
                             dbPartypics.Insert();
                         }
                         Toast.makeText(v.getContext(),"Party is saved into list. You can get and share from list screen",Toast.LENGTH_LONG).show();
                         }catch(Exception ex)
                         {
                             Toast.makeText(v.getContext(),"Party could not be saved due to technical problem! Please try again.",Toast.LENGTH_LONG).show();
                         }
                     }
                     else
                         Toast.makeText(v.getContext(),"Party could not be saved due to technical problem! Please try again.",Toast.LENGTH_LONG).show();

                 btnAddList.setEnabled(true);
             }






         }
     };

     private View.OnClickListener btnDeleteClick = new View.OnClickListener() {
         File imgFile;
         public void onClick(View v) {
             Cursor cPhotos = (Cursor) dbPictures.Select(dbPictures,"SELECT idNr,URI FROM " + dbPictures.tableName + " WHERE idNr IN (SELECT picId FROM " + dbPartypics.tableName + " WHERE partyId = " + partyId +")");

             if(cPhotos.getCount()>0){
                 cPhotos.moveToNext();
                 do
                 {
                 imgFile = new  File(Environment.getExternalStorageDirectory().toString() + "/" + cPhotos.getString(1));
                     if(imgFile.exists()){
                         imgFile.delete();
                     }
                     dbPictures.Delete(Long.valueOf(cPhotos.getString(0)));
                 }while(cPhotos.moveToNext());
                 dbPartypics.DeleteWithPartyId(partyId);
             }

             dbParties.Delete(Long.valueOf(partyId));

            finish();
         }
     };

    private View.OnClickListener btnCheckinClick = new View.OnClickListener() {
        public void onClick(View v) {
            isCheckin = true;
            new HttpAsyncTask(PartyDetailScreen.this, "Checking in...").execute("");
        }
    };

    private View.OnClickListener btnMapClick = new View.OnClickListener() {

        public void onClick(View v) {
            Intent myIntent;
            myIntent = new Intent(getApplicationContext(), DetailMapScreen.class);
            myIntent.putExtra("Latitude",latitude);
            myIntent.putExtra("Longitude",longitude);
            myIntent.putExtra("Name",lblName.getText().toString());
            startActivity(myIntent);
        }
    };


     private View.OnClickListener img1Click = new View.OnClickListener() {

         public void onClick(View v) {
             Bitmap bitmap = ((BitmapDrawable)img1.getDrawable()).getBitmap();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
             byte[] b = baos.toByteArray();

             Intent intent = new Intent(getApplicationContext(), PictureDetailScreen.class);
             intent.putExtra("picture", b);
             startActivity(intent);
         }
     };

     private View.OnClickListener img2Click = new View.OnClickListener() {

         public void onClick(View v) {
             Bitmap bitmap = ((BitmapDrawable)img2.getDrawable()).getBitmap();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
             byte[] b = baos.toByteArray();

             Intent intent = new Intent(getApplicationContext(), PictureDetailScreen.class);
             intent.putExtra("picture", b);
             startActivity(intent);

         }
     };

     private View.OnClickListener img3Click = new View.OnClickListener() {

         public void onClick(View v) {

             Bitmap bitmap = ((BitmapDrawable)img3.getDrawable()).getBitmap();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
             byte[] b = baos.toByteArray();

             Intent intent = new Intent(getApplicationContext(), PictureDetailScreen.class);
             intent.putExtra("picture", b);
             startActivity(intent);
         }
     };



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
             if(isCheckin)
                return wst.checkIn(userId,partyId);
             else
                mylist = wst.receiveParty(partyId);


             return "";
            // return wst.sendPartyInfo(txtName.getText().toString(),txtDate.getText().toString(),txtStart.getText().toString()
                 //    ,txtEnd.getText().toString(), longitude, latitude ,txtDetails.getText().toString(), Uid,imageExist);

         }

         // onPostExecute displays the results of the AsyncTask.
         @Override
         protected void onPostExecute(String result) {

             if(!isCheckin)
             {
                 if(mylist.size() == 0)
                 {
                     finish();
                     return;
                 }
                 lblName.setText(mylist.get(0).get("Name").toString());
                 lblBeginDate.setText(mylist.get(0).get("BeginDate").toString() + " " +mylist.get(0).get("Begin").toString());
                 lblEndDate.setText(mylist.get(0).get("EndDate").toString() + " " + mylist.get(0).get("End").toString());
                 lblMayor.setText(mylist.get(0).get("Mayor").toString());
                 lblDetail.setText(mylist.get(0).get("Description").toString());//Check-in:
                 lblCount.setText(mylist.get(0).get("AttCount").toString());

                 try{
                 Drawable image = LoadImageFromWebOperations("http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo1").toString());
                    img1.setImageDrawable(image);
                 image = LoadImageFromWebOperations("http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo2").toString());
                     img2.setImageDrawable(image);
                 image = LoadImageFromWebOperations("http://partyhunt.lecturesupport.com/" + mylist.get(0).get("Photo3").toString());
                     img3.setImageDrawable(image);
                 }catch(Exception ex)
                 {

                 }
                 finally
                 {
                     if(img1.getDrawable() == null)
                         img1.setVisibility(View.GONE);
                     if(img2.getDrawable() == null)
                         img2.setVisibility(View.GONE);
                     if(img3.getDrawable() == null)
                         img3.setVisibility(View.GONE);
                 }
             }
             else
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

             isCheckin = true;
             progress.dismiss();
          }

         private Drawable LoadImageFromWebOperations(String url) {
             try
             {
                 InputStream is = (InputStream) new URL(url).getContent();
                 Drawable d = Drawable.createFromStream(is, "src name");
                 return d;
             }catch (Exception e) {
                 System.out.println("Exc="+e);
                 return null;
             }
         }


     }
}
