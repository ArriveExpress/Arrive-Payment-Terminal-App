import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id(libs.plugins.android.dagger.hilt.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
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
                val value = props["${prefix}_API_KEY"] as String
                buildConfigField("String", "API_TOKEN", "\"$value\"")
                buildConfigField("String", "PUSHER_KEY", "\"${props["${prefix}_PUSHER_KEY"]}\"")
            }
        }
    }
}
// Versioning
val mVersionCode = 12
val mVersionName = "12"

val extraParams = rootProject.extra

android {
    namespace = "com.arrive.terminal"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.pushka_donation"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = mVersionCode
        versionName = mVersionName
    }

    signingConfigs {
        create("release") {
            val signingPropFile = file("signing.properties")
            if (signingPropFile.exists()) {
                val props = Properties().apply {
                    load(FileInputStream(signingPropFile))
                }
                android.signingConfigs.findByName("release")?.apply {
                    storeFile = file(props["RELEASE_STORE_FILE"].toString())
                    storePassword = props["RELEASE_STORE_PASSWORD"].toString()
                    keyAlias = props["RELEASE_KEY_ALIAS"].toString()
                    keyPassword = props["RELEASE_KEY_PASSWORD"].toString()
                }
            }
        }

        buildTypes {
            getByName("debug") {
                isDebuggable = true
                isMinifyEnabled = false
                isShrinkResources = false
                resValue("string", "app_name", "Arrive Live DEBUG")
                buildConfigField("String", "BASE_API_URL", "\"https://arrive-prod.shipto.shop\"")
                buildConfigField("String", "PUSHER_HOST", "\"arrive-prod.shipto.shop\"")

                setProperty(
                    "archivesBaseName",
                    buildString {
                        append(extraParams["APK_NAME_PREFIX"])
                        append("_")
                        append(android.defaultConfig.versionName)
                    }
                )
                signingConfig = signingConfigs.getByName("debug")
            }

            getByName("release") {
                isDebuggable = false
                isMinifyEnabled = false
                isShrinkResources = false
                resValue("string", "app_name", "Arrive Live")
                buildConfigField("String", "BASE_API_URL", "\"https://arrive.ytsolutions.co\"")
                buildConfigField("String", "PUSHER_HOST", "\"ws.arrive.ytsolutions.co\"")

                setProperty(
                    "archivesBaseName",
                    buildString {
                        append(extraParams["APK_NAME_PREFIX"])
                        append("_")
                        append(android.defaultConfig.versionName)
                    }
                )

                configureSigning(signingName = "release", signingPrefix = "RELEASE")
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
        implementation("org.slf4j:slf4j-simple:1.7.32") // Or another appropriate SLF4J implementation

    }

    configureBuildFields(buildType = "debug", prefix = "DEBUG")
    configureBuildFields(buildType = "release", prefix = "RELEASE")
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}