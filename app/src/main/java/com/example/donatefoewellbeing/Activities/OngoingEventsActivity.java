package com.example.donatefoewellbeing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.donatefoewellbeing.AdminDashboardActivity;
import com.example.donatefoewellbeing.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OngoingEventsActivity extends AppCompatActivity {

    FloatingActionButton addEventFab;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_events);

        addEventFab = findViewById(R.id.addEventFab);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OngoingEventsActivity.this, AddEventActivity.class));

            }
        });
    }
}