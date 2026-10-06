plugins {
    kotlin("android")
    kotlin("plugin.compose")
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

// Version and upload key are set once, in the root build.gradle.kts, from version.properties and keystore.properties
val appVersionName: String by rootProject.extra
val appVersionCode: Int by rootProject.extra
val keystoreProperties: java.util.Properties? by rootProject.extra
base.archivesName.set("moonlight-wear-$appVersionName-$appVersionCode")

android {
    namespace = "tt.co.jesses.moonlight.wear"
    compileSdk = 36
    defaultConfig {
        applicationId = "tt.co.jesses.moonlight.android"
        minSdk = 30
        targetSdk = 36
        versionCode = 1000 + appVersionCode // always above the phone build, see version.properties
        versionName = appVersionName
    }

    buildFeatures {
        compose = true
    }

    signingConfigs {
        keystoreProperties?.let { keys ->
            create("release") {
                // A relative storeFile is relative to the repo root
                storeFile = rootProject.file("..").resolve(keys.getProperty("storeFile"))
                storePassword = keys.getProperty("storePassword")
                keyAlias = keys.getProperty("keyAlias")
                keyPassword = keys.getProperty("keyPassword")
            }
        }
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            signingConfigs.findByName("release")?.let { signingConfig = it }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                debugSymbolLevel = "FULL"
            }
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
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
