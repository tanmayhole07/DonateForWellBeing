package com.example.donatefoewellbeing.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donatefoewellbeing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddEventReviewActivity extends AppCompatActivity {

    private String eventId, eventSection;
    private ImageButton backBtn;
    private TextView eventNameTv, eventDescriptionTv, eventOrganizationNameTv, dateTv, timeTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button submitBtn;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_review);

        eventId = getIntent().getStringExtra("eventId");
        eventSection = getIntent().getStringExtra("eventSection");

        backBtn = findViewById(R.id.backBtn);
        eventNameTv = findViewById(R.id.eventNameTv);
        eventDescriptionTv = findViewById(R.id.eventDescriptionTv);
        eventOrganizationNameTv = findViewById(R.id.eventOrganizationNameTv);
        dateTv = findViewById(R.id.dateTv);
        timeTv = findViewById(R.id.timeTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        loadEventDescription();
        loadMyReview();

    }

    private void inputData() {
        String ratings = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("ratings", ""+ratings);
        hashMap.put("review", ""+review);
        hashMap.put("timestamp", ""+timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddEventReviewActivity.this, "Review published successfully", Toast.LENGTH_SHORT).show();
                        clearData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEventReviewActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearData() {
        ratingBar.setRating((float) 0.0);
        reviewEt.setText("");
        reviewEt.setHint("Write a review");

        onBackPressed();
    }

    private void loadMyReview() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadEventDescription() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String eventName = "" + snapshot.child("eventName").getValue();
                String eventDescription = "" + snapshot.child("eventDescription").getValue();
                String eventOrganizationName = "" + snapshot.child("eventOrganizationName").getValue();
                String date = "" + snapshot.child("date").getValue();
                String time = "" + snapshot.child("time").getValue();

                eventNameTv.setText(eventName);
                eventDescriptionTv.setText(eventDescription);
                eventOrganizationNameTv.setText(eventOrganizationName);
                dateTv.setText(date);
                timeTv.setText(time);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}