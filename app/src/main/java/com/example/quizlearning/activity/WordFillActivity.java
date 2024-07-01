package com.example.quizlearning.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

//import android.content.SharedPreferences;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.quizlearning.R;
import com.example.quizlearning.TopicToStudy;
import com.example.quizlearning.model.WordFillModel;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;

public class WordFillActivity extends AppCompatActivity {

    private static final String TAG = "CHECK";
    TextView tvQuestionWF;
    TextView tvNumOfQuestionWF;
    DatabaseReference databaseReference;
//    ValueEventListener eventListener;
    List<WordModel> dataList;
    EditText etAnswer;
    Button btSubmit;
//    int numOfWordFillQuestion = 0;
    String topicId;
    private int studyLanguage;
    private int feedBackPerQuestion;

    private int favoriteOrNot = 0;
    ImageButton btSoundFront;
    private int shuffleOrNot;
    int position = 0;
    String question;
    private ArrayList<WordFillModel> userAnswerList;
    int correctNumber = 0;
    int wrongNumber = 0;
    FirebaseFirestore mStore;
    CollectionReference wordRef;

    TextToSpeech frontTextToSpeech;;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_fill);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topicId = bundle.getString("topic_id");
            studyLanguage = bundle.getInt("studyLanguage");
            feedBackPerQuestion = bundle.getInt("feedBackPerQuestion");
            shuffleOrNot = bundle.getInt("shuffleOrNot");
            favoriteOrNot = bundle.getInt("favoriteOrNot");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");

        dataList = new ArrayList<>();
        userAnswerList = new ArrayList<>();
        loadWords(dataList);

        tvQuestionWF = findViewById(R.id.tvQuestionWF);
        tvNumOfQuestionWF = findViewById(R.id.tvNumOfQuestionWF);
        etAnswer = findViewById(R.id.etAnswer);
        btSubmit = findViewById(R.id.btSubmit);
        btSoundFront = findViewById(R.id.btSoundFront);

        if(studyLanguage == 0) {
            frontTextToSpeech = new TextToSpeech(WordFillActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(new Locale("vi"));
                }
            });
        }
        else {
            frontTextToSpeech = new TextToSpeech(WordFillActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(Locale.ENGLISH);
                }
            });
        }
        btSoundFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frontText;
                if(studyLanguage == 0){
                    frontText = dataList.get(position).getWordMean();
                }
                else{
                    frontText = dataList.get(position).getWord();
                }
                frontTextToSpeech.speak(frontText, TextToSpeech.QUEUE_FLUSH,null);
