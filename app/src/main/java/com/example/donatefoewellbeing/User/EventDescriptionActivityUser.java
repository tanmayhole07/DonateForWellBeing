package com.example.donatefoewellbeing.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donatefoewellbeing.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventDescriptionActivityUser extends AppCompatActivity {

    private ImageView eventIv, trackLocationMapTv;
    private TextView editTv, deleteTv, eventNameTv, eventDescriptionTv, eventDateTv, eventTimeTv, eventLocationTv, eventOrganizationNameTv;
    private Button eventReviewsBtn;

    private String eventId, eventSection, latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description_user);

        eventIv = findViewById(R.id.eventIv);
        editTv = findViewById(R.id.editTv);
        deleteTv = findViewById(R.id.deleteTv);
        eventNameTv = findViewById(R.id.eventNameTv);
        eventDescriptionTv = findViewById(R.id.eventDescriptionTv);
        eventDateTv = findViewById(R.id.eventDateTv);
        eventTimeTv = findViewById(R.id.eventTimeTv);
        eventLocationTv = findViewById(R.id.eventLocationTv);
        eventOrganizationNameTv = findViewById(R.id.eventOrganizationNameTv);
//        trackLocationMapTv = findViewById(R.id.trackLocationMapTv);

        eventReviewsBtn = findViewById(R.id.eventReviewsBtn);

        eventId = getIntent().getStringExtra("eventId");
        eventSection = getIntent().getStringExtra("eventSection");

        eventLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMap();
            }
        });

        eventReviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDescriptionActivityUser.this, EventReviewsActivityUser.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventSection",eventSection);
                startActivity(intent);
            }
        });

        loadEventDescription();

    }

    private void loadEventDescription() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String eventName = "" + snapshot.child("eventName").getValue();
                String eventDescription = "" + snapshot.child("eventDescription").getValue();
                String eventOrganizationName = "" + snapshot.child("eventOrganizationName").getValue();
                String eventDate = "" + snapshot.child("date").getValue();
                String eventTime = "" + snapshot.child("time").getValue();
                String eventLocation = "" + snapshot.child("eventLocation").getValue();
                String eventImage = "" + snapshot.child("eventImage").getValue();
                latitude = ""+snapshot.child("latitude").getValue();
                longitude = ""+snapshot.child("longitude").getValue();

                eventNameTv.setText(eventName);
                eventDescriptionTv.setText(eventDescription);
                eventOrganizationNameTv.setText(eventOrganizationName);
                eventDateTv.setText(eventDate);
                eventTimeTv.setText(eventTime);
                eventLocationTv.setText(eventLocation);

                try {
                    Picasso.get().load(eventImage).into(eventIv);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OpenMap() {
        String address = "https://maps.google.com/maps?saar=" + latitude + "," + longitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

}