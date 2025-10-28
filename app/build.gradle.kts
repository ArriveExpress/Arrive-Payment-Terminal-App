import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id(libs.plugins.android.dagger.hilt.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.firebase.crashlytics.get().pluginId)
    id(libs.plugins.google.services.get().pluginId)
}

fun configureSigning(signingName: String, signingPrefix: String) {
    val signingPropFile = file("signing.properties")
    if (signingPropFile.exists()) {
        val props = Properties().apply {
            load(FileInputStream(signingPropFile))
        }
        android.signingConfigs.findByName(signingName)?.apply {
            storeFile = file(props["${signingPrefix}_STORE_FILE"].toString())
            storePassword = props["${signingPrefix}_STORE_PASSWORD"].toString()
            keyAlias = props["${signingPrefix}_KEY_ALIAS"].toString()
            keyPassword = props["${signingPrefix}_KEY_PASSWORD"].toString()
        }
    }
}

fun configureBuildFields(buildType: String, prefix: String) {
    val apiKeysPropFile = file("api_keys.properties")
    if (apiKeysPropFile.exists()) {
        val props = Properties().apply {
            load(FileInputStream(apiKeysPropFile))
        }

        if (props.containsKey("${prefix}_API_KEY")) {
            android.buildTypes.getByName(buildType) {
                buildConfigField("String", "API_TOKEN", "\"${props["${prefix}_API_KEY"]}\"")
                buildConfigField("String", "PUSHER_KEY", "\"${props["${prefix}_PUSHER_KEY"]}\"")
            }
        }
    }
}
// Versioning
val mVersionCode = 16
val mVersionName = "16"
val extraParams = rootProject.extra

android {
    namespace = "com.arrive.terminal"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.arrive.terminal"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = mVersionCode
        versionName = mVersionName
    }

    flavorDimensions += "env"

    productFlavors {
        create("pushka") {
            dimension = "env"
            applicationId = "com.example.pushka_donation"
            versionNameSuffix = "-pushka"
            resValue("string", "app_name", "Arrive Live")
            buildConfigField("String", "BASE_API_URL", "\"https://arrive.ytsolutions.co\"")
            buildConfigField("String", "PUSHER_HOST", "\"ws.arrive.ytsolutions.co\"")
        }

        create("terminalLive") {
            dimension = "env"
            applicationId = "com.arrive.terminal_live"
            versionNameSuffix = "-live"
            resValue("string", "app_name", "Arrive Testing Live")
            buildConfigField("String", "BASE_API_URL", "\"https://arrive.ytsolutions.co\"")
            buildConfigField("String", "PUSHER_HOST", "\"ws.arrive.ytsolutions.co\"")
        }

        create("terminalDev") {
            dimension = "env"
            applicationId = "com.arrive.terminal"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Arrive Testing Local")
            buildConfigField("String", "BASE_API_URL", "\"https://arrive-prod.shipto.shop\"")
            buildConfigField("String", "PUSHER_HOST", "\"arrive-prod.shipto.shop\"")
        }
    }

    signingConfigs {
        create("release")
        getByName("debug"){}
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            ext.set("enableCrashlytics", true)
            signingConfig = signingConfigs["debug"]
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            ext.set("enableCrashlytics", true)
            signingConfig = signingConfigs["release"]
            configureSigning("release", "RELEASE")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":card-payment"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.gson.converter)
    implementation(libs.pusher.java.client)

    implementation(libs.adapterDelegates.viewBinding)
    implementation(libs.adapterDelegates.layoutContainer)
    implementation(libs.adapterDelegates.dsl)

    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    kapt(libs.dagger.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(libs.data.store)
}

configureBuildFields("release", "RELEASE")
configureBuildFields("debug", "DEBUG")
