package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Entities.AddressEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddNewAddress extends AppCompatActivity {
    private String[] CollegeNames = {"K.J. Somaiya College of Engineering", "SIMSR", "S.K. Somaiya College of Arts & Comm", "K.J. Somaiya Polytechnic"};
    private Spinner spin1;
    private EditText room_no, building, floor;
    private Button add_address;
    private String college_name;
    private String building_name;
    private String floor_no, room;
    private SharedData sharedData;
    private Helper helper;
    private UserUtility userUtility;
    private List <AddressEntity> addressEntityList;
    private CheckBox default_check;
    private Boolean default_check_value;
    private Boolean new_addr;
    private AddressEntity addressEntity;
    private ImageButton backButton;
    private TextView college_title, room_title, buliding_title, floor_title, page_title, default_title;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initialize();
        final Boolean check;

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, CollegeNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(aa);
        spin1.setOnItemSelectedListener(new CollegeNameSpinnerClass());

        if(getIntent().hasExtra("default_check"))
            check = getIntent().getExtras().getBoolean("default_check");
        else
            check = false;

        default_check.setChecked(check);
        default_check_value = check;

        if(getIntent().hasExtra("type"))
            new_addr = getIntent().getExtras().getBoolean("type");
        else
            new_addr = true;

        if(!new_addr){
            addressEntity = sharedData.getAddressEntity();
            building.setText(addressEntity.getBuilding_name());
            college_name = addressEntity.getCollege_name();
            floor.setText(addressEntity.getFloor_no());
            room_no.setText(addressEntity.getRoom_no());
            spin1.setSelection(getIndex(spin1, addressEntity.getCollege_name()));
            add_address.setText("Edit Address");
        }

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking for empty fields
                building_name = building.getText().toString();
                floor_no = floor.getText().toString();
                room = room_no.getText().toString();
                if(building_name.isEmpty() || floor_no.isEmpty() || college_name.isEmpty() || room.isEmpty()){
                    printDialog("Empty fields","Fields cannot be left empty");
                }
                else {
                    UserEntity userEntity = sharedData.getUserEntity();

                    if (userEntity.getAddresses() == null) {
                        addressEntityList = new ArrayList<>();
                    } else {
                        addressEntityList = userEntity.getAddresses();
                    }
                    String address_id;
                    if(!new_addr){
                        address_id = addressEntity.getAddress_id();
                        int pos = getPosition(addressEntity.getAddress_id());
                        addressEntityList.remove(pos);

                        AddressEntity addressEntity = new AddressEntity(address_id, true, college_name, building_name, floor_no, room);

                        if (default_check_value)
                            addressEntityList.add(0, addressEntity);
                        else
                            addressEntityList.add(pos, addressEntity);

                        helper.printToast("Address edited successfully",1);
                    }
                    else {
                        address_id = "address_" + userEntity.getFirebaseID() + "_"+(addressEntityList.size()+1);

                        AddressEntity addressEntity = new AddressEntity(address_id, true, college_name, building_name, floor_no, room);

                        if (default_check_value)
                            addressEntityList.add(0, addressEntity);
                        else
                            addressEntityList.add(addressEntity);
                    }

                    userEntity.setAddresses(addressEntityList);
                    sharedData.setUserEntity(userEntity);
                    userUtility.addUser(userEntity);
                    helper.printToast("Address added successfully",1);
                }

                Intent intent = new Intent(getApplicationContext(),Address.class);
                startActivity(intent);
            }
        });

        default_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                default_check_value = b;

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    class CollegeNameSpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           college_name = (String) parent.getItemAtPosition(position);

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    public void initialize(){
        spin1 = (Spinner)findViewById(R.id.college_name_spinner);
        room_no = (EditText) findViewById(R.id.room_no);
        add_address= (Button) findViewById(R.id.add_address_button);
        sharedData = new SharedData(getApplicationContext());
        userUtility = new UserUtility();
        building = (EditText) findViewById(R.id.building_name_edit);
        floor = (EditText) findViewById(R.id.floor_no_edit);
        helper = new Helper(getApplicationContext());
        default_check = (CheckBox) findViewById(R.id.default_checkBox);
        backButton = (ImageButton) findViewById(R.id.back_button);
        college_title = (TextView) findViewById(R.id.college_name);
        buliding_title = (TextView) findViewById(R.id.building_name);
        room_title = (TextView) findViewById(R.id.room_no_title);
        floor_title = (TextView) findViewById(R.id.floor_no);
        default_title = (TextView) findViewById(R.id.default_text);
        page_title = (TextView) findViewById(R.id.pageTitle);
        assignFonts();
    }

    public void assignFonts(){
        page_title.setTypeface(helper.cambriaBold);
        add_address.setTypeface(helper.cambria);
        college_title.setTypeface(helper.comic);
        buliding_title.setTypeface(helper.comic);
        room_title.setTypeface(helper.comic);
        floor_title.setTypeface(helper.comic);
        default_title.setTypeface(helper.comic);
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    public int getPosition(String id){
        for (int i=0;i<addressEntityList.size();i++){
            String addr_id = addressEntityList.get(i).getAddress_id();
            if(addr_id.equalsIgnoreCase(id))
                return i;
        }
        return 0;
    }

    @Override
    public void onBackPressed(){
        new MaterialDialog.Builder(AddNewAddress.this)
                .title("Confirm Exit")
                .content("Unsaved changes will be lost, are you sure?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(),Address.class);
                        sharedData.setAddrFlag(0);
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

    public void printDialog(String title_name, String content_text) {
        new MaterialDialog.Builder(AddNewAddress.this)
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
        if(!sharedData.isSignedUp()){
            sharedData.clear();
            intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(AddNewAddress.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),Address.class);
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

