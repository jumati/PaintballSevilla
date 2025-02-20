plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.jm.paintballsevilla"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jm.paintballsevilla"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // TODO: Dependencies for Firebase
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, don't specify versions in Firebase dependencies
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth")
    // Firebase Adapter
    implementation("com.firebaseui:firebase-ui-firestore:8.0.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    // For control over item selection of both touch and mouse driven selection
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

}