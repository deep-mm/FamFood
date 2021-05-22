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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.FeedbackItemAdapter;
import com.canteenautomation.famfood.Adapter.TrackOrderItemAdapter;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.FeedbackUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private SharedData sharedData;
    private Helper helper;
    private List<Float> feedbackValues;
    private ImageButton backButton;
    private TextView pageTitle, thankText, feedbackHeading;
    private Button submit;
    private EditText comment;
    private List<FeedbackEntity> feedbackEntityList;
    private OrderEntity orderEntity;
    private UserEntity userEntity;
    private FeedbackEntity feedbackEntity;
    private RecyclerView itemList;
    private RecyclerView.LayoutManager mlayoutmanager;
    private FeedbackItemAdapter feedbackItemAdapter;
    private FeedbackUtility feedbackUtility;
    private Intent intent;
    private Boolean edit;
    private BackgroundService backgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initialize();

        feedbackEntity = checkIfExists(orderEntity.getOrder_id());
        if(feedbackEntity==null) {
            edit = false;
            feedbackEntity = new FeedbackEntity(orderEntity.getOrder_id(), userEntity.getFirebaseID(), userEntity.getName(), userEntity.getGender(), orderEntity.getOrder_time());
            HashMap<String, Float> itemReviews = new HashMap<>();
            List<OrderItemEntitty> orderItemEntitties = orderEntity.getOrderItemEntitty();
            for(int i=0;i<orderItemEntitties.size();i++){
                OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
                itemReviews.put(orderItemEntitty.getItemId(), 1.0f);
            }
            feedbackEntity.setItemReviews(itemReviews);
            feedbackEntity.setOrderId(orderEntity.getOrder_id());
        }
        else{
            edit = true;
            comment.setText(feedbackEntity.getReview());
        }

        feedbackValues = new ArrayList<>();
        HashMap<String, Float> itemReviews = feedbackEntity.getItemReviews();
        Object[] keys = itemReviews.keySet().toArray();
        for(int i=0;i<keys.length;i++){
            feedbackValues.add(itemReviews.get(keys[i].toString()));
        }
        sharedData.setFeedbackValues(feedbackValues);
        feedbackItemAdapter = new FeedbackItemAdapter(feedbackEntity.getItemReviews(),getApplicationContext(),0);
        itemList.setAdapter(feedbackItemAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackValues = sharedData.getFeedbackValues();
                HashMap<String, Float> itemReviews = feedbackEntity.getItemReviews();
                Object[] keys = itemReviews.keySet().toArray();
                for(int i=0;i<keys.length;i++){
                    itemReviews.put(keys[i].toString(),feedbackValues.get(i));
                }
                feedbackEntity.setItemReviews(itemReviews);
                String review = comment.getText().toString();
                feedbackEntity.setReview(review);
                feedbackUtility.addFeedback(sharedData.getCanteenEntity().getId(),feedbackEntity);
                if(!edit){
                    backgroundService.updateTrendEntity(feedbackEntity,getApplicationContext());
                }
                intent = new Intent(getApplicationContext(),OrderHistory.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });

    }

    public void initialize(){
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        backButton = (ImageButton) findViewById(R.id.back_button);
        pageTitle = (TextView) findViewById(R.id.feedback_title);
        thankText = (TextView) findViewById(R.id.thankyou_text);
        feedbackHeading = (TextView) findViewById(R.id.feedback_heading);
        submit = (Button) findViewById(R.id.submit_button);
        comment = (EditText) findViewById(R.id.comment);
        feedbackEntityList = sharedData.getFeedbackEntityList();
        if(feedbackEntityList==null) {
            feedbackEntityList = new ArrayList<>();
            sharedData.setFeedbackEntityList(feedbackEntityList);
        }
        orderEntity = sharedData.getOrderEntity();
        userEntity = sharedData.getUserEntity();
        assignFonts();
        itemList = (RecyclerView) findViewById(R.id.feedback_list);
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        itemList.setLayoutManager(mlayoutmanager);
        itemList.setItemAnimator(new DefaultItemAnimator());
        feedbackUtility = new FeedbackUtility(sharedData.getCanteenEntity().getId());
        backgroundService = new BackgroundService();
    }

    public void assignFonts(){
        pageTitle.setTypeface(helper.cambriaBold);
        thankText.setTypeface(helper.comic);
        feedbackHeading.setTypeface(helper.comic);
        submit.setTypeface(helper.cambria);
        comment.setTypeface(helper.comic);
    }

    public FeedbackEntity checkIfExists(String orderId){

        for(int i=0;i<feedbackEntityList.size();i++){
            FeedbackEntity feedbackEntity = feedbackEntityList.get(i);
            if(orderId.equals(feedbackEntity.getOrderId())){
                return feedbackEntity;
            }
        }

        return null;
    }

    @Override
    public void onBackPressed(){
        new MaterialDialog.Builder(FeedbackActivity.this)
                .title("Confirm Exit")
                .content("Unsaved changes will be lost, are you sure?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(),OrderHistory.class);
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
        new MaterialDialog.Builder(FeedbackActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), FeedbackActivity.class);
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
}
