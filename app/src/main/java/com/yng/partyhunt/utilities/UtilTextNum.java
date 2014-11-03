package com.yng.partyhunt.utilities;

import android.os.Build;
import android.text.InputType;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by yng1905 on 6/16/14.
 */
public class UtilTextNum {
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    //Calculate age according to given date
    public static int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    //Compare times
    public static boolean isTimeOneBeforeTwo(String time1, String time2)
    {
        Date date1,date2;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
        try {
             date1 = sdf.parse(time1);
             date2 = sdf.parse(time2);
        } catch (ParseException e) {
            return false;
        }

        return date1.compareTo(date2) < 0;
    }
    public static String addDay(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = null;
        try {
            dt = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();


        return sdf.format(dt);
    }

    public static String getDate()
    {
        Calendar c = Calendar.getInstance();

        return String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "."
                +String.valueOf(c.get(Calendar.MONTH) + 1) + "."
                +String.valueOf(c.get(Calendar.YEAR));
    }

    public static String getTime()
    {
        Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        return formatTime(hourOfDay, minute);
    }

    public static String formatTime(int hourOfDay, int minute)
    {
       String sAMPM;
       String sMinute;
       String sHour;
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

            if(hourOfDay < 10)
                sHour = "0" + String.valueOf(hourOfDay);
            else
                sHour = String.valueOf(hourOfDay);

            if(minute < 10)
                sMinute = "0" + String.valueOf(minute);
            else
                sMinute = String.valueOf(minute);

            return sHour + ":" + sMinute + " " + sAMPM;
    }
}
