package com.example.donatefoewellbeing.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.donatefoewellbeing.Admin.Main.AdminProfileActivity;
import com.example.donatefoewellbeing.Admin.Main.OngoingEventsActivity;
import com.example.donatefoewellbeing.Admin.Main.RecentEventsActivity;
import com.example.donatefoewellbeing.Admin.Main.UpcommingEventsActivity;
import com.example.donatefoewellbeing.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView ongoingEventsCv, upcommingEventsCv, recentEventsCv, adminProfileCv, logout;

    FirebaseAuth firebaseAuth;

    String mUID = "uid";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        ongoingEventsCv = findViewById(R.id.ongoingEventsCv);
        upcommingEventsCv = findViewById(R.id.upcommingEventsCv);
        recentEventsCv = findViewById(R.id.recentEventsCv);
        adminProfileCv = findViewById(R.id.adminProfileCv);
        logout = findViewById(R.id.logout);

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
            }
        });

        ongoingEventsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, OngoingEventsActivity.class));
            }
        });

        upcommingEventsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, UpcommingEventsActivity.class));
            }
        });

        recentEventsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, RecentEventsActivity.class));
            }
        });

        adminProfileCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminProfileActivity.class));
            }
        });

    }
}