package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.List;

public class TokenActivity extends AppCompatActivity {

    private Helper helper;
    private SharedData sharedData;
    private TextView tokenNumber, custName, dateTime, canteenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_token)
                .setSwipeBackView(R.layout.swipeback_default);

        initialize();

        OrderEntity orderEntity = sharedData.getOrderEntity();
        custName.setText(sharedData.getUserEntity().getName());
        String token = String.format("%03d", orderEntity.getOrder_token());
        tokenNumber.setText(token);
        dateTime.setText(orderEntity.getOrder_time());

        String name = getName(sharedData.getCanteenEntity().getId(),sharedData.getCanteenEntitities());
        canteenName.setText(name);
    }

    public void initialize(){
        helper = new Helper(getApplicationContext());
        sharedData = new SharedData(getApplicationContext());
        tokenNumber = (TextView) findViewById(R.id.token_number);
        custName = (TextView) findViewById(R.id.customer_name);
        dateTime = (TextView) findViewById(R.id.dateTime);
        canteenName = (TextView) findViewById(R.id.canteen_name);
        assignFonts();
    }

    public void assignFonts(){
        tokenNumber.setTypeface(helper.cambriaBold);
        custName.setTypeface(helper.segoeprb);
        canteenName.setTypeface(helper.segoeprb);
        dateTime.setTypeface(helper.comic);
    }

    public String getName(String id, List<CanteenEntity> canteenEntities){
        for(int i=0; i<canteenEntities.size();i++){
            CanteenEntity canteenEntity = canteenEntities.get(i);
            if(canteenEntity.getId().equals(id))
                return canteenEntity.getName();
        }
        return "";
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}
