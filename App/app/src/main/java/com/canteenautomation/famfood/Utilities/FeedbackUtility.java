package com.canteenautomation.famfood.Utilities;

import android.util.Log;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Listener.OnCanteenEntityChangeListener;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.canteenautomation.famfood.Listener.OnFeedbackListChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackUtility {

    private DatabaseReference mFeedbackListDatabaseReference;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private List<FeedbackEntity> feedbackEntityList;

    public FeedbackUtility(String canteenId, final OnFeedbackListChangeListener onFeedbackListChangeListener) {
        feedbackEntityList = new ArrayList<FeedbackEntity>();
        mFeedbackListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(canteenId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(onFeedbackListChangeListener!=null) {
                    feedbackEntityList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        FeedbackEntity feedbackEntity = postSnapshot.getValue(FeedbackEntity.class);
                        feedbackEntityList.add(feedbackEntity);
                    }
                    onFeedbackListChangeListener.onDataChanged(feedbackEntityList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onFeedbackListChangeListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                feedbackEntityList.add(dataSnapshot.getValue(FeedbackEntity.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                feedbackEntityList.remove(dataSnapshot.getValue(FeedbackEntity.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onFeedbackListChangeListener.onErrorOccured();
            }
        };

        mFeedbackListDatabaseReference.keepSynced(true);
        mFeedbackListDatabaseReference.addValueEventListener(valueEventListener);
        mFeedbackListDatabaseReference.addChildEventListener(childEventListener);
    }

    public FeedbackUtility(String canteenId){
        mFeedbackListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(canteenId);
    }

    public void removeUpdating(){
        mFeedbackListDatabaseReference.removeEventListener(valueEventListener);
        mFeedbackListDatabaseReference.removeEventListener(childEventListener);
    }

    public void startUpdating(){
        mFeedbackListDatabaseReference.addValueEventListener(valueEventListener);
        mFeedbackListDatabaseReference.addChildEventListener(childEventListener);
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Works as per the requirements
     */
    public void addFeedback(String canteenId, FeedbackEntity feedbackEntity){
        mFeedbackListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(canteenId);
        mFeedbackListDatabaseReference.child(feedbackEntity.getFeedbackId()).setValue(feedbackEntity);
    }
}
