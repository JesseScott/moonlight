plugins {
    kotlin("android")
    kotlin("plugin.compose")
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

// Version and upload key are set once, in the root build.gradle.kts, from version.properties and keystore.properties
val appVersionName: String by rootProject.extra
val appVersionCode: Int by rootProject.extra
val keystoreProperties: java.util.Properties? by rootProject.extra
base.archivesName.set("moonlight-$appVersionName-$appVersionCode")

android {
    namespace = "tt.co.jesses.moonlight.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "tt.co.jesses.moonlight.android"
        minSdk = 24
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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

    buildTypes {
        getByName("debug") {
            // Install alongside the Play Store release instead of clashing with its signature
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            signingConfigs.findByName("release")?.let { signingConfig = it }
            // The R8 mapping goes to the production Crashlytics project, which only the signed Play build should do:
            // a check-only build (CI, or a local build without keystore.properties) must not overwrite it.
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = signingConfigs.findByName("release") != null
            }
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
    constraints {
        implementation("androidx.vectordrawable:vectordrawable:1.1.0")
        implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
    }

    implementation(project(":common"))
    implementation(project(":widget"))

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.browser:browser:1.10.0")
    implementation("androidx.compose.foundation:foundation:1.11.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui:1.11.0")
    implementation("androidx.compose.ui:ui-tooling:1.11.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.11.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("com.google.android.gms:play-services-oss-licenses:17.5.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    // SunCalc
    implementation("org.shredzone.commons:commons-suncalc:3.11")
}