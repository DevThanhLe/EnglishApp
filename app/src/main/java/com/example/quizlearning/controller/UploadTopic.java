package com.example.quizlearning.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnMoveTopicToFolderListener;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnAddTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnAddTopicListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.activity.LoginActivity;
import com.example.quizlearning.model.TopicModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class UploadTopic extends AppCompatActivity {
    ImageView uploadImage;
    Button bSave;
    ModifierData modifierData;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    CollectionReference topicRef;
    Switch swShare;
    EditText etTopic, etDescription;
    String authId, authName, folderId;
    ImageButton back;
    //text:
    ImageView icPublic, icPrivate;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_topic);
        bSave = findViewById(R.id.bSave);
        etTopic = findViewById(R.id.etTopic);
        etDescription = findViewById(R.id.etDescription);
        swShare = findViewById(R.id.swShare);
        back = findViewById(R.id.back);
        progressDialog = new ProgressDialog(this);
        modifierData = new ModifierData();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (sharedPreferences.getString("username", null) == null && sharedPreferences.getString("idUser", null) == null) {
            startActivity(new Intent(UploadTopic.this, LoginActivity.class));
            finish();
        } else {
            authId = sharedPreferences.getString("userId", null);
            authName = sharedPreferences.getString("username", null);
        }

        //Nhận idFolder từ DetailFolderActivity
        folderId = getIntent().getStringExtra("folderId");
        //Topic Ref:
        topicRef = mStore.collection("Topics");

//        Toast.makeText(this, authId, Toast.LENGTH_SHORT).show();
        //Ẩn hiện public:
        icPrivate = findViewById(R.id.icPrivate);
        icPublic = findViewById(R.id.icPublic);
        swShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UploadTopic.this, String.valueOf(swShare.isChecked()), Toast.LENGTH_SHORT).show();
                if(swShare.isChecked()){
                    icPrivate.setVisibility(View.GONE);
                    icPublic.setVisibility(View.VISIBLE);
                }
                else{
                    icPrivate.setVisibility(View.VISIBLE);
                    icPublic.setVisibility(View.GONE);
                }
            }
        });
        bSave.setEnabled(false);
        //Topic != null
        etTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length() == 0) {
                    bSave.setEnabled(false);
                }
                else {
                    bSave.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0) {
                    bSave.setEnabled(false);
                }
                else {
                    bSave.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void uploadData(){
        // Lấy ID tài liệu đã được tạo
        String title = etTopic.getText().toString();
        String des = etDescription.getText().toString();
        Boolean share = swShare.isChecked();
        Date timeCreate = Calendar.getInstance().getTime();

        TopicModel topicModel = new TopicModel(title, des, share, authId, authName, timeCreate);
        progressDialog.show();
        modifierData.addTopic(topicModel, new OnAddTopicListener() {
            @Override
            public void onAddTopicSuccess(TopicModel topicModel) {
                if(folderId != null) {
                    modifierData.moveTopicToFolder(folderId, topicModel, new OnMoveTopicToFolderListener() {
                        @Override
                        public void onMoveTopicToFolderSuccess() {
                            Toast.makeText(UploadTopic.this, "Saved", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                        @Override
                        public void onMoveTopicToFolderFailure(Exception e) {
                            Toast.makeText(UploadTopic.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                }
                progressDialog.dismiss();
                finish();
            }
            @Override
            public void onAddTopicFailure(Exception e) {
                Toast.makeText(UploadTopic.this,"FAIL: "+ e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }

}