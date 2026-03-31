package com.financetracker.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AIAdvisor {

private static final String GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private String apiKey;

    public AIAdvisor() {
        try {
            Properties props = new Properties();
            InputStream input = AIAdvisor.class
                .getClassLoader()
                .getResourceAsStream("config.properties");
            props.load(input);
            this.apiKey = props.getProperty("gemini.api.key");
        } catch (IOException e) {
            System.out.println("Could not load Gemini API key: " + e.getMessage());
        }
    }

    public String getFinancialAdvice(String transactionSummary) {

        String prompt = "You are a personal finance advisor. "
            + "Analyze the following transaction data and give specific, "
            + "actionable advice in 4-5 lines. Be friendly and encouraging.\n\n"
            + "Transaction Summary:\n" + transactionSummary;

        // Build request body
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);

        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(GEMINI_URL + "?key=" + apiKey)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parse response safely
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            // Check for API error first
            if (json.has("error")) {
                String errorMsg = json.getAsJsonObject("error")
                                      .get("message").getAsString();
                return "Gemini API Error: " + errorMsg;
            }

            // Navigate the response safely
            if (!json.has("candidates")) {
                return "Unexpected response from Gemini. Please try again.";
            }

            JsonArray candidates = json.getAsJsonArray("candidates");
            if (candidates.size() == 0) {
                return "Gemini returned no response. Please try again.";
            }

            JsonObject candidate = candidates.get(0).getAsJsonObject();

            // Check finish reason
            if (candidate.has("finishReason") &&
                !candidate.get("finishReason").getAsString().equals("STOP")) {
                return "Gemini could not complete the response. Try again.";
            }

            return candidate
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        } catch (IOException e) {
            return "Could not connect to AI. Check your internet connection.";
        } catch (Exception e) {
            return "AI parsing error: " + e.getMessage();
        }
    }
}