package com.kidstudy.launcher.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.util.Log
import com.kidstudy.launcher.R
import com.kidstudy.launcher.utils.Constants

class AppMonitorService : Service() {
    private lateinit var handler: Handler
    private lateinit var activityManager: ActivityManager
    private val checkInterval = 2000L // 2秒检查一次

    override fun onCreate() {
        super.onCreate()
        // 前台服务（防止被杀死）
        createNotificationChannel()
        startForeground(1, createNotification())

        // 后台线程检查前台应用
        val thread = HandlerThread("AppMonitorThread", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        handler = Handler(thread.looper)
        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager

        // 开始循环检查
        handler.post(checkRunnable)
    }

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, checkInterval)
        }
    }

    // 检查前台应用
    private fun checkForegroundApp() {
        val whiteList = getWhiteListFromSP()
        // 加入自身包名，避免误杀
        whiteList.add(packageName)

        val foregroundApp = getForegroundPackageName()
        if (foregroundApp != null && !whiteList.contains(foregroundApp)) {
            // 强制关闭非白名单应用
            activityManager.forceStopPackage(foregroundApp)
            Log.d("AppMonitor", "强制关闭：$foregroundApp")
        }
    }

    // 获取前台应用包名
    private fun getForegroundPackageName(): String? {
        return try {
            @Suppress("DEPRECATION")
            activityManager.runningAppProcesses?.firstOrNull {
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }?.processName
        } catch (e: Exception) {
            null
        }
    }

    // 从SP读取白名单
    private fun getWhiteListFromSP(): MutableSet<String> {
        return getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .getStringSet(Constants.SP_KEY_WHITE_LIST, mutableSetOf()) ?: mutableSetOf()
    }

    // 创建前台服务通知
    private fun createNotification(): Notification {
        return Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("儿童学习桌面运行中")
            .setContentText("保护孩子学习环境")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    // 创建通知渠道
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            "儿童桌面监控",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkRunnable)
    }
}