package com.example.planetze.tracker;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.example.planetze.UserSession;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DailyEmissionProcessor {
    // database
    private FirebaseDatabase db;
    private DatabaseReference myRef;
    // String userId;
    String dateKey;
    String monthKey;
    Context context;

    // Transportation
    double distanceDriven;
    String carType; // convert to lowercase
    double transportTime;
    String frequency;
    String timeSpent;
    int numShortHaul;
    int numLongHaul;

    // Food
    int numChicken;
    int numPork;
    int numBeef;
    int numFish;
    int numVegetable;

    // Consumption
    int numCloth;
    Map<String, Integer> otherPurchase;
    String clothFrequency;
    int numDevice;
    double electricityBill;
    int annualBillEmission;

    public DailyEmissionProcessor(Context context, String dateKey, DataLoadListener listener) {
        // initialize the database reference
        db = FirebaseDatabase.getInstance();
        // userId = "user1"; // change to get actual user id
        myRef = db.getReference("users").child(UserSession.userId);
        this.dateKey = dateKey;
        monthKey = dateKey.substring(0, 7);
        this.context = context;

        // Track task completion (so that we only call the calculations and uploader
        // once all data are retrieved from database
        // TODO: change the value to total firebase queries
        AtomicInteger pendingTasks = new AtomicInteger(12);
        Runnable onComplete = () -> {
            if (pendingTasks.decrementAndGet() == 0 && listener != null) {
                listener.onDataLoaded(); // Notify when all data is loaded
            }
        };

        // retrieve data from database to initialize variables
        // transport ////////////////////////////////////////////
        // distance driven
        DatabaseReference distanceDrivenRef = myRef.child("daily_emission").child(dateKey)
                .child("transportation").child("drive_personal_vehicle")
                .child("distance_driven");
        distanceDrivenRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    distanceDriven = task.getResult().getValue(Double.class);
                } else {
                    // if doesn't exist, set it to 0
                    distanceDriven = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // the type of car the user has
        // TODO: find the correct index for this car type question
        // TODO: uncomment this for the final project
//        DatabaseReference carTypeRef = myRef.child("questionnaire_responses")
//                .child("transportation").child("2").child("answer");
//        carTypeRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//                    carType = task.getResult().getValue(String.class);
//                } else {
//                    // if doesn't exist, set it to ""0""
//                    carType = "";
//                }
//            } else {
//                // Handle Firebase request failure
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//            }
//        });
        carType = "Gasoline";

        // time spent on public transport
        DatabaseReference transportTimeRef = myRef.child("daily_emission").child(dateKey)
                .child("transportation").child("take_public_transportation")
                .child("transport_time");
        transportTimeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    transportTime = task.getResult().getValue(Double.class);
                } else {
                    // if doesn't exist, set it to 0
                    transportTime = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // frequency of taking bus in a year (from questionnaire)
        // TODO: find the correct index for this questionnaire
        // TODO: uncomment this
//        DatabaseReference frequencyRef = myRef.child("questionnaire_responses")
//                .child("transportation").child("4").child("answer");
//        frequencyRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//                    // If the food activity already exists, get the current value and add to it
//                    frequency = task.getResult().getValue(String.class);
//                } else {
//                    // if doesn't exist, set it to ""
//                    frequency = "";
//                }
//            } else {
//                // Handle Firebase request failure
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//            }
//        });
        frequency = "Yes, occasionally";

        // Time spent on bus per week (from questionnaire)
        // TODO: find the correct index for this questionnaire
        // TODO: uncomment this
//        DatabaseReference timeSpentRef = myRef.child("questionnaire_responses")
//                .child("transportation").child("5").child("answer");
//        timeSpentRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//                    timeSpent = task.getResult().getValue(String.class);
//                } else {
//                    // if doesn't exist, set it to ""
//                    timeSpent = "";
//                }
//            } else {
//                // Handle Firebase request failure
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//            }
//        });
        timeSpent = "Under 1 hour";

        // number of short flight
        DatabaseReference shortHaulRef = myRef.child("daily_emission").child(dateKey)
                .child("transportation").child("flight")
                .child("short");
        shortHaulRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numShortHaul = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numShortHaul = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of long flight
        DatabaseReference longHaulRef = myRef.child("daily_emission").child(dateKey)
                .child("transportation").child("flight")
                .child("long");
        longHaulRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numLongHaul = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numLongHaul = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // food ////////////////////////////////////////////
        // number of beef meal
        DatabaseReference beefRef = myRef.child("daily_emission").child(dateKey)
                .child("food").child("meal").child("beef");
        beefRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numBeef = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numBeef = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of pork meal
        DatabaseReference porkRef = myRef.child("daily_emission").child(dateKey)
                .child("food").child("meal").child("pork");
        porkRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numPork = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numPork = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of chicken meal
        DatabaseReference chickenRef = myRef.child("daily_emission").child(dateKey)
                .child("food").child("meal").child("chicken");
        chickenRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numChicken = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numChicken = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of fish meal
        DatabaseReference fishRef = myRef.child("daily_emission").child(dateKey)
                .child("food").child("meal").child("fish");
        fishRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numFish = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numFish = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of plant-based meal
        DatabaseReference vegetableRef = myRef.child("daily_emission").child(dateKey)
                .child("food").child("meal").child("plant_based");
        vegetableRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numVegetable = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numVegetable = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // consumption ////////////////////////////////////////////
        // number of clothes purchased
        DatabaseReference clothesRef = myRef.child("daily_emission").child(dateKey)
                .child("consumption").child("buy_new_clothes")
                .child("num_cloth");
        clothesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    numCloth = task.getResult().getValue(Integer.class);
                } else {
                    // if doesn't exist, set it to 0
                    numCloth = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // cloth purchase frequency
        // TODO: replace with actual path
        // TODO: uncomment
//        DatabaseReference clothesFreqRef = myRef.child("questionnaire_responses")
//                .child("consumption").child("0").child("answer");
//        clothesFreqRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//                    clothFrequency = task.getResult().getValue(String.class);
//                } else {
//                    // if doesn't exist, set it to ""
//                    clothFrequency = "";
//                }
//            } else {
//                // Handle Firebase request failure
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//            }
//        });
        clothFrequency = "Monthly";

        // number of electronic devices purchased
        DatabaseReference deviceRef = myRef.child("daily_emission").child(dateKey)
                .child("consumption").child("buy_electronics");
        deviceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Retrieve the map of electronic devices
                    // TODO: make sure to verify this works!!!!!!!
                    Map<String, Object> electronicsMap = (Map<String, Object>) task.getResult().getValue();
                    numDevice = 0;
                    if (electronicsMap != null) {
                        // Sum up the values in the map
                        for (Object value : electronicsMap.values()) {
                            if (value instanceof Number){
                                numDevice += ((Number) value).intValue();
                            }

                        }
                    }
                } else {
                    numDevice = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // number of each type of other purchases
        otherPurchase = new HashMap<>();
        DatabaseReference otherRef = myRef.child("daily_emission").child(dateKey)
                .child("consumption").child("other_purchases");
        otherRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // TODO: make sure to verify this works!!!!!!!
                    Map<String, Object> otherMap = (Map<String, Object>) task.getResult().getValue();
                    if (otherMap != null) {
                        for (Map.Entry<String, Object> entry : otherMap.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            int intValue = ((Number) value).intValue();
                            otherPurchase.put(key, intValue);
                        }
                    }
                } else {
                    otherPurchase = null;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // bill amount
        String month = dateKey.substring(0, dateKey.length() - 3);
        DatabaseReference billRef = myRef.child("bill")
                .child(month).child("electricity");
        billRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    electricityBill = task.getResult().getValue(Double.class);
                } else {
                    electricityBill = 0;
                }
                onComplete.run();
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // the annual emission from last year
        // TODO: change to correct path
        // TODO: uncomment
//        DatabaseReference annualBillRef = myRef.child("questionnaire_responses")
//                .child("consumption").child("3").child("answer");;
//        annualBillRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//                    annualBillEmission = task.getResult().getValue(Integer.class);
//                } else {
//                    annualBillEmission = 0;
//                }
//            } else {
//                // Handle Firebase request failure
//                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//            }
//        });
        annualBillEmission = 2400;
    }

    public double carCalculator() {
        // Car emission - use emissionFactor x distance
        double emissionFactor;
        switch (carType.toLowerCase()) {
            case "gasoline":
                emissionFactor = 0.24;
                break;
            case "diesel":
                emissionFactor = 0.27;
                break;
            case "hybrid":
                emissionFactor = 0.16;
                break;
            case "electric":
                emissionFactor = 0.05;
                break;
            default:
                emissionFactor = 0;
                break;
        }
        return emissionFactor * distanceDriven;
    }

    public double publicTransportCalculator() {
        // Public transport
        // Assumptions:
        //// 1. Using the initial questionnaire, we assume that the user belong to the same
        //      frequency category as last year. (Ex. occasionally and 1-3 hours) Then, we use
        //      that emission and do some dividing to get an hourly emission
        //// 2. Each emission in formula is for a year's time, so we divide by 365 then by 24 to
        //      find hourly emission (we multiply this by today's transport time)
        //// 3. for each time category, it is unclear whether the boundaries are inclusive or
        //      exclusive. We choose to have the lower bound as inclusive and upper bound as
        //      exclusive. (this makes sense because having higher foot print is a better warning
        //      for the user to reduce their foot print) [ex. 1-3 is 1 <= ___ < 3]
        double annual;
        // TODO: replace the strings with the actual strings stored in database
        Map<String, Integer> Occasionally = new HashMap<>();
        Occasionally.put("under 1 hour", 246);
        Occasionally.put("1-3 hours", 819);
        Occasionally.put("3-5 hours", 1638);
        Occasionally.put("5-10 hours", 3071);
        Occasionally.put("more than 10 hour", 4095);

        Map<String, Integer> Frequently = new HashMap<>();
        Frequently.put("under 1 hour", 573);
        Frequently.put("1-3 hours", 1911);
        Frequently.put("3-5 hours", 3822);
        Frequently.put("5-10 hours", 7166);
        Frequently.put("more than 10 hour", 9555);

        Map<String, Integer> Always = new HashMap<>();
        Always.put("under 1 hour", 1050);
        Always.put("1-3 hours", 2363);
        Always.put("3-5 hours", 4103);
        Always.put("5-10 hours", 9611);
        Always.put("more than 10 hour", 13750);

        // TODO: replace with the actual answers stored in database
        if(frequency.equals("Yes, occasionally")) {
            annual = Occasionally.get(timeSpent.toLowerCase());
        } else if(frequency.equals("Frequently")) {
            annual = Frequently.get(timeSpent.toLowerCase());
        } else {
            annual = Always.get(timeSpent.toLowerCase());
        }

        return annual / 365.0 / 24.0 * transportTime;
    }

    public double flightCalculator() {
        // Short haul - we don't know total flight the user take in this year
        // Assumption: We take each category's emission, divide each one by the lower bound
        //             (for example, 1-2 flights = 225, then we divide 225 by 1) to get emission
        //             per flight for each category. Then, we find the average of those emission
        //             to be more accurate.
        double emissionPerShortFlight = ((225.0) + (600.0 / 3.0) + (1200.0 / 6.0)
                + (1800.0 / 11.0)) / 4.0;
        double dailyShortHaulEmission = emissionPerShortFlight * numShortHaul;

        // Long haul - same issue and assumption as above
        double emissionPerLongFlight = ((825.0) + (2200.0 / 3.0) + (4400.0 / 6.0)
                + (6600.0 / 11.0)) / 4.0;
        double dailyLongHaulEmission = emissionPerLongFlight * numLongHaul;
       return dailyShortHaulEmission + dailyLongHaulEmission;
    }

    public double transportCalculator() {
        return carCalculator() + publicTransportCalculator() + flightCalculator();
    }

    public void transportUploader() {
        double carEmission = carCalculator();
        double publicEmission = publicTransportCalculator();
        double flightEmission = flightCalculator();
        double emission = transportCalculator();

        // Store in database
        DatabaseReference carRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("drive_personal_vehicle");
        carRef.setValue(carEmission);

        // Store in database
        DatabaseReference publicRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("take_public_transportation");
        publicRef.setValue(publicEmission);

        // Store in database
        DatabaseReference flightRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("flight");
        flightRef.setValue(flightEmission);

        // Store in database
        DatabaseReference walkRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("cycling_or_walking");
        flightRef.setValue(0.0);

        // Store in database
        DatabaseReference totalRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("transportation");
        totalRef.setValue(emission);
     }

    public double foodCalculator() {
        // for emission based on food, here are the assumptions made:
        // 1. for beef, chicken, pork, and fish, we use the emission for eating them daily
        //    and divide it by 365 to get emission for one day. We assume this resulting
        //    emission can be scaled using number of serving. For example, if I had 2
        //    serving of chicken today, I take the emission for an year: 950 kg, divide it
        //    by 365 and multiply by 2.
        // 2. for plant-based meal, we use the emission for Vegan (since they only eat
        //    plant-based meal). Same as above, divide it by 365 and times number of serving
        double emissionBeef = (2500.0 / 365.0) * numBeef;
        double emissionPork = (1450.0 / 365.0) * numPork;
        double emissionChicken = (950.0 / 365.0) * numChicken;
        double emissionFish = (800.0 / 365.0) * numFish;
        double emissionVegetable = (500.0 / 365.0) * numVegetable;
        return emissionBeef + emissionPork + emissionChicken + emissionFish + emissionVegetable;
    }

    public void foodUploader() {
        double foodEmission = foodCalculator();
        // Store in database
        DatabaseReference foodRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("food");
        foodRef.setValue(foodEmission);
    }

    public double clothesCalculator() {
        // Assumption: using the annual emission category from questionnaire as the baseline
        //             emission, assume that that is how often the user make clothing purchases.
        //             Divide by the number of purchase per year to find emission per item.
        //             Assume that: Monthly - 12 purchase/year
        //                          Quarterly - 4 purchases/year
        //                          Annually - 1 purchase/year
        //                          Rarely - 1 purchase every few years (0.5/year for simplicity)
        double emissionPerCloth;
        switch (clothFrequency) {
            case "Monthly":
                emissionPerCloth = 360.0 / 12.0;
                break;
            case "Quarterly":
                emissionPerCloth = 120.0 / 4.0;
                break;
            case "Annually":
                emissionPerCloth = 100.0;
                break;
            default:
                emissionPerCloth = 5.0 / 0.5;
                break;
        }
        return emissionPerCloth * numCloth;
    }

    public double deviceCalculator() {
        // From the formula, we assume that every device purchased converts to 300kg emission
        // since 1 time a year turns into 300kg emission
        return numDevice * 300;
    }

    public double otherCalculator() {
        if(otherPurchase == null) {
            return 0.0;
        }

        // Assumptions:
        // No formulas are provided for other purchase, so we are going to calculate the
        // emission based on online averages for each categories:
        // Furniture: ~150 kg CO2e (taking an average between 50-200 kg)
        // Appliances: ~600 kg CO2e (taking an average between 200-1,000 kg)
        // Entertainment: ~350 kg CO2e (taking an average between 100-600 kg)
        // Personal Care: ~3 kg CO2e (taking an average between 1-5 kg)
        // Pet Supplies: ~30 kg CO2e (taking an average between 5-50 kg)
        // Books and Stationery: ~3.5 kg CO2e (taking an average between 2-5 kg)
        // Other Items: ~52.5 kg CO2e (taking an average between 5-100 kg)
        double total = 0;
        for (Map.Entry<String, Integer> entry : otherPurchase.entrySet()) {
            String purchaseType = entry.getKey();
            int numPurchase = entry.getValue();

            if(purchaseType.equals("furniture")) {
                total += numPurchase * 150;
            }
            if(purchaseType.equals("appliances")) {
                total += numPurchase * 600;
            }
            if(purchaseType.equals("entertainment")) {
                total += numPurchase * 350;
            }
            if(purchaseType.equals("personal_care")) {
                total += numPurchase * 3;
            }
            if(purchaseType.equals("pet_supplies")) {
                total += numPurchase * 30;
            }
            if(purchaseType.equals("books_and_stationery")) {
                total += numPurchase * 3.5;
            }
            if(purchaseType.equals("other")) {
                total += numPurchase * 52.5;
            }
        }
        return total;
    }

    public double billCalculator() {
        // Assumptions:
        // based on the questionnaire intially, assume that the amount of bill each month
        // should fall in the same range as last year (which is realistic). If the user's
        // bill amount is outside of this range, notify the user to change their questionnaire
        // answer if their monthly bill is increasing / decreasing (this notification will be done
        // in the fragment)

        // Also, we only calculate the monthly emission because it would not make sense to
        // provide a daily emission for an activity that occurs monthly
        if(electricityBill == 0) {
            // means the user have not yet logged their bill
            return 0;
        }
        return annualBillEmission / 12.0;
    }

    public double consumptionCalculator() {
        // Assumptions, since monthly bills are tracked monthly, we do not include it in our
        // daily emission
        return clothesCalculator() + deviceCalculator() + otherCalculator();
    }

    public void consumptionUploader() {
        double clothEmission = clothesCalculator();
        double deviceEmission = deviceCalculator();
        double otherEmission = otherCalculator();
        double billMonthlyEmission = billCalculator();
        double total = consumptionCalculator();

        // Store in database
        DatabaseReference clothRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("buy_new_clothes");
        clothRef.setValue(clothEmission);

        DatabaseReference deviceRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("buy_electronics");
        deviceRef.setValue(deviceEmission);

        DatabaseReference otherRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("other_purchases");
        otherRef.setValue(otherEmission);

        DatabaseReference totalRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("consumption");
        totalRef.setValue(total);

        // upload the monthly emission
        // Here, we store monthly emission only.
        DatabaseReference billMonthRef = myRef.child("bill").child(monthKey)
                .child("monthly_emission");
        billMonthRef.setValue(billMonthlyEmission);
    }

    public double dailyTotalCalculator() {
        return transportCalculator() + foodCalculator() + consumptionCalculator();
    }

    public void dailyTotalUploader() {
        // Upload the daily total
        double total = dailyTotalCalculator();
        // upload to database
        DatabaseReference totalRef = myRef.child("daily_emission").child(dateKey)
                .child("emission").child("total");
        totalRef.setValue(total);
    }

    public void mainUploader() {
        // call all uploader to upload data
        transportUploader();
        foodUploader();
        consumptionUploader();
        dailyTotalUploader();
    }
}
