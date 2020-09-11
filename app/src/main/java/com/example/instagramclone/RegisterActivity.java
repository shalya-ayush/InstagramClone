package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout username;
    private TextInputLayout fullname;
    private TextInputLayout email;
    private TextInputLayout password;
    private Button reg_new_user;
    private Button reg_old_user;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.register_username);
        fullname = findViewById(R.id.register_fullname);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        reg_new_user = findViewById(R.id.btn_new_user);
        reg_old_user = findViewById(R.id.btn_old_user);
        progressBar = findViewById(R.id.progress_bar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        reg_old_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_old_user = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent_old_user);
            }
        });

        reg_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUsername = username.getEditText().getText().toString().trim();
                String getFullname = fullname.getEditText().getText().toString();
                String getEmail = email.getEditText().getText().toString();
                String getPassword = password.getEditText().getText().toString();
                if (TextUtils.isEmpty(getUsername) || TextUtils.isEmpty(getFullname) ||
                        TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
                } else if (getPassword.length() < 6) {
                    password.setError("6 characters required");
                } else if (getUsername.contains(" ")) {
                    username.setError("Must not contain white spaces");
                } else {
                    registerUser(getUsername, getFullname, getEmail, getPassword);
                }
            }
        });

    }

    private void registerUser(final String username, final String fullname, final String email, String password) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("username", username);
                map.put("fullName", fullname);
                map.put("email", email);
                map.put("bio", "");
                map.put("imageUrl", "default");
                map.put("id", mAuth.getCurrentUser().getUid());
                databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Empty credentials not allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}