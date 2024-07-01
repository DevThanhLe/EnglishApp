package com.example.quizlearning.controller;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnUpdateTopicInFolderListener;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnUpdateTopicListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.activity.DetailFolderActivity;
import com.example.quizlearning.activity.LoginActivity;
import com.example.quizlearning.activity.MainActivity;
import com.example.quizlearning.model.TopicModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class UpdateTopic extends AppCompatActivity {
    private static final String TAG = "UpdateTopic";
    Button bSave;
    Switch swShare;
    EditText etTopic, etDescription;
    String authId,authName, topicId, folderId;
    DatabaseReference databaseReference;
    FirebaseFirestore mStore;
    ModifierData modifierData;
    CollectionReference topicRef;
    ProgressDialog progressDialog ;
    ImageView icPublic, icPrivate;
    SharedPreferences sharedPreferences;
    ImageButton back;
    Date timeCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_topic);
        bSave = findViewById(R.id.bSaveEdit);
        etTopic = findViewById(R.id.etUpdateTopic);
        etDescription = findViewById(R.id.etUpdateDescription);
        swShare = findViewById(R.id.swEditShare);
        back = findViewById(R.id.back);
        //getIntent từ idUser ở DetailActivity
        icPrivate = findViewById(R.id.icPrivate);
        icPublic = findViewById(R.id.icPublic);
        progressDialog = new ProgressDialog(this);
        modifierData = new ModifierData();
        mStore = FirebaseFirestore.getInstance();
        topicRef = mStore.collection("Topics");
        //Intent từ DetailTopicActivity
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        if ( sharedPreferences.getString("userId", null) == null) {
            startActivity(new Intent(UpdateTopic.this, LoginActivity.class));
            finish();
        } else {
            authId = sharedPreferences.getString("userId", null);
            authName = sharedPreferences.getString("username", null);
        }
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            etTopic.setText(bundle.getString("topicTitle"));
            etDescription.setText(bundle.getString("topicDescription"));
            swShare.setChecked(bundle.getBoolean("topicShare"));
            topicId = (bundle.getString("topicId"));
            folderId = (bundle.getString("folderId"));
            if(bundle.getBoolean("topicShare")){
                icPrivate.setVisibility(View.GONE);
                icPublic.setVisibility(View.VISIBLE);
            }
            timeCreate = new Date(bundle.getLong("timeCreate"));
        }
        if (swShare.isChecked()) {
            icPrivate.setVisibility(View.GONE);
            icPublic.setVisibility(View.VISIBLE);
        } else {
            icPrivate.setVisibility(View.VISIBLE);
            icPublic.setVisibility(View.GONE);
        }
        if(etTopic.getText().toString().trim().length() == 0){
            bSave.setEnabled(false);
        }
        etTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bSave.setEnabled(etTopic.getText().toString().trim().length() != 0);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bSave.setEnabled(etTopic.getText().toString().trim().length() != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {
                bSave.setEnabled(etTopic.getText().toString().trim().length() != 0);
            }
        });
        setShare();
        swShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateTopic.this, String.valueOf(swShare.isChecked()), Toast.LENGTH_SHORT).show();
                setShare();
            }
        });
//        databaseReference = FirebaseDatabase.getInstance().getReference("Topic").child(topicId);

//
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                updateData();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void updateData(){
        String title = etTopic.getText().toString();
        String des = etDescription.getText().toString();
        Boolean share = swShare.isChecked();

        TopicModel topicModel = new TopicModel(topicId, title, des, share, authId, timeCreate);
        topicModel.setUsername(authName);
        progressDialog.show();
        modifierData.updateTopic(topicModel, folderId,new OnUpdateTopicListener() {
            @Override
            public void onUpdateTopicSuccess(TopicModel topicModel) {
                Toast.makeText(UpdateTopic.this, "Changed", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onUpdateTopicFailure(Exception e) {
                Toast.makeText(UpdateTopic.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("desc", des);
        resultIntent.putExtra("share", share);
        setResult(RESULT_OK, resultIntent);
        progressDialog.dismiss();

        finish();

    }
    public void setShare(){
        if (swShare.isChecked()) {
            icPrivate.setVisibility(View.GONE);
            icPublic.setVisibility(View.VISIBLE);
        } else {
            icPrivate.setVisibility(View.VISIBLE);
            icPublic.setVisibility(View.GONE);
        }
    }
}