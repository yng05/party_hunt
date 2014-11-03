package com.yng.partyhunt;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yng1905 on 6/14/14.
 */
public class SigninScreen extends ActionBarActivity {

    private Button btnSignin,btnRegister,btnNewPass;
    private ArrayList<HashMap<String, String>> mylist;
    private EditText txtEmail,txtPassword;
    String resultMessage = "";
    private int asyncTaskMode;


    WebServiceTasks wst = new WebServiceTasks();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_screen);

        RelativeLayout layout =(RelativeLayout)findViewById(R.id.lytSignin);

        layout.setBackgroundResource(R.drawable.background);

        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(btnSigninClick);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(btnRegisterClick);

        btnNewPass = (Button) findViewById(R.id.btnNewPass);
        btnNewPass.setOnClickListener(btnNewPassClick);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPass);
    }

    private View.OnClickListener btnRegisterClick = new View.OnClickListener() {

        public void onClick(View v) {

            Intent myIntent = new Intent(SigninScreen.this, RegisterScreen.class);
            SigninScreen.this.startActivity(myIntent);

        }
    };

    private View.OnClickListener btnNewPassClick = new View.OnClickListener() {

        public void onClick(View v) {
            asyncTaskMode = 1;
            new HttpAsyncTask(SigninScreen.this, "Please wait, new password is being sent").execute("");
        }
    };

    private View.OnClickListener btnSigninClick = new View.OnClickListener() {

        public void onClick(View v) {
            asyncTaskMode = 0;
            new HttpAsyncTask(SigninScreen.this, "Signing in...").execute("");

        }
    };
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
            if(asyncTaskMode == 0)
                mylist = wst.receiveUser(txtEmail.getText().toString(), txtPassword.getText().toString());
            else if (asyncTaskMode == 1)
                resultMessage = wst.sendMailForPass(txtEmail.getText().toString());

            return "";


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();

            if(asyncTaskMode == 0){
                if(wst.getIsError())
                {
                    Toast.makeText(getApplicationContext(), wst.getErrorText(), Toast.LENGTH_LONG).show();
                    return;
                }

                if(mylist.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Your user name or password is wrong!", Toast.LENGTH_LONG).show();
                    return;
                }

                txtPassword.setText("");
                Intent myIntent = new Intent(SigninScreen.this, MainActivity.class);
                myIntent.putExtra("Id",mylist.get(0).get("Id").toString());
                myIntent.putExtra("Name",mylist.get(0).get("Name").toString());
                myIntent.putExtra("Surname", mylist.get(0).get("Surname").toString());


                SigninScreen.this.startActivity(myIntent);
            }
            else if (asyncTaskMode == 1)
            {
                Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_LONG).show();
            }

        }
    }



}
