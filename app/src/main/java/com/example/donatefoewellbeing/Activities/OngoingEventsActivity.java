package com.example.donatefoewellbeing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.donatefoewellbeing.Adapters.AdapterOngoingEvent;
import com.example.donatefoewellbeing.AdminDashboardActivity;
import com.example.donatefoewellbeing.Models.ModelOngoingEvent;
import com.example.donatefoewellbeing.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingEventsActivity extends AppCompatActivity {

    private FloatingActionButton addEventFab;
    private ImageButton backBtn;
    private RecyclerView eventsRv;

    private ArrayList<ModelOngoingEvent> ongoingEventList;
    private AdapterOngoingEvent adapterOngoingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_events);

        addEventFab = findViewById(R.id.addEventFab);
        backBtn = findViewById(R.id.backBtn);
        eventsRv = findViewById(R.id.eventsRv);

        //firebase Variables
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();

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

        loadOngoingEvents();
    }

    private void loadOngoingEvents() {

        ongoingEventList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child("OngoingEvents")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ongoingEventList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelOngoingEvent modelOngoingEvent = ds.getValue(ModelOngoingEvent.class);
                            ongoingEventList.add(modelOngoingEvent);
                        }
                        adapterOngoingEvent = new AdapterOngoingEvent(OngoingEventsActivity.this, ongoingEventList);
                        eventsRv.setAdapter(adapterOngoingEvent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}