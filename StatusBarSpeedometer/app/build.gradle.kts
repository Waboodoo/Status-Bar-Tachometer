plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    namespace = "ch.rmy.android.statusbar_tacho"
    compileSdk = 33

    defaultConfig {
        applicationId = "ch.rmy.android.statusbar_tacho"
        minSdk =  21
        targetSdk = 33
        versionName = "3.5.1"
        //noinspection HighAppVersionCode
        versionCode = 2003050100
        // 20,(2 digits major),(2 digits minor),(2 digits patch),(2 digits build)

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        languageVersion = "1.6"
        jvmTarget = "1.8"
    }

    sourceSets.getByName("main") {
        java.setSrcDirs(listOf("src/main/kotlin"))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.core:core-ktx:1.9.0@aar")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
}
