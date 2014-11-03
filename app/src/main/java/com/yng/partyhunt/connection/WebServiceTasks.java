package com.yng.partyhunt.connection;

import android.os.Environment;
import android.util.Log;

import com.yng.partyhunt.utilities.UtilClients;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yng1905 on 6/10/14.
 */
public class WebServiceTasks {

    public static int TIMEOUT_MILLISEC = 10000;
    private String result;
    private boolean isError = false;
    private String errorText = "";
    String url;
    JSONObject json;
    HttpParams p = new BasicHttpParams();
    HashMap<String, String> map = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> mylist =
            new ArrayList<HashMap<String, String>>();
    ArrayList<BasicNameValuePair> bnvp;

    String uploadFilePath = "/mnt/sdcard/";
    String uploadFileName;
    int serverResponseCode = 0;

    public String getErrorText()
    {
        return this.errorText;
    }

    public boolean getIsError()
    {
        return this.isError;
    }

    public String sendTestUser(String uname, String fname)
    {
        url = "http://partyhunt.lecturesupport.com/testsave.php";

        try {
        json = new JSONObject();
        json.put("UserName", uname);
        json.put("FullName", fname);

        this.result = sendData();
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }

    //Sends the information of new user. Usage: Register screen
    public String sendNewUser(String email, String pass,String birthDate, String name, String surname)
    {
        url = "http://partyhunt.lecturesupport.com/register.php";

        try {
            json = new JSONObject();
            json.put("Email", email);
            json.put("Name", name);
            json.put("Surname", surname);
            json.put("Password", pass);
            json.put("Birthdate", birthDate);

            this.result = sendData();
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }

    //Sends the information of new and old password. Usage: Change Password screen
    public String sendNewPassword(String uid,String pass,String current)
    {
        url = "http://partyhunt.lecturesupport.com/setpassword.php";

        try {
            json = new JSONObject();
            json.put("Uid", uid);
            json.put("Password", pass);
            json.put("Current", current);

            this.result = sendData();
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }

    //Sends the information of user and party. Usage: Party Detail screen
    public String checkIn(String uId, String pId)
    {
        url = "http://partyhunt.lecturesupport.com/checkin.php";

        try {
            json = new JSONObject();
            json.put("Uid", uId);
            json.put("Pid", pId);

            this.result = sendData();
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }

    //Sends the information of party. Usage: Add Party screen
    public String sendPartyInfo(String name, String beginDate, String endDate,String begin, String end, String longitude, String latitude
                                , String description, String idMayor, boolean[] imageExist)
    {
        for(int i = 0; i < 3; i++)
        {
            if(imageExist[i])
            {
                uploadFile("PartyImage" + Integer.toString(i+1) + idMayor + ".jpeg");
            }
        }

        url = "http://partyhunt.lecturesupport.com/addparty.php";

        try {
            json = new JSONObject();
            json.put("Name", name);
            json.put("BeginDate", beginDate);
            json.put("EndDate", endDate);
            json.put("Begin", begin);
            json.put("End", end);
            json.put("Longitude", longitude);
            json.put("Latitude", latitude);
            json.put("Description", description);
            json.put("IdMayor", idMayor);

            this.result = sendData();

            if(this.result.toString().length() > 6 && this.result.toString().substring(0,6).equals("Error"))
            {
                this.result = "Failed";
            }
            else
            {
                this.result = this.result.toString().replace(" \n","");
            }
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }

    //Triggers the server to send new password. Usage: Sign-in screen
    public String sendMailForPass(String email)
    {
        url = "http://partyhunt.lecturesupport.com/sendpassword.php";

        try {
            json = new JSONObject();
            json.put("Email", email);

            this.result = sendData();
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }

    }


    public ArrayList<HashMap<String, String>> receiveTestUser()
    {
        url = "http://partyhunt.lecturesupport.com/testsend.php";
        p.setParameter("user", "1");
        bnvp = new ArrayList<BasicNameValuePair>();
        bnvp.add(new BasicNameValuePair("user", "1"));
        String responseBody = receiveData();

        JSONObject json = null;


        ArrayList<HashMap<String, String>> mylist =
                new ArrayList<HashMap<String, String>>();

        try {
            json = new JSONObject(responseBody);

        JSONArray jArray = json.getJSONArray("posts");


        for (int i = 0; i < jArray.length(); i++) {

            JSONObject e = jArray.getJSONObject(i);
            String s = e.getString("post");
            JSONObject jObject = new JSONObject(s);

            map.put("UserName", jObject.getString("UserName"));
            map.put("FullName", jObject.getString("FullName"));

            mylist.add(map);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mylist;
    }

    //Gets information of parties. Usage: Map Screen
    public ArrayList<HashMap<String, String>> receiveParties(Double latitude,Double longitude,Double distance)
    {

        Double latMin = latitude - (0.142705 * distance);
        Double latMax = latitude + (0.142705 * distance);
        Double longMin = longitude - (0.142705 * distance);
        Double longMax = longitude + (0.142705 * distance);

        url = "http://partyhunt.lecturesupport.com/getparties.php";
        p.setParameter("latMin", latitude);
        bnvp = new ArrayList<BasicNameValuePair>();
        bnvp.add(new BasicNameValuePair("LatitudeMin", String.valueOf(latMin)));
        bnvp.add(new BasicNameValuePair("LatitudeMax", String.valueOf(latMax)));
        bnvp.add(new BasicNameValuePair("LongitudeMin", String.valueOf(longMin)));
        bnvp.add(new BasicNameValuePair("LongitudeMax", String.valueOf(longMax)));
        String responseBody = receiveData();

        JSONObject json = null;



        mylist.clear();

        try {

            JSONArray jArrayPhotos = new JSONArray(responseBody);


            for (int j = 0; j < jArrayPhotos.length(); j++) {
                HashMap<String, String> mapP = new HashMap<String, String>();
                JSONObject eParties = jArrayPhotos.getJSONObject(j);
                mapP.put("Id", eParties.getString("Id"));
                mapP.put("Name", eParties.getString("Name"));
                mapP.put("AttCount", eParties.getString("AttCount"));
                mapP.put("Latitude", eParties.getString("Latitude"));
                mapP.put("Longitude", eParties.getString("Longitude"));

                mylist.add(mapP);

                errorText = "";
                isError = false;
            }


        } catch (JSONException e) {
            errorText = result;
            isError = true;
        }
        finally {
            return mylist;
        }
    }

    //Gets user info. Usage: Sign-in screen
    public ArrayList<HashMap<String, String>> receiveUser(String email,String password)
    {
        url = "http://partyhunt.lecturesupport.com/login.php";
        p.setParameter("Email", email);
        bnvp = new ArrayList<BasicNameValuePair>();
        bnvp.add(new BasicNameValuePair("Email", email));
        bnvp.add(new BasicNameValuePair("Password", password));
        String responseBody = receiveData();

        JSONObject json = null;



        mylist.clear();

        try {
            json = new JSONObject(responseBody);

            JSONArray jArray = json.getJSONArray("posts");


            for (int i = 0; i < jArray.length(); i++) {

                JSONObject e = jArray.getJSONObject(i);
                String s = e.getString("post");
                JSONObject jObject = new JSONObject(s);

                map.put("Id", jObject.getString("Id"));
                map.put("Name", jObject.getString("Name"));
                map.put("Surname", jObject.getString("Surname"));

                mylist.add(map);

                errorText = "";
                isError = false;
            }
        } catch (JSONException e) {
            errorText = responseBody;
            isError = true;
        }
        finally {
            return mylist;
        }
    }

    //Gets party info. Usage: Main screen, Party Detail screen
    public ArrayList<HashMap<String, String>> receiveParty(String partyId)
    {
        url = "http://partyhunt.lecturesupport.com/getparty.php";
        p.setParameter("PartyId", partyId);
        bnvp = new ArrayList<BasicNameValuePair>();
        bnvp.add(new BasicNameValuePair("PartyId", partyId));
        String responseBody = receiveData();
        String responsePhotos = receivePhotos(partyId);
        JSONObject json = null;



        mylist.clear();

        try {
            json = new JSONObject(responseBody);

            JSONArray jArray = json.getJSONArray("posts");


            for (int i = 0; i < jArray.length(); i++) {

                JSONObject e = jArray.getJSONObject(i);
                String s = e.getString("post");
                JSONObject jObject = new JSONObject(s);

                map.put("Name", jObject.getString("Name"));
                map.put("BeginDate", jObject.getString("BeginDate"));
                map.put("EndDate", jObject.getString("EndDate"));
                map.put("Begin", jObject.getString("Begin"));
                map.put("End", jObject.getString("End"));
                map.put("Description", jObject.getString("Description"));
                map.put("Mayor", jObject.getString("Mayor"));
                map.put("Latitude", jObject.getString("Latitude"));
                map.put("Longitude", jObject.getString("Longitude"));
                map.put("AttCount", jObject.getString("AttCount"));


                    JSONArray jArrayPhotos = new JSONArray(responsePhotos);


                    for (int j = 0; j < jArrayPhotos.length(); j++) {

                        JSONObject ePhotos = jArrayPhotos.getJSONObject(j);
                        map.put("Photo" + String.valueOf(j+1) , ePhotos.getString("Path"));
                    }

                mylist.add(map);

                errorText = "";
                isError = false;
            }


        } catch (JSONException e) {
            errorText = result;
            isError = true;
        }
        finally {
            return mylist;
        }
    }


    public String receivePhotos(String partyId)
    {
        url = "http://partyhunt.lecturesupport.com/getphotos.php";
        p.setParameter("PartyId", partyId);
        bnvp = new ArrayList<BasicNameValuePair>();
        bnvp.add(new BasicNameValuePair("PartyId", partyId));
        String responseBody = receiveData();

        return responseBody;
    }

    //Next two method has been developed to be basement of data transfer methods. All the other specialized
    //methods set the variables and call the methods below

    protected String sendData(){
        try {

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(url);
            request.setEntity(new ByteArrayEntity(json.toString().getBytes(
                    "UTF8")));
            request.setHeader("json", json.toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            if (entity != null) {
                InputStream instream = entity.getContent();

                String result = UtilClients.convertStreamToString(instream);
                Log.i("Read from server", result);
                this.result = result;
                //Toast.makeText(this, result,Toast.LENGTH_LONG).show();
            }
        } catch (Throwable t) {
            this.result = "Failed";
            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
        }
        finally {
            return this.result;
        }
    }


    protected String receiveData()
    {
        String result = "";
        try {
            // http://androidarabia.net/quran4android/phpserver/connecttoserver.php

            // Log.i(getClass().getSimpleName(), "send  task - start");
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            //

            // p.setParameter("name", pvo.getName());


            // Instantiate an HttpClient
            HttpClient httpclient = new DefaultHttpClient(p);

            HttpPost httppost = new HttpPost(url);

            // Instantiate a GET HTTP method
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                //
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        2);

                for(int i = 0; i < bnvp.size(); i++)
                {
                    nameValuePairs.add(bnvp.get(i));
                }

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost,
                        responseHandler);
                // Parse
                result = responseBody;
                //Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();

            } catch (ClientProtocolException e) {
                result = "";
                e.printStackTrace();
            } catch (IOException e) {
                result = "";
                e.printStackTrace();
            }

            // Log.i(getClass().getSimpleName(), "send  task - end");

        } catch (Throwable t) {
            result = "";
        }
        finally {
            return result;
        }

    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(Environment.getExternalStorageDirectory().toString()+"/"+sourceFileUri);

        if (!sourceFile.isFile()) {
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://partyhunt.lecturesupport.com/uploadimage.php");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                /*if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;

                            messageText.setText(msg);
                            Toast.makeText(UploadToServer.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }*/

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();



                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {


                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

}
