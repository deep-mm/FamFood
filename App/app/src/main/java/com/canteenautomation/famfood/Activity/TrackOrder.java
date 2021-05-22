package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.TrackOrderItemAdapter;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.DeliveryManEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetOrderListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TrackOrder extends AppCompatActivity {

    private Helper helper;
    private SharedData sharedData;
    private TextView page_title, canteenName, orderDateTime, itemsTitle, totalPriceTitle, totalPrice, deliveryBoyTitle, deliveryBoy, tokenText;
    private ImageView contact;
    private LinearLayout tokenButton, deliveryLayout;
    private OrderUtility orderUtility;
    private OrderEntity orderEntitySelected;
    private ImageButton backButton;
    private Intent intent;
    private List<OrderItemEntitty> orderItemEntitties;
    private String itemId;
    private List<ItemEntity> itemEntities;
    private MaterialDialog materialDialog;
    private RecyclerView itemList;
    private RecyclerView.LayoutManager mlayoutmanager;
    private TrackOrderItemAdapter trackOrderItemAdapter;
    private int flag = 0;
    private BackgroundService backgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        initialize();

        if(getIntent().hasExtra("flag")){
            flag = getIntent().getExtras().getInt("flag");
        }

        if(getIntent().hasExtra("orderplaced")){
            successDialog();
            backgroundService.updateTrendEntity(sharedData.getOrderEntity(),getApplicationContext());
        }

        OrderEntity orderEntity = sharedData.getOrderEntity();
        String orderId = orderEntity.getOrder_id();
        UserEntity userEntity = sharedData.getUserEntity();
        String userId = userEntity.getFirebaseID();

        trackOrderItemAdapter = new TrackOrderItemAdapter(orderEntity.getOrderItemEntitty(),getApplicationContext());
        itemList.setAdapter(trackOrderItemAdapter);

        canteenName.setText(getName(orderEntity.getCanteen_id(),sharedData.getCanteenEntitities()));
        StringBuffer stringBuffer1 = new StringBuffer(orderEntity.getOrder_time());
        final String orderDate = "Ordered on <b>"+stringBuffer1.substring(0,10)+"</b> at <b>"+stringBuffer1.substring(10)+"</b>";
        orderDateTime.setText(Html.fromHtml(orderDate));
        totalPrice.setText("₹ "+orderEntity.getAmount());

        orderUtility.getOrder(orderId, userId, new GetOrderListener() {
            @Override
            public void onCompleteTask(OrderEntity orderEntity) {
                if(orderEntity!=null) {
                    orderItemEntitties = orderEntity.getOrderItemEntitty();
                    sharedData.setOrderEntity(orderEntity);
                    if(checkOrderStatus(orderEntity) && sharedData.getUserEntity().getUserType().equalsIgnoreCase("Faculty")){
                        deliveryLayout.setVisibility(View.VISIBLE);
                        if(orderEntity.getDelivery_guy()!=null) {
                            deliveryBoy.setText(orderEntity.getDelivery_guy().getDeliman_id());
                        }
                        else{
                            deliveryLayout.setVisibility(View.INVISIBLE);
                        }
                    }
                    else{
                        deliveryLayout.setVisibility(View.INVISIBLE);
                    }
                    trackOrderItemAdapter = new TrackOrderItemAdapter(orderItemEntitties,getApplicationContext());
                    itemList.setAdapter(trackOrderItemAdapter);
                }
            }

            @Override
            public void onErrorOccured() {
                //Error
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        tokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),TokenActivity.class);
                startActivity(intent);
            }
        });

    }

    public String getName(String id, List<CanteenEntity> canteenEntities){
        for(int i=0; i<canteenEntities.size();i++){
            CanteenEntity canteenEntity = canteenEntities.get(i);
            if(canteenEntity.getId().equals(id))
                return canteenEntity.getName();
        }
        return "";
    }

    public boolean checkOrderStatus(OrderEntity order){
        List<OrderItemEntitty> orderItemEntitties = order.getOrderItemEntitty();
        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
            if(orderItemEntitty.getStatus()==3)
                continue;
            else
                return false;
        }
        return true;
    }

    public void initialize(){
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        page_title = (TextView) findViewById(R.id.track_order_title);
        canteenName = (TextView) findViewById(R.id.canteen_name);
        orderDateTime = (TextView) findViewById(R.id.order_date_time);
        itemsTitle = (TextView) findViewById(R.id.items_title);
        totalPriceTitle = (TextView) findViewById(R.id.total_price_text);
        totalPrice = (TextView) findViewById(R.id.total_price);
        deliveryBoyTitle = (TextView) findViewById(R.id.delivery_boy_title);
        deliveryBoy = (TextView) findViewById(R.id.delivery_boy_name);
        deliveryLayout = (LinearLayout) findViewById(R.id.delivery_boy_layout);
        contact = (ImageView) findViewById(R.id.phone_icon);
        tokenButton = (LinearLayout) findViewById(R.id.token_button);
        orderUtility = new OrderUtility();
        backButton = (ImageButton) findViewById(R.id.back_button);
        tokenText = (TextView) findViewById(R.id.token_text);
        itemList = (RecyclerView) findViewById(R.id.items_recycler);
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        itemList.setLayoutManager(mlayoutmanager);
        itemList.setItemAnimator(new DefaultItemAnimator());
        backgroundService = new BackgroundService();
        assignFonts();
    }

    public void assignFonts(){
        page_title.setTypeface(helper.cambriaBold);
        canteenName.setTypeface(helper.comicBold);
        orderDateTime.setTypeface(helper.comic);
        itemsTitle.setTypeface(helper.comicBold);
        totalPriceTitle.setTypeface(helper.comicBold);
        totalPrice.setTypeface(helper.comic);
        deliveryBoyTitle.setTypeface(helper.comicBold);
        deliveryBoy.setTypeface(helper.comic);
        tokenText.setTypeface(helper.cambriaBold);
    }
    @Override
    public void onBackPressed(){
        if(flag==1){
            intent = new Intent(getApplicationContext(), ItemActivity.class);
            startActivity(intent);
        }
        else {
            intent = new Intent(getApplicationContext(), OrderHistory.class);
            startActivity(intent);
        }
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

        orderUtility.startUpdating();
        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        orderUtility.removeUpdating();
    }

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(TrackOrder.this)
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

    public void successDialog(){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Order placed Successfully")
                .setContentText("Now sit back and relax\nTasty food awaits you...")
                .show();

    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(TrackOrder.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), TrackOrder.class);
                        intent.putExtra("id",itemId);
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
