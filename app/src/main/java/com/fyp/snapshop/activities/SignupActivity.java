package com.fyp.snapshop.activities;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.fyp.snapshop.JavaClass.Validation;
import com.fyp.snapshop.Models.Model;
import com.fyp.snapshop.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etEmail, etAddress, etDateOfBirth, etPassword,  etPrice ;
    private TextInputLayout tilName, tilPhone, tilEmail, tilAddress, tilDateOfBirth, tilPassword,  tilPrice ;
    private String name="",number="",email="",dob="",address="",password="",price="",type="";
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private Button btnRegister;
    private RadioGroup radioGroup;
    RadioButton rbEditor, rbCustomer;
    private ImageView ivProfileImage;
    private static final int Gallery_Req = 1;
    public Uri ImageUri;
    private int flag = 0;
    private FlatDialog flatDialog;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef;
    String downloadUrl;


    //..........
    String lat,lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getLocation();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database'
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        flatDialog = new FlatDialog(this);

        bindViews();
        listeners();
        calander();
        checkNetworkStatus();

    }

    private void bindViews() {

        ivProfileImage = findViewById(R.id.iv_profile_image);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_number);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etDateOfBirth = findViewById(R.id.et_dob);
        etPassword = findViewById(R.id.et_password);
        etPrice = findViewById(R.id.text_input_edittext_price);
        tilName = findViewById(R.id.til_name);
        tilPhone= findViewById(R.id.til_number);
        tilEmail= findViewById(R.id.til_email);
        tilAddress= findViewById(R.id.til_address);
        tilDateOfBirth= findViewById(R.id.til_dob);
        tilPassword= findViewById(R.id.til_password);
        tilPrice= findViewById(R.id.til_price);
        radioGroup = findViewById(R.id.radio_group);
        rbEditor = findViewById(R.id.rb_editor);
        rbCustomer = findViewById(R.id.rb_customer);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.spin_kit);
        scrollView = findViewById(R.id.scrollview);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void calander() {
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MMM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            etDateOfBirth.setText(sdf.format(myCalendar.getTime()));             };

        etDateOfBirth.setOnTouchListener((v, event) -> {
            new DatePickerDialog(SignupActivity.this,R.style.DatePickerTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar
                    .get(Calendar.MONTH), myCalendar
                    .get(Calendar.DAY_OF_MONTH)).show();
            return true;
        });

    }

    private void listeners() {
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i)
            {
                case R.id.rb_editor:
                    tilPrice.setVisibility(View.VISIBLE);
                    type = "editor";

                    break;
                case R.id.rb_customer:
                    type = "customer";
                    etPrice.setVisibility(View.GONE);
                    break;
            }
        });

        btnRegister.setOnClickListener(view -> checkValidation());

        ivProfileImage.setOnClickListener(view -> {
            Intent picture = new Intent();
            picture.setAction(Intent.ACTION_GET_CONTENT);
            picture.setType("image/*");
            startActivityForResult(picture, Gallery_Req);
            flag = 10;
        });
    }

    private void checkNetworkStatus() {

        Tovuti.from(getApplicationContext()).monitor((connectionType, isConnected, isFast) -> showAlertDialog(isConnected));
    }

    private void showAlertDialog(Boolean isConnected) {

        if (flatDialog != null) {
            flatDialog.setTitle("Something going wrong").
                    setSubtitle("Choose an action")
                    .setFirstButtonText("Go to WIFI setting").
                    setSecondButtonText("Go to Network setting");
            if (!isConnected) {

                flatDialog.withFirstButtonListner(view -> {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                });
                flatDialog.withSecondButtonListner(view -> startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)));
                flatDialog.show();
            } else {

                flatDialog.dismiss();
            }
        }
    }

    private void checkValidation() {

        //Get Text From EditText
        email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        name = Objects.requireNonNull(etName.getText()).toString().trim();
        number = Objects.requireNonNull(etPhone.getText()).toString().trim();
        address = Objects.requireNonNull(etAddress.getText()).toString().trim();
        price = Objects.requireNonNull(etPrice.getText()).toString().trim();
        dob = Objects.requireNonNull(etDateOfBirth.getText()).toString().trim();

        //check logo
        if(ImageUri==null){
            Toast.makeText(SignupActivity.this, "Please upload your Profile Picture", Toast.LENGTH_LONG).show();
        }
        else {
            if (TextUtils.isEmpty(name)) {
                tilName.setError("Name is required");
                setFocus(etName);
            } else if (!Validation.checkTextPattern(name)) {
                tilName.setError("Name is invalid");
                setFocus(etName);
            }  else if (TextUtils.isEmpty(number)) {
                tilName.setErrorEnabled(false);
                tilPhone.setError("Phone number is required");
                setFocus(etPhone);
            } else if (!Validation.checkPhone(number)) {
                tilName.setErrorEnabled(false);
                tilPhone.setError("Phone number not in correct format");
                setFocus(etPhone);

            } else if (TextUtils.isEmpty(email)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setError("Email is required");
                setFocus(etEmail);
            } else if (!Validation.checkMail(email)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setError("Email is invalid");
                setFocus(etEmail);
            }  else if (TextUtils.isEmpty(address)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setError("Address is required");
                setFocus(etAddress);
            } else if (TextUtils.isEmpty(dob)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setErrorEnabled(false);
                tilDateOfBirth.setError("Date Of Birth is required");
                setFocus(etDateOfBirth);
            }else if (TextUtils.isEmpty(password)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setErrorEnabled(false);
                tilDateOfBirth.setErrorEnabled(false);
                tilPassword.setError("Password is required");
                setFocus(etPassword);
            } else if (!Validation.checkPassword(password)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setErrorEnabled(false);
                tilDateOfBirth.setErrorEnabled(false);
                tilPassword.setError("Password must contain at least 6 letters and must contain at least 1 Capital,special character and number");
                setFocus(etPassword);

            }
            else if (radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please Check The Type", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(price)) {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setErrorEnabled(false);
                tilDateOfBirth.setErrorEnabled(false);
                tilPassword.setErrorEnabled(false);
                tilPrice.setError("Price is required");
                setFocus(etPrice);
            }else {
                tilName.setErrorEnabled(false);
                tilPhone.setErrorEnabled(false);
                tilEmail.setErrorEnabled(false);
                tilAddress.setErrorEnabled(false);
                tilPassword.setErrorEnabled(false);
                tilDateOfBirth.setErrorEnabled(false);
                tilPrice.setErrorEnabled(false);
                Toast.makeText(SignupActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }
            authentication();
        }
    }

    private void authentication() {
        scrollView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Sprite cubeGrid = new FoldingCube();
        progressBar.setIndeterminateDrawable(cubeGrid);
        try {


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            saveData();
                            sharedpreff();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            scrollView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        }
        catch (Exception e)
        {
            Toast.makeText(SignupActivity.this, ""+e, Toast.LENGTH_SHORT).show();
            scrollView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void saveData() {


//        if(lat!=null && lang!=null) {
            Model model = new Model(name, number, email, address, password, type, user.getUid(), dob, price);
            //Database Storage
            //Firebase Storage
            StorageReference storageImage = FirebaseStorage.getInstance().getReference().child("SnapShop pics");

            if (type.equals("customer")) {
                if (ImageUri != null) {
                    final StorageReference filepath = storageImage.child(Objects.requireNonNull(ImageUri.getLastPathSegment()));
                    filepath.putFile(ImageUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadUrl = uri.toString();

                        myRef.child("customer").child(user.getUid()).child("image").setValue(downloadUrl);
                        myRef.child("All user").child(user.getUid()).child("image").setValue(downloadUrl);
                    }));
                }

                myRef.child("customer").child(user.getUid()).setValue(model);

                myRef.child("All user").child(user.getUid()).setValue(model);
            }
            if (type.equals("editor")) {
                if (ImageUri != null) {
                    final StorageReference filepath = storageImage.child(Objects.requireNonNull(ImageUri.getLastPathSegment()));
                    filepath.putFile(ImageUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadUrl = uri.toString();

                        myRef.child("editor").child(user.getUid()).child("image").setValue(downloadUrl);
                        myRef.child("All user").child(user.getUid()).child("image").setValue(downloadUrl);
                    }));
                }

                myRef.child("editor").child(user.getUid()).setValue(model);
                myRef.child("All user").child(user.getUid()).setValue(model);



                myRef.child("editor").child(user.getUid()).child("latitude").setValue(lat);
                myRef.child("editor").child(user.getUid()).child("longitude").setValue(lang);

                myRef.child("All user").child(user.getUid()).child("latitude").setValue(lat);
                myRef.child("All user").child(user.getUid()).child("longitude").setValue(lang);


            }


    }

    //set focus on error every time
    private void setFocus(TextInputEditText textInputEditText) {
        textInputEditText.post(() -> {
            textInputEditText.requestFocusFromTouch();
            //noinspection AccessStaticViaInstance
            InputMethodManager lManager = (InputMethodManager) SignupActivity.this.getSystemService(SignupActivity.this.INPUT_METHOD_SERVICE);
            assert lManager != null;
            lManager.showSoftInput(textInputEditText, 0);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Req && resultCode == RESULT_OK) {
            assert data != null;
            Uri imageuri = data.getData();
            assert imageuri != null;
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;
            ImageUri = result.getUri();
            if (resultCode == RESULT_OK) {
                if (flag == 10) {
                    Glide.with(this).load(ImageUri).apply(RequestOptions.circleCropTransform()).into(ivProfileImage);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error 2" + error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void sharedpreff(){

        SharedPreferences prefs = getSharedPreferences("firebase_token", Context.MODE_PRIVATE);
        String tok = prefs.getString("token","");
        Log.e("tokenFalse ",tok);

        Model model =  new Model(name,number,email,address,password,type,user.getUid(),dob,price);
        if(type.equals("customer"))
        {
            myRef.child("customer").child(mAuth.getUid()).setValue(model);
            if (!tok.equals("")){
                myRef.child("customer").child(mAuth.getUid()).child( "token" ).setValue( tok );
            }
            Toast.makeText(SignupActivity.this, "Token Ok", Toast.LENGTH_SHORT).show();
        }

        if(type.equals("editor")) {
            myRef.child("editor").child(mAuth.getUid()).setValue(model);
            if (!tok.equals("")){
                myRef.child("editor").child(mAuth.getUid()).child( "token" ).setValue( tok );
            }
            myRef.child("editor").child(user.getUid()).child("latitude").setValue(lat);
            myRef.child("editor").child(user.getUid()).child("longitude").setValue(lang);
            myRef.child("All user").child(user.getUid()).child("latitude").setValue(lat);
            myRef.child("All user").child(user.getUid()).child("longitude").setValue(lang);

            Toast.makeText(SignupActivity.this, "Token Ok", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(SignupActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }



    public void getLocation(){



        final SharedPreferences locationss=getSharedPreferences("location",MODE_PRIVATE);
        final SharedPreferences.Editor leditor=locationss.edit();

        FusedLocationProviderClient fusedLocationClient;
        LocationManager locationManager;


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(SignupActivity.this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(SignupActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SignupActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION
            },2);
        }else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener( new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {

                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                Double latitude=location.getLatitude();
                                Double longitude=location.getLongitude();


//                                leditor.putString("lat",latitude.toString());
//                                leditor.putString("long",longitude.toString());
//                                leditor.apply();

                                lat=latitude+"";
                                lang=longitude+"";
                                Toast.makeText(SignupActivity.this, lang+"", Toast.LENGTH_SHORT).show();
                                location.reset();

                            }else{
                                Toast.makeText(SignupActivity.this, "Turn on ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(intent);
                            }

                        }

                    });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }
}