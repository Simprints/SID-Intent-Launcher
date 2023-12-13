plugins {
    id("com.android.application")

    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")

    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.simprints.intentlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.simprints.intentlauncher"
        minSdk = 21
        targetSdk = 34
        versionCode = 4
        versionName = "2023.12.1"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    dataBinding {
        enable = true
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
    }
}

dependencies {

    implementation(libs.simprints)

    implementation(libs.coroutines)
    implementation(libs.hilt.core)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.compose.graphics)
    implementation(libs.compose.preview)
    implementation(libs.compose.liveData)
    implementation(libs.compose.navigation)
    implementation(libs.compose.stateEvent)
    debugImplementation(libs.compose.tooling)

    implementation(libs.accompanist.themeAdapter)

    // Other UI
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    kapt(libs.dataBinding.compiler)
    implementation(libs.android.appCompat)
    implementation(libs.android.material)
    implementation(libs.android.coreKtx)
    implementation(libs.android.recycler)

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.viewModel)
    implementation(libs.lifecycle.compose)

    // Data
    implementation(libs.room.core)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.datastore)
}
