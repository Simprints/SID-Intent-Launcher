plugins {
    id("com.android.application")

    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")

    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("androidx.room")
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.simprints.intentlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.simprints.intentlauncher"
        minSdk = 21
        targetSdk = 34
        versionCode = 5
        versionName = "2024.05.1"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    ksp(libs.hilt.compiler)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.materialIcons)
    implementation(libs.compose.foundation)
    implementation(libs.compose.graphics)
    implementation(libs.compose.preview)
    implementation(libs.compose.liveData)
    implementation(libs.compose.navigation)
    implementation(libs.compose.stateEvent)
    debugImplementation(libs.compose.tooling)

    implementation(libs.accompanist.themeAdapter)
    implementation(libs.lifecycle.compose)

    // Data
    implementation(libs.room.core)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.gson)
    implementation(libs.datastore)
}
