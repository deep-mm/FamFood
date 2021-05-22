package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetUserListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class EnterOtp extends AppCompatActivity {

    private TextView otp_text, mobile_otp_no, no_otp, resend_code,enter_otp_text;
    private Button verify;
    private ImageButton edit_pen;
    private String enteredPin;
    private FirebaseAuth mAuth;
    private String mobile;
    private SharedData sharedData;
    private Helper helper;
    private boolean flag = false;
    private UserUtility userUtility;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Intent intent;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        initialize();
        mobileLogin();

        if(getIntent().hasExtra("mobile")){
            mobile = getIntent().getExtras().getString("mobile");
            mobile_otp_no.setText(mobile);
        }
        if(getIntent().hasExtra("fromActivity")){
            flag = true;
        }

        startPhoneNumberVerification(mobile);

        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.pin_blanks);
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    enteredPin = str.toString();
                }
            });
        }

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enteredPin!=null) {
                    onProgressStart();
                    verifyPhoneNumberWithCode(mVerificationId, enteredPin);
                }

                else
                    showMessageDialog("Invalid Pin","Please enter valid OTP sent to your mobile");
            }
        });

        edit_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(mobile,mResendToken);
            }
        });

    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks

        //showMessageDialog("Code Resent", "OTP sent on the entered mobile number");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if(flag) {
            onProgressStart();
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                sharedData.setFirebaseUser(user);
                                sharedData.isLoggedIn(true);
                                sharedData.isSignedUp(true);
                                //UserEntity userEntity = new UserEntity(firebaseUser.getUid(), helper.currentTime(), genderText, dobText, mobileText, emailText);
                                UserEntity userEntity = sharedData.getUserEntity();
                                userUtility.addUser(userEntity);
                                onProgressStop();
                                intent = new Intent(getApplicationContext(),ChangeCanteenActivity.class);
                                startActivity(intent);
                            } else {
                                onProgressStop();
                                showMessageDialog("Mobile authentication failed", "Invalid OTP");
                            }

                            // ...
                        }
                    });
        }

        else {
            onProgressStart();
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                sharedData.setFirebaseUser(user);
                                sharedData.isLoggedIn(true);
                                userUtility.getUser(user.getUid(), new GetUserListener() {
                                    @Override
                                    public void onCompleteTask(UserEntity userEntity) {
                                        if(userEntity==null){
                                            onProgressStop();
                                            intent = new Intent(getApplicationContext(), SignupActivity.class);
                                            intent.putExtra("fromActivity","MobileSignin");
                                            startActivity(intent);
                                        }
                                        else{
                                            sharedData.isSignedUp(true);
                                            sharedData.setUserEntity(userEntity);
                                            onProgressStop();
                                            intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onErrorOccured() {
                                        onProgressStop();
                                        error("Mobile authentication failed", "Error Occured");
                                    }
                                });
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    showMessageDialog("Mobile authentication failed", "Invalid OTP");
                                }
                            }
                        }
                    });
        }
    }

    public void mobileLogin(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    error("Mobile authentication failed", "Invalid credentials");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    error("Mobile authentication failed", "Something went wrong.. Please try again");
                }
                else{
                    error("Mobile authentication failed", "Something went wrong.. Please try again");
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                //showMessageDialog("Code sent", "OTP sent on the entered mobile number");
            }
        };
    }

    public void initialize(){
        otp_text= (TextView) findViewById(R.id.otp_text);
        mobile_otp_no = (TextView) findViewById(R.id.otp_mobile_text);
        no_otp = (TextView) findViewById(R.id.didntRec_text);
        verify = (Button) findViewById(R.id.verify_button);
        edit_pen=(ImageButton) findViewById(R.id.edit_mobile);
        resend_code= (TextView) findViewById(R.id.Resend);
        enter_otp_text= (TextView) findViewById(R.id.enter_otp_text) ;
        mAuth = FirebaseAuth.getInstance();
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        userUtility = new UserUtility();
        assignFonts();
    }

    public void assignFonts(){
        otp_text.setTypeface(helper.comic);
        mobile_otp_no.setTypeface(helper.comicBold);
        enter_otp_text.setTypeface(helper.cambria);
        no_otp.setTypeface(helper.comic);
        resend_code.setTypeface(helper.segoeprb);
        verify.setTypeface(helper.cambria);
    }

    public void error(String title_name, String content_text) {
        new MaterialDialog.Builder(EnterOtp.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mAuth.signOut();
                        sharedData.isLoggedIn(false);
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void showMessageDialog(String title_name, String content_text) {
        new MaterialDialog.Builder(EnterOtp.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    @Override
    public void onStart(){
        super.onStart();

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

        if(!helper.isNetworkAvailable()){
            noConnectivityDialog();
        }
    }


    public void noConnectivityDialog() {
        new MaterialDialog.Builder(EnterOtp.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        onBackPressed();
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

    @Override
    public void onBackPressed(){
        new MaterialDialog.Builder(EnterOtp.this)
                .title("Exit")
                .content("Are you sure you want to cancel?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        EnterOtp.super.onBackPressed();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(EnterOtp.this)
                .title("Signing In")
                .content("Please Wait")
                .progress(true, 0)
                .show();
    }

    public void onProgressStop(){
        materialDialog.dismiss();
    }

}
