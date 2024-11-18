plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.signatureverification"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.signatureverification"
        minSdk = 21
        targetSdk = 34
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
    buildFeatures {
        mlModelBinding =true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation (libs.cardview)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(project(":openCV"))
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    this.implementation ("org.tensorflow:tensorflow-lite:2.11.0")
    this.implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.11.0")
    implementation (libs.ucrop)
    implementation ("com.github.yalantis:ucrop:2.2.8")

}