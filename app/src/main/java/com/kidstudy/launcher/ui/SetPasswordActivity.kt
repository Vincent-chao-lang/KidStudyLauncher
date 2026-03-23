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
 * 设置家长密码页面
 */
class SetPasswordActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSavePwd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)

        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnSavePwd = findViewById(R.id.btn_save_pwd)

        // 保存密码按钮
        btnSavePwd.setOnClickListener {
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // 验证密码
            when {
                password.isEmpty() -> {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                }
                password.length < 4 || password.length > 6 -> {
                    Toast.makeText(this, "密码长度为4-6位", Toast.LENGTH_SHORT).show()
                }
                !password.all { it.isDigit() } -> {
                    Toast.makeText(this, "密码只能包含数字", Toast.LENGTH_SHORT).show()
                }
                confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, getString(R.string.toast_password_not_match), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 保存密码
                    getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(Constants.SP_KEY_PASSWORD, password)
                        .apply()

                    Toast.makeText(this, getString(R.string.toast_password_set_success), Toast.LENGTH_SHORT).show()

                    // 返回家长控制页面
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    // 屏蔽返回键
    override fun onBackPressed() {
        // 允许返回
        super.onBackPressed()
    }
}
