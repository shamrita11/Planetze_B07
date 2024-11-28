import org.gradle.kotlin.dsl.libs

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.planetze"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.planetze"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.google.firebase.database)
    implementation(libs.cardview)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.recyclerview)
    implementation(libs.material.calendarview)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.database)
    
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.google.firebase.auth)
    testImplementation(libs.google.firebase.database)
    testImplementation(libs.core)
    
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.espresso.core)
    
    // Import Glide Library for GIF
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    // Import for Splash Screen
    implementation(libs.core.splashscreen)
}
