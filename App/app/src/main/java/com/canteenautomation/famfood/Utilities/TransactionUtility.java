package com.canteenautomation.famfood.Utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.TransactionEntity;
import com.canteenautomation.famfood.Listener.OnMenuChangeListener;
import com.canteenautomation.famfood.Listener.OnTransactionChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionUtility {

    private DatabaseReference mTransactionDatabaseReference;
    private final String TAG = "TransactionUtilityClass";
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;

    public TransactionUtility(String userId) {
        mTransactionDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions").child(userId);
    }

    public TransactionUtility(String userId, final OnTransactionChangeListener onTransactionChangeListener){

        mTransactionDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Transactions").child(userId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(onTransactionChangeListener!=null) {
                    onTransactionChangeListener.onDataChanged(dataSnapshot.getValue(TransactionEntity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onTransactionChangeListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //TODO: Add later on
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mTransactionDatabaseReference.addChildEventListener(childEventListener);
        mTransactionDatabaseReference.addValueEventListener(valueEventListener);
    }

    public void removeUpdating(){
        mTransactionDatabaseReference.removeEventListener(valueEventListener);
        mTransactionDatabaseReference.removeEventListener(childEventListener);
    }

    public void startUpdating(){
        mTransactionDatabaseReference.addValueEventListener(valueEventListener);
        mTransactionDatabaseReference.addChildEventListener(childEventListener);
    }

    /*
    Tested on 23/01/19 by Deep Mehta
    Works as per requirements
     */
    public void addTransaction(TransactionEntity transactionEntity){
        mTransactionDatabaseReference.setValue(transactionEntity);
    }


}
