package com.example.donatefoewellbeing.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donatefoewellbeing.Admin.Main.AdminProfileActivity;
import com.example.donatefoewellbeing.Admin.Main.OngoingEventsActivity;
import com.example.donatefoewellbeing.Admin.Main.RecentEventsActivity;
import com.example.donatefoewellbeing.Admin.Main.UpcommingEventsActivity;
import com.example.donatefoewellbeing.CommonActivities.LoginActivity;
import com.example.donatefoewellbeing.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView ongoingEventsCv, upcommingEventsCv, recentEventsCv, adminProfileCv, logoutCv;
    private TextView nameTv;
    private ImageView profileIv;

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
        logoutCv = findViewById(R.id.logoutCv);
        nameTv = findViewById(R.id.nameTv);
        profileIv = findViewById(R.id.profileIv);

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        logoutCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
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

        checkUserStatus();


    }

    private void loadUserData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String deliveryAddress = "" + ds.child("address").getValue();
                            String profileImage = "" + ds.child("image").getValue();

                            nameTv.setText(name);
//                            emailTv.setText(email);
//                            phoneTv.setText(phone);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.admin_profile_bg).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.logo1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            loadUserData();

        } else {
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        }
    }
}