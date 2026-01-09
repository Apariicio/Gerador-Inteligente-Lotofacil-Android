plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.aparicioamaral.quinzenumerosaleatorios"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aparicioamaral.quinzenumerosaleatorios"
        minSdk = 29
        targetSdk = 35
        versionCode = 44
        versionName = "4.5.3"

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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}