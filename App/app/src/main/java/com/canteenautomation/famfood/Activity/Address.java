package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.canteenautomation.famfood.Adapter.AddressAdapter;
import com.canteenautomation.famfood.Entities.AddressEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Address extends AppCompatActivity {
    private List<AddressEntity> addressEntities;
    private RecyclerView mRecyclerView1;
    private AddressAdapter mAdapter;
    private SharedData sharedData;
    private UserUtility userUtility;
    private RelativeLayout addressBox;
    private TextView address, def_title, other_title, page_title;
    private Button addAddress;
    private Intent intent;
    private ImageButton backButton;
    private AddressEntity default_addr;
    private RippleView rippleView;
    private OrderUtility orderUtility;
    private Helper helper;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initialize();

        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView1.setItemAnimator( new DefaultItemAnimator());

        UserEntity userEntity = sharedData.getUserEntity();
        if(userEntity.getAddresses()==null){
            addressEntities= new ArrayList<>();
            userEntity.setAddresses(addressEntities);
            sharedData.setUserEntity(userEntity);
            userUtility.addUser(userEntity);
        }else{
            addressEntities = userEntity.getAddresses();
        }

        if(addressEntities.size()>=1){
            default_addr = addressEntities.get(0);
            address.setText(default_addr.getCollege_name()+ "\nBuilding: " + default_addr.getBuilding_name()+ "\nFloor: " + default_addr.getFloor_no() + "\nRoom: " + default_addr.getRoom_no());
            addressEntities.remove(0);
        }

        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedData.setAddressEntity(default_addr);
                final String addr = default_addr.getCollege_name()+ "\nBuilding: " + default_addr.getBuilding_name()+ "\nFloor: " + default_addr.getFloor_no() + "\nRoom: " + default_addr.getRoom_no();
                if(flag==1){
                    new MaterialDialog.Builder(Address.this)
                            .title("Confirm Address")
                            .content(addr)
                            .positiveText("Yes")
                            .negativeText("No")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    OrderEntity orderEntity = sharedData.getOrderEntity();
                                    orderEntity.setDelivery_add(addr);
                                    sharedData.setOrderEntity(orderEntity);
                                    intent = new Intent(getApplicationContext(),PaytmPayment.class);
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
                else {
                    Intent intent = new Intent(getApplicationContext(), AddNewAddress.class);
                    intent.putExtra("default_check", true);
                    intent.putExtra("type", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            }
        });

        mAdapter = new AddressAdapter(addressEntities,getApplicationContext());
        mRecyclerView1.setAdapter(mAdapter);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),AddNewAddress.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void initialize(){
        sharedData = new SharedData(getApplicationContext());
        userUtility = new UserUtility();
        mRecyclerView1 = (RecyclerView) findViewById(R.id.address_recycler);
        addressBox = (RelativeLayout) findViewById(R.id.addr_box);
        address = (TextView) findViewById(R.id.address);
        addAddress = (Button) findViewById(R.id.add_address_button);
        backButton = (ImageButton) findViewById(R.id.back_button);
        rippleView = (RippleView) findViewById(R.id.ripple);
        helper = new Helper(getApplicationContext());
        def_title = (TextView) findViewById(R.id.def_add_title);
        other_title = (TextView) findViewById(R.id.other_add_title);
        page_title = (TextView) findViewById(R.id.pageTitle);
        flag = sharedData.getAddrFlag();
        assignFonts();
    }

    public void assignFonts(){
        address.setTypeface(helper.comic);
        def_title.setTypeface(helper.comicBold);
        other_title.setTypeface(helper.comicBold);
        page_title.setTypeface(helper.cambriaBold);
        addAddress.setTypeface(helper.cambria);
    }

    @Override
    public void onBackPressed(){
        if(flag==1) {
            super.onBackPressed();
        }
        else{
            intent = new Intent(getApplicationContext(), ItemActivity.class);
            startActivity(intent);
        }
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
        new MaterialDialog.Builder(Address.this)
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
