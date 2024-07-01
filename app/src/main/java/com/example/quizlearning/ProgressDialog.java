package com.example.quizlearning;

import android.app.AlertDialog;
import android.content.Context;

public class ProgressDialog {
    AlertDialog dialog;
    Context mContext;
    public ProgressDialog(Context context) {
        mContext = context;
        dialog = new AlertDialog.Builder(mContext)
                .setView(R.layout.progress_layout)
                .setCancelable(false)
                .create();
    }
    public void show(){
        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }
}
