package com.example.planetze;

import static java.security.AccessController.getContext;

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
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private TextView tvName, tvEmail, tvTotalCarbon, TotalCarbonFood;
    private TextView TotalCarbonTransportation, TotalCarbonConsumption, TotalCarbonHousing;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_account; // Ensure this matches your XML layout file name
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize Firebase and UI elements
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        buttonLogOut = findViewById(R.id.btn_logout);
        buttonEditSurvey = findViewById(R.id.btn_editsurvey);
        buttonChangePassword = findViewById(R.id.btn_changePassword);
        tvName = findViewById(R.id.userName);
        tvEmail = findViewById(R.id.userEmail);
        tvTotalCarbon = findViewById(R.id.userTotalCarbon);
        TotalCarbonFood = findViewById(R.id.userTotalCarbonFood);
        TotalCarbonTransportation = findViewById(R.id.userTotalCarbonTransportation);
        TotalCarbonConsumption = findViewById(R.id.userTotalCarbonConsumption);
        TotalCarbonHousing = findViewById(R.id.userTotalCarbonHousing);

        // Fetch user ID from UserSession
        String uid = UserSession.getUserId(this);

        if (uid != null) {
            // Display user's info
            ref.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Fetch name and email
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    // Safely handle the retrieval of TotalC02Emissions with a default value
                    Double carbonValue = snapshot.child("TotalC02Emissions").getValue(Double.class);
                    double carbon = (carbonValue != null) ? carbonValue : 0.0;

                    // Fetch the breakdown of CO2 emissions
                    Double carbonFood = snapshot.child("averagetotalc02emissionsperyear_food").getValue(Double.class);
                    Double carbonTransportation = snapshot.child("averagetotalc02emissionsperyear_transportation").getValue(Double.class);
                    Double carbonConsumption = snapshot.child("averagetotalc02emissionsperyear_consumption").getValue(Double.class);
                    Double carbonHousing = snapshot.child("averagetotalc02emissionsperyear_housing").getValue(Double.class);

                    // Use default values if any of the above values are null
                    carbonFood = (carbonFood != null) ? carbonFood : 0.0;
                    carbonTransportation = (carbonTransportation != null) ? carbonTransportation : 0.0;
                    carbonConsumption = (carbonConsumption != null) ? carbonConsumption : 0.0;
                    carbonHousing = (carbonHousing != null) ? carbonHousing : 0.0;

                    // Format the values with 2 decimal places
                    String carbonString = String.format("Total: %.2f kg", carbon);
                    String carbonFoodString = String.format("Food: %.2f kg per year", carbonFood);
                    String carbonTransportationString = String.format("Transportation: %.2f kg per year", carbonTransportation);
                    String carbonConsumptionString = String.format("Consumption: %.2f kg per year", carbonConsumption);
                    String carbonHousingString = String.format("Housing: %.2f kg per year", carbonHousing);

                    // Set the retrieved data to the respective TextViews
                    tvName.setText(name != null ? name : "No name available");
                    tvEmail.setText(email != null ? email : "No email available");
                    tvTotalCarbon.setText(carbonString);

                    // Initialize the additional TextViews with respective values
                    TotalCarbonFood.setText(carbonFoodString);
                    TotalCarbonTransportation.setText(carbonTransportationString);
                    TotalCarbonConsumption.setText(carbonConsumptionString);
                    TotalCarbonHousing.setText(carbonHousingString);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error if the database read fails
                    Log.e("Firebase", "Error reading data: " + error.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "User ID is not available. Please log in again.", Toast.LENGTH_SHORT).show();
        }

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSession.clearUserId(getApplicationContext());
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
                Intent intent = new Intent(getApplicationContext(), CarbonFootprintQuestionnaireActivity.class);
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
                        mAuth.getCurrentUser().updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
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