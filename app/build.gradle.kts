import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

val localProperties = Properties().apply {
    val localFile = project.rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    } else {
        logger.warn("local.properties not found; apiKey will be empty")
    }
}

android {
    namespace = "com.example.cookeasy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cookeasy"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "apiKey", "\"${localProperties.getProperty("apiKey") ?: ""}\"")    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        buildConfig = true // Enable buildConfig feature
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.compose.animation:animation:1.6.0") // For animations

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Coroutines for async tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // YouTube Player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")

    // Media3 for video playback
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.1.1") // For HLS streams if needed

    // Flow Layout for tags
    implementation("com.google.accompanist:accompanist-flowlayout:0.31.2-alpha")

    // Gemini API
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    implementation ("androidx.compose.material:material:1.7.0") // Material 2
    // Core Compose dependencies
    implementation ("androidx.activity:activity-compose:1.9.2") // For ComponentActivity
    implementation( "androidx.compose.ui:ui:1.7.0" )// Core Compose UI
    implementation ("androidx.compose.runtime:runtime:1.7.0")
    implementation ("androidx.compose.material3:material3:1.3.0") // Material 3
    implementation ("androidx.compose.animation:animation:1.7.0") // For animations (used in OnboardingActivity)
}
