plugins {
    kotlin("android")
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "tt.co.jesses.moonlight.wear"
    compileSdk = 34
    defaultConfig {
        applicationId = "tt.co.jesses.moonlight.wear"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            //shrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":common"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.wear:wear:1.3.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.wear.compose:compose-foundation:1.3.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-compiler:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
