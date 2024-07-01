package com.example.quizlearning.Services.AIFeature;

import com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI.OnTranslateListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class Translate {
    public Translate() {
    }

    public void translateText(String fromLangCode, String toLangCode, String text, OnTranslateListener listener) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLangCode)
                .setTargetLanguage(toLangCode)
                .build();
        final Translator translator = Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        translator.translate(text)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        listener.onTranslateSuccess(s);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    listener.onTranslateFailure(e);
                                });
                    }
                }).addOnFailureListener(e -> {
                    listener.onTranslateFailure(e);
                });
    }
}
