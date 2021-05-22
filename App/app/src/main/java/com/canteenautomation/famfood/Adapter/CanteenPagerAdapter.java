package com.canteenautomation.famfood.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.canteenautomation.famfood.Activity.CanteenFragment;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.List;

public class CanteenPagerAdapter extends FragmentPagerAdapter {

    private int numItems;
    private List<CanteenEntity> canteenEntities;

    public CanteenPagerAdapter(FragmentManager fm,int numItems,List<CanteenEntity> canteenEntities) {
        super(fm);
        this.numItems = numItems;
        this.canteenEntities = canteenEntities;
    }

    @Override
    public Fragment getItem(int position) {
        CanteenEntity canteenEntity = canteenEntities.get(position);
        return CanteenFragment.newInstance(canteenEntity.getName(),canteenEntity.getPhoto(),canteenEntity.getRating(),canteenEntity.getId());
    }

    @Override
    public int getCount() {
        return numItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CanteenEntity canteenEntity = canteenEntities.get(position);
        return canteenEntity.getName();
    }
}
