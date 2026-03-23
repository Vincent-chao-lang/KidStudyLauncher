package com.kidstudy.launcher.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kidstudy.launcher.R
import com.kidstudy.launcher.utils.Constants
import com.kidstudy.launcher.utils.ServiceUtils
import com.kidstudy.launcher.service.KioskAccessibilityService

class ParentControlActivity : AppCompatActivity() {

    private lateinit var llAppList: LinearLayout
    private val installedApps = mutableListOf<AppInfo>()

    data class AppInfo(val pkgName: String, val name: String, val isChecked: Boolean = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_control)

        llAppList = findViewById(R.id.ll_app_list)

        // 检查是否已设置密码
        val hasPassword = !getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .getString(Constants.SP_KEY_PASSWORD, null).isNullOrEmpty()

        if (!hasPassword) {
            Toast.makeText(this, "请先设置家长密码", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SetPasswordActivity::class.java))
            return
        }

        // 申请必要权限
        requestPermissions()

        // 加载已安装应用
        loadInstalledApps()

        // 保存白名单按钮
        findViewById<View>(R.id.btn_save).setOnClickListener {
            saveWhiteList()
            Toast.makeText(this, "白名单已保存", Toast.LENGTH_SHORT).show()
            // 启动儿童桌面
            startActivity(Intent(this, KidLauncherActivity::class.java))
            finish()
        }

        // 设置密码按钮
        findViewById<View>(R.id.btn_set_pwd).setOnClickListener {
            // 先验证当前密码
            startActivity(Intent(this, PasswordVerifyActivity::class.java))
        }
    }

    // 申请权限
    private fun requestPermissions() {
        // 检查并引导开启系统设置权限
        if (!Settings.System.canWrite(this)) {
            Toast.makeText(this, "请授予系统设置权限", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        // 检查并引导开启悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        // 引导开启无障碍服务
        if (!ServiceUtils.isAccessibilityServiceEnabled(this, KioskAccessibilityService::class.java)) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // 引导开启使用情况访问权限
        if (!hasUsageStatsPermission()) {
            startActivity(Intent("android.settings.USAGE_ACCESS_SETTINGS"))
        }
    }

    // 加载已安装应用
    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)

        // 读取已保存的白名单
        val savedWhiteList = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .getStringSet(Constants.SP_KEY_WHITE_LIST, mutableSetOf()) ?: mutableSetOf()

        // 解析应用信息
        resolveInfos.forEach {
            val pkgName = it.activityInfo.packageName
            val name = it.loadLabel(pm).toString()
            installedApps.add(AppInfo(pkgName, name, savedWhiteList.contains(pkgName)))
        }

        // 渲染应用列表（带复选框）
        installedApps.forEach { app ->
            val checkBox = CheckBox(this).apply {
                text = app.name
                isChecked = app.isChecked
                tag = app.pkgName
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            llAppList.addView(checkBox)
        }
    }

    // 保存白名单
    private fun saveWhiteList() {
        val whiteList = mutableSetOf<String>()
        for (i in 0 until llAppList.childCount) {
            val checkBox = llAppList.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                whiteList.add(checkBox.tag.toString())
            }
        }
        // 保存到SP
        getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .edit()
            .putStringSet(Constants.SP_KEY_WHITE_LIST, whiteList)
            .apply()
    }

    // 检查使用情况访问权限
    private fun hasUsageStatsPermission(): Boolean {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val now = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            android.app.usage.UsageStatsManager.INTERVAL_DAILY,
            now - 1000 * 3600,
            now
        )
        return stats != null && stats.isNotEmpty()
    }
}