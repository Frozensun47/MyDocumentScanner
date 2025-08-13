plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.myapplications.mydocscanner"
    // Changed compileSdk to 34, a recent stable version. 35 is a preview release.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.myapplications.mydocscanner"
        minSdk = 30
        // Changed targetSdk to 34 to match compileSdk.
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // Added composeOptions to specify the Kotlin compiler extension version.
    // This is crucial for Jetpack Compose.
    // Ensure this version is compatible with your project's Kotlin version.
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Compatible with Kotlin 1.9.22
    }
}

dependencies {
    // Define the camerax_version variable.

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Added dependency for extended Material icons, which includes SwapHoriz
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // CameraX dependencies now use the defined variable
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)

    // Accompanist for Permissions (simplifies permission handling)
    implementation(libs.accompanist.permissions)

    // Jetpack Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
}
