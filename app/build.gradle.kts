plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Add Hilt plugin
    id("com.google.dagger.hilt.android") version "2.51.1"

    // Add kapt plugin for annotation processing
    kotlin("kapt")
}

android {
    namespace = "com.prototype.silver_tab"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    defaultConfig {
        applicationId = "com.prototype.silver_tab"
        minSdk = 24
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures{
        compose = true
        buildConfig = true
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev-sp") {
            dimension = "environment"
            applicationIdSuffix = ".dev.sp"
            resValue("string", "app_name", "SilTab DevSP")
            buildConfigField("String", "BASE_URL", value = "\"http://192.168.224.224:8099/\"")
        }

        create("dev-campinas") {
            dimension = "environment"
            applicationIdSuffix = ".dev.campinas"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "SilTab DevCamp")
            buildConfigField("String", "BASE_URL", "\"http://10.42.253.88:5000/\"")
        }

        create("bgate") {
            dimension = "environment"
            applicationIdSuffix = ".bgate"
            resValue("string", "app_name", "SilTab BGATE")
            buildConfigField("String", "BASE_URL", "\"http://192.168.224.227:8099/\"") //https://bgate-uat.bydauto.com/stock_api/
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    // Fix duplicate build feature declaration
    buildFeatures {
        compose = true
    }

    // Add kapt configuration
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    // Hilt dependencies
    implementation(libs.daggerHilt)
    implementation(libs.androidx.hilt.work)
    kapt(libs.daggerHiltCompiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Existing dependencies
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation(libs.jjwt)
    implementation(libs.androidx.animation.core.android)

    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")

    implementation ("com.jakewharton.timber:timber:5.0.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation (libs.androidx.security.crypto)

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.3")

    implementation(libs.accompanist.swiperefresh)

    implementation(libs.androidx.material)

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation ("com.squareup.moshi:moshi:1.15.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)

    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")

    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    configurations.all {
        exclude(group = "androidx.wear.compose", module = "compose-material-core")
    }
}