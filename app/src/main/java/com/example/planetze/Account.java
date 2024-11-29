package com.example.planetze;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Account extends BaseActivity {

    private Button buttonLogOut, buttonEditSurvey, buttonChangePassword;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private TextView tvName, tvEmail, tvTotalCarbon;
    private String uid;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_account; // Ensure this matches your XML layout file name
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        buttonLogOut = findViewById(R.id.btn_logout);
        buttonEditSurvey = findViewById(R.id.btn_editsurvey);
        buttonChangePassword = findViewById(R.id.btn_changePassword);
        tvName = findViewById(R.id.userName);
        tvEmail = findViewById(R.id.userEmail);
        tvTotalCarbon = findViewById(R.id.userTotalCarbon);
        uid = user.getUid();

        // display user's info
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(uid).child("name").getValue(String.class);
                String email = snapshot.child(uid).child("email").getValue(String.class);

                // Safely handle the retrieval of TotalC02Emissions with a default value
                Double carbonValue = snapshot.child(uid).child("TotalC02Emissions").getValue(Double.class);
                double carbon = (carbonValue != null) ? carbonValue : 0.0;

                // Format carbon emissions to a string with 2 decimal places
                String carbonString = String.format("%.2f", carbon);

                // Set the retrieved data to the respective TextViews
                tvName.setText(name != null ? name : "No name available");
                tvEmail.setText(email != null ? email : "No email available");
                tvTotalCarbon.setText(carbonString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if the database read fails
                Log.e("Firebase", "Error reading data: " + error.getMessage());
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.setUserId(null);
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

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating a dialog to reset user password
                final EditText resetPassword = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder((view.getContext()));
                passwordResetDialog.setTitle("Change Password");
                passwordResetDialog.setMessage("Enter new password");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // read new password & set it
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Account.this, "Password Reset Successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Account.this, "Password Reset Failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                passwordResetDialog.create().show();
            }
        });
    }
}