package com.yng.partyhunt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.app.partyhunt.R;
import com.yng.partyhunt.connection.WebServiceTasks;
import com.yng.partyhunt.utilities.UtilTextNum;

import java.util.Calendar;

/**
 * Created by yng1905 on 6/12/14.
 */
public class RegisterScreen extends ActionBarActivity {

    private Button btnRegister;
    public EditText txtEmail,txtName,txtSurname,txtPassword,txtBirthDate,txtPassApproval;
    String resultMessage = "";
    private int mYear, mMonth, mDay;
    private boolean isFirst = true;



    WebServiceTasks wst = new WebServiceTasks();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(btnRegisterClick);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtName = (EditText) findViewById(R.id.txtName);
        txtSurname = (EditText) findViewById(R.id.txtSurname);
        txtPassword = (EditText) findViewById(R.id.txtPass);
        txtBirthDate = (EditText) findViewById(R.id.txtBirthDate);
        UtilTextNum.disableSoftInputFromAppearing(txtBirthDate);
        txtBirthDate.setOnTouchListener(txtBirthDateClick);
        txtPassApproval = (EditText) findViewById(R.id.txtPassApproval);


    }

    private View.OnTouchListener txtBirthDateClick = new View.OnTouchListener() {
     public boolean onTouch(View v, MotionEvent motionEvent) {
         if(isFirst){
            isFirst = false;
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
                            txtBirthDate.setText(dayOfMonth + "."
                                    + (monthOfYear + 1) + "." + year);
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            isFirst = true;

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
         }
         return true;
        }

    };

    private View.OnClickListener btnRegisterClick = new View.OnClickListener() {

        public void onClick(View v) {
            if(txtEmail.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter an email address!", Toast.LENGTH_LONG).show();
                return;
            }

            if(!UtilTextNum.checkEmail(txtEmail.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Please enter a valid email address!", Toast.LENGTH_LONG).show();
                return;
            }

            if(txtName.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter your first name!", Toast.LENGTH_LONG).show();
                return;
            }
            if(txtSurname.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter your second name!", Toast.LENGTH_LONG).show();
                return;
            }
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
            if(txtBirthDate.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter your birth date!", Toast.LENGTH_LONG).show();
                return;
            }
            if(UtilTextNum.getAge(mYear,mMonth,mDay) < 18)
            {
                Toast.makeText(getApplicationContext(), "You must be more than 18 years old to be resgistered!", Toast.LENGTH_LONG).show();
                return;
            }


            new HttpAsyncTask(RegisterScreen.this,"Please wait during register process...").execute("");

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

            return wst.sendNewUser(txtEmail.getText().toString(), txtPassword.getText().toString()
                            ,txtBirthDate.getText().toString(), txtName.getText().toString()
                            , txtSurname.getText().toString());


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

        }
    }





}

