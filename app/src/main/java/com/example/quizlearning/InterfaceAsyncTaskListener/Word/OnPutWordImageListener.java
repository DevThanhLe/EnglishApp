package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import android.net.Uri;

public interface OnPutWordImageListener {
    void onPutImageSuccess(Uri imageUri);
    void onPutImageFailure(Exception error);
}
