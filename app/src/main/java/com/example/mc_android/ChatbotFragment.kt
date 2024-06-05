package com.example.mc_android

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mc_android.databinding.FragmentChatbotBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatbotFragment : Fragment() {
    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val MY_SECRET_KEY = "API_KEY"

    private val messages = mutableListOf<Pair<String, Boolean>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.sendButton.isEnabled = s.toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.sendButton.setOnClickListener {
            val inputText = binding.userInput.text.toString()
            addMessage(inputText, true)
            callAPI(inputText)
            // 입력창 초기화
            binding.userInput.setText("")
        }

        binding.helpButton.setOnClickListener {
            val helpChatbot = HelpChatbotFragment()
            helpChatbot.show(parentFragmentManager, "HelpChatbot")
        }
    }

    private fun addMessage(text: String, isQuestion: Boolean) {
        messages.add(Pair(text, isQuestion))
        updateChat()
    }

    private fun updateChat() {
        binding.chatContainer.removeAllViews()
        for ((text, isQuestion) in messages) {
            val textView = TextView(context).apply {
                this.text = text
                this.setPadding(16, 16, 16, 16)
                this.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                    gravity = if (isQuestion) Gravity.END else Gravity.START
                }

                if (isQuestion) {
                    this.setBackgroundColor(Color.parseColor("#7E57C2"))
                    this.setTextColor(Color.WHITE)
                } else {
                    this.setBackgroundColor(Color.WHITE)
                    this.setTextColor(Color.BLACK)
                }
            }
            binding.chatContainer.addView(textView)
        }

        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun callAPI(question: String) {
        addMessage("...", false)

        val arr = JSONArray()
        val baseAi = JSONObject().apply {
            put("role", "system")
            put("content",
                "당신은 사용자의 런닝을 도와주는 ChatBot입니다. " +
                        "모든 답변은 3줄로 합니다. 인사는 받아줄 수 있지만 런닝, 운동 이외의 답변에는 대답할 수 없습니다.")
        }
        val userMsg = JSONObject().apply {
            put("role", "user")
            put("content", question)
        }
        arr.put(baseAi)
        arr.put(userMsg)

        val jsonObject = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", arr)
        }

        val body = RequestBody.create(JSON, jsonObject.toString())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $MY_SECRET_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    addResponse("Failed to load response due to ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                        requireActivity().runOnUiThread {
                            addResponse(result.trim())
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        addResponse("Failed to load response due to ${response.body?.string()}")
                    }
                }
            }
        })
    }

    private fun addResponse(response: String) {
        messages.removeAt(messages.size - 1)
        addMessage(response, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}