package com.canteenautomation.famfood.Utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Listener.OnMenuChangeListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuUtility {

    private DatabaseReference mMenuDatabaseReference;
    private final String TAG = "ManuUtilityClass";
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private List<ItemCategoryEntity> itemCategoryEntities;

    public MenuUtility(String canteenId) {
        mMenuDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Menu").child(canteenId);
    }

    public MenuUtility(String canteenId, final OnMenuChangeListener onMenuChangeListener){
        itemCategoryEntities = new ArrayList<ItemCategoryEntity>();
        mMenuDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Menu").child(canteenId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(onMenuChangeListener!=null) {
                    itemCategoryEntities.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        ItemCategoryEntity itemCategoryEntity = postSnapshot.getValue(ItemCategoryEntity.class);
                        itemCategoryEntities.add(itemCategoryEntity);
                    }
                    onMenuChangeListener.onDataChanged(itemCategoryEntities);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onMenuChangeListener.onErrorOccured();
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                itemCategoryEntities.add(dataSnapshot.getValue(ItemCategoryEntity.class));
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
                onMenuChangeListener.onErrorOccured();
            }
        };

        mMenuDatabaseReference.keepSynced(true);
        mMenuDatabaseReference.addChildEventListener(childEventListener);
        mMenuDatabaseReference.addValueEventListener(valueEventListener);
    }


    public List<ItemCategoryEntity> getItemCategoryEntities() {
        return itemCategoryEntities;
    }

    public void removeUpdating(){
        mMenuDatabaseReference.removeEventListener(valueEventListener);
        mMenuDatabaseReference.removeEventListener(childEventListener);
    }

    public void startUpdating(){
        mMenuDatabaseReference.addValueEventListener(valueEventListener);
        mMenuDatabaseReference.addChildEventListener(childEventListener);
    }

    /*
    Tested on 23/01/19 by Deep Mehta
    Works as per requirements
     */
    public void addMenuItem(){
        ItemEntity itemEntity1 = new ItemEntity("canteen001_Item_LunchItems_130","Chappati Bhaji","http","53","15",true,1);
        ItemEntity itemEntity2 = new ItemEntity("canteen001_Item_LunchItems_131","Chapati Paneer BhaJI","http","59","15",true,1);
        ItemEntity itemEntity3 = new ItemEntity("canteen001_Item_LunchItems_132","Puri Bhaji","http","59","15",false,1);
        ItemEntity itemEntity4 = new ItemEntity("canteen001_Item_LunchItems_133","Puri Paneer Bhaji","http","59","15",false,1);
        ItemEntity itemEntity5 = new ItemEntity("canteen001_Item_LunchItems_134","Potato Bhajl","http","62","15",true,1);
        ItemEntity itemEntity6 = new ItemEntity("canteen001_Item_LunchItems_135","Triple Noodles","http","62","15",true,1);
        ItemEntity itemEntity7 = new ItemEntity("canteen001_Item_LunchItems_136","Chinese Bhel","http","47","15",true,1);
        ItemEntity itemEntity8 = new ItemEntity("canteen001_Item_LunchItems_137","Cheese Chinese Bhel","http","69","15",true,1);
        ItemEntity itemEntity9 = new ItemEntity("canteen001_Item_LunchItems_138","Veg Manchurian ","http","47","15",false,1);
        ItemEntity itemEntity10 = new ItemEntity("canteen001_Item_LunchItems_139","Veg Chilly","http","47","15",true,1);

        ItemEntity itemEntity11 = new ItemEntity("canteen001_Item_LunchItems_140","Veg Garlic","http","47","15",false,1);
        ItemEntity itemEntity12 = new ItemEntity("canteen001_Item_LunchItems_141","Paneer Chilly","http","77","15",false,1);
        ItemEntity itemEntity13 = new ItemEntity("canteen001_Item_LunchItems_142","Idli Chilly","http","38","15",false,1);
        ItemEntity itemEntity14 = new ItemEntity("canteen001_Item_LunchItems_143","Idli Manchurian","http","38","15",true,1);
        ItemEntity itemEntity15 = new ItemEntity("canteen001_Item_LunchItems_144","American Chopsuey","http","47","15",true,1);
        ItemEntity itemEntity16 = new ItemEntity("canteen001_Item_LunchItems_145","Schezwan Chopsuey","http","47","15",false,1);
        ItemEntity itemEntity17 = new ItemEntity("canteen001_Item_LunchItems_146","Chinese Chopsuey","http","47","15",false,1);
        ItemEntity itemEntity18 = new ItemEntity("canteen001_Item_LunchItems_147","Manchow Soup","http","47","15",true,1);
        ItemEntity itemEntity19 = new ItemEntity("canteen001_Item_LunchItems_148","Tomato Soup","http","47","15",true,1);
        ItemEntity itemEntity20 = new ItemEntity("canteen001_Item_LunchItems_149","Sweet Corn Soup","http","47","15",true,1);

        ItemEntity itemEntity21 = new ItemEntity("canteen001_Item_LunchItems_150","Idli Fry ","http","30","15",true,1);
        ItemEntity itemEntity22 = new ItemEntity("canteen001_Item_LunchItems_151","Dahi Misal Pav ","http","44","15",false,1);
        ItemEntity itemEntity23 = new ItemEntity("canteen001_Item_LunchItems_152","Upma","http","22","15",false,1);
        ItemEntity itemEntity24 = new ItemEntity("canteen001_Item_LunchItems_153","Poha","http","22","15",false,1);
        ItemEntity itemEntity25 = new ItemEntity("canteen001_Item_LunchItems_154","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity26 = new ItemEntity("canteen001_Item_LunchItems_155","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity27 = new ItemEntity("canteen001_Item_LunchItems_156","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity28 = new ItemEntity("canteen001_Item_LunchItems_157","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity29 = new ItemEntity("canteen001_Item_LunchItems_158","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity30 = new ItemEntity("canteen001_Item_LunchItems_159","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity31 = new ItemEntity("canteen001_Item_LunchItems_160","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity32 = new ItemEntity("canteen001_Item_LunchItems_161","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity33 = new ItemEntity("canteen001_Item_LunchItems_162","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity34 = new ItemEntity("canteen001_Item_LunchItems_163","Dahi Kachori ","http","33","15",false,1);
        ItemEntity itemEntity35 = new ItemEntity("canteen001_Item_LunchItems_164","Dahi Kachori ","http","33","15",false,1);


        List<ItemEntity> itemEntities = new ArrayList<>();
        itemEntities.add(itemEntity1);
        itemEntities.add(itemEntity2);
        itemEntities.add(itemEntity3);
        itemEntities.add(itemEntity4);
        itemEntities.add(itemEntity5);
        itemEntities.add(itemEntity6);
        itemEntities.add(itemEntity7);
        itemEntities.add(itemEntity8);
        itemEntities.add(itemEntity9);
        itemEntities.add(itemEntity10);
        itemEntities.add(itemEntity11);
        itemEntities.add(itemEntity12);
        itemEntities.add(itemEntity13);
        itemEntities.add(itemEntity14);
        itemEntities.add(itemEntity15);
        itemEntities.add(itemEntity16);
        itemEntities.add(itemEntity17);
        itemEntities.add(itemEntity18);
        itemEntities.add(itemEntity19);
        itemEntities.add(itemEntity20);
        itemEntities.add(itemEntity21);
        itemEntities.add(itemEntity22);
        itemEntities.add(itemEntity23);
        itemEntities.add(itemEntity24);
        itemEntities.add(itemEntity25);
        itemEntities.add(itemEntity26);
        itemEntities.add(itemEntity27);
        itemEntities.add(itemEntity28);
        itemEntities.add(itemEntity29);
        itemEntities.add(itemEntity30);
        itemEntities.add(itemEntity31);
        itemEntities.add(itemEntity32);
        itemEntities.add(itemEntity33);
        itemEntities.add(itemEntity34);
        itemEntities.add(itemEntity35);
        ItemCategoryEntity itemCategoryEntity = new ItemCategoryEntity("LunchItems", "https://timesofindia.indiatimes.com/thumb/msid-56401712,imgsize-61375,width-800,height-600,resizemode-4/56401712.jpg", itemEntities);
        mMenuDatabaseReference.child("LunchItems").setValue(itemCategoryEntity);
    }


}
