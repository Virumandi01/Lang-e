plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.example.language"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.language"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies{
    //noinspection UseTomlInstead
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.mlkit:text-recognition:16.0.1")
    // Markdown Rendering (Makes code look like code)
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:syntax-highlight:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")
// Room Database (For Offline Dictionary)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // CHANGED: Use 'ksp' here. It works perfectly with new Android Studio.
    ksp("androidx.room:room-compiler:$room_version")

}
configurations.all {
    exclude(group = "org.jetbrains", module = "annotations-java5")
}