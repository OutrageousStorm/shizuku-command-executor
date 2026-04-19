plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    namespace = "com.outrageousstorm.shizuku"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core:1.10.1")
    implementation("dev.rikka.shizuku:api:13.0.0")
    implementation("dev.rikka.shizuku:provider:13.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