//                Toast.makeText(FlashCardActivity.this,"FRONT",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadWords(List<WordModel> dataList) {

        mStore = FirebaseFirestore.getInstance();

        if(favoriteOrNot == 0) {
            wordRef = mStore.collection("Topics")
                    .document(topicId)
                    .collection("Words");
        }
        else{
            wordRef = mStore.collection("Favorites")
                    .document(userId)
                    .collection("Words");

        }
        wordRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();

                    dataList.clear(); // Đảm bảo là danh sách đã rỗng trước khi thêm dữ liệu mới
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        WordModel wordModel = documentSnapshot.toObject(WordModel.class);
                        dataList.add(wordModel);
                    }

                    if (shuffleOrNot == 1) {
                        Collections.shuffle(dataList);
                    }

                    if (studyLanguage == 0) {
                        question = "Hãy ghi từ có nghĩa là : " + dataList.get(position).getWordMean();
                        tvQuestionWF.setText(question);
                    } else {
                        question = "ghi nghĩa của từ sau : " + dataList.get(position).getWord();
                        tvQuestionWF.setText(question);
                    }

                    tvNumOfQuestionWF.setText("1/" + dataList.size());

                    btSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (studyLanguage == 0) {
                                String userSubmit = etAnswer.getText().toString();
                                WordFillModel wf = new WordFillModel(dataList.get(position).getWordMean()
                                        , dataList.get(position).getWord()
                                        , userSubmit, question);

                                if (userSubmit.trim().toLowerCase().equals(dataList.get(position).getWord().toLowerCase())) {
                                    correctNumber = correctNumber + 1;
                                    if (feedBackPerQuestion == 1) {
                                        showCustomDialog(userSubmit);
                                    }
                                } else {
                                    wrongNumber = wrongNumber + 1;
                                    if (feedBackPerQuestion == 1) {
                                        showCustomDialog(userSubmit);
                                    }
                                }
                                userAnswerList.add(wf);
                            } else {
                                String userSubmit = etAnswer.getText().toString();
                                WordFillModel wf = new WordFillModel(dataList.get(position).getWord()
                                        , dataList.get(position).getWordMean()
                                        , userSubmit, question);

                                if (userSubmit.trim().toLowerCase().equals(dataList.get(position).getWordMean().toLowerCase())) {
                                    correctNumber = correctNumber + 1;
                                    if (feedBackPerQuestion == 1) {
                                        showCustomDialog(userSubmit);
                                    }
                                } else {
                                    wrongNumber = wrongNumber + 1;
                                    if (feedBackPerQuestion == 1) {
                                        showCustomDialog(userSubmit);
                                    }
                                }
                                userAnswerList.add(wf);
                            }

                            if (feedBackPerQuestion == 0) {
                                if (position + 1 < dataList.size()) {
                                    position = position + 1;
                                    if (studyLanguage == 0) {
                                        question = "Hãy ghi từ có nghĩa là : " + dataList.get(position).getWordMean();
                                        tvQuestionWF.setText(question);
                                    } else {
                                        question = "ghi nghĩa của từ sau : " + dataList.get(position).getWord();
                                        tvQuestionWF.setText(question);
                                    }
                                    tvNumOfQuestionWF.setText(String.valueOf(position + 1) + "/" + dataList.size());
                                    etAnswer.setText("");
                                } else {
                                    etAnswer.setText("");
                                    Intent intent = new Intent(WordFillActivity.this, FeedBackWordFillActivity.class);
                                    intent.putExtra("feedBack", (ArrayList<WordFillModel>) userAnswerList);
                                    intent.putExtra("correctNum", correctNumber);
                                    intent.putExtra("wrongNum", wrongNumber);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    });
                } else {
                    Exception exception = task.getException();
                    // Xử lý lỗi nếu có
                    if (exception != null) {
                        // Log.e("Firestore", "Error getting topics", exception);
                    }
                }
            }
        });

    }

    private void showCustomDialog(String userSubmit) {
        // Inflate layout for the dialog
        LayoutInflater inflater = (LayoutInflater) WordFillActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.feedback_per_question, null);

        // Initialize switches
        String titleView;
        TextView tvCorrectAnswer = dialogView.findViewById(R.id.tvCorrectAnswer);
        TextView tvYourAnswer = dialogView.findViewById(R.id.tvYourAnswer);
        TextView tvCorrectorNot = dialogView.findViewById(R.id.tvCorrectorNot);
        ImageView myImageView = dialogView.findViewById(R.id.myImageView);
        ConstraintLayout layoutFeedback = dialogView.findViewById(R.id.layoutFeedback);
        // Build the custom dialog
        if(studyLanguage == 0) {
            if (userSubmit.trim().toLowerCase().equals(dataList.get(position).getWord().toLowerCase())) {
                titleView = "Chính xác !";
                tvCorrectorNot.setText(titleView);
//                int color = ContextCompat.getColor(this, R.color.colorSecondary);
//                layoutFeedback.setBackgroundColor(color);
            } else {
                titleView = "Sai mất rồi !";
                tvCorrectorNot.setText(titleView);
                myImageView.setImageResource(R.drawable.pandawrong);
            }
        }
        else{
            if (userSubmit.trim().toLowerCase().equals(dataList.get(position).getWordMean().toLowerCase())) {
                titleView = "Chính xác !";
                tvCorrectorNot.setText(titleView);
            } else {
                titleView = "Sai mất rồi !";
                tvCorrectorNot.setText(titleView);
                myImageView.setImageResource(R.drawable.pandawrong);
            }
        }

        if(studyLanguage == 0) {
            tvCorrectAnswer.setText("Đáp án : " + dataList.get(position).getWord());
            tvYourAnswer.setText("bạn chọn : " + userSubmit);
        }
        else {
            tvCorrectAnswer.setText("Đáp án : " + dataList.get(position).getWordMean());
            tvYourAnswer.setText("bạn chọn : " + userSubmit);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (position + 1 < dataList.size()) {
                            position = position + 1;
                            if (studyLanguage == 0) {
                                question = "Hãy ghi từ có nghĩa là : " + dataList.get(position).getWordMean();
                                tvQuestionWF.setText(question);
                            } else {
                                question = "ghi nghĩa của từ sau : " + dataList.get(position).getWord();
                                tvQuestionWF.setText(question);
                            }
                            tvNumOfQuestionWF.setText(String.valueOf(position + 1) + "/" + dataList.size());
                            etAnswer.setText("");
                        } else {
                            etAnswer.setText("");
                            Intent intent = new Intent(WordFillActivity.this, FeedBackWordFillActivity.class);
                            intent.putExtra("feedBack", (ArrayList<WordFillModel>) userAnswerList);
                            intent.putExtra("correctNum", correctNumber);
                            intent.putExtra("wrongNum", wrongNumber);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}