package com.example.quizlearning.Services.AIFeature;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI.OnRecognizeTextListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class RecognizeText {
    private static final String TAG = "RecognizeText";
    private Context mContext;
    private TextRecognizer recognizer;

    public RecognizeText(Context mContext) {
        this.mContext = mContext;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }
    public void recognizeText(Uri imageSource, OnRecognizeTextListener listener) {
        if(imageSource != null) {
            try {
                InputImage image = InputImage.fromFilePath(mContext, imageSource);
                Task<Text> result = recognizer.process(image)
                        .addOnSuccessListener(text -> {
                            Log.d(TAG, "recognizeText: " + text.getText());
                            listener.onRecognizeTextSuccess(text.getText());
                        })
                        .addOnFailureListener(e -> {
                            listener.onRecognizeTextFailure(e);
                            Log.e(TAG, "recognizeText addOnFailureListener: " + e.getMessage());
                        });
            } catch (IOException e) {
                Log.e(TAG, "recognizeText: " + e.getMessage());
            }
        }
    }
}
