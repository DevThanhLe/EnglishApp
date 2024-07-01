package com.example.quizlearning.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetFolderId;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordInTopicFolderListener;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.WORDSTATE;
import com.example.quizlearning.controller.UpdateWord;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicReference;

public class DetailWordActivity extends AppCompatActivity {
    TextView  detailWord, detailMeanWord;
    ImageView detailImage;
    ImageButton deleteWord, editWord;
    String imageUrl, topicId, wordId;
    FirebaseFirestore mStore;

    ModifierData modifierData;
    ProgressDialog progressDialog;
    WordModel currentWord;
    WORDSTATE state;
    String wordMean, wordDescription, word;
    private static final int UPDATE_WORD_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_word);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
        actionBar.setDisplayHomeAsUpEnabled(true);

        detailImage = findViewById(R.id.detailImageWord);
//        detailDescWord = findViewById(R.id.detailDescWord);
        detailWord = findViewById(R.id.detailWord);
        detailMeanWord = findViewById(R.id.detailMeanWord);
        editWord = findViewById(R.id.editWord);
        deleteWord = findViewById(R.id.deleteWord);

        mStore = FirebaseFirestore.getInstance();
        modifierData = new ModifierData();
        progressDialog = new ProgressDialog(this);
        //GetIntent từ WordAdapter
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            word = bundle.getString("word");
            wordDescription = bundle.getString("wordDescription");
            wordMean = bundle.getString("wordMean");
            imageUrl = bundle.getString("wordImage");
            topicId = bundle.getString("topicId");
            state = (WORDSTATE) bundle.getSerializable("state");
            wordId = bundle.getString("wordId");
            Glide.with(this).load(bundle.getString("wordImage")).into(detailImage);
        }
        createCurrentWord();
        //Nếu word có nằm trong topic của 1 folder thì chạy hàm này:
        if(topicId == null){
            deleteWord.setVisibility(View.GONE);
            editWord.setVisibility(View.GONE);
        }

        editWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });
        deleteWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn xóa?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                           modifierData.removeWord(topicId, currentWord, new OnRemoveWordListener() {
                               @Override
                               public void onRemoveWordSuccess() {
                                   Toast.makeText(DetailWordActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                               startActivity(new Intent(DetailWordActivity.this, MainActivity.class));
                                   finish();
                               }

                               @Override
                               public void onRemoveWordFailure(Exception e) {
                                   Toast.makeText(DetailWordActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                                   finish();
                               }
                           });
//                       }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Người dùng nhấn "Hủy", không làm gì cả
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_WORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String newWord = data.getStringExtra("word");
                String newWordMean = data.getStringExtra("wordMean");
                String newWordDesc = data.getStringExtra("wordDesc");
                String newImage = data.getStringExtra("wordImage");
                // Kiểm tra xem có sự thay đổi không
                if (!newWord.equals(word) || !newWordMean.equals(wordMean)
                        || !newWordDesc.equals(wordDescription) || !newImage.equals(imageUrl)) {
                    word = newWord;
                    wordMean = newWordMean;
                    wordDescription = newWordDesc;
                    imageUrl = newImage;
                    createCurrentWord();
                    Glide.with(this).load(currentWord.getWordImage()).into(detailImage);
                }
            }
        }
    }
    public void createCurrentWord(){
        currentWord = new WordModel(word, wordMean, imageUrl, topicId, wordId, state);
        detailWord.setText(currentWord.getWord());
        detailMeanWord.setText(currentWord.getWordMean());
    }
    public void updateEvent(){
        Intent intent = new Intent(DetailWordActivity.this, UpdateWord.class)
                .putExtra("word", currentWord.getWord())
                .putExtra("wordMean", currentWord.getWordMean())
                .putExtra("image", currentWord.getWordImage())
                .putExtra("topicId", currentWord.getTopicId())
                .putExtra("wordId", currentWord.getWordId());
        startActivityForResult(intent, UPDATE_WORD_REQUEST_CODE);
    }
}
