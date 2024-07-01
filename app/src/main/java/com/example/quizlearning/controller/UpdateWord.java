package com.example.quizlearning.controller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnPutWordImageListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnUpdateWordInTopicFolderListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.Services.FirestoreService.DefaultImage;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnUpdateWordListener;
import com.example.quizlearning.R;
import com.example.quizlearning.WORDSTATE;
import com.example.quizlearning.activity.DetailTopicActivity;
import com.example.quizlearning.activity.DetailWordActivity;
import com.example.quizlearning.model.WordModel;

public class UpdateWord extends AppCompatActivity {
    private static final String TAG = "UpdateWord";
    EditText etWord, etMeanWord;
    Button bSave;
    ImageButton back;
    ImageView editImageWord;
    String userId, topicId, wordId;
    ModifierData modifierData;
    Uri uri;
    String imageURL, oldImageUrl, word;
    ProgressDialog progressDialog;
    DefaultImage defaultImage = new DefaultImage();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_word);
        bSave = findViewById(R.id.bSaveEdit);
        etWord = findViewById(R.id.etUpdateWord);

        etMeanWord = findViewById(R.id.etUpdateMeanWord);
        editImageWord = findViewById(R.id.editImageWord);
        modifierData = new ModifierData();
        progressDialog = new ProgressDialog(this);

        back = findViewById(R.id.back);
        //getIntent từ idUser ở DetailActivity
        userId = getIntent().getStringExtra("userId");
        //
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            if(result.getData() !=null){
                                uri = result.getData().getData();
                            }
                            editImageWord.setImageURI(uri);
                        }
                        else{
                            Toast.makeText(UpdateWord.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //Intent từ DetailWordActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(UpdateWord.this).load(bundle.getString("image")).into(editImageWord);
            word = bundle.getString("word");
            etWord.setText(bundle.getString("word"));
            etMeanWord.setText(bundle.getString("wordMean"));
            oldImageUrl = bundle.getString("image");
            topicId = bundle.getString("topicId");
            wordId = bundle.getString("wordId");
        }
        checkNull();

        etWord.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkNull();

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNull();

            }
            @Override
            public void afterTextChanged(Editable s) {
                checkNull();

            }
        });
        etMeanWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkNull();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNull();
            }
            @Override
            public void afterTextChanged(Editable s) {
                checkNull();

            }
        });

//
        editImageWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
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
    public void saveData(){
        progressDialog.show();
        if (uri != null) {
            modifierData.putImage(uri, "", new OnPutWordImageListener() {
                @Override
                public void onPutImageSuccess(Uri imageUri) {
                    imageURL = imageUri.toString();
                    updateData();
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
            updateData();
            progressDialog.dismiss();
        }
    }
    public void updateData(){
//    public WordModel(String word, String wordMean, String wordImage, String topicId, String wordId, WORDSTATE state) {

        WordModel wordModel = new WordModel(
                etWord.getText().toString().trim(),
                etMeanWord.getText().toString().trim(),
                imageURL,
                topicId,
                wordId,
                WORDSTATE.UNLEARNED); // chưa làm!
            modifierData.updateWord(wordModel, topicId, wordId, new OnUpdateWordListener() {
                @Override
                public void onUpdateWordSuccess(WordModel result) {
                    Toast.makeText(UpdateWord.this, "Changed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUpdateWordFailure(Exception e) {
                    Toast.makeText(UpdateWord.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
//        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("word", etWord.getText().toString().trim());
        resultIntent.putExtra("wordMean", etMeanWord.getText().toString().trim());
        resultIntent.putExtra("wordImage",  imageURL);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    private void checkNull(){
        bSave.setEnabled(etWord.getText().toString().trim().length() != 0 && etMeanWord.getText().toString().trim().length() != 0);
    }

}