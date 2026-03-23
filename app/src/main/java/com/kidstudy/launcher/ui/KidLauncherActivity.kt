package com.kidstudy.launcher.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidstudy.launcher.R
import com.kidstudy.launcher.service.AppMonitorService
import com.kidstudy.launcher.utils.Constants
import com.kidstudy.launcher.utils.startService

class KidLauncherActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kid_launcher)

        // 沉浸式全屏（屏蔽状态栏/导航栏）
        setFullScreen()

        // 启动应用监控服务
        startService<AppMonitorService>()

        // 初始化桌面图标
        gridLayout = findViewById(R.id.grid_apps)
        initAppIcons()

        // 点击桌面空白处无响应（防止误触）
        findViewById<View>(R.id.root_layout).setOnClickListener {}
    }

    // 沉浸式全屏
    private fun setFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 保持屏幕常亮

        // 隐藏导航栏（全面屏）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    // 初始化白名单应用图标
    private fun initAppIcons() {
        val whiteList = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
            .getStringSet(Constants.SP_KEY_WHITE_LIST, mutableSetOf()) ?: mutableSetOf()

        // 清空原有图标
        gridLayout.removeAllViews()

        // 遍历白名单，添加图标
        whiteList.forEach { pkgName ->
            val appInfo = packageManager.getApplicationInfo(pkgName, 0)
            val icon = packageManager.getApplicationIcon(appInfo)
            val name = packageManager.getApplicationLabel(appInfo).toString()

            // 创建图标布局
            val itemView = layoutInflater.inflate(R.layout.item_app_icon, gridLayout, false)
            itemView.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(icon)
            itemView.findViewById<TextView>(R.id.tv_app_name).text = name

            // 点击打开应用
            itemView.setOnClickListener {
                val intent = packageManager.getLaunchIntentForPackage(pkgName)
                if (intent != null) {
                    startActivity(intent)
                }
            }

            gridLayout.addView(itemView)
        }

        // 添加豆包AI入口
        val aiItemView = layoutInflater.inflate(R.layout.item_app_icon, gridLayout, false)
        aiItemView.findViewById<ImageView>(R.id.iv_app_icon).setImageResource(R.mipmap.ic_ai)
        aiItemView.findViewById<TextView>(R.id.tv_app_name).text = "AI辅导"
        aiItemView.setOnClickListener {
            // 打开AI辅导页面
            startActivity(Intent(this, AITutorActivity::class.java))
        }
        gridLayout.addView(aiItemView)
    }

    // 屏蔽返回键
    override fun onBackPressed() {
        // 空实现，不响应返回
    }
}