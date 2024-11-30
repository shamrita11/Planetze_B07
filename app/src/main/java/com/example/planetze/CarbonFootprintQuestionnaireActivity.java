package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planetze.tracker.TrackerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CarbonFootprintQuestionnaireActivity extends AppCompatActivity {

    // Declare UI components
    private Spinner countrySpinner; // Q0: Country Selection
    private RadioGroup[] radioGroups; // Array for RadioGroups for easy handling
    private Spinner[] spinners; // Array for Spinners
    private Button btnSubmit; // Submit button

    private ArrayList<Map<String, String>> responses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollview); // Reference your updated XML layout file


        // Link UI components to their IDs
        countrySpinner = findViewById(R.id.country_spinner); // Q0: Country Selection


        // Initialize RadioGroups
        radioGroups = new RadioGroup[]{
                findViewById(R.id.radio_group_1), // Q1: Car ownership
                findViewById(R.id.radio_group_1_1), // Q1.1: Type of car
                findViewById(R.id.radio_group_1_2), // Q1.2: Mileage
                findViewById(R.id.radio_group_2), // Q2: Public transport frequency
                findViewById(R.id.radio_group_3), // Q3: Public transport time
                findViewById(R.id.radio_group_4), // Q4: Short-haul flights
                findViewById(R.id.radio_group_5), // Q5: Long-haul flights
                findViewById(R.id.radio_group_6), // Q6: Diet type
                findViewById(R.id.radio_group_7), // Q7: Food waste
                findViewById(R.id.radio_group_8), // Q8: Housing type
                findViewById(R.id.radio_group_9), // Q9: Household size
                findViewById(R.id.radio_group_10), // Q10: Home size
                findViewById(R.id.radio_group_11), // Q11: Heating energy
                findViewById(R.id.radio_group_12), // Q12: Monthly electricity bill
                findViewById(R.id.radio_group_13), // Q13: Water heating energy
                findViewById(R.id.radio_group_14), // Q14: Renewable energy use
                findViewById(R.id.radio_group_15), // Q15: Clothes shopping
                findViewById(R.id.radio_group_16), // Q16: Eco-friendly products
                findViewById(R.id.radio_group_17), // Q17: Electronics purchased
                findViewById(R.id.radio_group_18)  // Q18: Recycling habits
        };


        // Initialize Spinners
        spinners = new Spinner[]{
                findViewById(R.id.beef_spinner), // Q6.1: Beef consumption
                findViewById(R.id.pork_spinner), // Q6.2: Pork consumption
                findViewById(R.id.chicken_spinner), // Q6.3: Chicken consumption
                findViewById(R.id.fish_seafood_spinner) // Q6.4: Fish consumption
        };


        // Hide all questions except the first one
        for (int i = 1; i < radioGroups.length; i++) {
            radioGroups[i].setVisibility(View.GONE);
        }

        for (Spinner spinner : spinners) {
            spinner.setVisibility(View.GONE);
        }
        btnSubmit = findViewById(R.id.btn_submit); // Submit button
        btnSubmit.setVisibility(View.GONE);

        // Reveal questions dynamically
        setupQuestionFlow();

        btnSubmit.setOnClickListener(v -> {
            ArrayList<String> unansweredQuestionKeys = new ArrayList<>();

            for (Map<String, String> response : responses) {
                String questionKey = response.keySet().stream().filter(k -> k.startsWith("q")).findFirst().orElse("");
                String answerKey = response.keySet().stream().filter(k -> k.startsWith("a")).findFirst().orElse("");
                String answer = response.get(answerKey);

                if (questionKey.matches("q[1-9]|q1[0-8]")) {
                    continue; // Skip main questions
                }

                if (answer == null || answer.isEmpty()) {
                    unansweredQuestionKeys.add(questionKey.replace("_", "."));
                }
            }

            String[] q6_1_keys = {"q6_1_beef", "q6_1_pork", "q6_1_chicken", "q6_1_fish"};
            for (int i = 0; i < spinners.length; i++) {
                if (spinners[i].getVisibility() == View.VISIBLE && spinners[i].getSelectedItemPosition() == 0) {
                    unansweredQuestionKeys.add(q6_1_keys[i].replace("_", "."));
                }
            }

            if (!unansweredQuestionKeys.isEmpty()) {
                String errorMessage = "Please answer the following question(s): " + String.join(", ", unansweredQuestionKeys);
                Toast.makeText(CarbonFootprintQuestionnaireActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CarbonFootprintQuestionnaireActivity.this, "Submitted! View Your Results in Your Eco Gauge Dashboard and User Page!", Toast.LENGTH_LONG).show();

                String userId = UserSession.userId;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);

                // Check if variables are already initialized
                ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Map for variables to initialize
                        Map<String, Object> updates = new HashMap<>();


                            String selectedCountry = countrySpinner.getSelectedItem().toString();
                            updates.put("usercountry", selectedCountry);

                        if (!snapshot.hasChild("averagetotalc02emissionsperyear")) {
                            updates.put("averagetotalc02emissionsperyear", 0);
                        }

                        if (!snapshot.hasChild("averagetotalc02emissionsperyear_food")) {
                            updates.put("averagetotalc02emissionsperyear_food", 0);
                        }

                        if (!snapshot.hasChild("averagetotalc02emissionsperyear_consumption")) {
                            updates.put("averagetotalc02emissionsperyear_consumption", 0);
                        }

                        if (!snapshot.hasChild("averagetotalc02emissionsperyear_housing")) {
                            updates.put("averagetotalc02emissionsperyear_housing", 0);
                        }

                        if (!snapshot.hasChild("averagetotalc02emissionsperyear_transportation")) {
                            updates.put("averagetotalc02emissionsperyear_transportation", 0);
                        }

                        updates.put("on_boarded", true); // Always set "on_boarded" to true

                        if (!updates.isEmpty()) {
                            ref.updateChildren(updates).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(CarbonFootprintQuestionnaireActivity.this, TrackerActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CarbonFootprintQuestionnaireActivity.this, "Failed to update user data. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            // If no updates are needed, navigate directly to TrackerActivity
                            Intent intent = new Intent(CarbonFootprintQuestionnaireActivity.this, TrackerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CarbonFootprintQuestionnaireActivity.this, "Failed to check user data. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


        /**
         * Sets up the question flow to display one question at a time
         * while dynamically managing question visibility and responses.
         */
    private void setupQuestionFlow() {
        // Q0: Country Selection
        countrySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String questionKey = "q0";
                String question = "Please identify your current location";
                String answerKey = "a0";
                String answer = (position > 0) ? parent.getItemAtPosition(position).toString() : "";

                // Update the response dynamically
                updateResponse(questionKey, question, answerKey, answer, position > 0);

                // Manage visibility of Q1: Do you own a car?
                findViewById(R.id.question_1_container).setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                radioGroups[0].setVisibility(position > 0 ? View.VISIBLE : View.GONE);

                if (position == 0) {
                    // If no country is selected, hide dependent questions
                    updateResponse("q1", "Do you own or regularly use a car?", "a1", "", false);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                updateResponse("q0", "Please identify your current location", "a0", "", false);
            }
        });

        // Q1: Do you own a car?
        radioGroups[0].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q1";
            String question = "Do you own or regularly use a car?";
            String answerKey = "a1";
            String answer = "";

            if (checkedId == R.id.radio_1_option_yes) {
                answer = "Yes";

                // Show Q1.1
                updateResponse("q1_1", "What type of car do you drive?", "a1_1", "", true);
                findViewById(R.id.question_1_1_container).setVisibility(View.VISIBLE);
                radioGroups[1].setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radio_1_option_no) {
                answer = "No";

                // Hide Q1.1 and Q1.2
                updateResponse("q1_1", "What type of car do you drive?", "a1_1", "", false);
                updateResponse("q1_2", "How many kilometres/miles do you drive per year?", "a1_2", "", false);

                findViewById(R.id.question_1_1_container).setVisibility(View.GONE);
                findViewById(R.id.question_1_2_container).setVisibility(View.GONE);
                radioGroups[1].setVisibility(View.GONE);
                radioGroups[2].setVisibility(View.GONE);

                // Show Q2: Public Transport Frequency
                boolean showNext = checkedId != -1;
                updateResponse("q2", "How often do you use public transportation (bus, train, subway)?", "a2", "", showNext);
                findViewById(R.id.question_2_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
                radioGroups[3].setVisibility(showNext ? View.VISIBLE : View.GONE);
            }

            // Store the response for Q1
            updateResponse(questionKey, question, answerKey, answer, true);
        });

        // Q1.1: Type of car
        radioGroups[1].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q1_1";
            String question = "What type of car do you drive?";
            String answerKey = "a1_1";
            String answer = "";

            if (checkedId == R.id.radio_1_1_option_1) {
                answer = "Gasoline";
            } else if (checkedId == R.id.radio_1_1_option_2) {
                answer = "Diesel";
            } else if (checkedId == R.id.radio_1_1_option_3) {
                answer = "Hybrid";
            } else if (checkedId == R.id.radio_1_1_option_4) {
                answer = "Electric";
            } else if (checkedId == R.id.radio_1_1_option_5) {
                answer = "I don't know";
            }

            updateResponse(questionKey, question, answerKey, answer, true);

            // Show Q1.2: Mileage if a type of car is selected
            boolean showNext = checkedId != -1;
            updateResponse("q1_2", "How many kilometres/miles do you drive per year?", "a1_2", "", showNext);
            findViewById(R.id.question_1_2_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[2].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q1.2: Mileage
        radioGroups[2].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q1_2";
            String question = "How many kilometres/miles do you drive per year?";
            String answerKey = "a1_2";
            String answer = "";

            if (checkedId == R.id.radio_1_2_option_1) {
                answer = "Up to 5,000 km (3,000 miles)";
            } else if (checkedId == R.id.radio_1_2_option_2) {
                answer = "5,000–10,000 km (3,000–6,000 miles)";
            } else if (checkedId == R.id.radio_1_2_option_3) {
                answer = "10,000–15,000 km (6,000–9,000 miles)";
            } else if (checkedId == R.id.radio_1_2_option_4) {
                answer = "15,000–20,000 km (9,000–12,000 miles)";
            } else if (checkedId == R.id.radio_1_2_option_5) {
                answer = "20,000–25,000 km (12,000–15,000 miles)";
            } else if (checkedId == R.id.radio_1_2_option_6) {
                answer = "More than 25,000 km (15,000 miles)";
            }

            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show Q2: Public Transport Frequency
            boolean showNext = checkedId != -1;
            updateResponse("q2", "How often do you use public transportation (bus, train, subway)?", "a2", "", showNext);
            findViewById(R.id.question_2_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[3].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q2: Public Transport Frequency
        radioGroups[3].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q2";
            String question = "How often do you use public transportation (bus, train, subway)?";
            String answerKey = "a2";
            String answer = "";

            if (checkedId == R.id.radio_2_option_1) {
                answer = "Never";
            } else if (checkedId == R.id.radio_2_option_2) {
                answer = "Occasionally (1-2 times/week)";
            } else if (checkedId == R.id.radio_2_option_3) {
                answer = "Frequently (3-4 times/week)";
            } else if (checkedId == R.id.radio_2_option_4) {
                answer = "Always (5+ times/week)";
            }

            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show Q3: Public Transport Duration
            boolean showNext = checkedId != -1;
            updateResponse("q3", "How much time do you spend on public transport per week (bus, train, subway)?", "a3", "", showNext);
            findViewById(R.id.question_3_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[4].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q3: Public Transport Duration
        radioGroups[4].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q3";
            String question = "How much time do you spend on public transport per week (bus, train, subway)?";
            String answerKey = "a3";
            String answer = "";

            // Determine the answer based on the selected radio button
            if (checkedId == R.id.radio_3_option_1) {
                answer = "Under 1 hour";
            } else if (checkedId == R.id.radio_3_option_2) {
                answer = "1-3 hours";
            } else if (checkedId == R.id.radio_3_option_3) {
                answer = "3-5 hours";
            } else if (checkedId == R.id.radio_3_option_4) {
                answer = "5-10 hours";
            } else if (checkedId == R.id.radio_3_option_5) {
                answer = "More than 10 hours";
            }

            // Update response for Q3
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q4: Short-Haul Flights
            boolean showNext = checkedId != -1;
            updateResponse("q4", "How many short-haul flights have you taken in the past year?", "a4", "", showNext);
            findViewById(R.id.question_4_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[5].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q4: Short-Haul Flights
        radioGroups[5].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q4";
            String question = "How many short-haul flights (less than 1,500 km / 932 miles) have you taken in the past year?";
            String answerKey = "a4";
            String answer = "";

            // Determine the answer based on the selected radio button
            if (checkedId == R.id.radio_4_option_1) {
                answer = "None";
            } else if (checkedId == R.id.radio_4_option_2) {
                answer = "1–2 flights";
            } else if (checkedId == R.id.radio_4_option_3) {
                answer = "3–5 flights";
            } else if (checkedId == R.id.radio_4_option_4) {
                answer = "6–10 flights";
            } else if (checkedId == R.id.radio_4_option_5) {
                answer = "More than 10 flights";
            }

            // Update response for Q4
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q5: Long-Haul Flights
            boolean showNext = checkedId != -1;
            updateResponse("q5", "How many long-haul flights have you taken in the past year?", "a5", "", showNext);
            findViewById(R.id.question_5_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[6].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q5: Long-Haul Flights
        radioGroups[6].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q5";
            String question = "How many long-haul flights (more than 1,500 km / 932 miles) have you taken in the past year?";
            String answerKey = "a5";
            String answer = "";

            // Determine the answer based on the selected radio button
            if (checkedId == R.id.radio_5_option_1) {
                answer = "None";
            } else if (checkedId == R.id.radio_5_option_2) {
                answer = "1–2 flights";
            } else if (checkedId == R.id.radio_5_option_3) {
                answer = "3–5 flights";
            } else if (checkedId == R.id.radio_5_option_4) {
                answer = "6–10 flights";
            } else if (checkedId == R.id.radio_5_option_5) {
                answer = "More than 10 flights";
            }

            // Update response for Q5
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q6: Diet Type
            boolean showNext = checkedId != -1;
            updateResponse("q6", "What best describes your diet?", "a6", "", showNext);
            findViewById(R.id.question_6_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[7].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q6: Diet Type
        radioGroups[7].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q6";
            String question = "What best describes your diet?";
            String answerKey = "a6";
            String answer = "";

            if (checkedId == R.id.radio_6_option_1) {
                answer = "Vegetarian";
            } else if (checkedId == R.id.radio_6_option_2) {
                answer = "Vegan";
            } else if (checkedId == R.id.radio_6_option_3) {
                answer = "Pescatarian (fish/seafood)";
            } else if (checkedId == R.id.radio_6_option_meat_based) {
                answer = "Meat-based (eat all types of animal products)";
            }

            // Update response for Q6
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show or hide dependent questions (Q6.1: Meat Consumption) based on the answer
            boolean showMeatQuestions = checkedId == R.id.radio_6_option_meat_based;

            // Manage visibility for meat consumption spinners (Q6.1 sub-questions)
            findViewById(R.id.question_6_1_container).setVisibility(showMeatQuestions ? View.VISIBLE : View.GONE);
            for (Spinner spinner : spinners) {
                spinner.setVisibility(showMeatQuestions ? View.VISIBLE : View.GONE);
                if (!showMeatQuestions) {
                    spinner.setSelection(0); // Reset spinner selection if hidden
                }
            }

            // Update responses for Q6.1 sub-questions dynamically
            updateResponse("q6_1_beef", "How much beef do you consume weekly?", "a6_1_beef", "", showMeatQuestions);
            updateResponse("q6_1_pork", "How much pork do you consume weekly?", "a6_1_pork", "", showMeatQuestions);
            updateResponse("q6_1_chicken", "How much chicken do you consume weekly?", "a6_1_chicken", "", showMeatQuestions);
            updateResponse("q6_1_fish", "How much fish/seafood do you consume weekly?", "a6_1_fish", "", showMeatQuestions);

            // Show Q7: Food Waste, regardless of meat-based diet selection
            boolean showNext = checkedId != -1;
            updateResponse("q7", "How often do you waste food or throw away uneaten leftovers?", "a7", "", showNext);
            findViewById(R.id.question_7_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[8].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });


        // Q6.1: Meat Consumption (Beef, Pork, Chicken, Fish)
        spinners[0].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String questionKey = "q6_1_beef";
                String question = "beef?";
                String answerKey = "a6_1_beef";
                String answer = position > 0 ? parent.getItemAtPosition(position).toString() : "";

                // Update response for beef consumption
                updateResponse(questionKey, question, answerKey, answer, position > 0);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        });

        spinners[1].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String questionKey = "q6_1_pork";
                String question = "pork?";
                String answerKey = "a6_1_pork";
                String answer = position > 0 ? parent.getItemAtPosition(position).toString() : "";

                // Update response for pork consumption
                updateResponse(questionKey, question, answerKey, answer, position > 0);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        });

        // Chicken Consumption Spinner (Q6.1 - Chicken)
        spinners[2].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String questionKey = "q6_1_chicken";
                String question = "chicken?";
                String answerKey = "a6_1_chicken";
                String answer = position > 0 ? parent.getItemAtPosition(position).toString() : "";

                // Update response for chicken consumption
                updateResponse(questionKey, question, answerKey, answer, position > 0);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        });

        // Fish/Seafood Consumption Spinner (Q6.1 - Fish)
        spinners[3].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String questionKey = "q6_1_fish";
                String question = "fish/seafood?";
                String answerKey = "a6_1_fish";
                String answer = position > 0 ? parent.getItemAtPosition(position).toString() : "";

                // Update response for fish consumption
                updateResponse(questionKey, question, answerKey, answer, position > 0);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        });

        // Q7: Food Waste
        radioGroups[8].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q7";
            String question = "How often do you waste food or throw away uneaten leftovers?";
            String answerKey = "a7";
            String answer = "";

            if (checkedId == R.id.radio_7_option_1) {
                answer = "Never";
            } else if (checkedId == R.id.radio_7_option_2) {
                answer = "Rarely";
            } else if (checkedId == R.id.radio_7_option_3) {
                answer = "Occasionally";
            } else if (checkedId == R.id.radio_7_option_4) {
                answer = "Frequently";
            }

            // Update response for Q7
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q8: Housing Type
            boolean showNext = checkedId != -1;
            updateResponse("q8", "What type of home do you live in?", "a8", "", showNext);
            findViewById(R.id.question_8_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[9].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q8: Housing Type
        radioGroups[9].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q8";
            String question = "What type of home do you live in?";
            String answerKey = "a8";
            String answer = "";

            if (checkedId == R.id.radio_8_option_1) {
                answer = "Detached house";
            } else if (checkedId == R.id.radio_8_option_2) {
                answer = "Semi-detached house";
            } else if (checkedId == R.id.radio_8_option_3) {
                answer = "Townhouse";
            } else if (checkedId == R.id.radio_8_option_4) {
                answer = "Condo/Apartment";
            } else if (checkedId == R.id.radio_8_option_5) {
                answer = "Other";
            }

            // Update response for Q8
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q9: Household Size
            boolean showNext = checkedId != -1;
            updateResponse("q9", "How many people live in your household?", "a9", "", showNext);
            findViewById(R.id.question_9_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[10].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q9: Household Size
        radioGroups[10].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q9";
            String question = "How many people live in your household?";
            String answerKey = "a9";
            String answer = "";

            if (checkedId == R.id.radio_9_option_1) {
                answer = "1";
            } else if (checkedId == R.id.radio_9_option_2) {
                answer = "2";
            } else if (checkedId == R.id.radio_9_option_3) {
                answer = "3–4";
            } else if (checkedId == R.id.radio_9_option_4) {
                answer = "5 or more people";
            }

            // Update response for Q9
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q10: Home Size
            boolean showNext = checkedId != -1;
            updateResponse("q10", "What is the size of your home?", "a10", "", showNext);
            findViewById(R.id.question_10_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[11].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q10: Home Size
        radioGroups[11].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q10";
            String question = "What is the size of your home?";
            String answerKey = "a10";
            String answer = "";

            if (checkedId == R.id.radio_10_option_1) {
                answer = "Less than 1000 sq. ft.";
            } else if (checkedId == R.id.radio_10_option_2) {
                answer = "1000–2000 sq. ft.";
            } else if (checkedId == R.id.radio_10_option_3) {
                answer = "Over 2,000 sq. ft.";
            }

            // Update response for Q10
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q11: Heating Energy
            boolean showNext = checkedId != -1;
            updateResponse("q11", "What type of energy do you use to heat your home?", "a11", "", showNext);
            findViewById(R.id.question_11_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[12].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q11: Heating Energy
        radioGroups[12].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q11";
            String question = "What type of energy do you use to heat your home?";
            String answerKey = "a11";
            String answer = "";

            if (checkedId == R.id.radio_11_option_1) {
                answer = "Natural Gas";
            } else if (checkedId == R.id.radio_11_option_2) {
                answer = "Electricity";
            } else if (checkedId == R.id.radio_11_option_3) {
                answer = "Oil";
            } else if (checkedId == R.id.radio_11_option_4) {
                answer = "Propane";
            } else if (checkedId == R.id.radio_11_option_5) {
                answer = "Wood";
            } else if (checkedId == R.id.radio_11_option_6) {
                answer = "Other";
            }

            // Update response for Q11
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q12: Monthly Electricity Bill
            boolean showNext = checkedId != -1;
            updateResponse("q12", "What is your average monthly electricity bill?", "a12", "", showNext);
            findViewById(R.id.question_12_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[13].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q12: Monthly Electricity Bill
        radioGroups[13].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q12";
            String question = "What is your average monthly electricity bill?";
            String answerKey = "a12";
            String answer = "";

            if (checkedId == R.id.radio_12_option_1) {
                answer = "Under $50";
            } else if (checkedId == R.id.radio_12_option_2) {
                answer = "$50–$100";
            } else if (checkedId == R.id.radio_12_option_3) {
                answer = "$100–$150";
            } else if (checkedId == R.id.radio_12_option_4) {
                answer = "$150–$200";
            } else if (checkedId == R.id.radio_12_option_5) {
                answer = "Over $200";
            }

            // Update response for Q12
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q13: Water Heating Energy
            boolean showNext = checkedId != -1;
            updateResponse("q13", "What type of energy do you use to heat water in your home?", "a13", "", showNext);
            findViewById(R.id.question_13_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[14].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q13: Water Heating Energy
        radioGroups[14].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q13";
            String question = "What type of energy do you use to heat water in your home?";
            String answerKey = "a13";
            String answer = "";

            if (checkedId == R.id.radio_13_option_1) {
                answer = "Natural Gas";
            } else if (checkedId == R.id.radio_13_option_2) {
                answer = "Electricity";
            } else if (checkedId == R.id.radio_13_option_3) {
                answer = "Oil";
            } else if (checkedId == R.id.radio_13_option_4) {
                answer = "Propane";
            } else if (checkedId == R.id.radio_13_option_5) {
                answer = "Solar";
            } else if (checkedId == R.id.radio_13_option_6) {
                answer = "Other";
            }

            // Update response for Q13
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q14: Renewable Energy Usage
            boolean showNext = checkedId != -1;
            updateResponse("q14", "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?", "a14", "", showNext);
            findViewById(R.id.question_14_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[15].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q14: Renewable Energy Usage
        radioGroups[15].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q14";
            String question = "Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?";
            String answerKey = "a14";
            String answer = "";

            if (checkedId == R.id.radio_14_option_1) {
                answer = "Yes, primarily (more than 50% of energy use)";
            } else if (checkedId == R.id.radio_14_option_2) {
                answer = "Yes, partially (less than 50% of energy use)";
            } else if (checkedId == R.id.radio_14_option_3) {
                answer = "No";
            }

            // Update response for Q14
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q15: Clothes Shopping
            boolean showNext = checkedId != -1;
            updateResponse("q15", "How often do you buy new clothes?", "a15", "", showNext);
            findViewById(R.id.question_15_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[16].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q15: Clothes Shopping
        radioGroups[16].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q15";
            String question = "How often do you buy new clothes?";
            String answerKey = "a15";
            String answer = "";

            if (checkedId == R.id.radio_15_option_1) {
                answer = "Monthly";
            } else if (checkedId == R.id.radio_15_option_2) {
                answer = "Quarterly";
            } else if (checkedId == R.id.radio_15_option_3) {
                answer = "Annually";
            } else if (checkedId == R.id.radio_15_option_4) {
                answer = "Rarely";
            }

            // Update response for Q15
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q16: Eco-Friendly Products
            boolean showNext = checkedId != -1;
            updateResponse("q16", "How often do you buy second-hand or eco-friendly products?", "a16", "", showNext);
            findViewById(R.id.question_16_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[17].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q16: Eco-Friendly Products
        radioGroups[17].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q16";
            String question = "How often do you buy second-hand or eco-friendly products?";
            String answerKey = "a16";
            String answer = "";

            if (checkedId == R.id.radio_16_option_1) {
                answer = "Yes, regularly";
            } else if (checkedId == R.id.radio_16_option_2) {
                answer = "Yes, occasionally";
            } else if (checkedId == R.id.radio_16_option_3) {
                answer = "No";
            }

            // Update response for Q16
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q17: Electronics Purchased
            boolean showNext = checkedId != -1;
            updateResponse("q17", "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?", "a17", "", showNext);
            findViewById(R.id.question_17_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[18].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q17: Electronics Purchased
        radioGroups[18].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q17";
            String question = "How many electronic devices (phones, laptops, etc.) have you purchased in the past year?";
            String answerKey = "a17";
            String answer = "";

            if (checkedId == R.id.radio_17_option_1) {
                answer = "None";
            } else if (checkedId == R.id.radio_17_option_2) {
                answer = "1";
            } else if (checkedId == R.id.radio_17_option_3) {
                answer = "2";
            } else if (checkedId == R.id.radio_17_option_4) {
                answer = "3 or more";
            }

            // Update response for Q17
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // Show the next question, e.g., Q18: Recycling Habits
            boolean showNext = checkedId != -1;
            updateResponse("q18", "How often do you recycle?", "a18", "", showNext);
            findViewById(R.id.question_18_container).setVisibility(showNext ? View.VISIBLE : View.GONE);
            radioGroups[19].setVisibility(showNext ? View.VISIBLE : View.GONE);
        });

        // Q18: Recycling Habits
        radioGroups[19].setOnCheckedChangeListener((group, checkedId) -> {
            String questionKey = "q18";
            String question = "How often do you recycle?";
            String answerKey = "a18";
            String answer = "";

            if (checkedId == R.id.radio_18_option_1) {
                answer = "Never";
            } else if (checkedId == R.id.radio_18_option_2) {
                answer = "Occasionally";
            } else if (checkedId == R.id.radio_18_option_3) {
                answer = "Frequently";
            } else if (checkedId == R.id.radio_18_option_4) {
                answer = "Always";
            }

            // Update response for Q18
            updateResponse(questionKey, question, answerKey, answer, checkedId != -1);

            // At the end of the questionnaire, enable the submit button
            boolean showSubmit = checkedId != -1;
            findViewById(R.id.submit_button_container).setVisibility(showSubmit ? View.VISIBLE : View.GONE);
            btnSubmit.setVisibility(showSubmit ? View.VISIBLE : View.GONE);
        });
    }


    private void updateResponse(String questionKey, String question, String answerKey, String answer, boolean isVisible) {
        // Check if the question already exists
        for (Map<String, String> response : responses) {
            if (response.containsKey(questionKey)) {
                if (!isVisible) {
                    // Remove the response if the question is not visible
                    responses.remove(response);
                    return;
                } else {
                    // Update the answer if the question is still visible
                    response.put(answerKey, answer);
                    return;
                }
            }
        }

        // If the question is not found and isVisible is true, add a new response
        if (isVisible) {
            Map<String, String> newResponse = new HashMap<>();
            newResponse.put(questionKey, question);
            newResponse.put(answerKey, answer);
            responses.add(newResponse);
        }
    }





}

