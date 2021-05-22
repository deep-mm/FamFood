package com.canteenautomation.famfood.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.CategoriesPagerAdapter;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.CategoryEntity;
import com.canteenautomation.famfood.Entities.ItemCategoryEntity;
import com.canteenautomation.famfood.Entities.ItemEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.OrderItemEntitty;
import com.canteenautomation.famfood.Entities.PaytmAPIEntity;
import com.canteenautomation.famfood.Entities.TrendEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnItemQuantityChangeListener;
import com.canteenautomation.famfood.Listener.OnMenuChangeListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.BackgroundService;
import com.canteenautomation.famfood.Utilities.CanteenUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.MenuUtility;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private SharedData sharedData;
    private ViewPager vpPager;
    private FragmentPagerAdapter adapterViewPager;
    private List<CategoryEntity> categoryEntities;
    private SmartTabLayout navigationTabStrip;
    public static TextView totalPrice, itemQuantity, viewCart, itemQuantityCartTop,menu_title;
    private ImageView rightArrow, cartIcon, searchIcon, priceSort, timeSort;
    public static ImageView sortIcon;
    private CanteenEntity canteenEntity;
    public static RelativeLayout cartPopup;
    private Helper helper;
    private Intent intent;
    private TextView name, number, userType;
    private MenuUtility menuUtility;
    private MaterialDialog materialDialog;
    private NavigationView navigationView;
    private LinearLayout priceLayout, expectedLayout;
    private ImageButton floatingActionButton;
    private List<OrderEntity> orderEntities;
    private List<ItemEntity> itemEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        initialize();

        if(sharedData.isLoggedIn() && sharedData.isSignedUp()){
            startService(new Intent(this, BackgroundService.class));
        }

        final UserEntity userEntity = sharedData.getUserEntity();
        cartPopup.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerLayout = navigationView.getHeaderView(0);

        navigationView.getMenu().getItem(0).setChecked(true);

        name = (TextView) headerLayout.findViewById(R.id.name);
        number = (TextView) headerLayout.findViewById(R.id.number);
        userType = (TextView) headerLayout.findViewById(R.id.user_type);

        name.setTypeface(helper.comic);
        number.setTypeface(helper.comic);
        userType.setTypeface(helper.comic);

        if(userEntity==null){
            name.setText("Guest User");
            number.setText("");
            userType.setText("#Guest");
            navigationView.getMenu().findItem(R.id.address).setVisible(false);
            navigationView.getMenu().findItem(R.id.log_out).setTitle("Login");
            navigationView.getMenu().findItem(R.id.order_history).setVisible(false);
        }
        else {
            name.setText(userEntity.getName());
            StringBuffer mobile = new StringBuffer(userEntity.getUserMobile());
            number.setText(mobile.substring(3));
            userType.setText("#"+userEntity.getUserType());
            if(userEntity.getUserType().equalsIgnoreCase("Faculty")){
                navigationView.getMenu().findItem(R.id.address).setVisible(true);
            }
            else{
                navigationView.getMenu().findItem(R.id.address).setVisible(false);
            }
        }



        //final List<String> categoriesName = canteenEntity.getCategories();

        onProgressStart();
        menuUtility = new MenuUtility(canteenEntity.getId(), new OnMenuChangeListener() {
            @Override
            public void onDataChanged(List<ItemCategoryEntity> itemCategoryEntities) {
                sharedData.setItemCategoryEntity(itemCategoryEntities);
                sharedData.setItemEntities(getItemEntityList(itemCategoryEntities));
                categoryEntities.clear();
                if(sharedData.getTrendingItems()!=null){
                   if(sharedData.getTrendingItems().size()>0){
                       CategoryEntity categoryEntity = new CategoryEntity("categoryTrend", "Recommended", "https://firebasestorage.googleapis.com/v0/b/cars-8af71.appspot.com/o/canteen001%2FCategoryPhotos%2Frecommend.png?alt=media&token=66068ece-0605-497c-8c10-6b19a7750711");
                       categoryEntities.add(categoryEntity);
                   }
                }
                UserEntity userEntity1 = sharedData.getUserEntity();
                if(userEntity1!=null) {
                    List<String> fav = userEntity1.getFavourites();
                    if(fav==null)
                        fav = new ArrayList<>();
                    if(fav.size()>0) {
                        CategoryEntity categoryEntity = new CategoryEntity("categoryFav", "Favourites", "https://www.b1homes.com.au/wp-content/themes/b1/images/feature/favourites.png");
                        categoryEntities.add(categoryEntity);
                    }
                }
                for(int i=0;i<itemCategoryEntities.size();i++){
                    ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
                    itemEntities.addAll(itemCategoryEntity.getItemEntities());
                    CategoryEntity categoryEntity = new CategoryEntity("category"+i,itemCategoryEntity.getCategoryName(),itemCategoryEntity.getCategoryPhoto());
                    categoryEntities.add(categoryEntity);
                }
                sharedData.setItemEntities(itemEntities);
                adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                    @Override
                    public void onQuantityChange() {

                    }
                });
                vpPager.setAdapter(adapterViewPager);
                vpPager.setPageTransformer(true, new DefaultTransformer());
                navigationTabStrip.setViewPager(vpPager);
                onProgressStop();
            }

            @Override
            public void onErrorOccured() {

            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //menuUtility.addMenuItem();
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCart();
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCart();
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.valueOf(itemQuantityCartTop.getText().toString())>0)
                    goToCart();

                else
                    helper.printToast("Cart is empty",1);
            }
        });

        sortIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterPopup();
            }
        });

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderEntities.clear();
                getActiveOrders();
                sharedData.setActiveOrderEntityList(orderEntities);
                if(orderEntities.size()==1){
                    sharedData.setOrderEntity(orderEntities.get(0));
                    intent = new Intent(getApplicationContext(),TrackOrder.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }
                else{
                    intent = new Intent(getApplicationContext(),OrderHistory.class);
                    startActivity(intent);
                }
            }
        });

        orderEntities.clear();
        getActiveOrders();
        if(orderEntities.size()>0)
            floatingActionButton.setVisibility(View.VISIBLE);
        else
            floatingActionButton.setVisibility(View.INVISIBLE);
    }

    public void goToCart(){
        Intent intent = new Intent(getApplicationContext(),CartActivity.class);
        startActivity(intent);
    }

    public void initialize(){
        sharedData = new SharedData(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        categoryEntities = new ArrayList<CategoryEntity>();
        navigationTabStrip = (SmartTabLayout) findViewById(R.id.nav_strip);
        totalPrice = (TextView) findViewById(R.id.item_price);
        itemQuantity = (TextView) findViewById(R.id.item_quantity);
        viewCart = (TextView) findViewById(R.id.view_cart_text);
        rightArrow = (ImageView) findViewById(R.id.right_arrow);
        itemQuantityCartTop = (TextView) findViewById(R.id.item_quantity_cartTop);
        canteenEntity = sharedData.getCanteenEntity();
        cartPopup = (RelativeLayout) findViewById(R.id.cartPopup);
        cartIcon = (ImageView) findViewById(R.id.cart_icon);
        helper = new Helper(getApplicationContext());
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        sortIcon = (ImageView) findViewById(R.id.sort_icon);
        menu_title = (TextView) findViewById(R.id.menu_title_text);
        orderEntities = new ArrayList<>();
        floatingActionButton = (ImageButton) findViewById(R.id.floatingButton);
        itemEntities = new ArrayList<>();
        assignFonts();

    }

    public List<ItemEntity> getItemEntityList(List<ItemCategoryEntity> itemCategoryEntities){
        List<ItemEntity> itemEntities = new ArrayList<>();
        for(int i=0;i<itemCategoryEntities.size();i++){
            ItemCategoryEntity itemCategoryEntity = itemCategoryEntities.get(i);
            itemEntities.addAll(itemCategoryEntity.getItemEntities());
        }
        return itemEntities;
    }

    public void getActiveOrders(){
        List<OrderEntity> orderEntities1 = sharedData.getOrderEntityList();
        if(orderEntities1!=null) {
            for (int i = 0; i < orderEntities1.size(); i++) {
                OrderEntity orderEntity = orderEntities1.get(i);
                if (!checkOrderStatus(orderEntity)) {
                    if(orderEntity.getTransactionStatus()!=null && orderEntity.getTransactionStatus().equals("TXN_SUCCESS"))
                        orderEntities.add(orderEntity);
                }
            }
        }
    }

    public boolean checkOrderStatus(OrderEntity order){
        List<OrderItemEntitty> orderItemEntitties = order.getOrderItemEntitty();
        for(int i=0;i<orderItemEntitties.size();i++){
            OrderItemEntitty orderItemEntitty = orderItemEntitties.get(i);
            if(orderItemEntitty.getStatus()==4)
                continue;
            else
                return false;
        }
        return true;
    }

    public void assignFonts(){
        menu_title.setTypeface(helper.cambriaBold);
        totalPrice.setTypeface(helper.comic);
        itemQuantity.setTypeface(helper.comic);
        viewCart.setTypeface(helper.comic);
        itemQuantityCartTop.setTypeface(helper.comic);
    }

    public void showFilterPopup(){
        LayoutInflater inflater = (LayoutInflater) ItemActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.activity_filter,
                (ViewGroup) findViewById(R.id.popup_window));
        PopupWindow pw = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        pw.showAsDropDown(sortIcon,0,0);
        Switch jain = (Switch) layout.findViewById(R.id.jain_switch);
        expectedLayout = (LinearLayout) layout.findViewById(R.id.time_sort_layout);
        priceLayout = (LinearLayout) layout.findViewById(R.id.price_sort_layout);
        priceSort = (ImageView) layout.findViewById(R.id.price_sort_icon);
        timeSort = (ImageView) layout.findViewById(R.id.time_sort_icon);
        TextView timeSortText, priceSortText;
        timeSortText = (TextView) layout.findViewById(R.id.time_sort_text);
        priceSortText = (TextView) layout.findViewById(R.id.price_sort_text);
        timeSortText.setTypeface(helper.comic);
        priceSortText.setTypeface(helper.comic);
        jain.setTypeface(helper.comic);

        jain.setChecked(sharedData.isJainChecked());
        if(sharedData.isTimeSort()==0 && sharedData.isPriceSort()==0) {
            changeTimeStatus(2);
            changePriceStatus(2);
        }
        else if(sharedData.isTimeSort()==0){
            changePriceStatus(sharedData.isPriceSort()-1);
            changeTimeStatus(2);
        }
        else if(sharedData.isPriceSort()==0){
            changeTimeStatus(sharedData.isTimeSort()-1);
            changePriceStatus(2);
        }
        else{
            changeTimeStatus(2);
            changePriceStatus(2);
        }

        jain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedData.isJainChecked(b);
                adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                    @Override
                    public void onQuantityChange() {
                    }
                });
                vpPager.setAdapter(adapterViewPager);
                //ItemEntities only jain
            }
        });

        priceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePriceStatus(sharedData.isPriceSort());
                changeTimeStatus(2);
            }
        });

        expectedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTimeStatus(sharedData.isTimeSort());
                changePriceStatus(2);
            }
        });

    }

    public void changePriceStatus(int status){

        switch (status){
            case 0:priceLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_highlighted));
                    priceSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    //Sort itementities low to high price
                    sharedData.isPriceSort(1);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    break;

            case 1:priceLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_highlighted));
                    priceSort.setBackground(getResources().getDrawable(R.drawable.high_low));
                    //Sort itementities high to low price
                    sharedData.isPriceSort(2);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    break;

            case 2:priceLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle));
                    priceSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    //Sort itementities high to low price
                    sharedData.isPriceSort(0);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    break;

            default:priceLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle));
                    priceSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    //Sort itementities high to low price
                    sharedData.isPriceSort(0);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
            }
    }


    public void changeTimeStatus(int status){

        switch (status){
            case 0:expectedLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_highlighted));
                    timeSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    //Sort itementities low to high time
                    sharedData.isTimeSort(1);
                    break;

            case 1:expectedLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle_highlighted));
                    timeSort.setBackground(getResources().getDrawable(R.drawable.high_low));
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    //Sort itementities high to low time
                    sharedData.isTimeSort(2);
                    break;

            case 2:expectedLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle));
                    timeSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    sharedData.isTimeSort(0);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
                    break;

            default:expectedLayout.setBackground(getResources().getDrawable(R.drawable.solid_rectangle));
                    timeSort.setBackground(getResources().getDrawable(R.drawable.low_high));
                    sharedData.isTimeSort(0);
                    adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
                        @Override
                        public void onQuantityChange() {
                        }
                    });
                    vpPager.setAdapter(adapterViewPager);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new MaterialDialog.Builder(ItemActivity.this)
                    .title("Confirm Exit")
                    .content("Are you sure, you want to exit?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            finishAffinity();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //
                        }
                    })
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu) {
            //
        } else if (id == R.id.order_history) {
            if(sharedData.isLoggedIn()) {
                Intent intent = new Intent(getApplicationContext(), OrderHistory.class);
                startActivity(intent);
            }
            else{
                userNotLoggedIn();
                navigationView.getMenu().findItem(R.id.menu).setChecked(true);
            }

        } else if (id == R.id.change_canteen) {
            menuUtility.removeUpdating();
            Intent intent = new Intent(getApplicationContext(),ChangeCanteenActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.address) {
            Intent intent = new Intent(getApplicationContext(),Address.class);
            sharedData.setAddrFlag(0);
            startActivity(intent);
        }
        else if (id == R.id.reviews) {
            Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.log_out) {
            if(sharedData.isLoggedIn()) {
                new MaterialDialog.Builder(ItemActivity.this)
                        .title("Confirm Logout")
                        .content("Are you sure, you want to logout?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                intent = new Intent(getApplicationContext(), BackgroundService.class);
                                stopService(intent);
                                firebaseAuth.signOut();
                                sharedData.clear();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                navigationView.getMenu().findItem(R.id.menu).setChecked(true);
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .show();
            }
            else{
                firebaseAuth = FirebaseAuth.getInstance();
                if(firebaseAuth!=null)
                    firebaseAuth.signOut();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        navigationView.getMenu().getItem(0).setChecked(true);
        adapterViewPager = new CategoriesPagerAdapter(getSupportFragmentManager(), categoryEntities.size(), categoryEntities, new OnItemQuantityChangeListener() {
            @Override
            public void onQuantityChange() {

            }
        });
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new DefaultTransformer());
        navigationTabStrip.setViewPager(vpPager);
        //menuUtility.startUpdating();
        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //menuUtility.removeUpdating();
    }


    public void noConnectivityDialog() {
        new MaterialDialog.Builder(ItemActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), ItemActivity.class);
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

    public void userNotLoggedIn(){
        new MaterialDialog.Builder(ItemActivity.this)
                .title("Login Required")
                .content("Login to the app to continue")
                .positiveText("Login")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        firebaseAuth = FirebaseAuth.getInstance();
                        if(firebaseAuth!=null)
                            firebaseAuth.signOut();
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .canceledOnTouchOutside(false)
                .cancelable(true)
                .show();
    }

    public void onProgressStart(){
        materialDialog = new MaterialDialog.Builder(ItemActivity.this)
                .title("Syncing Data")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop(){
        materialDialog.dismiss();
    }
}
