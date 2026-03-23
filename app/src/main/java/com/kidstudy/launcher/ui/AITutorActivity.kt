package com.kidstudy.launcher.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kidstudy.launcher.R
import java.util.Locale

/**
 * AI辅导页面
 *
 * 注意：此功能需要配置豆包AI API Key才能使用
 * 配置方法：修改 AITutorActivity.kt 中的 API_KEY 变量
 */
class AITutorActivity : AppCompatActivity() {

    private lateinit var etQuestion: EditText
    private lateinit var tvAnswer: TextView
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_tutor)

        etQuestion = findViewById(R.id.et_question)
        tvAnswer = findViewById(R.id.tv_answer)

        // 初始化TTS
        initTTS()

        // 显示初始提示
        showInitialMessage()

        // 发送问题按钮
        findViewById<View>(R.id.btn_send).setOnClickListener {
            val question = etQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "请输入问题", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 检查是否配置了API Key
            if (!isApiKeyConfigured()) {
                tvAnswer.text = "⚠️ AI功能未配置\n\n" +
                        "家长需要先配置豆包AI API Key：\n\n" +
                        "配置步骤：\n" +
                        "1. 访问火山引擎控制台\n" +
                        "2. 开通豆包大模型服务\n" +
                        "3. 在 AITutorActivity.kt 中填入 API Key\n\n" +
                        "或者长按\"家长控制\"按钮进入设置页面"
                return@setOnClickListener
            }

            // 禁用按钮防止重复点击
            it.isEnabled = false
            tvAnswer.text = "🤔 正在思考..."

            // 模拟AI回答（演示用）
            simulateAIResponse(question) {
                it.isEnabled = true
            }
        }
    }

    /**
     * 显示初始消息
     */
    private fun showInitialMessage() {
        if (!isApiKeyConfigured()) {
            tvAnswer.text = "👋 欢迎使用AI学习助手！\n\n" +
                    "⚠️ AI功能需要家长配置API Key后才能使用\n\n" +
                    "家长可以：\n" +
                    "• 长按桌面\"家长控制\"按钮\n" +
                    "• 进入设置页面配置AI功能\n\n" +
                    "💡 提示：你可以先体验其他功能哦！"
        } else {
            tvAnswer.text = "👋 你好！我是AI学习小助手\n\n" +
                    "有什么问题可以问我哦~\n\n" +
                    "例如：\n" +
                    "• \"1+1等于多少？\"\n" +
                    "• \"天空为什么是蓝色的？\"\n" +
                    "• \"给我讲个故事吧\""
        }
    }

    /**
     * 检查API Key是否已配置
     */
    private fun isApiKeyConfigured(): Boolean {
        return try {
            val field = AITutorActivity::class.java.getDeclaredField("API_KEY")
            field.isAccessible = true
            val value = field.get(this) as? String
            value != null && value != "你的豆包API_Key" && value.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 模拟AI回答（演示用）
     * 实际使用时需要配置API Key后调用真实的豆包AI接口
     */
    private fun simulateAIResponse(question: String, onComplete: () -> Unit) {
        // 延迟1秒模拟网络请求
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val answer = when {
                question.contains("1+1") || question.contains("一加一") -> "1 + 1 = 2呀！你真棒，会算加法了！👏"
                question.contains("天空") || question.contains("蓝色") -> "天空看起来是蓝色的，是因为太阳光穿过大气层时，蓝色光被散射得最多。就像你用三棱镜看到彩虹一样神奇！🌈"
                question.contains("故事") -> "从前有一只小兔子，它特别喜欢吃胡萝卜。有一天它发现了一个超大的胡萝卜园，开心极了！小兔子学会了分享，把胡萝卜分给了其他小动物，大家都很开心。故事告诉我们要学会分享哦！🐰"
                question.contains("你好") || question.contains("是谁") -> "你好呀！我是AI学习小助手，可以帮你解答问题、讲故事、教你知识。有什么想问的吗？😊"
                else -> "这是个好问题！不过我的AI功能还需要家长配置API Key才能更好地回答你哦~\n\n你可以先试试问我：\"1+1等于多少？\"或者\"给我讲个故事吧\""
            }

            tvAnswer.text = answer
            playTTS(answer)
            etQuestion.text?.clear()
            onComplete()
        }, 1000)
    }

    /**
     * 初始化文字转语音
     */
    private fun initTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 不支持中文语音，静默处理
                }
            }
        }
    }

    /**
     * TTS朗读
     */
    private fun playTTS(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance")
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.apply {
            stop()
            shutdown()
        }
        tts = null
    }
}
