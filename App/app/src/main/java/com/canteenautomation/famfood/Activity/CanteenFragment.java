package com.canteenautomation.famfood.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.canteenautomation.famfood.Utilities.UserUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CanteenFragment extends android.support.v4.app.Fragment {

    private String name, photo, rate, canteenId;
    private SharedData sharedData;
    private Helper helper;
    private CanteenEntity canteenEntity;
    private CanteenEntity current;
    private Intent intent;
    private UserUtility userUtility;

    public static CanteenFragment newInstance(String name, String photo, String rating, String canteenId) {
        CanteenFragment canteenFragment = new CanteenFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("photo", photo);
        args.putString("rating",rating);
        args.putString("id",canteenId);
        canteenFragment.setArguments(args);
        return canteenFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name = getArguments().getString("name");
        photo = getArguments().getString("photo");
        rate = getArguments().getString("rating");
        canteenId = getArguments().getString("id");
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen,
                container, false);

        userUtility = new UserUtility();
        ImageView canteenCircle = (ImageView) view.findViewById(R.id.canteen_circle);
        ImageView canteenIcon = (ImageView) view.findViewById(R.id.college_photo);
        TextView canteenName = (TextView) view.findViewById(R.id.college_name);
        TextView rating = (TextView) view.findViewById(R.id.rating);
        Picasso .get()
                .load(photo)
                .into(canteenIcon);
        rating.setText(rate);
        canteenName.setText(name);

        canteenName.setTypeface(helper.comicBold);
        rating.setTypeface(helper.comic);

        canteenCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CanteenEntity> canteenEntityList = sharedData.getCanteenEntitities();
                int position = ChangeCanteenActivity.currentPage;
                canteenEntity = canteenEntityList.get(position);

                if(sharedData.getCanteenEntity()!=null) {
                    current = sharedData.getCanteenEntity();
                    if (current.getId().equals(canteenEntity.getId())) {
                        //Nothing
                    }
                    else{
                        HashMap<String,Integer> cart = new HashMap<>();
                        sharedData.setCart(cart);
                    }
                }
                sharedData.setCanteenEntity(canteenEntity);
                UserEntity userEntity = sharedData.getUserEntity();
                if(userEntity!=null) {
                    List<String> canteens = userEntity.getRecentCanteens();
                    if (canteens == null)
                        canteens = new ArrayList<>();

                    if (canteens.contains(canteenId))
                        canteens.remove(canteenId);

                    canteens.add(0, canteenEntity.getId());
                    userEntity.setRecentCanteens(canteens);
                    userUtility.addUser(userEntity);
                }
                intent = new Intent(getApplicationContext(),ItemActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
