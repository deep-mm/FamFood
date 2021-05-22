package com.canteenautomation.famfood.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.canteenautomation.famfood.Activity.CategoryFragment;
import com.canteenautomation.famfood.Entities.CategoryEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.Listener.OnItemQuantityChangeListener;

import java.util.List;

public class CategoriesPagerAdapter extends FragmentPagerAdapter {

    private int numItems;
    private List<CategoryEntity> categoryEntities;
    private OnItemQuantityChangeListener onItemQuantityChangeListener;

    public CategoriesPagerAdapter(FragmentManager fm, int numItems, List<CategoryEntity> categoryEntities, OnItemQuantityChangeListener onItemQuantityChangeListener) {
        super(fm);
        this.numItems = numItems;
        this.categoryEntities = categoryEntities;
        this.onItemQuantityChangeListener = onItemQuantityChangeListener;
    }

    @Override
    public Fragment getItem(int position) {
        CategoryEntity categoryEntity = categoryEntities.get(position);
        return CategoryFragment.newInstance(categoryEntity.getId(),categoryEntity.getName(),categoryEntity.getPhoto(), onItemQuantityChangeListener);
    }

    @Override
    public int getCount() {
        return numItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CategoryEntity categoryEntity = categoryEntities.get(position);
        return categoryEntity.getName();
    }
}
