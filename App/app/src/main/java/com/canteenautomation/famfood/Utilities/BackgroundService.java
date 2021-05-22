package com.canteenautomation.famfood.Utilities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.canteenautomation.famfood.Activity.TrackOrder;
import com.canteenautomation.famfood.Adapter.OrderHistoryAdapter;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Entities.TrendEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetTrendListener;
import com.canteenautomation.famfood.Listener.GetUserListener;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.canteenautomation.famfood.Listener.OnFeedbackListChangeListener;
import com.canteenautomation.famfood.Listener.OnOrderListChangeListener;
import com.canteenautomation.famfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public SharedData sharedData;
    private Helper helper;
    private CanteenUtility canteenUtility;
    private MenuUtility menuUtility;
    private OrderUtility orderUtility;
    private FeedbackUtility feedbackUtility;
    private UserUtility userUtility;
    private TrendUtility trendUtility;
    private FirebaseAuth mAuth;
    private List<TrendEntity> trendEntities;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());

        setOrderUtility();
        setFeedbackUtility();

    }

    public void setFeedbackUtility(){
        CanteenEntity canteenEntity = sharedData.getCanteenEntity();
        if(canteenEntity!=null){
            feedbackUtility = new FeedbackUtility(canteenEntity.getId(), new OnFeedbackListChangeListener() {
                @Override
                public void onDataChanged(List<FeedbackEntity> feedbackEntityList) {
                    if(feedbackEntityList!=null)
                        sharedData.setFeedbackEntityList(feedbackEntityList);
                }

                @Override
                public void onErrorOccured() {

                }
            });
        }
    }

    public void setOrderUtility(){
        UserEntity userEntity = sharedData.getUserEntity();
        if(userEntity!=null) {
            orderUtility = new OrderUtility(userEntity.getFirebaseID(), new OnOrderListChangeListener() {
                @Override
                public void onCompleteTask(List<OrderEntity> orderEntityList) {
                    List<OrderEntity> orderEntityListOld = sharedData.getOrderEntityList();
                    sharedData.setOrderEntityList(orderEntityList);
                    if (orderEntityList != null && orderEntityListOld != null)
                        checkDifference(orderEntityListOld, orderEntityList);
                }

                @Override
                public void onErrorOccured() {

                }
            });
        }
    }

    public void setTrendUtility(){
        CanteenEntity canteenEntity = sharedData.getCanteenEntity();
        if(canteenEntity!=null) {
            trendUtility = new TrendUtility(canteenEntity.getId(), new GetTrendListener() {
                @Override
                public void onCompleteTask(List<TrendEntity> trendEntityList) {
                    if(trendEntityList!=null){
                        sharedData.setTrendList(trendEntityList);
                        List<String> trendItems = new ArrayList<>();
                        for(int i=0;i<trendEntityList.size();i++){
                            TrendEntity trendEntity = trendEntityList.get(i);
                            if(trendEntity.getQuantityOrdered()>=5 && trendEntity.getAvgRating()>=3){
                                trendItems.add(trendEntity.getItemId());
                            }
                        }
                        sharedData.setTrendingItems(trendItems);
                    }
                }

                @Override
                public void onErrorOccured() {

                }
            });
        }
    }

    public void updateTrendEntity(OrderEntity orderEntity, Context context){
        sharedData = new SharedData(context);
        trendUtility = new TrendUtility(sharedData.getCanteenEntity().getId());
        List<OrderItemEntitty> orderItemEntitties = orderEntity.getOrderItemEntitty();
        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
            TrendEntity trendEntity = getCurrentTrend(orderItemEntitty.getItemId());
            TrendEntity updatedTrendEntity = new TrendEntity(trendEntity.getItemId(),trendEntity.getQuantityOrdered()+1,trendEntity.getAvgRating(),trendEntity.getQuantityRated());
            trendUtility.addTrending(sharedData.getCanteenEntity().getId(),updatedTrendEntity);
        }
    }


    public void updateTrendEntity(FeedbackEntity feedbackEntity, Context context){
        sharedData = new SharedData(context);
        trendUtility = new TrendUtility(sharedData.getCanteenEntity().getId());
        HashMap<String, Float> itemReviews = feedbackEntity.getItemReviews();
        Object[] keys = itemReviews.keySet().toArray();
        for(int i=0;i<keys.length;i++){
            String itemId = keys[i].toString();
            float val = itemReviews.get(itemId);
            TrendEntity trendEntity = getCurrentTrend(itemId);
            double rating = trendEntity.getAvgRating();
            int quantityRated = trendEntity.getQuantityRated();
            double avgRating = ((rating*quantityRated) + val)/(quantityRated+1);
            TrendEntity updatedTrendEntity = new TrendEntity(trendEntity.getItemId(),trendEntity.getQuantityOrdered(),avgRating,quantityRated+1);
            trendUtility.addTrending(sharedData.getCanteenEntity().getId(),updatedTrendEntity);
        }
    }

    public TrendEntity getCurrentTrend(String itemId){
        if(sharedData.getTrendList()==null){
            trendEntities = new ArrayList<>();
        }
        else{
            trendEntities = sharedData.getTrendList();
        }

        for(int i=0; i<trendEntities.size(); i++){
            TrendEntity trendEntity = trendEntities.get(i);
            if(trendEntity.getItemId().equals(itemId)){
                return trendEntity;
            }
        }

        TrendEntity trendEntity = new TrendEntity(itemId,0,0.0,0);
        return trendEntity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void notification(OrderItemEntitty orderItemEntitty, Service myService){
        String itemId = orderItemEntitty.getItemId();
        String itemName = orderItemEntitty.getItemName();
        String itemStatus = getStatus(orderItemEntitty.getStatus());

        String arr[] = itemId.split("_");

        int id = Integer.parseInt(arr[arr.length-1]+1);

        Intent intent = new Intent(context, TrackOrder.class);
        intent.putExtra("id",itemId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(mNotificationManager, itemId);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, itemId);
        mBuilder.setSmallIcon(R.mipmap.app_icon);
        mBuilder.setBadgeIconType(R.mipmap.app_icon);
        mBuilder.setContentTitle("Status Update - "+itemName);
        mBuilder.setContentText(itemStatus)
                .setPriority(Notification.PRIORITY_MAX)
                .setChannelId(itemId)
                .setContentIntent(pendingNotificationIntent);

        Notification notification = mBuilder.build();

        mNotificationManager.notify(id, notification);
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager, String channelId) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String channelName = "FamFood";

        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        mChannel.setDescription("Status Update");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    public void checkDifference(List<OrderEntity> orderEntityList1, List<OrderEntity> orderEntityList2){

        for(int i=0;i<orderEntityList1.size();i++){
            OrderEntity orderEntity1 = orderEntityList1.get(i);
            OrderEntity orderEntity2 = findOrder(orderEntity1, orderEntityList2);
            if(orderEntity2!=null) {
                checkDifferenceItemEntity(orderEntity1, orderEntity2);
            }
        }
    }

    public OrderEntity findOrder(OrderEntity orderEntity, List<OrderEntity> orderEntityList){
        for(int i=0; i<orderEntityList.size(); i++){
            OrderEntity orderEntity1 = orderEntityList.get(i);
            if(orderEntity.getOrder_id().equals(orderEntity1.getOrder_id())){
                return orderEntity1;
            }
        }
        return null;
    }

    public String getStatus(int status){
        switch (status){
            case 0: return "Order placed successfully";

            case 1: return "Order is accepted";

            case 2: return "Order is being prepared, sit back & relax";

            case 3:
                    if(sharedData.getUserEntity().getUserType().equalsIgnoreCase("Student")){
                        return "Order is prepared & ready to be picked up";
                    }
                    else{
                        return "Order is prepared & out for delivery";
                    }

            case 4: return "Order is delivered";

            default: return "Order placed successfully";
        }
    }

    public void checkDifferenceItemEntity(OrderEntity orderEntity1, OrderEntity orderEntity2 ){

        List<OrderItemEntitty> itemEntityList1 = orderEntity1.getOrderItemEntitty();
        List<OrderItemEntitty> itemEntityList2 = orderEntity2.getOrderItemEntitty();
        for(int i=0;i<itemEntityList1.size();i++){
            OrderItemEntitty orderItemEntitty1 = itemEntityList1.get(i);
            OrderItemEntitty orderItemEntitty2 = findItem(orderItemEntitty1,itemEntityList2);
            if(orderItemEntitty2!=null) {
                if (orderItemEntitty1.getStatus() != orderItemEntitty2.getStatus()) {
                    sharedData.setOrderEntity(orderEntity2);
                    notification(orderItemEntitty2, this);
                }
            }
        }
    }

    public OrderItemEntitty findItem(OrderItemEntitty orderItemEntitty, List<OrderItemEntitty> orderItemEntitties){
        String id = orderItemEntitty.getItemId();
        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty1 = orderItemEntitties.get(i);
            if(id.equals(orderItemEntitty1.getItemId()))
                return orderItemEntitty1;
        }
        return null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 5000);
                setOrderUtility();
                setFeedbackUtility();
                setTrendUtility();
            }
        };

        handler.postDelayed(runnable, 5000);

        return START_STICKY;
    }
}
