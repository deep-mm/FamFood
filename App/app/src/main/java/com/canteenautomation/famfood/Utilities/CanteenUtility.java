package com.canteenautomation.famfood.Utilities;

import android.util.Log;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.PaytmAPIEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnCanteenEntityChangeListener;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CanteenUtility {

    private DatabaseReference mCanteenListDatabaseReference;
    private DatabaseReference mCanteenListDatabaseReference1;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener1;
    private ChildEventListener childEventListener1;
    private final String TAG = "CanteenUtilityClass";
    private List<CanteenEntity> canteenEntityList;

    public CanteenUtility(final OnCanteenListChangeListener onCanteenListChangeListener) {
        canteenEntityList = new ArrayList<CanteenEntity>();
        mCanteenListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Canteens");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(onCanteenListChangeListener!=null) {
                    canteenEntityList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        CanteenEntity canteenEntity = postSnapshot.getValue(CanteenEntity.class);
                        canteenEntityList.add(canteenEntity);
                    }
                    onCanteenListChangeListener.onDataChanged(canteenEntityList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onCanteenListChangeListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                canteenEntityList.add(dataSnapshot.getValue(CanteenEntity.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                canteenEntityList.remove(dataSnapshot.getValue(CanteenEntity.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onCanteenListChangeListener.onErrorOccured();
            }
        };

        mCanteenListDatabaseReference.keepSynced(true);
        mCanteenListDatabaseReference.addValueEventListener(valueEventListener);
        mCanteenListDatabaseReference.addChildEventListener(childEventListener);
    }

    public CanteenUtility(){
        mCanteenListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Canteens");
    }

    public void getCanteen(String canteenId,final OnCanteenEntityChangeListener onCanteenEntityChangeListener ){
        mCanteenListDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("Canteens").child(canteenId);

        mCanteenListDatabaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CanteenEntity canteenEntity = dataSnapshot.getValue(CanteenEntity.class);
                onCanteenEntityChangeListener.onDataChanged(canteenEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Error occured while retreiving user");
                onCanteenEntityChangeListener.onErrorOccured();
            }
        });
    }
    /*
    Tested on 22/01/19 by Deep Mehta
    Works as per the requirements
     */
    public List<CanteenEntity> getCanteenEntityList() {
        return canteenEntityList;
    }

    public void removeUpdating(){
        mCanteenListDatabaseReference.removeEventListener(valueEventListener);
        mCanteenListDatabaseReference.removeEventListener(childEventListener);
    }

    public void startUpdating(){
        mCanteenListDatabaseReference.addValueEventListener(valueEventListener);
        mCanteenListDatabaseReference.addChildEventListener(childEventListener);
    }

    public void removeUpdatingEntity(){
        mCanteenListDatabaseReference1.removeEventListener(valueEventListener1);
        mCanteenListDatabaseReference1.removeEventListener(childEventListener1);
    }

    public void startUpdatingEntity(){
        mCanteenListDatabaseReference1.addValueEventListener(valueEventListener1);
        mCanteenListDatabaseReference1.addChildEventListener(childEventListener1);
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Works as per the requirements
     */
    public void addCanteen(CanteenEntity canteenEntity){
        mCanteenListDatabaseReference.child(canteenEntity.getId()).setValue(canteenEntity);
    }

    /*CanteenEntity canteenEntity = sharedData.getCanteenEntity();
    String merchantKey = "UYcNhq5&pFn2%5P1";
    String MID = "VWgiWN01208844400057";
    String channelId = "WAP";
    String website = "DEFAULT";
    String industryType = "Retail";
    String url = "http://famfoodpaytm1.000webhostapp.com/generateChecksum.php";
    PaytmAPIEntity paytmAPIEntity = new PaytmAPIEntity(MID, merchantKey, channelId, website, industryType, url);
                canteenEntity.setPaytmAPIEntity(paytmAPIEntity);
                canteenEntity.setActiveToken(0);
                canteenEntity.setStatus(1);
    CanteenUtility canteenUtility = new CanteenUtility();
                canteenUtility.addCanteen(canteenEntity);*/
}
