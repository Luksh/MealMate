plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
}

android {
    namespace = "np.com.ismt.laxman.mealmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "np.com.ismt.laxman.mealmate"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //firebase bom
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    //firebase analytics
    implementation("com.google.firebase:firebase-analytics")

    //firebase-auth
    implementation("com.google.firebase:firebase-auth")

    //firebase firestore
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    //gson
    implementation("com.google.code.gson:gson:2.8.8")

    //camera-X dependencies
    implementation(libs.cameraXCore)
    implementation(libs.camera2)
    implementation(libs.cameraLifeCycle)
    implementation(libs.cameraVideo)
    implementation(libs.cameraView)
    implementation(libs.cameraExtensions)

    //for location
    implementation(libs.location)

    //for google maps
    implementation(libs.googleMaps)


    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}