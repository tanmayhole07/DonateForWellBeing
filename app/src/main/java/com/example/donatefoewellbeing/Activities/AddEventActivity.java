package com.example.donatefoewellbeing.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.donatefoewellbeing.LoginActivity;
import com.example.donatefoewellbeing.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    private ImageView eventPicIv;
    private EditText eventNameEt, eventDescriptionEt, eventOrganizationNameEt, dateEt, timeEt, eventLocationEt;
    private Button addEventBtn;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;


    private Uri image_uri;
    private ProgressDialog pd;
    String mUID = "uid";

    //Firebase Variables
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventPicIv = findViewById(R.id.eventPicIv);
        eventNameEt = findViewById(R.id.eventNameEt);
        eventDescriptionEt = findViewById(R.id.eventDescriptionEt);
        eventOrganizationNameEt = findViewById(R.id.eventOrganizationNameEt);
        dateEt = findViewById(R.id.dateEt);
        timeEt = findViewById(R.id.timeEt);
        eventLocationEt = findViewById(R.id.eventLocationEt);
        addEventBtn = findViewById(R.id.addEventBtn);

        eventPicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();


        checkUserStatus();

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String eventName, eventDescription, eventOrganizationName, date, time, eventLocation;
    private void inputData() {

        eventName = eventNameEt.getText().toString().trim();
        eventDescription = eventDescriptionEt.getText().toString().trim();
        eventOrganizationName = eventOrganizationNameEt.getText().toString().trim();
        date = dateEt.getText().toString().trim();
        time = timeEt.getText().toString().trim();
        eventLocation = eventLocationEt.getText().toString().trim();

        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(AddEventActivity.this, "Event Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventDescription)) {
            Toast.makeText(AddEventActivity.this, "Event Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventOrganizationName)) {
            Toast.makeText(AddEventActivity.this, "Event Organizer Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(AddEventActivity.this, "Event Date is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(AddEventActivity.this, "Event time is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventLocation)) {
            Toast.makeText(AddEventActivity.this, "Event Location is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        addEvent();

    }

    private void addEvent() {
        pd.setMessage("Adding Event");
        if (image_uri==null){

            final String timeStamp = "" + System.currentTimeMillis();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("eventName", "" + eventName);
            hashMap.put("eventDescription", "" + eventDescription);
            hashMap.put("eventOrganizationName", "" + eventOrganizationName);
            hashMap.put("date", "" + date);
            hashMap.put("time", "" + time);
            hashMap.put("eventLocation", "" + eventLocation);
            hashMap.put("timeStamp", "" + timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
            ref.child("OngoingEvents").child(timeStamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AddEventActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            final String timeStamp = "" + System.currentTimeMillis();

            String filePathAndName = "event_images/" +""+timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("eventName", "" + eventName);
                                hashMap.put("eventImage", "" + downloadImageUri);
                                hashMap.put("eventDescription", "" + eventDescription);
                                hashMap.put("eventOrganizationName", "" + eventOrganizationName);
                                hashMap.put("date", "" + date);
                                hashMap.put("time", "" + time);
                                hashMap.put("eventLocation", "" + eventLocation);
                                hashMap.put("timeStamp", "" + timeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                                ref.child("OngoingEvents").child(timeStamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddEventActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Camera & Storage permissions, requests and setting Data//

    private void showImagePicDialog() {

        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0){
                            //camera clicked
                            if (checkCameraPermission()){
                                pickFromCamera();
                            }else {
                                requestCameraPermission();
                            }
                        }else {
                            //gallery clicked
                            if (checkStoragePermission()){
                                pickFromGallery();
                            }else {
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();

    }

    private void pickFromCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Permission request and Activity result//


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "Camera permission is required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "Storage permission is required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                eventPicIv.setImageURI(image_uri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                eventPicIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();


        } else {
            startActivity(new Intent(AddEventActivity.this, LoginActivity.class));
            finish();
        }
    }

}