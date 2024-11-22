package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends AppCompatActivity {

    private Button buttonLogOut, buttonEditSurvey;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private TextView tvName, tvEmail;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        buttonLogOut = findViewById(R.id.btn_logout);
        buttonEditSurvey = findViewById(R.id.btn_editsurvey);
        tvName = findViewById(R.id.userName);
        tvEmail = findViewById(R.id.userEmail);
        uid = user.getUid();

        // display user's info
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(uid).child("name").getValue(String.class);
                String email = snapshot.child(uid).child("email").getValue(String.class);
                tvName.setText(name);
                tvEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Account.this, "Failed to retrieve data.", Toast.LENGTH_LONG).show();
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
                Toast.makeText(Account.this, "Log out successful", Toast.LENGTH_SHORT).show();
            }
        });

        buttonEditSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditSurvey.class);
                startActivity(intent);
                finish();
            }
        });
    }
}