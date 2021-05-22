package com.canteenautomation.famfood.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Activity.LoginActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Helper {

    Context c;
    AssetManager am;
    public static Typeface cambria, comic, segoeprb, cambriaBold, comicBold;

    public Helper(Context c) {
        this.c = c;
        defineFonts();
    }

    public void printToast(String text,int length){
        Toast.makeText(c,text,length).show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String currentTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public String currentClock(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public String currentDateTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public String convertDate(String oldDate, String newDate, String date){

        Date dateVal;
        SimpleDateFormat df = new SimpleDateFormat(oldDate);
        try {
            dateVal = df.parse(date);
        } catch (ParseException e) {
            return oldDate;
        }

        df = new SimpleDateFormat(newDate);
        String formattedDate = df.format(dateVal);
        return formattedDate;
    }

    public String encodeStringToURLSafe(String pathParam) {
        try {
            return URLEncoder.encode(pathParam, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return pathParam;
        }
    }

    public String currentTimeSec(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public void defineFonts(){
        am = c.getAssets();

        cambria = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "cambria.ttf"));

        comic = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "comic.TTF"));

        segoeprb = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "segoeprb.ttf"));

        cambriaBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "cambriab.ttf"));

        comicBold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "comicb.ttf"));

    }
}
