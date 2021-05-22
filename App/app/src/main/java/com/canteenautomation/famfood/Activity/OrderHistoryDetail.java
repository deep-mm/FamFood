package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.CartItemAdapter;
import com.canteenautomation.famfood.Adapter.OrderItemAdapter;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderHistoryItemEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistoryDetail extends AppCompatActivity {

    private Helper helper;
    private SharedData sharedData;
    private ImageButton backButton;
    private RecyclerView itemList;
    private LinearLayoutManager mlayoutmanager;
    private List<OrderHistoryItemEntity> orderHistoryItemEntityList;
    private List<ItemEntity> itemEntities;
    private TextView orderTotal, taxesTotal, couponDisc, totalAmount;
    private TextView pageTitle, item_heading, payment_heading, cartTotal_title, taxes_title, coupon_title, total_title;
    private double total;
    private Button reorderButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_order_history_detail)
                .setSwipeBackView(R.layout.swipeback_default);

        initialize();

        itemList.setLayoutManager(mlayoutmanager);
        itemList.setItemAnimator(new DefaultItemAnimator());

        OrderEntity orderEntity = sharedData.getOrderEntity();
        final List<OrderItemEntitty> orderItemEntitties = orderEntity.getOrderItemEntitty();

        List<ItemCategoryEntity> itemCategoryEntities = sharedData.getItemCategoryEntity();
        for(int i=0;i<itemCategoryEntities.size();i++){
            ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
            itemEntities.addAll(itemCategoryEntity.getItemEntities());
        }

        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
            String id = orderItemEntitty.getItemId();
            ItemEntity itemEntity = findItemEntity(id);
            OrderHistoryItemEntity orderHistoryItemEntity = new OrderHistoryItemEntity(orderItemEntitty,itemEntity);
            orderHistoryItemEntityList.add(orderHistoryItemEntity);
            float price = Float.valueOf(itemEntity.getPrice())*orderItemEntitty.getQuantity();
            total = total + price;
        }
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(orderHistoryItemEntityList, getApplicationContext());
        itemList.setAdapter(orderItemAdapter);

        cartTotal();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        reorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Integer> cart = new HashMap<>();
                for(int i=0;i<orderItemEntitties.size();i++){
                    OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
                    cart.put(orderItemEntitty.getItemId(),orderItemEntitty.getQuantity());
                }
                sharedData.setCart(cart);
                intent = new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);
            }
        });
    }

    void cartTotal(){
        orderTotal.setText("₹ "+String.valueOf(total));
        Double taxesVal = 0.05*total; //TODO: Determine how to calc
        String tax = String.format("%.2f", taxesVal);
        taxesTotal.setText("₹ "+tax);
        couponDisc.setText("₹ "+"0.0");
        total = total + taxesVal;
        totalAmount.setText("₹ "+String.valueOf(total));
    }

    void initialize(){
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        backButton = (ImageButton) findViewById(R.id.back_button);
        itemList = (RecyclerView) findViewById(R.id.order_recycler);
        itemEntities = new ArrayList<>();
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        orderHistoryItemEntityList = new ArrayList<>();
        orderTotal = (TextView) findViewById(R.id.order_total);
        taxesTotal = (TextView) findViewById(R.id.taxes);
        couponDisc = (TextView) findViewById(R.id.coupon_disc);
        totalAmount = (TextView) findViewById(R.id.total_payable);
        reorderButton = (Button) findViewById(R.id.reorder_button);
        pageTitle = (TextView) findViewById(R.id.pageTitle);
        item_heading = (TextView) findViewById(R.id.items_text);
        payment_heading = (TextView) findViewById(R.id.payment_text);
        cartTotal_title = (TextView) findViewById(R.id.cart_total_text);
        taxes_title = (TextView) findViewById(R.id.taxes_text);
        coupon_title = (TextView) findViewById(R.id.coupon_text);
        total_title = (TextView) findViewById(R.id.total_payable_text);
        assignFonts();
    }

    public void assignFonts(){
        pageTitle.setTypeface(helper.cambriaBold);
        reorderButton.setTypeface(helper.cambria);
        item_heading.setTypeface(helper.comicBold);
        payment_heading.setTypeface(helper.comicBold);
        cartTotal_title.setTypeface(helper.comic);
        taxes_title.setTypeface(helper.comic);
        coupon_title.setTypeface(helper.comic);
        total_title.setTypeface(helper.comic);
        orderTotal.setTypeface(helper.comic);
        taxesTotal.setTypeface(helper.comic);
        couponDisc.setTypeface(helper.comic);
        totalAmount.setTypeface(helper.comic);
    }

    public ItemEntity findItemEntity(String id){

        for(int i=0;i<itemEntities.size();i++){
            ItemEntity itemEntity = itemEntities.get(i);
            if(itemEntity.getItemId().equals(id)){
                return itemEntity;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
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
        new MaterialDialog.Builder(OrderHistoryDetail.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(),OrderHistoryDetail.class);
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
