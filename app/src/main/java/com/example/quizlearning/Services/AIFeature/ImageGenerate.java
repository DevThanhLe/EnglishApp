package com.example.quizlearning.Services.AIFeature;

import android.util.Log;

import com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI.OnCallAPIOpenAIListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ImageGenerate {
    private static final String TAG = "ImageGenerate";
    public static final MediaType JSON = MediaType.get("application/json");
    private static final String API_KEY = "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";

    OkHttpClient client = new OkHttpClient();

    String imageUrl;
    String filename;

    public ImageGenerate() {}

    private void generateImage(String text, OnCallAPIOpenAIListener listener) {
        JSONObject object = new JSONObject();

        try {
            object.put("prompt", text);
            object.put("size", "256x256");
        } catch (JSONException e) {
            Log.e(TAG, "callApi: "+e.getMessage());
        }

        RequestBody requestBody = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer "+ API_KEY)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    listener.onCallAPIOpenAISuccess(imageUrl);
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: "+e.getMessage());
                    listener.onCallAPIOpenAIFailure(e);
                }
            }
        });
    }
}
