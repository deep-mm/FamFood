package com.canteenautomation.famfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.canteenautomation.famfood.Adapter.FeedbackItemAdapter;
import com.canteenautomation.famfood.Adapter.ReviewPageAdapter;
import com.canteenautomation.famfood.Entities.CanteenEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.Entities.UserEntity;
import com.canteenautomation.famfood.Listener.OnFeedbackListChangeListener;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.FeedbackUtility;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.SharedData;
import com.codemybrainsout.ratingdialog.RatingDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private ImageButton addReview, backButton;
    private SharedData sharedData;
    private Helper helper;
    private String canteenName;
    private RatingDialog ratingDialog;
    private String userFeedback;
    private float userRating;
    private TextView pageTitle;
    private List<FeedbackEntity> feedbackEntities;
    private FeedbackUtility feedbackUtility;
    private FeedbackEntity feedbackEntity;
    private MaterialDialog materialDialog;
    private Intent intent;
    private RecyclerView reviewList;
    private RecyclerView.LayoutManager mlayoutmanager;
    private ReviewPageAdapter reviewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initialize();

        if(sharedData.getUserEntity()==null){
            addReview.setVisibility(View.INVISIBLE);
        }
        if(sharedData.getFeedbackEntityList()!=null) {
            feedbackEntities = getList(sharedData.getFeedbackEntityList());
            reviewPageAdapter = new ReviewPageAdapter(feedbackEntities, getApplicationContext());
            reviewList.setAdapter(reviewPageAdapter);
        }
        else{
            feedbackEntities = new ArrayList<>();
            sharedData.setFeedbackEntityList(feedbackEntities);
        }

        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEntity userEntity = sharedData.getUserEntity();
                String feedbackId = userEntity.getFirebaseID()+"_"+helper.currentDateTime();
                feedbackEntity = new FeedbackEntity(feedbackId,userEntity.getFirebaseID(),userEntity.getName(),userEntity.getGender(),helper.currentTime());
                ratingDialog = new RatingDialog.Builder(ReviewActivity.this)
                        .icon(getResources().getDrawable(R.mipmap.app_logo))
                        .title("How was your experience at "+canteenName+"?")
                        .titleTextColor(R.color.black)
                        .threshold(6)
                        .formTitle("Submit FeedbackActivity")
                        .formHint("Please share your valuable feedback")
                        .formSubmitText("Submit")
                        .formCancelText("Cancel")
                        .ratingBarColor(R.color.colorOrange)
                        .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                            @Override
                            public void onRatingSelected(float rating, boolean thresholdCleared) {
                                userRating = rating;
                                feedbackEntity.setOverallReview(rating);
                                feedbackUtility.addFeedback(sharedData.getCanteenEntity().getId(),feedbackEntity);
                            }
                        })
                        .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                            @Override
                            public void onFormSubmitted(String feedback) {
                                userFeedback = feedback;
                                feedbackEntity.setReview(feedback);
                                feedbackUtility.addFeedback(sharedData.getCanteenEntity().getId(),feedbackEntity);
                            }
                        }).build();
                ratingDialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CanteenEntity canteenEntity = sharedData.getCanteenEntity();
        if(canteenEntity!=null){
            feedbackUtility = new FeedbackUtility(canteenEntity.getId(), new OnFeedbackListChangeListener() {
                @Override
                public void onDataChanged(List<FeedbackEntity> feedbackEntityList) {
                    if(feedbackEntityList!=null)
                        sharedData.setFeedbackEntityList(feedbackEntityList);
                        feedbackEntities = getList(sharedData.getFeedbackEntityList());
                        reviewPageAdapter = new ReviewPageAdapter(feedbackEntities,getApplicationContext());
                        reviewList.setAdapter(reviewPageAdapter);
                }

                @Override
                public void onErrorOccured() {

                }
            });
        }
    }

    public Date getDate(String sDate1){
        Date date1= null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(sDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }


    public List<FeedbackEntity> getList(List<FeedbackEntity> feedbackEntities1){
        List<FeedbackEntity> fe = new ArrayList<>();
        for(int i=0;i<feedbackEntities1.size();i++){
            FeedbackEntity feedbackEntity = feedbackEntities1.get(i);
            if(feedbackEntity.getReview()!=null || feedbackEntity.getItemReviews()!=null){
                fe.add(feedbackEntity);
            }
        }
        Collections.sort(fe, new Comparator<FeedbackEntity>() {
            public int compare(FeedbackEntity o1, FeedbackEntity o2) {
                return getDate(o1.getDateTime()).compareTo(getDate(o2.getDateTime()));
            }
        });

        Collections.reverse(fe);
        return fe;
    }

    public void initialize(){
        sharedData = new SharedData(getApplicationContext());
        helper = new Helper(getApplicationContext());
        addReview = (ImageButton) findViewById(R.id.add_review_button);
        canteenName = sharedData.getCanteenEntity().getName();
        feedbackUtility = new FeedbackUtility(sharedData.getCanteenEntity().getId());
        pageTitle = (TextView) findViewById(R.id.pageTitle);
        backButton = (ImageButton) findViewById(R.id.back_button);
        assignFonts();
        reviewList = (RecyclerView) findViewById(R.id.review_list);
        mlayoutmanager = new LinearLayoutManager(getApplicationContext());
        reviewList.setLayoutManager(mlayoutmanager);
        reviewList.setItemAnimator(new DefaultItemAnimator());
    }

    public void assignFonts(){
        pageTitle.setTypeface(helper.cambriaBold);
    }

    public void onProgressStart() {
        materialDialog = new MaterialDialog.Builder(ReviewActivity.this)
                .title("Syncing Data")
                .content("Please Wait")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public void onProgressStop() {
        materialDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        feedbackUtility.startUpdating();

        if (!helper.isNetworkAvailable()) {
            noConnectivityDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        feedbackUtility.removeUpdating();
    }

    public void noConnectivityDialog() {
        new MaterialDialog.Builder(ReviewActivity.this)
                .title("No Internet Connection")
                .content("Phone is not connected to interent. Make sure you have an active internet connection")
                .positiveText("Retry")
                .negativeText("Exit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        intent = new Intent(getApplicationContext(), FeedbackActivity.class);
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

    public void checkLogin() {
        if (!sharedData.isSignedUp()) {
            sharedData.clear();
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
