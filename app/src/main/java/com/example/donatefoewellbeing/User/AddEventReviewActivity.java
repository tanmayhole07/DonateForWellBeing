package com.example.donatefoewellbeing.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.donatefoewellbeing.R;

public class AddEventReviewActivity extends AppCompatActivity {

    private String eventId, eventSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_review);

        eventId = getIntent().getStringExtra("eventId");
        eventSection = getIntent().getStringExtra("eventSection");

    }
}