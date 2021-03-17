package com.example.donatefoewellbeing.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.donatefoewellbeing.CommonActivities.LoginActivity;
import com.example.donatefoewellbeing.Models.ModelTask;
import com.example.donatefoewellbeing.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView eventPicIv;
    private EditText eventNameEt, eventDescriptionEt, eventOrganizationNameEt, dateEt, timeEt, eventLocationEt;
    private TextView toolbarText;
    private Button updateEventBtn;
    private ImageButton backBtn;
    String eventId, eventSection;
    private FloatingActionButton step1CompleteFab;

    private ConstraintLayout layout_add_product_1, layout_add_product_2;

    private GoogleMap mMap;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //image pick constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    ModelTask model = new ModelTask();
    private LatLng ltl = new LatLng(36.7783, 119.4179);
    private int taskFlag = 0;
    String Latitude, Longitude;
    LatLng oldlatLng;
    Marker oldMarker;
    Marker newMarkr;


    ModelTask passedIntent = new ModelTask();

    private Uri image_uri;
    private ProgressDialog pd;
    String mUID = "uid";

    //Firebase Variables
    FirebaseAuth firebaseAuth;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventId = getIntent().getStringExtra("eventId");
        eventSection = getIntent().getStringExtra("eventSection");

        eventPicIv = findViewById(R.id.eventPicIv);
        eventNameEt = findViewById(R.id.eventNameEt);
        eventDescriptionEt = findViewById(R.id.eventDescriptionEt);
        eventOrganizationNameEt = findViewById(R.id.eventOrganizationNameEt);
        dateEt = findViewById(R.id.dateEt);
        timeEt = findViewById(R.id.timeEt);
        eventLocationEt = findViewById(R.id.eventLocationEt);
        step1CompleteFab = findViewById(R.id.step1CompleteFab);
        updateEventBtn = findViewById(R.id.addEventBtn);
        toolbarText = findViewById(R.id.toolbarText);
        backBtn = findViewById(R.id.backBtn);
        layout_add_product_1 = findViewById(R.id.layout_add_product_1);
        layout_add_product_2 = findViewById(R.id.layout_add_product_2);

        passedIntent = (ModelTask) getIntent().getSerializableExtra("clickedData");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dateEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeEt.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        eventPicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });

        step1CompleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData1();
            }
        });

        updateEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData2();
            }
        });

        if(passedIntent != null){
            oldlatLng = new LatLng(passedIntent.getLatitude(),passedIntent.getLongitude());
        }

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkUserStatus();

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEt.setText(sdf.format(myCalendar.getTime()));
    }


    private String eventName, eventDescription, eventOrganizationName, date, time, eventLocation;

    private void inputData1() {

        eventName = eventNameEt.getText().toString().trim();
        eventDescription = eventDescriptionEt.getText().toString().trim();
        eventOrganizationName = eventOrganizationNameEt.getText().toString().trim();
        date = dateEt.getText().toString().trim();
        time = timeEt.getText().toString().trim();

        if (TextUtils.isEmpty(eventName)) {
            Toast.makeText(EditEventActivity.this, "Event Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventDescription)) {
            Toast.makeText(EditEventActivity.this, "Event Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(eventOrganizationName)) {
            Toast.makeText(EditEventActivity.this, "Event Organizer Name is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(EditEventActivity.this, "Event Date is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(time)) {
            Toast.makeText(EditEventActivity.this, "Event time is required...", Toast.LENGTH_SHORT).show();
            return;
        }


        layout2();

    }

    private void inputData2() {
        eventLocation = eventLocationEt.getText().toString().trim();
        if (TextUtils.isEmpty(eventLocation)) {
            Toast.makeText(EditEventActivity.this, "Event time is required...", Toast.LENGTH_SHORT).show();
            return;
        }

        updateEvent();
    }

    private void layout2() {
        layout_add_product_2.setVisibility(View.VISIBLE);
        layout_add_product_1.setVisibility(View.GONE);
    }

    private void layout1() {
        layout_add_product_2.setVisibility(View.GONE);
        layout_add_product_1.setVisibility(View.VISIBLE);
    }

    private void updateEvent() {
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
//            hashMap.put("latitude", "" + model.getLatitude());
//            hashMap.put("longitude", "" + model.getLongitude());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
            ref.child(eventSection).child(eventId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(EditEventActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
//                                hashMap.put("latitude", "" + model.getLatitude());
//                                hashMap.put("longitude", "" + model.getLongitude());

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Events");
                                ref.child(eventSection).child(eventId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(EditEventActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(EditEventActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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


                eventNameEt.setText(eventName);
                eventDescriptionEt.setText(eventDescription);
                eventOrganizationNameEt.setText(eventOrganizationName);
                dateEt.setText(eventDate);
                timeEt.setText(eventTime);
                eventLocationEt.setText(eventLocation);

                try {
                    Picasso.get().load(eventImage).into(eventPicIv);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
//        ContentValues cv = new ContentValues();
//        cv.put(MediaStore.Images.Media.TITLE,"Temp_Image Title");
//        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");
//
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
//        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
//                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(90, 90)
                .setActivityTitle("Crop Image")
                .setFixAspectRatio(true)
                .setCropMenuCropButtonTitle("Done")
                .start(this);
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
//                image_uri = data.getData();
//                eventPicIv.setImageURI(image_uri);

                image_uri = data.getData();
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(90, 90)
                        .setActivityTitle("Crop Image")
                        .setFixAspectRatio(true)
                        .setCropMenuCropButtonTitle("Done")
                        .start(this);

            }
//            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
//                eventPicIv.setImageURI(image_uri);
//            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_uri = result.getUri();
                eventPicIv.setImageURI(image_uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                eventPicIv.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            updateEventBtn.setText("Update Event");
            layout1();
            loadEventDescription();

        } else {
            startActivity(new Intent(EditEventActivity.this, LoginActivity.class));
            finish();
        }
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null)
                return currentLocation;
            else{
                currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                return currentLocation;
            }
        }
        else{
            // ask the user to turn on the GPS
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        taskFlag = 0;
        // showing the current location
        final Location location = getLocation();
        LatLng currentLoc = new LatLng(model.getLatitude(), model.getLongitude());
        if(passedIntent != null){

            oldMarker = mMap.addMarker(new MarkerOptions().position(oldlatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldlatLng,15));
        }else{
            newMarkr = mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15));

        }

        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);



        // https://stackoverflow.com/questions/24302112/how-to-get-the-latitude-and-longitude-of-location-where-user-taps-on-the-map-in
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // remove marker when new one added
                if(taskFlag == 0) {
                    taskFlag = 1;
                    if(passedIntent != null){
                        oldMarker.remove();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(100)
                            .fillColor(0x40ff0000)  //semi-transparent
                            .strokeColor(Color.RED)
                            .strokeWidth(5);
                    mMap.addCircle(circleOptions);
                    ltl = latLng;
                    if(passedIntent != null){
                        passedIntent.setLatitude(ltl.latitude);
                        passedIntent.setLongitude(ltl.longitude);
                    }else{
                        model.setLatitude(ltl.latitude);
                        model.setLongitude(ltl.longitude);
                    }
                }
            }
        });
    }
}