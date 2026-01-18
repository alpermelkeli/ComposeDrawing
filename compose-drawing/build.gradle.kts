plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

android {
    namespace = "com.alpermelkeli.composedrawing"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)

    // Foundation for gestures
    implementation("androidx.compose.foundation:foundation")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.tooling)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.alpermelkeli"
            artifactId = "compose-drawing"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Compose Drawing")
                description.set("A Jetpack Compose library for drawing on canvas with customizable tools")
                url.set("https://github.com/alpermelkeli/compose-drawing")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("alpermelkeli")
                        name.set("Alper Melkeli")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/alpermelkeli/compose-drawing.git")
                    developerConnection.set("scm:git:ssh://github.com/alpermelkeli/compose-drawing.git")
                    url.set("https://github.com/alpermelkeli/compose-drawing/tree/main")
                }
            }
        }
    }
}
