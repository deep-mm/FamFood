package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetUserListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private TextView emailText, passwordText, otpText, forgotPasswordText, noLoginText, signUpText, newUserText;
    private Button loginButton;
    private ImageButton googleButton, facebookButton;
    private CheckBox otpCheckBox;
    private EditText emailEditText, passwordEditText;
    private Helper helper;
    private SharedData sharedData;
    private UserUtility userUtility;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private MaterialDialog materialDialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initalize();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(isValidEmail(email) && !isFieldEmpty(password)) {
                    emailLogin(email, password);
                }
                else if(!isValidEmail(email)){
                    printDialog("Invalid Email","Please enter a valid email-id");
                }
                else if(isFieldEmpty(password)){
                    printDialog("Invalid Password","Password field cannot be left empty");
                }
                else{
                    printDialog("Error occured","Unexpected error has occured :(");
                }

            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });

        otpCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), EnterMobile.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        noLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anonymousLogin();
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), SignupActivity.class);
                intent.putExtra("fromActivity","EmailSignin");
                startActivity(intent);
            }
        });
    }

    public boolean isFieldEmpty(String value){
        if(value.isEmpty())
            return true;

        else
            return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    void initalize() {
        emailText = (TextView) findViewById(R.id.email_text);
        passwordText = (TextView) findViewById(R.id.password_text);
        otpText = (TextView) findViewById(R.id.otp_text);
        forgotPasswordText = (TextView) findViewById(R.id.forgotPassword);
        noLoginText = (TextView) findViewById(R.id.no_login_text);
        signUpText = (TextView) findViewById(R.id.sign_up_button);
        newUserText = (TextView) findViewById(R.id.new_user_text);
        loginButton = (Button) findViewById(R.id.login_button);
        googleButton = (ImageButton) findViewById(R.id.google_button);
        facebookButton = (ImageButton) findViewById(R.id.facebook_login);
        otpCheckBox = (CheckBox) findViewById(R.id.otp_checkBox);
        userUtility = new UserUtility();
        emailEditText = (EditText) findViewById(R.id.email_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        assignFonts();
    }

    public void assignFonts(){
        emailText.setTypeface(helper.comic);
        passwordText.setTypeface(helper.comic);
        otpText.setTypeface(helper.comic);
        forgotPasswordText.setTypeface(helper.comic);
        noLoginText.setTypeface(helper.comic);
        signUpText.setTypeface(helper.comic);
        newUserText.setTypeface(helper.comic);
        loginButton.setTypeface(helper.cambria);
    }

    public void anonymousLogin(){
        onProgressStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        onProgressStop();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedData.isLoggedIn(false);
                            intent = new Intent(getApplicationContext(),ChangeCanteenActivity.class);
                            startActivity(intent);
                        } else {
                            printDialog("Facebook login failed", "Something went wrong.. Please try again");
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                printDialog("Google login failed", "Something went wrong.. Please try again");
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        onProgressStart();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedData.setFirebaseUser(user);
                            sharedData.isLoggedIn(true);
                            userUtility.getUser(user.getUid(), new GetUserListener() {
                                @Override
                                public void onCompleteTask(UserEntity userEntity) {
                                    onProgressStop();
                                    if(userEntity==null){
                                        intent = new Intent(getApplicationContext(), SignupActivity.class);
                                        intent.putExtra("fromActivity","FacebookSignin");
                                        startActivity(intent);
                                    }
                                    else{
                                        sharedData.isSignedUp(true);
                                        sharedData.setUserEntity(userEntity);
                                        intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onErrorOccured() {
                                    printDialog("Facebook login failed", "Something went wrong.. Please try again");
                                    onProgressStop();
                                }
                            });
                        } else {
                            printDialog("Facebook login failed", "Something went wrong.. Please try again");
                            onProgressStop();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        onProgressStart();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedData.setFirebaseUser(user);
                            sharedData.isLoggedIn(true);
                            userUtility.getUser(user.getUid(), new GetUserListener() {
                                @Override
                                public void onCompleteTask(UserEntity userEntity) {
                                    onProgressStop();
                                    if(userEntity==null){
                                        intent = new Intent(getApplicationContext(), SignupActivity.class);
                                        intent.putExtra("fromActivity","GoogleSignin");
                                        startActivity(intent);
                                    }
                                    else{
                                        sharedData.isSignedUp(true);
                                        sharedData.setUserEntity(userEntity);
                                        intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onErrorOccured() {
                                    printDialog("Google login failed", "Something went wrong.. Please try again");
                                    onProgressStop();
                                }
                            });

                        } else {
                            printDialog("Google login failed", "Something went wrong.. Please try again");
                            onProgressStop();
                        }
                    }
                });
    }

    public void googleLogin() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebookLogin() {

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                printDialog("Facebook login cancelled", "Something went wrong.. Please try again");
            }

            @Override
            public void onError(FacebookException error) {
                printDialog("Facebook login failed", "Something went wrong.. Please try again");
            }
        });

    }

    private void emailLogin(String email, String password) {

        onProgressStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sharedData.setFirebaseUser(user);
                            sharedData.isLoggedIn(true);
                            userUtility.getUser(user.getUid(), new GetUserListener() {
                                @Override
                                public void onCompleteTask(UserEntity userEntity) {
                                    onProgressStop();
                                    if(userEntity==null){
                                        intent = new Intent(getApplicationContext(), SignupActivity.class);
                                        intent.putExtra("fromActivity","EmailSignin");
                                        startActivity(intent);
                                    }
                                    else{
                                        sharedData.isSignedUp(true);
                                        sharedData.setUserEntity(userEntity);
                                        intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onErrorOccured() {
                                    printDialog("Email login failed","Invalid Credentials");
                                    onProgressStop();
                                }
                            });
                        } else {
                            printDialog("Email login failed","Invalid Credentials");
                            onProgressStop();
                        }

                    }
                });
    }

    private void forgotPassword() {
        String email = emailEditText.getText().toString();
        if(isValidEmail(email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                printDialog("Password Reset", "Email sent to reset your password. " +
                                        "Reset your password using the link sent on your mail");
                            }
                        }
                    });
        }
        else{
            helper.printToast("Enter a valid email",1);
        }
    }



    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(LoginActivity.this)
                .title("Exit")
                .content("Are you sure you want to exit?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        finishAffinity();
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
            intent = new Intent(getApplicationContext(),ChangeCanteenActivity.class);
            startActivity(intent);
        }
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(LoginActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
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
        new MaterialDialog.Builder(LoginActivity.this)
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

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                .title("Signing In")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop(){
        materialDialog.dismiss();
    }
}