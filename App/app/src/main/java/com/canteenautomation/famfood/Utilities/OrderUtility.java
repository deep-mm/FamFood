package com.canteenautomation.famfood.Utilities;

import android.util.Log;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetOrderListener;
import com.canteenautomation.famfood.Listener.GetTokenListener;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.canteenautomation.famfood.Listener.OnOrderListChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderUtility {

    private DatabaseReference mOrderlistDatabaseReference, mOrderDatabaseReference, mTokenDatabaseReference;
    private ValueEventListener valueEventListener, valueEventListener2;
    private ChildEventListener childEventListener, childEventListener2;
    private List<OrderEntity> orderEntityList;

    public OrderUtility(String userId, final OnOrderListChangeListener onOrderListChangeListener) {
        orderEntityList = new ArrayList<OrderEntity>();
        mOrderlistDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(userId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(onOrderListChangeListener!=null){
                    orderEntityList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        OrderEntity orderEntity = postSnapshot.getValue(OrderEntity.class);
                        orderEntityList.add(orderEntity);
                    }
                    onOrderListChangeListener.onCompleteTask(orderEntityList);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onOrderListChangeListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                orderEntityList.add(dataSnapshot.getValue(OrderEntity.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                orderEntityList.remove(dataSnapshot.getValue(CanteenEntity.class).getId());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onOrderListChangeListener.onErrorOccured();
            }
        };

        mOrderlistDatabaseReference.keepSynced(true);
        mOrderlistDatabaseReference.addValueEventListener(valueEventListener);
        mOrderlistDatabaseReference.addChildEventListener(childEventListener);
    }

    public OrderUtility() {

    }

    public OrderUtility(String userId) {
        mOrderlistDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(userId);
    }

    public List<OrderEntity> getOrderEntityList() {
        return orderEntityList;
    }

    public void removeListUpdating(){
        mOrderlistDatabaseReference.removeEventListener(valueEventListener);
        mOrderlistDatabaseReference.removeEventListener(childEventListener);
    }

    public void startListUpdating(){
        mOrderlistDatabaseReference.addValueEventListener(valueEventListener);
        mOrderlistDatabaseReference.addChildEventListener(childEventListener);
    }

    public void removeUpdating(){
        mOrderDatabaseReference.removeEventListener(valueEventListener2);
        mOrderDatabaseReference.removeEventListener(childEventListener2);
    }

    public void startUpdating(){
        mOrderDatabaseReference.addValueEventListener(valueEventListener2);
        mOrderDatabaseReference.addChildEventListener(childEventListener2);
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Works as per the requirements
     */
    public void addOrder(OrderEntity orderEntity){
        mOrderlistDatabaseReference.child(orderEntity.getOrder_id()).setValue(orderEntity);
    }

    public void addActiveToken(int token){
        mTokenDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child("Token");
        mTokenDatabaseReference.child(Integer.toString(token)).setValue(token);
    }

    public void getActiveToken(int token, final GetTokenListener getTokenListener){
        mTokenDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child("Token").child(Integer.toString(token));

        mTokenDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    getTokenListener.Auth(true);
                else
                    getTokenListener.Auth(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getTokenListener.onErrorOccured();
            }
        });
    }

    public void getOrder(String orderId, String userId, final GetOrderListener getOrderListener){
        mOrderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(userId).child(orderId);

        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(getOrderListener!=null)
                    getOrderListener.onCompleteTask(dataSnapshot.getValue(OrderEntity.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getOrderListener.onErrorOccured();
            }
        };

        childEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //getOrderListener.onCompleteTask(dataSnapshot.getValue(OrderEntity.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //getOrderListener.onCompleteTask(dataSnapshot.getValue(OrderEntity.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getOrderListener.onErrorOccured();
            }
        };

        mOrderDatabaseReference.keepSynced(true);
        mOrderDatabaseReference.addValueEventListener(valueEventListener2);
        mOrderDatabaseReference.addChildEventListener(childEventListener2);
    }
}
