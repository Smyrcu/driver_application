package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmail, registerPassword, registerRepeatPassword, registerName, registerSurname;
    Button registerButton;
    ProgressBar registerProgressBar;
    Drawable errorIcon;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = findViewById(R.id.emailEditText);
        registerPassword = findViewById(R.id.passwordEditText);
        registerRepeatPassword = findViewById(R.id.repeatPasswordEditText);
        registerName = findViewById(R.id.nameEditText);
        registerSurname = findViewById(R.id.surnameEditText);
        registerButton = findViewById(R.id.registerButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        errorIcon = androidx.core.content.res.ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_error, getTheme());
        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();





    }

    public void registerButton(View view) {
        String email, password, repeatPassword, name, surname;

        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        repeatPassword = registerRepeatPassword.getText().toString();
        name = registerName.getText().toString();
        surname = registerSurname.getText().toString();
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();

        if (email.equals("")) {
            registerEmail.setError("Email required", errorIcon);
            return;
        }
        if (password.equals("")) {
            registerPassword.setError("Password required", errorIcon);
            return;
        }
        if (repeatPassword.equals("")) {
            registerRepeatPassword.setError("Repeat password required", errorIcon);
            return;
        }
        if (name.equals("")) {
            registerName.setError("Name required", errorIcon);
            return;
        }
        if (surname.equals("")) {
            registerSurname.setError("Surname required", errorIcon);
            return;
        }
        if (!registerPassword.getText().toString().equals(registerRepeatPassword.getText().toString())){
            registerRepeatPassword.setError("Passwords does not match", errorIcon);
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), LoggedInActivity.class));

                    String[] emailparts = email.split("@");
                    User actualUser = new User(emailparts[0]);
                    actualUser.setUserName(name);
                    actualUser.setUserSurname(surname);
                    actualUser.setUserFullName(name + " " + surname);
                    actualUser.setUserId(firebaseAuth.getCurrentUser().toString());

                    firebaseDatabase.getReference().child("users").child(emailparts[0]).setValue(actualUser);

                    finish();

                    Toast.makeText(RegisterActivity.this, "Registered successfully. Logged in.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Something went wrong." + task.getException().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), LoggedInActivity.class));
                    User actualUser = new User(email);
                    actualUser.setUserName(name);
                    actualUser.setUserSurname(surname);
                    actualUser.setUserId(firebaseAuth.getCurrentUser().toString());
                    firebaseDatabase.getReference().child("users").child(email).setValue(actualUser);



                    Toast.makeText(RegisterActivity.this, "Registered successfully. Logged in.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Something went wrong." + task.getException().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
