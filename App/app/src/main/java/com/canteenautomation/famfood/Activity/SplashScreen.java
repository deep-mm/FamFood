package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.CanteenUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private SharedData sharedData;
    private FirebaseAuth firebaseAuth;
    private Helper helper;
    private Intent intent;
    private UserUtility userUtility;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initialize();

        //Starting Background Service
        /*intent = new Intent(getApplicationContext(), BackgroundService.class);
        startService(intent);*/

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(firebaseAuth==null){
                    sharedData.clear();
                }
                if(sharedData.isLoggedIn()==null)
                    sharedData.isLoggedIn(false);

                if(sharedData.isSignedUp()==null)
                    sharedData.isSignedUp(false);

                if(sharedData.isLoggedIn()){
                    if(sharedData.isSignedUp()) {
                        intent = new Intent(getApplicationContext(), BackgroundService.class);
                        startService(intent);
                        UserEntity userEntity = sharedData.getUserEntity();
                        userEntity.setLastLogin(helper.currentTime());
                        userUtility.addUser(userEntity);

                        if(sharedData.getCanteenEntity()==null)
                            intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                        else
                            intent = new Intent(getApplicationContext(), ItemActivity.class);

                        startActivity(intent);
                    }
                    else{
                        firebaseAuth.signOut();
                        sharedData.isLoggedIn(false);
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
            }

        }, SPLASH_TIME_OUT);
    }

    public void initialize() {
        sharedData = new SharedData(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        helper = new Helper(getApplicationContext());
        userUtility = new UserUtility();

        //Initializing shared data values
        sharedData.isPriceSort(0);
        sharedData.isTimeSort(0);
        titleText = (TextView) findViewById(R.id.title_text);
        titleText.setTypeface(helper.segoeprb);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(SplashScreen.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), SplashScreen.class);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finishAffinity();
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }
}
