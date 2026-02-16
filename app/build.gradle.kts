plugins {
    alias(libs.plugins.android.application)

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
    implementation(project(":dictionary-core"))

    implementation(libs.generativeai)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.mlkit.text)
    implementation(libs.markwon.core)
    implementation(libs.markwon.syntax)
    implementation(libs.markwon.linkify)



}
configurations.all {
    exclude(group = "org.jetbrains", module = "annotations-java5")
}