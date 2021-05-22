package com.canteenautomation.famfood.Utilities;

import android.util.Log;
import android.widget.Toast;

import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.GetUserListener;
import com.canteenautomation.famfood.Listener.UserMobileListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserUtility {

    private DatabaseReference mUserListDatabaseReference;
    private DatabaseReference mUserMobileDatabaseReference;
    private final String TAG = "UserUtilityClass";

    public UserUtility(){
        mUserListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Customer");
        mUserMobileDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Mobile");
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Add User works perfectly as per the requirements
     */
    public void addUser(UserEntity userEntity){
        mUserMobileDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Mobile");
        mUserListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Customer");

        mUserListDatabaseReference.child(userEntity.getFirebaseID()).setValue(userEntity);
        mUserMobileDatabaseReference.child(userEntity.getUserMobile()).setValue(userEntity.getUserMobile());
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Get User works perfectly as per the requirements
    */
    public void getUser(String firebaseID, final GetUserListener getUserListener) {
        mUserListDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(firebaseID);

        mUserListDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserEntity userEntity = dataSnapshot.getValue(UserEntity.class);
                getUserListener.onCompleteTask(userEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Error occured while retreiving user");
                getUserListener.onErrorOccured();
            }
        });
    }

    /*
    Tested on 22/01/19 by Deep Mehta
    Check Mobile works perfectly as per the requirements
    */
    public void checkMobileExists(String mobile, final UserMobileListener userMobileListener){
        mUserMobileDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Mobile").child(mobile);

        mUserMobileDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userMobileListener.Auth(true);
                }
                else{
                    userMobileListener.Auth(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Error occured while retreiving user");
                userMobileListener.onErrorOccured();
            }
        });
    }


}
