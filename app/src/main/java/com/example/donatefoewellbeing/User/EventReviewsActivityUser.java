package com.example.donatefoewellbeing.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.donatefoewellbeing.Adapters.AdapterReview;
import com.example.donatefoewellbeing.Models.ModelReview;
import com.example.donatefoewellbeing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventReviewsActivityUser extends AppCompatActivity {

    private String eventId, eventSection;
    private ImageButton backBtn;
    private TextView eventNameTv, eventDescriptionTv, eventOrganizationNameTv, ratingsTv;
    private RecyclerView reviewsRv;
    private RatingBar ratingBar;
    private Button eventReviewsBtn;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_reviews_user);

        backBtn = findViewById(R.id.backBtn);
        eventNameTv = findViewById(R.id.eventNameTv);
        eventDescriptionTv = findViewById(R.id.eventDescriptionTv);
        eventOrganizationNameTv = findViewById(R.id.eventOrganizationNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewsRv = findViewById(R.id.reviewsRv);
        ratingsTv = findViewById(R.id.ratingsTv);

        eventReviewsBtn = findViewById(R.id.eventReviewsBtn);

        eventId = getIntent().getStringExtra("eventId");
        eventSection = getIntent().getStringExtra("eventSection");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        eventReviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventReviewsActivityUser.this, AddEventReviewActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventSection",eventSection);
                startActivity(intent);
            }
        });

        loadEventDescription();
        loadReviews();

    }

    private void loadEventDescription() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String eventName = "" + snapshot.child("eventName").getValue();
                String eventDescription = "" + snapshot.child("eventDescription").getValue();
                String eventOrganizationName = "" + snapshot.child("eventOrganizationName").getValue();

                eventNameTv.setText(eventName);
                eventDescriptionTv.setText(eventDescription);
                eventOrganizationNameTv.setText(eventOrganizationName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float ratimgSum = 0;
    private void loadReviews() {

        reviewArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArrayList.clear();
                        ratimgSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratimgSum = ratimgSum + rating;

                            ModelReview modelReview = ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }
                        adapterReview = new AdapterReview(EventReviewsActivityUser.this, reviewArrayList);
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratimgSum / numberOfReviews;

                        ratingsTv.setText(String.format("%.2f", avgRating) + "[" + numberOfReviews + "]");
                        ratingBar.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}