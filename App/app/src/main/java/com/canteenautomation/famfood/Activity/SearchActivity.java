package com.canteenautomation.famfood.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.canteenautomation.famfood.Adapter.MenuItemAdapter;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChange;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<ItemEntity> itemEntityArrayList = new ArrayList<>();
    private List<ItemCategoryEntity> itemCategoryEntities;
    private RecyclerView mRecyclerView;
    private MenuItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedData sharedData;
    private ImageView backButton;
    private Helper helper;
    private TextView page_title;
    private EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initialize();

        itemCategoryEntities = sharedData.getItemCategoryEntity();

        for(int i=0;i<itemCategoryEntities.size();i++){
            ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
            itemEntityArrayList.addAll(itemCategoryEntity.getItemEntities());
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator( new DefaultItemAnimator());

        mAdapter = new MenuItemAdapter("",itemEntityArrayList, getApplicationContext(), new OnItemQuantityChange() {
            @Override
            public void onQuantityChange() {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<ItemEntity> filteredList = new ArrayList<>();
        for(ItemCategoryEntity item: itemCategoryEntities){
            if(item.getCategoryName().toLowerCase().contains(text.toLowerCase())){
                filteredList.addAll(item.getItemEntities());
            }
        }
        for (ItemEntity item : itemEntityArrayList) {
            if (item.getItemName().toLowerCase().contains(text.toLowerCase())) {
                if(!filteredList.contains(item))
                    filteredList.add(item);
            }
        }

        mAdapter = new MenuItemAdapter("",filteredList, getApplicationContext(), new OnItemQuantityChange() {
            @Override
            public void onQuantityChange() {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initialize(){
        mRecyclerView= findViewById(R.id.search_recycler);
        sharedData = new SharedData(getApplicationContext());
        backButton = (ImageButton) findViewById(R.id.back_button);
        page_title = (TextView) findViewById(R.id.search_title);
        helper = new Helper(getApplicationContext());
        searchBox = findViewById(R.id.search_edit_text);
        assignFonts();
    }

    public void assignFonts(){
        page_title.setTypeface(helper.cambriaBold);
        searchBox.setTypeface(helper.comic);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
