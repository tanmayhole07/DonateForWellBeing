package com.example.donatefoewellbeing.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.io.File;
import java.io.FileOutputStream;

public class EventDescriptionActivityUser extends AppCompatActivity {

    private ImageView eventIv, trackLocationMapTv;
    private TextView editTv, deleteTv, eventNameTv, eventDescriptionTv, eventDateTv, eventTimeTv, eventLocationTv, eventOrganizationNameTv;
    private Button eventReviewsBtn, shareEventBtn;

    private String eventId, eventSection, latitude, longitude;

    private String eventName, eventDescription, eventOrganizationName, eventDate, eventTime;

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
        shareEventBtn = findViewById(R.id.shareEventBtn);

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

        shareEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) eventIv.getDrawable();
                if (bitmapDrawable == null){
                    //post without image
                    shareTextOnly(eventName, eventDescription, eventOrganizationName, eventDate, eventTime);
                }else {
                    // Post with image
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndIext(eventName, eventDescription, eventOrganizationName, eventDate, eventTime, bitmap);
                }

            }
        });

        loadEventDescription();

    }

    private void shareImageAndIext(String eventName, String eventDescription, String eventOrganizationName, String eventDate, String eventTime,  Bitmap bitmap) {

        String shareBody = "Event Name : "+ eventName + "\n" + "\n" +
                "Description : "+ eventDescription + "\n" + "\n" +
                "Organised by : "+ eventOrganizationName + "\n" + "\n" +
                "Date/Time : "+ "\n" + eventDate + " : " + eventTime;

        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sIntent.setType("image/png");
        startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.example.donatefoewellbeing.fileprovider", file);

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String pTitle, String pDescription, String eventOrganizationName, String eventDate, String eventTime) {
        String shareBody = "Event Name : "+ eventName + "\n" +
                            "Description : "+ eventDescription + "\n" +
                            "Organised by : "+ eventOrganizationName +
                            "Date/Time : "+ "\n" + eventDate + " : \t" + eventTime;

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private void loadEventDescription() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
        ref.child(eventSection).child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                eventName = "" + snapshot.child("eventName").getValue();
                eventDescription = "" + snapshot.child("eventDescription").getValue();
                eventOrganizationName = "" + snapshot.child("eventOrganizationName").getValue();
                eventDate = "" + snapshot.child("date").getValue();
                eventTime = "" + snapshot.child("time").getValue();
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