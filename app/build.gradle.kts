plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kidstudy.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kidstudy.launcher"
        minSdk = 21  // 降低到Android 5.0以兼容更多设备
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 支持多种CPU架构（包括模拟器）
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 生成签名APK（后续步骤会讲）
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true // 开启ViewBinding
    }

    // 生成包含所有架构的通用APK（用于模拟器兼容）
    splits {
        abi {
            isEnable = false // 禁用ABI拆分，生成通用APK
        }
    }
}

dependencies {
    // 核心依赖
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // 权限请求 - 暂时注释，JitPack 仓库在 CI 中可能有解析问题
    // implementation("com.permissionx.guolindev:permissionx:1.7.1")

    // 网络请求 - 用于调用豆包AI API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // 协程支持
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}