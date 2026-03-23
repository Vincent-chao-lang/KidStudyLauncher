package com.kidstudy.launcher.utils

import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityManager
import android.accessibilityservice.AccessibilityServiceInfo

object ServiceUtils {

    // 检查无障碍服务是否开启
    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.name == serviceClass.name) {
                return true
            }
        }
        return false
    }
}

// 启动服务的扩展函数
inline fun <reified T : android.app.Service> Context.startService() {
    val intent = Intent(this, T::class.java)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}
