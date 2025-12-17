import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "tt.co.jesses.moonlight.widget"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.compose.ui:ui:1.6.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.2")
}
