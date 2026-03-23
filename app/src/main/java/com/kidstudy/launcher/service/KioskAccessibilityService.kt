package com.kidstudy.launcher.service

import android.content.Intent
import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.kidstudy.launcher.MyApplication
import com.kidstudy.launcher.ui.PasswordVerifyActivity
import com.kidstudy.launcher.utils.Constants
import android.view.accessibility.AccessibilityEvent
import com.kidstudy.launcher.utils.Constants

class KioskAccessibilityService : AccessibilityService() {

    // 白名单应用包名（从SharedPreferences读取）
    private val whiteListPackages = mutableSetOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 拦截系统弹窗（如通知栏、电源菜单）
        val pkgName = event.packageName?.toString()
        if (pkgName == "android" || pkgName == "com.android.systemui") {
            performGlobalAction(GLOBAL_ACTION_BACK)
            return
        }

        // 拦截非白名单应用窗口
        if (!whiteListPackages.contains(pkgName) && pkgName != packageName) {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // 拦截物理按键
        return when (event.keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_APP_SWITCH,
            KeyEvent.KEYCODE_NOTIFICATION -> true // 消费事件，不传递
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                // 连续按5次音量键唤醒密码验证
                VolumeKeyCounter.count()
                false // 不拦截音量键，仅计数
            }
            else -> super.onKeyEvent(event)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 加载白名单
        whiteListPackages.addAll(getWhiteListFromSP())
    }

    override fun onInterrupt() {}

    // 从SP读取白名单
    private fun getWhiteListFromSP(): Set<String> {
        return getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .getStringSet(Constants.SP_KEY_WHITE_LIST, mutableSetOf()) ?: mutableSetOf()
    }
}

// 音量键计数器
object VolumeKeyCounter {
    private var count = 0
    private var lastClickTime = 0L

    fun count() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 1000) {
            count++
            if (count >= 5) {
                // 唤醒密码验证页面
                val intent = Intent(MyApplication.context, PasswordVerifyActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MyApplication.context.startActivity(intent)
                count = 0
            }
        } else {
            count = 1
        }
        lastClickTime = currentTime
    }
}