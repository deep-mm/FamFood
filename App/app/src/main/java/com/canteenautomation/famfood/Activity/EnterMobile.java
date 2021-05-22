package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

public class EnterMobile extends AppCompatActivity {
    private TextView mobile_no;
    private EditText enter_mobile;
    private Button send_otp;
    private String mobile;
    private ImageButton back_button;
    private SharedData sharedData;
    private Helper helper;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile);

        initialize();

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile = enter_mobile.getText().toString();
                if(isValidMobile(mobile)) {
                    mobile = "+91" + mobile;
                    intent = new Intent(getApplicationContext(), EnterOtp.class);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                }
                else{
                    printDialog("Invalid Mobile","Please enter a valid mobile number");
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   onBackPressed();
               }
           });

    }

    private boolean isValidMobile(String phone) {
        if(phone.length()==10)
            return android.util.Patterns.PHONE.matcher(phone).matches();
        else
            return false;
    }

    @Override
    public void onStart(){
        super.onStart();
        checkLogin();

        if(!helper.isNetworkAvailable()){
            noConnectivityDialog();
        }

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        checkLogin();

        if(!helper.isNetworkAvailable()){
            noConnectivityDialog();
        }
    }

    public void checkLogin(){
        if(sharedData.isLoggedIn() && sharedData.isSignedUp()){
            Intent intent = new Intent(getApplicationContext(),ChangeCanteenActivity.class);
            startActivity(intent);
        }
    }

    void initialize() {
        mobile_no = (TextView) findViewById(R.id.mobile_text);
        enter_mobile = (EditText) findViewById(R.id.mobileInput);
        send_otp = (Button) findViewById(R.id.send_otp_button);
        back_button = (ImageButton) findViewById(R.id.back_button);
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        assignFonts();
    }

        public void assignFonts(){
        mobile_no.setTypeface(helper.comic);
        send_otp.setTypeface(helper.cambria);
    }



    @Override
    public void onBackPressed(){
        finish();
        intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(EnterMobile.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),EnterMobile.class);
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

    public void printDialog(String title_name, String content_text) {
        new MaterialDialog.Builder(EnterMobile.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }
}