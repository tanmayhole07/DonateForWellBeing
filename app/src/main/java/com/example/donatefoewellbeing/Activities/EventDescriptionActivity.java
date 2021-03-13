package com.example.donatefoewellbeing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donatefoewellbeing.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventDescriptionActivity extends AppCompatActivity {

    private ImageView eventIv;
    private TextView editTv, deleteTv, eventNameTv, eventDescriptionTv, eventDateTv, eventTimeTv, eventLocationTv, eventOrganizationNameTv;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_description);

        eventIv = findViewById(R.id.eventIv);
        editTv = findViewById(R.id.editTv);
        deleteTv = findViewById(R.id.deleteTv);
        eventNameTv = findViewById(R.id.eventNameTv);
        eventDescriptionTv = findViewById(R.id.eventDescriptionTv);
        eventDateTv = findViewById(R.id.eventDateTv);
        eventTimeTv = findViewById(R.id.eventTimeTv);
        eventLocationTv = findViewById(R.id.eventLocationTv);
        eventOrganizationNameTv = findViewById(R.id.eventOrganizationNameTv);

        eventId = getIntent().getStringExtra("eventId");
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDescriptionActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });
        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent();
            }
        });

        loadEventDescription();
    }

    private void deleteEvent() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child("OngoingEvents").orderByChild("timeStamp").equalTo(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                            Toast.makeText(EventDescriptionActivity.this, "Deleted Info Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadEventDescription() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child("OngoingEvents").child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String eventName = "" + snapshot.child("eventName").getValue();
                String eventDescription = "" + snapshot.child("eventDescription").getValue();
                String eventOrganizationName = "" + snapshot.child("eventOrganizationName").getValue();
                String eventDate = "" + snapshot.child("date").getValue();
                String eventTime = "" + snapshot.child("time").getValue();
                String eventLocation = "" + snapshot.child("eventLocation").getValue();
                String eventImage = "" + snapshot.child("eventImage").getValue();

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
}