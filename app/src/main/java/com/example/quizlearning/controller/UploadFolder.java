package com.example.quizlearning.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnAddFolderListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.activity.MainActivity;
import com.example.quizlearning.model.FolderModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UploadFolder extends AppCompatActivity {
    Button bSaveFolder;
    EditText etNameFolder;
    String userId, auth, topicId;
    ImageButton back;
    FirebaseFirestore mStore;
    ProgressDialog progressDialog;
    CollectionReference folderRef;
    ModifierData modifierData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_folder);
        bSaveFolder = findViewById(R.id.bSaveFolder);
        etNameFolder = findViewById(R.id.etNameFolder);
        back = findViewById(R.id.back);
        //getIntent từ idUser ở HomeFragment
        userId = getIntent().getStringExtra("userId");

        progressDialog = new ProgressDialog(this);
        mStore=  FirebaseFirestore.getInstance();
        folderRef = mStore.collection("Folders");
        modifierData = new ModifierData();
        //Ẩn hiện public:
        bSaveFolder.setEnabled(false);
        //Topic != null
        etNameFolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0) {
                    bSaveFolder.setEnabled(false);
                }
                else {
                    bSaveFolder.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        bSaveFolder.setOnClickListener(new View.OnClickListener() {
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
    public void uploadData() {
        String nameFolder = etNameFolder.getText().toString();
        //Lấy username từ sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", "Anonymous");
        DocumentReference documentRef = folderRef.document();
        String id = documentRef.getId();
        //Get time update:
//        String currentDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        //Database Ref:
        FolderModel folderModel = new FolderModel(nameFolder, id, userName);
        folderModel.setAuthor(userId);
        modifierData.addFolder(folderModel, new OnAddFolderListener() {
            @Override
            public void onAddFolderSuccess(FolderModel folderModel) {
                progressDialog.dismiss();
                Toast.makeText(UploadFolder.this, "Saved", Toast.LENGTH_SHORT).show();
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("topicAdded", true);
//                setResult(RESULT_OK, resultIntent);
//                startActivity(new Intent(UploadFolder.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAddFolderFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadFolder.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

