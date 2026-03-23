// MyApplication.kt
package com.kidstudy.launcher

import android.app.Application

class MyApplication : Application() {
    companion object {
        lateinit var context: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}