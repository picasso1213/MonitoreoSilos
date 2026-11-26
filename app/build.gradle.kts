plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("jacoco")
}

android {
    namespace = "com.example.monitoreosilos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.monitoreosilos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("keystore/monitoreo-silos.jks")
            storePassword = "silos2026"
            keyAlias = "monitoreosilos"
            keyPassword = "silos2026"
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            optimization {
                enable = false
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.navigation:navigation-compose:2.10.2")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.14.12")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

}
jacoco {
    toolVersion = "0.8.14"
}

// Cobertura de la lógica: ViewModels, repositorios probables en JVM y reglas de Silo.
// Excluye Compose, Room, DataStore y navegación, que necesitan el runtime de Android.
tasks.register<JacocoReport>("jacocoLogicaReport") {
    group = "verification"
    description = "Cobertura de la lógica (ViewModel, Repository, Silo)"
    dependsOn("testDebugUnitTest")

    val build = layout.buildDirectory
    executionData.setFrom(
        fileTree(build) { include("outputs/unit_test_code_coverage/**/*.exec", "jacoco/*.exec") }
    )
    classDirectories.setFrom(
        fileTree(build) {
            include(
                "**/kotlin-classes/debug/**/ui/viewmodel/*ViewModel*.class",
                "**/built_in_kotlinc/debug/**/ui/viewmodel/*ViewModel*.class",
                "**/kotlin-classes/debug/**/data/repository/*.class",
                "**/built_in_kotlinc/debug/**/data/repository/*.class",
                "**/kotlin-classes/debug/**/data/model/Silo.class",
                "**/built_in_kotlinc/debug/**/data/model/Silo.class"
            )
            exclude("**/*Companion*.class")
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

