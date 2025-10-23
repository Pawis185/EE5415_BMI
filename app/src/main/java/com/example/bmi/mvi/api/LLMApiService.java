package com.example.bmi.mvi.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LLM API Service - 调用AI API获取健康建议
 * 支持多语言响应
 */
public class LLMApiService {

    private static final String TAG = "LLMApiService";

    // DeepSeek API配置
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "";

    // 使用单线程池，避免并发问题
    private ExecutorService executorService;
    private Handler mainHandler;

    public LLMApiService() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 回调接口
     */
    public interface LLMCallback {
        void onSuccess(String suggestion);
        void onError(String error);
    }

    /**
     * 获取健康建议
     */
    public void getHealthSuggestion(double bmi, int age, String gender,
                                    String language, LLMCallback callback) {
        // 检查executorService是否已关闭
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            try {
                String suggestion = callLLMApi(bmi, age, gender, language);

                // 移除Markdown格式符号
                suggestion = removeMarkdownFormatting(suggestion);

                String finalSuggestion = suggestion;
                mainHandler.post(() -> callback.onSuccess(finalSuggestion));
            } catch (Exception e) {
                Log.e(TAG, "Error calling LLM API", e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * 移除Markdown格式符号
     */
    private String removeMarkdownFormatting(String text) {
        if (text == null) return "";

        // 移除**粗体标记
        text = text.replaceAll("\\*\\*", "");

        // 移除*斜体标记
        text = text.replaceAll("\\*", "");

        // 移除#标题标记
        text = text.replaceAll("^#{1,6}\\s+", "");

        // 移除行首的- 或* 列表标记
        text = text.replaceAll("(?m)^[-*]\\s+", "");

        return text.trim();
    }

    /**
     * 调用LLM API
     */
    private String callLLMApi(double bmi, int age, String gender, String language) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            // 设置请求
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            // 构建提示词
            String prompt = buildPrompt(bmi, age, gender, language);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "deepseek-chat");

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            // 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }

                    // 解析响应
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject messageObj = choice.getJSONObject("message");
                        return messageObj.getString("content");
                    }
                }
            } else {
                // 读取错误信息
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line.trim());
                    }
                    Log.e(TAG, "API Error: " + errorResponse.toString());
                }
                throw new Exception("API request failed with code: " + responseCode);
            }

            throw new Exception("No response from API");

        } finally {
            conn.disconnect();
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(double bmi, int age, String gender, String language) {
        String genderText = gender.equals("male") ?
                (language.equals("zh") ? "男" : "male") :
                (language.equals("zh") ? "女" : "female");

        if (language.equals("zh")) {
            return String.format(
                    "作为健康顾问，为一位%d岁的%s提供简短的健康建议。他/她的BMI是%.1f。" +
                            "请用中文回复，不超过150字，包括饮食和运动建议。请使用纯文本格式，不要使用任何Markdown标记（如**、*、#等）。",
                    age, genderText, bmi
            );
        } else {
            return String.format(
                    "As a health advisor, provide brief health suggestions for a %d-year-old %s with a BMI of %.1f. " +
                            "Reply in English, no more than 150 words, including diet and exercise advice. " +
                            "Use plain text format without any Markdown formatting (no **, *, #, etc.).",
                    age, genderText, bmi
            );
        }
    }

    /**
     * 关闭服务（不立即关闭线程池）
     */
    public void shutdown() {
        // 不在这里关闭executorService，避免横竖屏切换时出错
        // executorService会在应用退出时自动回收
    }

    /**
     * 强制关闭（仅在应用完全退出时调用）
     */
    public void forceShutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}