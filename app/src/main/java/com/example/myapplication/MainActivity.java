package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public String currentUser;
    public FirebaseUser actualUser;


    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView registerTextView;
    private CheckBox rememberCheckBox;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    SharedPreferences savedLogin, savedPassword;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberCheckBox = findViewById(R.id.rememberCheckBox);
        progressBar = findViewById(R.id.progressBar);
        registerTextView = findViewById(R.id.registerTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        savedLogin = getSharedPreferences("Login", Activity.MODE_PRIVATE);
        savedPassword = getSharedPreferences("Password", Activity.MODE_PRIVATE);
        loadData();

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity (new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    public void LoginBtn(View view) {
        String email = emailEditText.getText().toString().trim();
        String pass = passwordEditText.getText().toString().trim();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (rememberCheckBox.isChecked()) {
            saveData();
        }

        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Email is required");
            return;
        }
        if(TextUtils.isEmpty(pass)){
            passwordEditText.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //Authentication

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoggedInActivity.class));
                 actualUser =  firebaseAuth.getCurrentUser();
                 currentUser = actualUser.toString();
                 User user = new User(email);


                String[] emailparts = email.split("@");
                User actualUser = new User(emailparts[0]);
                actualUser.setUserName(databaseReference.child("users").child(emailparts[0]).child("userName").get().toString());
                actualUser.setUserName(databaseReference.child("users").child(emailparts[0]).child("userSurname").get().toString());
                

            } else {
                Toast.makeText(MainActivity.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

        });


    }

    private void loadData(){
        String loadLoginFromPref = savedLogin.getString("Login", "");
        String loadPasswordFromPref = savedPassword.getString("Password", "");
        emailEditText.setText(loadLoginFromPref);
        passwordEditText.setText(loadPasswordFromPref);
    }

    private void saveData(){
        SharedPreferences.Editor loginEditor = savedLogin.edit();
        SharedPreferences.Editor passwordEditor = savedPassword.edit();
        loginEditor.putString("Login", emailEditText.getText().toString());
        passwordEditor.putString("Password", passwordEditText.getText().toString());
        loginEditor.commit();
        passwordEditor.commit();

    }

    @Override
    public void onBackPressed() {
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (firebaseAuth.getCurrentUser() != null) {
//            startActivity(new Intent(this, LoggedInActivity.class));
//        }
//    }
}