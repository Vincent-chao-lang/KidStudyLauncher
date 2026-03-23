package com.kidstudy.launcher.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kidstudy.launcher.R
import com.kidstudy.launcher.utils.Constants

/**
 * 密码验证页面
 * 用于家长控制入口验证
 */
class PasswordVerifyActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_verify)

        etPassword = findViewById(R.id.et_password)
        btnConfirm = findViewById(R.id.btn_confirm)

        // 确认按钮
        btnConfirm.setOnClickListener {
            val inputPassword = etPassword.text.toString().trim()

            if (inputPassword.isEmpty()) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 验证密码
            val savedPassword = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
                .getString(Constants.SP_KEY_PASSWORD, null)

            if (TextUtils.isEmpty(savedPassword)) {
                // 还没有设置密码，跳转到设置页面
                Toast.makeText(this, "请先设置家长密码", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SetPasswordActivity::class.java))
                finish()
            } else if (inputPassword == savedPassword) {
                // 密码正确，进入家长控制
                Toast.makeText(this, "密码正确", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ParentControlActivity::class.java))
                finish()
            } else {
                // 密码错误
                Toast.makeText(this, getString(R.string.toast_password_error), Toast.LENGTH_SHORT).show()
                etPassword.text?.clear()
            }
        }
    }

    // 屏蔽返回键
    override fun onBackPressed() {
        // 如果密码验证失败，不允许返回
        finish()
    }
}
