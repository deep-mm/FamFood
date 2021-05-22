package com.canteenautomation.famfood.Utilities;

import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.TrendEntity;
import com.canteenautomation.famfood.Listener.GetTrendListener;
import com.canteenautomation.famfood.Listener.OnFeedbackListChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrendUtility {

    private DatabaseReference mTrendListDatabaseReference;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private List<TrendEntity> trendEntityList;

    public TrendUtility(String canteenId, final GetTrendListener getTrendListener) {
        trendEntityList = new ArrayList<TrendEntity>();
        mTrendListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trends").child(canteenId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(getTrendListener!=null) {
                    trendEntityList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        TrendEntity trendEntity = postSnapshot.getValue(TrendEntity.class);
                        trendEntityList.add(trendEntity);
                    }
                    getTrendListener.onCompleteTask(trendEntityList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getTrendListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                trendEntityList.add(dataSnapshot.getValue(TrendEntity.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                trendEntityList.remove(dataSnapshot.getValue(TrendEntity.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getTrendListener.onErrorOccured();
            }
        };

        mTrendListDatabaseReference.keepSynced(true);
        mTrendListDatabaseReference.addValueEventListener(valueEventListener);
        mTrendListDatabaseReference.addChildEventListener(childEventListener);
    }

    public TrendUtility(String canteenId){
        mTrendListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trends").child(canteenId);
    }

    public void removeUpdating(){
        mTrendListDatabaseReference.removeEventListener(valueEventListener);
        mTrendListDatabaseReference.removeEventListener(childEventListener);
    }

    public void startUpdating(){
        mTrendListDatabaseReference.addValueEventListener(valueEventListener);
        mTrendListDatabaseReference.addChildEventListener(childEventListener);
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Works as per the requirements
     */
    public void addTrending(String canteenId, TrendEntity trendEntity){
        mTrendListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trends").child(canteenId);
        mTrendListDatabaseReference.child(trendEntity.getItemId()).setValue(trendEntity);
    }
}
