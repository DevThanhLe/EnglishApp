package com.example.quizlearning.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordToTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnPutWordImageListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.DefaultImage;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.WORDSTATE;
import com.example.quizlearning.model.WordModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UploadWord extends AppCompatActivity {
    EditText etWord, etMeanWord;
    private static final String TAG = "UploadWord";

    ImageView uploadImageWord;
    ImageButton back;
    ModifierData modifierData;
    ProgressDialog progressDialog;
    FirebaseFirestore mStore;
    CollectionReference wordRef;
    Button bSaveWord;
    String imageURL;
    Uri uri;
    String topicId,folderId;
    DefaultImage defaultImage;
    Boolean nullWord, nullMeanWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_word);
        uploadImageWord = findViewById(R.id.uploadImageWord);
        bSaveWord = findViewById(R.id.bSaveWord);
        etWord = findViewById(R.id.etWord);
        etMeanWord= findViewById(R.id.etMeanWord);
        back= findViewById(R.id.back);
        modifierData = new ModifierData();
        defaultImage = new DefaultImage();
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            topicId = bundle.getString("topicId");
            folderId = bundle.getString("folderId");
        }
        progressDialog = new ProgressDialog(this);
        mStore = FirebaseFirestore.getInstance();
        if(folderId == null){
            wordRef = mStore
                    .collection("Topics")
                    .document(topicId)
                    .collection("Words");
        }
        else{
            wordRef = mStore
                    .collection("Folders")
                    .document(folderId)
                    .collection("Topics")
                    .document(topicId)
                    .collection("Words");
        }
    //If word & mean word null -> can not save

//        checkNull(nu);
        nullWord = etWord.getText().toString().trim().length() == 0;
        nullMeanWord =  etMeanWord.getText().toString().trim().length() == 0;
        if(nullWord && nullMeanWord){
            bSaveWord.setEnabled(false);
        }
        etWord.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkNull(nullWord, nullMeanWord);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nullWord = etWord.getText().toString().trim().length() == 0;
//                checkNull(nullWord, nullMeanWord);
            }
            @Override
            public void afterTextChanged(Editable s) {
                checkNull(nullWord, nullMeanWord);

            }
        });
        etMeanWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkNull(nullWord, nullMeanWord);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nullMeanWord = etMeanWord.getText().toString().trim().length() == 0;
//                checkNull(nullWord, nullMeanWord);
            }
            @Override
            public void afterTextChanged(Editable s) {
                checkNull(nullWord, nullMeanWord);
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImageWord.setImageURI(uri);
                        }
                        else{
                            Toast.makeText(UploadWord.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImageWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker = new Intent(Intent.ACTION_PICK);
                photopicker.setType("image/*");
                activityResultLauncher.launch(photopicker);
            }
        });

        bSaveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void saveData() {
        progressDialog.show();
        if (uri != null) {
            modifierData.putImage(uri, "", new OnPutWordImageListener() {
                @Override
                public void onPutImageSuccess(Uri imageUri) {
                    imageURL = imageUri.toString();
                    uploadData();
                    progressDialog.dismiss();
                }

                @Override
                public void onPutImageFailure(Exception error) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onPutImageFailure: " + error.getMessage());
                }
            });
        } else {
            imageURL = defaultImage.getDefaultWordImage();
            uploadData();
            progressDialog.dismiss();
        }
    }

    public void uploadData() {
        progressDialog.show();
        WordModel wordModel = new WordModel(
                etWord.getText().toString().trim(),
                etMeanWord.getText().toString().trim(),
                imageURL,
                topicId,
                WORDSTATE.UNLEARNED);

            modifierData.addWord(wordModel, topicId, new OnAddWordListener() {
                @Override
                public void onAddWordSuccess(WordModel wordModel) {
                    Toast.makeText(UploadWord.this, "Upload Success", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }

                @Override
                public void onAddWordFailure(Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadWord.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
//        }

    }
    private void checkNull(boolean nullWord, boolean nullMeanWord){
        if(!nullMeanWord && !nullWord){
            bSaveWord.setEnabled(true);
        }else if (nullMeanWord || nullWord )
        {
            bSaveWord.setEnabled(false);
        }
    }
}