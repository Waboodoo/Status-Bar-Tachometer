import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
}

extensions.configure<ApplicationExtension> {
    namespace = "ch.rmy.android.statusbar_tacho"
    compileSdk = 37

    defaultConfig {
        applicationId = "ch.rmy.android.statusbar_tacho"
        minSdk = 23
        targetSdk = 37
        versionName = "3.16.0"
        //noinspection HighAppVersionCode
        versionCode = 2003160000
        // 20,(2 digits major),(2 digits minor),(2 digits patch),(2 digits build)

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("debug") {
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
        resValues = false
        shaders = false
    }

    packaging {
        resources {
            excludes.add("DebugProbesKt.bin")
        }
    }

    lint {
        disable.add("MissingTranslation")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.service)

    /* Compose */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.uiToolingPreview)
    debugImplementation(libs.androidx.compose.uiTooling)
}
