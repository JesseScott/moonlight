plugins {
    //trick: for the same plugin versions in all submodules
    id("com.android.application").version("8.9.3").apply(false)
    id("com.android.library").version("8.9.3").apply(false)
    kotlin("android").version("2.1.0").apply(false)
    kotlin("multiplatform").version("2.1.0").apply(false)
    kotlin("plugin.compose").version("2.1.0").apply(false)
    id("com.google.dagger.hilt.android").version("2.55").apply(false)
    id("com.google.gms.google-services").version("4.4.2").apply(false)
    id("com.google.firebase.crashlytics").version("3.0.2").apply(false)
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.google.android.gms:oss-licenses-plugin:0.13.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
