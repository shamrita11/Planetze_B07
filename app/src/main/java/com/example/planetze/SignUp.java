package com.example.planetze;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonSignUp, buttonLogin, buttonBack;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ImageView eyeIcon;
    private FirebaseDatabase db;
    private DatabaseReference ref;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
            finish();
        }
    }

    // comment 22
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        editTextName = (EditText) findViewById(R.id.full_name);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.btn_signup);
        buttonLogin = findViewById(R.id.btn_login);
        buttonBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);
        eyeIcon = findViewById(R.id.eyeIcon);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name, email, password;
                name = String.valueOf(editTextName.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(SignUp.this, "Enter name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignUp.this, "Enter email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Enter password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in successful
                            // write user's full name & email to db
                            User user = new User(name, email, false);
                            String keyID = ref.push().getKey();
                            ref.child(keyID).setValue(user);
                            // send verification email
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Verification email success,
                                        Toast.makeText(SignUp.this, "Account created. Please check your inbox for a confirmation email.",
                                                Toast.LENGTH_LONG).show();
                                        // reset fields
                                        editTextName.setText("");
                                        editTextEmail.setText("");
                                        editTextPassword.setText("");
                                    }
                                    else {
                                        // If verification email not sent, display user-friendly message from Firebase
                                        Toast.makeText(SignUp.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display user-friendly message from Firebase
                            Toast.makeText(SignUp.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Initially set the input type to password
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Set the listener for the eye icon to toggle password visibility
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Show password
                    editTextPassword.setTransformationMethod(null);
                    eyeIcon.setImageResource(R.drawable.icon_open_eye);  // Change to the 'eye open' icon
                } else {
                    // Hide password
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.icon_closed_eye);  // Change back to the 'eye closed' icon
                }
            }
        });
    }
}