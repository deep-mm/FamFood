package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.OrderHistoryAdapter;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderHistory extends AppCompatActivity {

    private SharedData sharedData;
    private Helper helper;
    private ImageButton backButton;
    private RecyclerView orderList;
    private RecyclerView.LayoutManager mlayoutmanager;
    private OrderUtility orderUtility;
    private List<OrderEntity> orderEntities;
    private MaterialDialog materialDialog;
    private Intent intent;
    private TextView noOrderText, page_title;
    private ImageView noOrderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initialize();

        if(getIntent().hasExtra("flag")){
            successDialog();
        }

        orderList.setLayoutManager(mlayoutmanager);
        orderList.setItemAnimator(new DefaultItemAnimator());

        orderEntities = sharedData.getOrderEntityList();
        if (orderEntities == null)
            orderEntities = new ArrayList<>();

        if (orderEntities.size() == 0) {
            noOrderText.setVisibility(View.VISIBLE);
            noOrderIcon.setVisibility(View.VISIBLE);
        } else {
            noOrderText.setVisibility(View.INVISIBLE);
            noOrderIcon.setVisibility(View.INVISIBLE);
        }
        //Collections.reverse(orderEntities);

        Collections.sort(orderEntities, new Comparator<OrderEntity>() {
            public int compare(OrderEntity o1, OrderEntity o2) {
                return getDate(o1.getOrder_time()).compareTo(getDate(o2.getOrder_time()));
            }
        });

        Collections.reverse(orderEntities);

        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(orderEntities, getApplicationContext());
        orderList.setAdapter(orderHistoryAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public Date getDate(String sDate1){
        Date date1= null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(sDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public void initialize() {
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        backButton = (ImageButton) findViewById(R.id.back_button);
        orderList = (RecyclerView) findViewById(R.id.order_history);
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        orderEntities = new ArrayList<>();
        noOrderText = (TextView) findViewById(R.id.no_order_text);
        noOrderIcon = (ImageView) findViewById(R.id.icon);
        page_title = (TextView) findViewById(R.id.order_history_title);
        assignFonts();
    }

    public void assignFonts() {
        noOrderText.setTypeface(helper.segoeprb);
        page_title.setTypeface(helper.cambriaBold);

    }

    public void onProgressStart() {
        materialDialog = new MaterialDialog.Builder(OrderHistory.this)
                .title("Signing In")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop() {
        materialDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getApplicationContext(),ItemActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLogin();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(OrderHistory.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), OrderHistory.class);
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

    public void checkLogin() {
        if (!sharedData.isSignedUp()) {
            sharedData.clear();
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void successDialog(){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Thank You")
                .setContentText("Thank you for your valuable feedback")
                .show();

    }
}
