package com.fyp.snapshop.activities;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.androidstudy.networkmanager.Tovuti;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.facebook.stetho.Stetho;
import com.fyp.snapshop.BottomNavigation;
import com.fyp.snapshop.JavaClass.Validation;
import com.fyp.snapshop.Models.Model;
import com.fyp.snapshop.R;
import com.fyp.snapshop.SharedPrefrence;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private TextView tvRegister, forgot_password;
    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassowrd;
    private Button btnLogin;
    private ImageView ivBiometric;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private CheckBox checkBox;
    private String emailLogin = "", passwordLogin = "", category = "";
    private FlatDialog flatDialog;
    final LoginActivity activity = this;

    //Firebase
    private FirebaseAuth mAuth;
    public FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef;
    Model model = new Model();
    String uid;

    String lat,lang;
    DatabaseReference myref;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Stetho.initializeWithDefaults(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef=database.getReference();
        myRef = database.getReference().child("All user");
        forgot_password = findViewById(R.id.forgot_password);
        flatDialog = new FlatDialog(this);

        getLocation();

        checkNetworkStatus();
        bindView();
        listeners();
        ResetPassword();


    }

    private void bindView() {
        tvRegister = findViewById(R.id.tv_register);
        btnLogin = findViewById(R.id.btn_login);
        //RelativeLayout relativeLayout = findViewById(R.id.relative);
        ivBiometric = findViewById(R.id.iv_biometric);
        etEmail = findViewById(R.id.et_login_email);
        tilEmail = findViewById(R.id.til_login_email);
        etPassword = findViewById(R.id.et_login_password);
        tilPassowrd = findViewById(R.id.til_login_password);
        checkBox = findViewById(R.id.cb_keep_me_login);
        linearLayout = findViewById(R.id.ll_hide);
        progressBar = findViewById(R.id.spin_kit_login);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("ClickableViewAccessibility")
    private void listeners() {

        tvRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        btnLogin.setOnTouchListener((v, event) -> {
            checkValidation();
            return false;
        });


        ivBiometric.setOnClickListener(view -> new TTFancyGifDialog.Builder(LoginActivity.this)
                .setTitle("FingerPrint Security")
                .setMessage("You don't have time for shopping, Visit our website for online shopping with discount price.")
                .setPositiveBtnText("OK")
                .setPositiveBtnBackground("#290B66")
                .setNegativeBtnText("CANCEL")
                .setNegativeBtnBackground("#5B3395")
                .setGifResource(R.drawable.lockgif)      //pass your gif, png or jpg
                .isCancellable(true)
                .OnPositiveClicked(() -> {
                    biometric();
                    BiometricDialog();


                })
                .OnNegativeClicked(() -> Toast.makeText(LoginActivity.this, "Cancel", Toast.LENGTH_SHORT).show())
                .build());

    }

    private void checkValidation() {

        emailLogin = Objects.requireNonNull(etEmail.getText()).toString().trim();
        passwordLogin = Objects.requireNonNull(etPassword.getText()).toString().trim();

        if (emailLogin.isEmpty()) {
            etEmail.requestFocus();
            tilEmail.setError("Email is required");
        } else if (!Validation.checkMail(emailLogin)) {
            etEmail.requestFocus();
            tilEmail.setError("Email is invalid");
        } else if (passwordLogin.isEmpty()) {
            tilEmail.setErrorEnabled(false);
            tilPassowrd.setError("Password is required");
//        } else if (!Validation.checkPassword(passwordLogin)) {
//            tilEmail.setErrorEnabled(false);
//            tilPassowrd.setError("Password badly formatted");
        } else {
            tilEmail.setErrorEnabled(false);
            tilPassowrd.setErrorEnabled(false);
            Toast.makeText(LoginActivity.this, "Success Login Please Wait", Toast.LENGTH_SHORT).show();
            authentication();
        }
    }
    Sprite cubeGrid = new FoldingCube();
    private void authentication() {

        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        tvRegister.setVisibility(View.INVISIBLE);

        progressBar.setIndeterminateDrawable(cubeGrid);

        try {

            mAuth.signInWithEmailAndPassword(emailLogin, passwordLogin)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            model.setEmail(emailLogin);
                            model.setPassword(passwordLogin);
                          //  model.setUid(uid);
                            user = mAuth.getCurrentUser();
                            myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    category = dataSnapshot.child("type").getValue(String.class);
                                    loginsharedpref();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            if (checkBox.isChecked()) {
                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                                editor.putString("EMAIL", emailLogin);
                                editor.apply();

                            }

                            if (category.equals("customer")) {
                                cubeGrid.stop();
                                startActivity(new Intent(LoginActivity.this, BottomNavigation.class));
                                finish();
                            }
                            if (category.equals("editor")) {
                                cubeGrid.stop();
                                startActivity(new Intent(LoginActivity.this, EditorActivity.class));
                                finish();
                            }


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            tvRegister.setVisibility(View.VISIBLE);

                        }

                    });


        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "" + e, Toast.LENGTH_SHORT).show();
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            tvRegister.setVisibility(View.VISIBLE);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void biometric() {
        executor = Executors.newSingleThreadExecutor();
        biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Welcom")
                .setDescription("scan you finger")
                .setNegativeButton("cancel", executor, (dialogInterface, i) -> {

                }).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void BiometricDialog() {
        biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {

                activity.runOnUiThread(() -> {
                    startActivity(new Intent(LoginActivity.this, BottomNavigation.class));
                    finish();
                    Toast.makeText(LoginActivity.this, "Authrnticate", Toast.LENGTH_SHORT).show();
                });

            }

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

    public void ResetPassword() {
        forgot_password.setOnClickListener(view -> {
            final FlatDialog flatDialog = new FlatDialog(LoginActivity.this);
            flatDialog.setTitle("Reset Password")
                    .setSubtitle("Enter Your Registered Email here")
                    .setFirstTextFieldHint("Email")
                    .setFirstButtonText("Reset")
                    .setSecondButtonText("CANCEL")
                    .withFirstButtonListner(view1 -> {
                        Toast.makeText(LoginActivity.this, flatDialog.getFirstTextField(), Toast.LENGTH_SHORT).show();
                        String useremail = flatDialog.getFirstTextField();
                        if (useremail.equals("")) {
                            Toast.makeText(LoginActivity.this, "Please Enter Registered Email", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, "Email Link is Sent", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error in Sending Password Reset Email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .withSecondButtonListner(view12 -> flatDialog.dismiss())
                    .show();
        });
    }

    public void loginsharedpref() {
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();

        uid=mAuth.getCurrentUser().getUid();

        SharedPreferences prefs = getSharedPreferences("firebase_token", Context.MODE_PRIVATE);
        String tok = prefs.getString("token", "");
        if (!tok.equals("") && tok != null) {
            myRef.child("editor").child(uid).child("token").setValue(tok);
            myRef.child("customer").child(uid).child("token").setValue(tok);

        }
        SharedPrefrence.getInstance( LoginActivity.this ).setParentUser( model );
        if(category.equals("editor")){
            cubeGrid.stop();
            startActivity(new Intent(LoginActivity.this, EditorActivity.class));
            editor.putString("type", "editor");
            editor.commit();
            finish();
        }
        if(category.equals("customer")){
            cubeGrid.stop();
            startActivity(new Intent(LoginActivity.this, BottomNavigation.class));
            editor.putString("type", "customer");
            editor.commit();
            finish();
        }

    }
    public void getLocation(){



        final SharedPreferences locationss=getSharedPreferences("location",MODE_PRIVATE);
        final SharedPreferences.Editor leditor=locationss.edit();

        FusedLocationProviderClient fusedLocationClient;
        LocationManager locationManager;


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(LoginActivity.this,
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
                                Toast.makeText(LoginActivity.this, lang+"", Toast.LENGTH_SHORT).show();
                                location.reset();

                            }else{
//                                Toast.makeText(LoginActivity.this, "Turn on ", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(intent);
                            }

                        }

                    });

        }
    }
}