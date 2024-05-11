plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.iotproject.drivercoordination_customer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iotproject.drivercoordination_customer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    ////    implementation ("com.squareup.okhttp3:okhttp:3.10.0") //okhttp3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

//    //define any required ok http without version
//    implementation("com.squareup.okhttp3:okhttp")
//    implementation("com.squareup.okhttp3:logging-interceptor")
//
//    //define a BOM and its version
//    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.0"))

    //add library
    //Design 软件包提供的 API 支持向应用添加 Material Design 组件和图案。
    //Design 支持库增加了对各种 Material Design 组件和模式的支持，供应用开发者在此基础上进行构建，例如抽屉式导航栏、悬浮操作按钮 (FAB)、信息提示控件和标签页。
    //implementation("com.android.support:design:28.0.0")冲突
    //Material Components for Android
    implementation("com.google.android.material:material:1.12.0-rc01")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    //字体库
    implementation("uk.co.chrisjenx:calligraphy:2.3.0")
    //cardview library
    implementation("com.android.support:cardview-v7:28.0.0")
    //open street map on android
    implementation("org.osmdroid:osmdroid-android:6.1.8")
    //Lottie animations
    implementation("com.airbnb.android:lottie:2.8.0")
    //loading animation
    implementation("com.wang.avi:library:2.1.3")
    //circle image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}