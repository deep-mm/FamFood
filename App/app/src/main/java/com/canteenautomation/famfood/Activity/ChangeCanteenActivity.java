package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.CanteenPagerAdapter;
import com.canteenautomation.famfood.Entities.ActiveTokenEntity;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Listener.OnCanteenListChangeListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.CanteenUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ChangeCanteenActivity extends AppCompatActivity {

    private List<CanteenEntity> canteenEntitties;
    private FragmentPagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private InkPageIndicator inkPageIndicator;
    public static int currentPage;
    private SharedData sharedData;
    private Helper helper;
    private MaterialDialog materialDialog;
    private Intent intent;
    private CanteenUtility canteenUtility;
    private TextView selectCanteenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_canteen);

        initialize();

        canteenEntitties = sharedData.getCanteenEntitities();
        if(canteenEntitties==null){
            canteenEntitties = new ArrayList<>();
            sharedData.setCanteenEntities(canteenEntitties);
        }
        adapterViewPager = new CanteenPagerAdapter(getSupportFragmentManager(),canteenEntitties.size(),canteenEntitties);
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new FlipHorizontalTransformer());
        inkPageIndicator.setViewPager(vpPager);

        onProgressStart();
        canteenUtility = new CanteenUtility(new OnCanteenListChangeListener() {
            @Override
            public void onDataChanged(List<CanteenEntity> canteenEntityList) {
                sharedData.setCanteenEntities(canteenEntityList);
                canteenEntitties = canteenEntityList;
                adapterViewPager = new CanteenPagerAdapter(getSupportFragmentManager(),canteenEntitties.size(),canteenEntitties);
                vpPager.setAdapter(adapterViewPager);
                vpPager.setPageTransformer(true, new FlipHorizontalTransformer());
                inkPageIndicator.setViewPager(vpPager);
                onProgressStop();
            }

            @Override
            public void onErrorOccured() {
                onProgressStop();
                helper.printToast("Error Occured",1);
            }
        });


        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initialize(){
        canteenEntitties = new ArrayList<CanteenEntity>();
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        currentPage = 0;
        selectCanteenText = (TextView) findViewById(R.id.select_canteen);
        selectCanteenText.setTypeface(helper.segoeprb);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        canteenUtility.startUpdating();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        canteenUtility.removeUpdating();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(ChangeCanteenActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), ChangeCanteenActivity.class);
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finishAffinity();
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(ChangeCanteenActivity.this)
                .title("Syncing Data")
                .content("Please Wait")
                .progress(true, 0)
                .show();
    }

    public void onProgressStop(){
        materialDialog.dismiss();
    }



}
