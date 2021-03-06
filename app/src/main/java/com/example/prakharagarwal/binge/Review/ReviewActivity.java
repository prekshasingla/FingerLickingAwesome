package com.example.prakharagarwal.binge.Review;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prakharagarwal.binge.LoginActivity;
import com.example.prakharagarwal.binge.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReviewActivity extends FragmentActivity {

    RecyclerView mRecyclerView;
    ReviewTextAdapter reviewTextAdapter;
    List<Review> reviews;
    SlidingUpPanelLayout slidingUpPanelLayout;
    String id;
    static TextView emptyView;
    String restaurantName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        reviews=new ArrayList<Review>();
        emptyView = (TextView)findViewById(R.id.review_text_empty);


        slidingUpPanelLayout=(SlidingUpPanelLayout)findViewById(R.id.reviews_sliding_up);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState== SlidingUpPanelLayout.PanelState.EXPANDED)
                    ((ImageView)findViewById(R.id.review_text_heading_image)).setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);


                if(newState== SlidingUpPanelLayout.PanelState.COLLAPSED)
                    ((ImageView)findViewById(R.id.review_text_heading_image)).setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);

            }
        });


        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);

        mRecyclerView=(RecyclerView)findViewById(R.id.review_text_fragment_recycler_view);
        reviewTextAdapter= new ReviewTextAdapter(this,reviews);
        try{
            mRecyclerView.setAdapter(reviewTextAdapter);
        }catch (NoClassDefFoundError e){

        }
        mRecyclerView.setLayoutManager(linearLayoutManager);
        checkIfEmpty();

        TextView buttonWriteReview=(TextView) findViewById(R.id.button_write_review);
        buttonWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
//            prefs.edit().clear();
                String uID = prefs.getString("username", null);
                if (uID == null) {
                    Intent intent = new Intent(ReviewActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ReviewActivity.this, WriteReviewActivity.class);
                    intent.putExtra("restaurantID", id);
                    intent.putExtra("restaurantName", restaurantName);
                    intent.putExtra("user", uID);
                    startActivity(intent);

                }
            }
        });



    }


    public void addAllReviews(List<Review> reviews, String id, String restaurantName){

        this.id=id;
        this.restaurantName=restaurantName;
        reviewTextAdapter.addAll(reviews);
        reviewTextAdapter.notifyDataSetChanged();
        checkIfEmpty();
    }

    void checkIfEmpty() {
        if (emptyView != null && reviewTextAdapter != null) {
            final boolean emptyViewVisible =
                    reviewTextAdapter.getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            mRecyclerView.setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }


    public SlidingUpPanelLayout getSlidingUpPanelLayout(){
        return slidingUpPanelLayout;
    }

}