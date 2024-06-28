plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("maven-publish")
}

android {
    namespace = "com.maden.mface"
    compileSdk = 34

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    /*
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
     */
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.face.detection)
    implementation(libs.play.services.mlkit.face.detection)
    implementation(libs.play.services.vision)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.support)

    implementation(libs.gson)
}


afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.madenmustafa"
                artifactId = "m-face"
                version = "0.1.0"
            }
        }
        repositories {
            // Configure JitPack repository
            maven {
                url = uri("https://jitpack.io")
            }
        }
    }
}


/**
 * Disables the execution of specific tasks of type AbstractArchiveTask.
 * It iterates over all tasks of this type and checks if their names are
 * either "releaseSourcesJar" or "debugSourcesJar". If a task has one of these names,
 * its enabled property is set to false, effectively preventing it from executing.
 */
tasks.withType<AbstractArchiveTask> {
    if(this.name == "releaseSourcesJar" || this.name == "debugSourcesJar") {
        enabled = false
    }
}