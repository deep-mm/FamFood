package com.canteenautomation.famfood.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.UserMobileListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button registerButton;
    private SharedData sharedData;
    private Helper helper;
    private String fromActivity;
    private Intent intent;
    private ArrayAdapter spinnerGender;
    private int selected = 4;
    private Spinner spin;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar myCalendar;
    private ImageButton backButton;
    private EditText fullName, mobile, dateText, email, password, confirmPass;
    private TextView signup_text;
    private String fullNameText, mobileText, dobText, emailText, passwordText, confirmPassText, genderText;
    private Date date2;
    private LinearLayout emailLayout, passwordLayout, confirmPassLayout;
    private UserUtility userUtility;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialize();

        if(getIntent().hasExtra("fromActivity")){
            fromActivity = getIntent().getExtras().getString("fromActivity");
        }

        if (getIntent().hasExtra("fromActivity")) {
            if (getIntent().getExtras().getString("fromActivity").equals("MobileSignin")) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                mobile.setText(firebaseUser.getPhoneNumber());
                emailElementsInvisible();

            } else if (getIntent().getExtras().getString("fromActivity").equals("EmailSignin")) {
                emailElementsVisible();

            } else {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                emailElementsInvisible();
                emailLayout.setVisibility(View.VISIBLE);
                email.setText(firebaseUser.getEmail());
                email.setFocusable(false);
                fullName.setText(firebaseUser.getDisplayName());
            }
        }

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -10);
                calendar.add(Calendar.MONTH, Calendar.JANUARY);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                long upperLimit = calendar.getTimeInMillis();
                datePickerDialog.getDatePicker().setMaxDate(upperLimit);
                datePickerDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                onProgressStart();
                if (getValues()) {
                    mobileText = "+91"+mobileText;
                    userUtility.checkMobileExists(mobileText, new UserMobileListener() {
                        @Override
                        public void Auth(boolean check) {
                            if (check) {
                                onProgressStop();
                                mobileExists("Account already exists","Mobile already linked to another account, do you want to login using this mobile?",mobileText);
                            } else {
                                if (fromActivity.equals("EmailSignin")) {
                                        createAccount(emailText, passwordText);
                                } else if (fromActivity.equals("MobileSignin")) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    emailText = "";
                                    StringBuffer mobile = new StringBuffer(mobileText);
                                    mobileText = mobile.substring(3);
                                        UserEntity userEntity = new UserEntity(firebaseUser.getUid(), helper.currentTime(), "Student", genderText, dobText, mobileText, emailText, fullNameText);
                                        userUtility.addUser(userEntity);
                                        sharedData.isSignedUp(true);
                                        sharedData.setUserEntity(userEntity);
                                        onProgressStop();
                                        intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                                        startActivity(intent);
                                } else {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String mobile = mobileText;
                                        UserEntity userEntity = new UserEntity(firebaseUser.getUid(), helper.currentTime(), "Student", genderText, dobText, mobileText, emailText, fullNameText);
                                        sharedData.setUserEntity(userEntity);
                                        intent = new Intent(getApplicationContext(), EnterOtp.class);
                                        intent.putExtra("mobile", mobile);
                                        intent.putExtra("fromActivity", true);
                                        onProgressStop();
                                        startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onErrorOccured() {
                            onProgressStop();
                        }
                    });

                }
                else{
                    onProgressStop();
                    //helper.printToast("Mobile field cannot be left empty",1);
                }
            }
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        String gender[] = {"Select Gender","Male","Female","Others"};

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.activity_signup,gender){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerGender = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gender);
        spinnerGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(spinnerGender);
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

    private boolean isValidMobile(String phone) {
        if(phone.length()==10)
            return android.util.Patterns.PHONE.matcher(phone).matches();
        else
            return false;
    }

    public boolean getValues(){
        fullNameText = fullName.getText().toString();
        mobileText = mobile.getText().toString();
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
        confirmPassText = confirmPass.getText().toString();
        dobText = dateText.getText().toString();
        String gender[] = {"Select Gender","Male","Female","Others"};
        genderText = gender[selected];
        if(fullNameText.length()<2){
            printDialog("Invalid Name","Please enter your full name");
        }
        else if(!isValidMobile(mobileText)){
            printDialog("Invalid Mobile","Please enter a valid mobile number");
        }
        else if(!isValidEmail(emailText) && emailLayout.getVisibility()==View.VISIBLE){
            printDialog("Invalid Email", "Please enter a valid email-id");
        }

        else if(isFieldEmpty(passwordText) && passwordLayout.getVisibility()==View.VISIBLE){
            printDialog("Invalid Password", "Password field cannot be left empty");
        }
        else if(passwordText.length()<8 && passwordLayout.getVisibility()==View.VISIBLE){
            printDialog("Invalid Password", "Password must be minimum 8 characters");
        }
        else if(!confirmPassText.equals(passwordText) && passwordLayout.getVisibility()==View.VISIBLE){
            printDialog("Invalid Confirm Password", "Confirm password must same as password");
        }
        else{
            return true;
        }
        return false;

    }

    public void emailElementsInvisible(){
        emailLayout.setVisibility(View.INVISIBLE);
        passwordLayout.setVisibility(View.INVISIBLE);
        confirmPassLayout.setVisibility(View.INVISIBLE);
    }

    public void emailElementsVisible(){
        emailLayout.setVisibility(View.VISIBLE);
        passwordLayout.setVisibility(View.VISIBLE);
        confirmPassLayout.setVisibility(View.VISIBLE);
    }

    public void initialize() {
        signup_text = (TextView) findViewById(R.id.signup_text);
        registerButton = (Button) findViewById(R.id.register_button);
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        spin = (Spinner) findViewById(R.id.spinner_gender);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }
        };
        backButton = (ImageButton) findViewById(R.id.back_button);
        fullName = (EditText) findViewById(R.id.name_text);
        mobile = (EditText) findViewById(R.id.mobile_text);
        email = (EditText) findViewById(R.id.email_text);
        password = (EditText) findViewById(R.id.password_text);
        confirmPass = (EditText) findViewById(R.id.confirmPass_text);
        dateText = (EditText) findViewById(R.id.dob_text);
        emailLayout = (LinearLayout) findViewById(R.id.email);
        passwordLayout = (LinearLayout) findViewById(R.id.password);
        confirmPassLayout = (LinearLayout) findViewById(R.id.confirmPass);
        userUtility = new UserUtility();
        assignFonts();
    }

    public void assignFonts(){
        signup_text.setTypeface(helper.cambriaBold);
        registerButton.setTypeface(helper.cambria);
    }

    private void updateLabel(Calendar myCalendar) {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date2 = myCalendar.getTime();
        dateText.setText(sdf.format(date2));
    }

    private void createAccount(String email, String password) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendEmailVerification();
                        } else {
                            error("Sign up failed", "Please try again");
                        }
                    }
                });
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessageDialog("Verify Email", "Verification email sent to " + user.getEmail() + ". Verify using the link provided in the mail and re-login into the app ");
                        } else {
                            error("Sign up failed", "Please try again");
                        }
                    }
                });
    }

    public void error(String title_name, String content_text) {
        new MaterialDialog.Builder(SignupActivity.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mAuth.signOut();
                        sharedData.isLoggedIn(false);
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void showMessageDialog(String title_name, String content_text) {
        new MaterialDialog.Builder(SignupActivity.this)
                .title(title_name)
                .content(content_text)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        String mobile = mobileText;
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserEntity userEntity = new UserEntity(firebaseUser.getUid(), helper.currentTime(), "Student", genderText, dobText, mobileText, emailText, fullNameText);
                        sharedData.setUserEntity(userEntity);
                        intent = new Intent(getApplicationContext(), EnterOtp.class);
                        intent.putExtra("mobile", mobile);
                        intent.putExtra("fromActivity", true);
                        onProgressStop();
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }


    public void noConnectivityDialog() {
        new MaterialDialog.Builder(SignupActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),SignupActivity.class);
                        intent.putExtra("fromActivity",fromActivity);
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

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(SignupActivity.this)
                .title("Are you sure?")
                .content("All data will be lost. Confirm exit?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        mAuth.signOut();
                        sharedData.isLoggedIn(false);
                        sharedData.isSignedUp(false);
                        intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(SignupActivity.this)
                .title("Signing In")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void printDialog(String title_name, String content_text) {
        new MaterialDialog.Builder(SignupActivity.this)
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

    public void mobileExists(final String title_name, final String content_text, final String mobileNumber){
        new MaterialDialog.Builder(SignupActivity.this)
                .title(title_name)
                .content(content_text)
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), EnterOtp.class);
                        intent.putExtra("mobile", mobileNumber);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mobile.setText("");
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop(){
        materialDialog.dismiss();
    }
}
