package com.kidstudy.launcher.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kidstudy.launcher.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

/**
 * AI辅导页面 - 使用豆包AI API
 *
 * API接入说明：
 * 1. 访问火山引擎官网注册账号：https://console.volcengine.com/
 * 2. 开通豆包大模型服务，获取 API Key
 * 3. 将 API Key 填入下方的 API_KEY 常量
 * 4. API文档：https://www.volcengine.com/docs/82379/1263481
 */
class AITutorActivity : AppCompatActivity() {

    private lateinit var etQuestion: EditText
    private lateinit var tvAnswer: TextView

    // ==================== 配置区域 ====================
    // TODO: 替换为你的豆包API Key（从火山引擎控制台获取）
    private val API_KEY = "你的豆包API_Key"

    // 豆包API端点（使用豆包-pro模型）
    private val API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions"

    // 请求超时时间（秒）
    private val TIMEOUT_SECONDS = 30L

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    // ==================== TTS相关 ====================
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_tutor)

        etQuestion = findViewById(R.id.et_question)
        tvAnswer = findViewById(R.id.tv_answer)

        // 初始化TTS
        initTTS()

        // 发送问题按钮
        findViewById<View>(R.id.btn_send).setOnClickListener {
            val question = etQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "请输入问题", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (API_KEY == "你的豆包API_Key") {
                tvAnswer.text = "请先在代码中配置您的豆包API Key\n\n" +
                        "获取方式：\n" +
                        "1. 访问火山引擎控制台\n" +
                        "2. 开通豆包大模型服务\n" +
                        "3. 获取API Key并填入代码"
                return@setOnClickListener
            }

            // 禁用按钮防止重复点击
            it.isEnabled = false
            tvAnswer.text = "正在思考中..."

            sendQuestionToDouBao(question) {
                it.isEnabled = true
            }
        }
    }

    /**
     * 初始化文字转语音
     */
    private fun initTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 设置中文语音
                val result = tts?.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "不支持中文语音", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 调用豆包API
     */
    private fun sendQuestionToDouBao(question: String, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 构建请求体
                val requestBody = buildRequestBody(question)

                // 构建HTTP请求
                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                // 发送请求
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    throw IOException("请求失败: ${response.code}")
                }

                // 解析响应
                val responseBody = response.body?.string() ?: throw IOException("响应为空")
                val answer = parseResponse(responseBody)

                // 主线程更新UI
                withContext(Dispatchers.Main) {
                    tvAnswer.text = answer
                    // TTS朗读回答
                    playTTS(answer)
                    etQuestion.text?.clear()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvAnswer.text = "出错了：${e.message}\n\n请检查网络连接和API Key配置"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }

    /**
     * 构建API请求体
     */
    private fun buildRequestBody(question: String): okhttp3.RequestBody {
        val json = JSONObject().apply {
            put("model", "ep-20241118142901-xv9wl") // 豆包模型ID，需根据实际情况修改

            // 系统提示词
            put("system_message", "你是一个儿童学习辅导老师，名字叫小豆。你的特点是：\n" +
                    "1. 回答简单易懂，适合3-10岁孩子理解\n" +
                    "2. 语言生动有趣，多用比喻和例子\n" +
                    "3. 鼓励孩子思考，不要直接给答案\n" +
                    "4. 用温暖友善的语气交流")

            // 对话消息
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "你是一个儿童学习辅导老师，名字叫小豆。回答要简单易懂，适合3-10岁孩子理解，语言生动有趣。")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", question)
                })
            }
            put("messages", messages)

            // 参数设置
            put("temperature", 0.7)
            put("max_tokens", 500)
        }

        return json.toString()
            .toRequestBody("application/json".toMediaType())
    }

    /**
     * 解析API响应
     */
    private fun parseResponse(responseBody: String): String {
        try {
            val json = JSONObject(responseBody)
            val choices = json.getJSONArray("choices")
            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                return message.getString("content")
            }
            return "抱歉，没有收到回答"
        } catch (e: Exception) {
            return "解析回答失败: ${e.message}"
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
