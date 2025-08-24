plugins {
    id("com.android.application")
    id("kotlin-android")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "ch.rmy.android.statusbar_tacho"
    compileSdk = 36

    defaultConfig {
        applicationId = "ch.rmy.android.statusbar_tacho"
        minSdk = 21
        targetSdk = 35
        versionName = "3.14.0"
        //noinspection HighAppVersionCode
        versionCode = 2003140000
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

    buildFeatures {
        compose = true
        buildConfig = false
        viewBinding = false
        dataBinding = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    packaging {
        resources {
            excludes.add("DebugProbesKt.bin")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets.getByName("main") {
        java.setSrcDirs(listOf("src/main/kotlin"))
    }

    lint {
        disable.add("MissingTranslation")
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.runtime)

    /* Compose */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.materialIconsExtended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.uiToolingPreview)
    debugImplementation(libs.androidx.compose.uiTooling)
}
