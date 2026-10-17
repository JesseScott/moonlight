import java.util.Properties

plugins {
    //trick: for the same plugin versions in all submodules
    id("com.android.application").version("9.4.1").apply(false)
    id("com.android.library").version("9.4.1").apply(false)
    // AGP 9 compiles Kotlin itself (built-in Kotlin), so there is no kotlin("android") plugin. The Compose compiler
    // plugin's version sets the Kotlin version AGP uses; KSP replaces kapt for Hilt.
    kotlin("plugin.compose").version("2.4.21").apply(false)
    id("com.google.devtools.ksp").version("2.3.12").apply(false)
    id("com.google.dagger.hilt.android").version("2.60.1").apply(false)
    id("com.google.gms.google-services").version("4.5.0").apply(false)
    id("com.google.firebase.crashlytics").version("3.0.7").apply(false)
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.google.android.gms:oss-licenses-plugin:0.13.0")
    }
}

// The version comes from version.properties (repo root), so the AAB's file name always matches what is inside it.
// Bump versionCode there for every Play upload.
val versionProperties = Properties().apply { file("../version.properties").inputStream().use { load(it) } }
extra["appVersionName"] = versionProperties.getProperty("versionName")
extra["appVersionCode"] = versionProperties.getProperty("versionCode").toInt()

// The upload key for Google Play. keystore.properties (gitignored, repo root) says where it is:
//   storeFile=C:/path/to/the/unlocked/vault/moonlight-upload.jks
//   storePassword=...
//   keyAlias=upload
//   keyPassword=...
// Without it, release builds are produced unsigned: fine for checking that they build, but Play needs them signed.
// A relative storeFile is resolved from the repo root.
val keystoreFile = file("../keystore.properties")
extra["keystoreProperties"] = if (keystoreFile.exists()) Properties().apply { keystoreFile.inputStream().use { load(it) } } else null

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
