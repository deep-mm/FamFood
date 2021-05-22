package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.CartItemAdapter;
import com.canteenautomation.famfood.Adapter.MenuItemAdapter;
import com.canteenautomation.famfood.Entities.ActiveTokenEntity;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Listener.GetTokenListener;
import com.canteenautomation.famfood.Listener.OnCanteenEntityChangeListener;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.CanteenUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.google.firebase.auth.FirebaseAuth;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CartActivity extends AppCompatActivity {

    private Helper helper;
    private SharedData sharedData;
    private ImageButton back_button;
    private RecyclerView itemList;
    private RecyclerView.LayoutManager mlayoutmanager;
    private TextView cartTotal, taxes, couponDisc, totalPayable;
    private TextView pageTitle, item_heading, payment_heading, cartTotal_title, taxes_title, coupon_title, total_title;
    private ArrayList<ItemEntity> itemEntities;
    private Button payButton;
    private List<ItemEntity> cartItemEntities;
    private OrderUtility orderUtility;
    private Intent intent;
    private OrderEntity orderEntity;
    private List<OrderItemEntitty> orderItemEntitties;
    private List<String> items;
    private Boolean jain;
    private FirebaseAuth firebaseAuth;
    private CanteenUtility canteenUtility;
    private MaterialDialog materialDialog;
    private Boolean disabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initialize();

        itemList.setLayoutManager(mlayoutmanager);
        itemList.setItemAnimator(new DefaultItemAnimator());

        List<ItemCategoryEntity> itemCategoryEntities = sharedData.getItemCategoryEntity();
        for(int i=0;i<itemCategoryEntities.size();i++){
            ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
            itemEntities.addAll(itemCategoryEntity.getItemEntities());
        }
        HashMap<String,Integer> cartItems = sharedData.getCart();

        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            String id = itemEntity.getItemId();
            if(cartItems.containsKey(id)) {
                cartItemEntities.add(itemEntity);
            }
        }

        CartItemAdapter cartItemAdapter = new CartItemAdapter(cartItemEntities, getApplicationContext(), new OnItemQuantityChange() {
            @Override
            public void onQuantityChange() {
                calcCartValue();
            }
        });
        itemList.setAdapter(cartItemAdapter);

        calcCartValue();

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (disabled) {
                            helper.printToast("Canteen in currently not accepting orders",1);
                        } else {
                            new MaterialDialog.Builder(CartActivity.this)
                                    .title("Confirm Order")
                                    .content("Go ahead with the payment?")
                                    .positiveText("Yes")
                                    .negativeText("No")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            if (sharedData.isLoggedIn() == false) {
                                                userNotLoggedIn();
                                            } else {
                                                newOrder();
                                                canteenUtility.getCanteen(sharedData.getCanteenEntity().getId(), new OnCanteenEntityChangeListener() {
                                                    @Override
                                                    public void onDataChanged(CanteenEntity canteenEntity) {
                                                        OrderEntity orderEntity = sharedData.getOrderEntity();
                                                        int token = 0;
                                                        if (canteenEntity.getActiveTokenEntity() == null) {
                                                            ActiveTokenEntity activeTokenEntity = new ActiveTokenEntity(1);
                                                            canteenEntity.setActiveTokenEntity(activeTokenEntity);
                                                            canteenUtility.addCanteen(canteenEntity);
                                                            token = 1;
                                                        } else {
                                                            token = canteenEntity.getActiveTokenEntity().getActiveToken();
                                                        }
                                                        orderEntity.setOrder_token(token);
                                                        int newToken = token + 1;
                                                        if (newToken == 1000)
                                                            newToken = 1;

                                                        ActiveTokenEntity activeTokenEntity = new ActiveTokenEntity(newToken);
                                                        canteenEntity.setActiveTokenEntity(activeTokenEntity);
                                                        canteenUtility.addCanteen(canteenEntity);
                                                        sharedData.setOrderEntity(orderEntity);
                                                        String userType = sharedData.getUserEntity().getUserType();
                                                        if (userType.equalsIgnoreCase("Faculty")) {
                                                            intent = new Intent(getApplicationContext(), Address.class);
                                                            sharedData.setAddrFlag(1);
                                                            startActivity(intent);
                                                        } else {
                                                            intent = new Intent(getApplicationContext(), PaytmPayment.class);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onErrorOccured() {

                                                    }
                                                });
                                            }
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
                    }
                });

    }

    public void userNotLoggedIn(){
        new MaterialDialog.Builder(CartActivity.this)
                .title("Login Required")
                .content("Login to the app to continue..")
                .positiveText("Login")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        firebaseAuth = FirebaseAuth.getInstance();
                        if(firebaseAuth!=null)
                            firebaseAuth.signOut();
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(true)
                .show();
    }

    public void newOrder(){
        String canteenId = sharedData.getCanteenEntity().getId();
        String userId = sharedData.getUserEntity().getFirebaseID();
        HashMap<String,Integer> cartItems = sharedData.getCart();
        List<String> statusUpdateTime = new ArrayList<>();
        statusUpdateTime.add(0,helper.currentTime());

        items = sharedData.getJainItems();
        if(items==null)
            items = new ArrayList<>();

        for (int i=0;i<cartItemEntities.size();i++){
            ItemEntity itemEntity = cartItemEntities.get(i);
            jain = false;
            if(items.contains(itemEntity.getItemId()))
                jain = true;
            OrderItemEntitty orderItemEntitty = new OrderItemEntitty(itemEntity.getItemId(),itemEntity.getItemName(),cartItems.get(itemEntity.getItemId()),0,jain,statusUpdateTime,helper.currentClock());
            orderItemEntitties.add(orderItemEntitty);
        }
        StringBuffer totalPay = new StringBuffer(totalPayable.getText().toString());
        orderEntity = new OrderEntity(canteenId, "Order_"+helper.currentDateTime()+"_"+userId, orderItemEntitties, totalPay.substring(2), helper.currentTime(),userId);
        sharedData.setOrderEntity(orderEntity);
    }

    public int generateToken(){
        Random random = new Random();
        int max = 999;
        int min = 100;
        final int rand = random.nextInt(max - min + 1) + min;
        return rand;
    }

    public void calcCartValue(){
        List<ItemEntity> cartItems = getCartItems();
        Double cartTotalVal = calcTotal(cartItems);
        Double taxesVal = 0.05*cartTotalVal; //TODO: Determine how to calc
        Double couponDiscVal = 0.0; //TODO: Later Stage
        Double total = cartTotalVal + taxesVal - couponDiscVal;
        cartTotal.setText("₹ "+String.valueOf(cartTotalVal));
        String tax = String.format("%.2f", taxesVal);
        taxes.setText("₹ "+tax);
        couponDisc.setText("₹ "+String.valueOf(couponDiscVal));
        totalPayable.setText("₹ "+String.valueOf(total));
        if(cartTotalVal<=0){
            helper.printToast("Cart Empty",1);
            onBackPressed();
        }
    }

    public Double calcTotal(List<ItemEntity> itemEntities){
        HashMap<String,Integer> cartItems = sharedData.getCart();
        Double total = 0.0;
        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            Double price = Double.valueOf(itemEntity.getPrice());
            int quant = cartItems.get(itemEntity.getItemId());
            Double totalPr = price*quant;
            total = total + totalPr;
        }
        return total;
    }

    public List<ItemEntity> getCartItems(){
        HashMap<String,Integer> items = sharedData.getCart();
        List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            if(items.containsKey(itemEntity.getItemId()))
                itemEntityList.add(itemEntity);
        }
        return itemEntityList;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void initialize(){
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        back_button = (ImageButton) findViewById(R.id.back_button);
        itemList = (RecyclerView) findViewById(R.id.cart_recycler);
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        cartTotal = (TextView) findViewById(R.id.cart_total);
        taxes = (TextView) findViewById(R.id.taxes);
        couponDisc = (TextView) findViewById(R.id.coupon_disc);
        totalPayable = (TextView) findViewById(R.id.total_payable);
        itemEntities = new ArrayList<ItemEntity>();
        payButton = (Button) findViewById(R.id.pay_button);
        cartItemEntities = new ArrayList<>();
        orderItemEntitties = new ArrayList<>();

        if(sharedData.getUserEntity()!=null)
            orderUtility = new OrderUtility(sharedData.getUserEntity().getFirebaseID());

        pageTitle = (TextView) findViewById(R.id.pageTitle);
        item_heading = (TextView) findViewById(R.id.items_text);
        payment_heading = (TextView) findViewById(R.id.payment_text);
        cartTotal_title = (TextView) findViewById(R.id.cart_total_text);
        taxes_title = (TextView) findViewById(R.id.taxes_text);
        coupon_title = (TextView) findViewById(R.id.coupon_text);
        total_title = (TextView) findViewById(R.id.total_payable_text);
        assignFonts();
        canteenUtility = new CanteenUtility();
        checkCanteenStatus();
    }

    public void assignFonts(){
        pageTitle.setTypeface(helper.cambriaBold);
        payButton.setTypeface(helper.cambria);
        item_heading.setTypeface(helper.comicBold);
        payment_heading.setTypeface(helper.comicBold);
        cartTotal_title.setTypeface(helper.comic);
        taxes_title.setTypeface(helper.comic);
        coupon_title.setTypeface(helper.comic);
        total_title.setTypeface(helper.comic);
        cartTotal.setTypeface(helper.comic);
        taxes.setTypeface(helper.comic);
        couponDisc.setTypeface(helper.comic);
        totalPayable.setTypeface(helper.comic);
    }

    public void checkCanteenStatus(){
        canteenUtility.getCanteen(sharedData.getCanteenEntity().getId(), new OnCanteenEntityChangeListener() {
            @Override
            public void onDataChanged(CanteenEntity canteenEntity) {
                sharedData.setCanteenEntity(canteenEntity);
                if(canteenEntity.getStatus()==0){
                    payButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.disabled_signup_button));
                    disabled = true;

                }
                else{
                    payButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.signup_button));
                    disabled = false;
                }
            }

            @Override
            public void onErrorOccured() {

            }
        });
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
        new MaterialDialog.Builder(CartActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),CartActivity.class);
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

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(CartActivity.this)
                .title("Syncing Data")
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
