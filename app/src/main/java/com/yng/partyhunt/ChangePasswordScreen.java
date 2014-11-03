package com.yng.partyhunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;

/**
 * Created by yng1905 on 7/3/14.
 */
public class ChangePasswordScreen extends ActionBarActivity {

    private Button btnChange;
    public EditText txtCurrent,txtPassword,txtPassApproval;
    String resultMessage = "";
    private int mYear, mMonth, mDay;
    private boolean isFirst = true;
    private String Uid;



    WebServiceTasks wst = new WebServiceTasks();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword_screen);

        Uid = getIntent().getStringExtra("UserId");

        btnChange = (Button) findViewById(R.id.btnChange);
        btnChange.setOnClickListener(btnChangeClick);

        txtCurrent  = (EditText) findViewById(R.id.txtCurrent);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtPassApproval = (EditText) findViewById(R.id.txtPassApproval);


    }



    private View.OnClickListener btnChangeClick = new View.OnClickListener() {

        public void onClick(View v) {

            if(txtPassword.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_LONG).show();
                return;
            }
            if(txtPassApproval.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter your password again!", Toast.LENGTH_LONG).show();
                return;
            }
            if(!txtPassApproval.getText().toString().equals(txtPassword.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "The passwords that you have given are not matched. Please check it again!", Toast.LENGTH_LONG).show();
                return;
            }



            new HttpAsyncTask(ChangePasswordScreen.this,"New password is being sent...").execute("");

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

            return wst.sendNewPassword(Uid,txtPassword.getText().toString(),txtCurrent.getText().toString());

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if(result.toString().length() > 6 && result.substring(0,7).toString().equals("-Failed"))
            {
                Toast.makeText(getBaseContext(),result.substring(8).toString(), Toast.LENGTH_LONG).show();
                return;
            }
            if(result.toString().length() > 5 && result.substring(0,6).toString().equals("Failed"))
            {
                Toast.makeText(getBaseContext(),"Please check your internet connection", Toast.LENGTH_LONG).show();
                return;
            }
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        }
    }





}

