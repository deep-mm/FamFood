package com.canteenautomation.famfood.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.canteenautomation.famfood.Activity.AddNewAddress;
import com.canteenautomation.famfood.Activity.ItemActivity;
import com.canteenautomation.famfood.Entities.AddressEntity;
import com.canteenautomation.famfood.Entities.FeedbackEntity;
import com.canteenautomation.famfood.Entities.OrderEntity;
import com.canteenautomation.famfood.R;
import com.canteenautomation.famfood.Utilities.Helper;
import com.canteenautomation.famfood.Utilities.OrderUtility;
import com.canteenautomation.famfood.Utilities.SharedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ReviewPageAdapter extends RecyclerView.Adapter<ReviewPageAdapter.MyViewHolder> {
    private final List<FeedbackEntity> dvalues;
    Context context;
    SharedData sharedData;
    Helper helper;

    public ReviewPageAdapter() {
        dvalues = null;
    }

    public ReviewPageAdapter(List<FeedbackEntity> dvalues, Context c) {
        this.dvalues = dvalues;
        context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_review, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        sharedData = new SharedData(context);
        helper = new Helper(context);

        FeedbackEntity feedbackEntity = dvalues.get(position);
        if(feedbackEntity.getUserGender().equalsIgnoreCase("female")){
            holder.profilePic.setBackgroundResource(R.drawable.profile_female);
        }
        else{
            holder.profilePic.setBackgroundResource(R.drawable.profile_male);
        }
        holder.profileName.setText(feedbackEntity.getUserName());
        holder.profileName.setTypeface(helper.comicBold);
        holder.review_text.setText(feedbackEntity.getReview());
        holder.review_text.setTypeface(helper.comic);
        StringBuffer stringBuffer = new StringBuffer(feedbackEntity.getDateTime());
        holder.dateTime.setText(stringBuffer.substring(0,10));
        holder.dateTime.setTypeface(helper.comic);

        RecyclerView.LayoutManager mlayoutmanager;
        mlayoutmanager = new LinearLayoutManager(context);
        holder.feedbackList.setLayoutManager(mlayoutmanager);
        holder.feedbackList.setItemAnimator(new DefaultItemAnimator());

        if(feedbackEntity.getItemReviews()!=null){
            FeedbackItemAdapter feedbackItemAdapter = new FeedbackItemAdapter(feedbackEntity.getItemReviews(), context, 1);
            holder.feedbackList.setAdapter(feedbackItemAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return dvalues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic;
        TextView profileName, review_text, dateTime;
        RecyclerView feedbackList;

        public MyViewHolder(View itemView) {
            super(itemView);
            profilePic = (ImageView) itemView.findViewById(R.id.profile_pic);
            profileName = (TextView) itemView.findViewById(R.id.profile_name);
            review_text = (TextView) itemView.findViewById(R.id.review_text);
            feedbackList = (RecyclerView) itemView.findViewById(R.id.feedback_list);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
        }
    }
}
