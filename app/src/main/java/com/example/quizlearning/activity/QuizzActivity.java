package com.example.quizlearning.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.R;
import com.example.quizlearning.model.QuizzModel;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.os.Handler;


public class QuizzActivity extends AppCompatActivity {

    TextView tvQuestion;
    TextView tvNumOfQuestion;
    TextView tvAanswer;
    TextView tvBanswer;
    TextView tvCanswer;
    TextView tvDanswer;
    String topicId;
    int posA = 0;
    int posB = 0;
    int posC = 0;
    int posD = 0;
    int position = 0;
    private int feedBackPerQuestion;
    private int shuffleOrNot;
    ArrayList<Integer> answerList;
    String question;
    DatabaseReference databaseReference;
    int correctNumber = 0;
    int wrongNumber = 0;
    List<WordModel> dataList;
    private TextView countdownText;
    private Button startButton;
    private CountDownTimer countDownTimer;
    private long originalTimeMillis; // Thời gian gốc là 25 phút
    private long timeRemainingMillis;
    private boolean isCounting = true;
    private int studyLanguage;
    private ArrayList<QuizzModel> userAnswerList;
    FirebaseFirestore mStore;
    CollectionReference wordRef;
    TextToSpeech frontTextToSpeech;;
    ImageButton btSoundFront;
    String userId;
    private int favoriteOrNot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topicId = bundle.getString("topic_id");
            studyLanguage = bundle.getInt("studyLanguage");
            feedBackPerQuestion = bundle.getInt("feedBackPerQuestion");
            shuffleOrNot = bundle.getInt("shuffleOrNot");
            favoriteOrNot = bundle.getInt("favoriteOrNot");
        }

        dataList = new ArrayList<>();
        answerList = new ArrayList<>();
        userAnswerList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");

        loadQuizData(dataList);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvNumOfQuestion = findViewById(R.id.tvNumOfQuestion);
        tvAanswer = findViewById(R.id.tvAanswer);
        tvBanswer = findViewById(R.id.tvBanswer);
        tvCanswer = findViewById(R.id.tvCanswer);
        tvDanswer = findViewById(R.id.tvDanswer);
        btSoundFront = findViewById(R.id.btSoundFront);
        countdownText = findViewById(R.id.countdownText);

        if(studyLanguage == 0) {
            frontTextToSpeech = new TextToSpeech(QuizzActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(new Locale("vi"));
                }
            });
        }
        else {
            frontTextToSpeech = new TextToSpeech(QuizzActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(Locale.ENGLISH);
                }
            });
        }

    }

    private void loadQuizData(List<WordModel> dataList) {
        mStore = FirebaseFirestore.getInstance();
        if(favoriteOrNot == 0) {
            wordRef = mStore.collection("Topics")
                    .document(topicId)
                    .collection("Words");
        }
        else {
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

                    originalTimeMillis = dataList.size() * 60 * 1000;
                    startCountdown();

                    if (studyLanguage == 0) {
                        question = "Đáp án nào có nghĩa là: " + dataList.get(position).getWordMean();
                        tvQuestion.setText(question);
                    } else {
                        question = "Đáp án nào là nghĩa của từ: " + dataList.get(position).getWord();
                        tvQuestion.setText(question);
                    }

                    tvNumOfQuestion.setText("1/" + dataList.size());
                    setQuizz(position, dataList);

                    btSoundFront.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String frontText;
                            if (studyLanguage == 0) {
                                frontText = dataList.get(position).getWordMean();
                            } else {
                                frontText = dataList.get(position).getWord();
                            }
                            frontTextToSpeech.speak(frontText, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    });

                    tvAanswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvAanswer.setBackgroundResource(R.drawable.selected_answer);
                            String userSubmitA = tvAanswer.getText().toString();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvAanswer.setBackgroundResource(R.drawable.border_radius);
                                    QuizzButtonClick(userSubmitA);
                                }
                            }, 500);

                        }
                    });

                    tvBanswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvBanswer.setBackgroundResource(R.drawable.selected_answer);
                            String userSubmitB = tvBanswer.getText().toString();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvBanswer.setBackgroundResource(R.drawable.border_radius);
                                    QuizzButtonClick(userSubmitB);
                                }
                            }, 500);

                        }
                    });

                    tvCanswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvCanswer.setBackgroundResource(R.drawable.selected_answer);
                            String userSubmitC = tvCanswer.getText().toString();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvCanswer.setBackgroundResource(R.drawable.border_radius);
                                    QuizzButtonClick(userSubmitC);
                                }
                            }, 500);
                        }
                    });

                    tvDanswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvDanswer.setBackgroundResource(R.drawable.selected_answer);
                            String userSubmitD = tvDanswer.getText().toString();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvDanswer.setBackgroundResource(R.drawable.border_radius);
                                    QuizzButtonClick(userSubmitD);
                                }
                            }, 500);

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

    public void setQuizz(int position, List<WordModel> dataList) {
        answerList.clear();
        posA = position;
        answerList.add(posA);
        if (posA + 1 < dataList.size()) {
            posB = posA + 1;
        } else {
            posB = 0;
        }
        if (posB + 1 < dataList.size()) {
            posC = posB + 1;
        } else {
            posC = 0;
        }
        if (posC + 1 < dataList.size()) {
            posD = posC + 1;
        } else {
            posD = 0;
        }
        answerList.add(posB);
        answerList.add(posC);
        answerList.add(posD);
        Collections.shuffle(answerList);
        if(studyLanguage == 0) {
            tvAanswer.setText("A. " + dataList.get(answerList.get(0)).getWord());
            tvBanswer.setText("B. " + dataList.get(answerList.get(1)).getWord());
            tvCanswer.setText("C. " + dataList.get(answerList.get(2)).getWord());
            tvDanswer.setText("D. " + dataList.get(answerList.get(3)).getWord());
        }
        else{
            tvAanswer.setText("A. " + dataList.get(answerList.get(0)).getWordMean());
            tvBanswer.setText("B. " + dataList.get(answerList.get(1)).getWordMean());
            tvCanswer.setText("C. " + dataList.get(answerList.get(2)).getWordMean());
            tvDanswer.setText("D. " + dataList.get(answerList.get(3)).getWordMean());
        }
    }
    public void QuizzButtonClick(String userSubmit) {

        if(studyLanguage == 0) {
            QuizzModel qm = new QuizzModel(question,tvAanswer.getText().toString()
                    ,tvBanswer.getText().toString()
                    ,tvCanswer.getText().toString()
                    ,tvDanswer.getText().toString()
                    ,userSubmit
                    ,dataList.get(position).getWord(),dataList.get(position).getWordId());

            String[] parts = userSubmit.split("\\.");
            String AnswerType = parts[0];
            String AnswerWord = parts[1];

            if (AnswerWord.trim().toLowerCase().equals(dataList.get(position).getWord().toLowerCase())) {
                correctNumber = correctNumber + 1;
                if(feedBackPerQuestion == 1){
                    showCustomDialog(AnswerWord,1);
                }
            } else {
                wrongNumber = wrongNumber + 1;
                if(feedBackPerQuestion == 1){
                    showCustomDialog(AnswerWord,0);
                }
            }
            userAnswerList.add(qm);
        }
        else{
            QuizzModel qm = new QuizzModel(question,tvAanswer.getText().toString()
                    ,tvBanswer.getText().toString()
                    ,tvCanswer.getText().toString()
                    ,tvDanswer.getText().toString()
                    ,userSubmit
                    ,dataList.get(position).getWordMean(),dataList.get(position).getWordId());

            String[] parts = userSubmit.split("\\.");
            String AnswerType = parts[0];
            String AnswerWord = parts[1];
            if (AnswerWord.trim().toLowerCase().equals(dataList.get(position).getWordMean().toLowerCase())) {
                correctNumber = correctNumber + 1;
                if(feedBackPerQuestion == 1){
                    showCustomDialog(AnswerWord,1);
                }
            } else {
                wrongNumber = wrongNumber + 1;
                if(feedBackPerQuestion == 1){
                    showCustomDialog(AnswerWord,0);
                }
            }
            userAnswerList.add(qm);
        }

        if(feedBackPerQuestion == 0) {
            if (position + 1 < dataList.size()) {
                position = position + 1;
                if (studyLanguage == 0) {
                    question = "Đáp án nào có nghĩa là: " + dataList.get(position).getWordMean();
                    tvQuestion.setText(question);
                } else {
                    question = "Đáp án nào là nghĩa của từ: " + dataList.get(position).getWord();
                    tvQuestion.setText(question);
                }
                tvNumOfQuestion.setText(String.valueOf(position + 1) + "/" + dataList.size());
                setQuizz(position, dataList);
            } else if (position + 1 == dataList.size()) {
                long timeFinish = stopCountdown();
                Intent intent = new Intent(QuizzActivity.this, FeedBackQuizzActivity.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("feedBack", (ArrayList<QuizzModel>) userAnswerList);
                intent.putExtra("correctNum", correctNumber);
                intent.putExtra("wrongNum", wrongNumber);
                intent.putExtra("favoriteOrNot",favoriteOrNot);
                intent.putExtra("timeFinish", timeFinish);
                startActivity(intent);
                finish();
            }
        }
    }

    private void startCountdown() {
        // Tạo và khởi chạy CountDownTimer
        countDownTimer = new CountDownTimer(originalTimeMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                isCounting = true;
                timeRemainingMillis = millisUntilFinished;
                // Mỗi lần đếm, cập nhật giao diện người dùng (vd: hiển thị thời gian còn lại)
                int minutesRemaining = (int) (millisUntilFinished / 1000) / 60;
                int secondsRemaining = (int) (millisUntilFinished / 1000) % 60;

                String timeRemaining = String.format("%02d:%02d", minutesRemaining, secondsRemaining);
                countdownText.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                isCounting = false;
                long timeFinish = getElapsedSeconds();
                String userNotSubmit = "";
                if(position + 1 < dataList.size() || position ==  dataList.size() - 1) {
                    for (int m = position; m < dataList.size(); m++) {
//                        setQuizz(m,dataList);
                        answerList.clear();
                        int pA =0,pB =0,pC =0,pD =0;
                        pA = m;
                        answerList.add(pA);
                        if (pA + 1 < dataList.size()) {
                            pB = pA + 1;
                        } else {
                            pB = 0;
                        }
                        if (pB + 1 < dataList.size()) {
                            pC = pB + 1;
                        } else {
                            pC = 0;
                        }
                        if (pC + 1 < dataList.size()) {
                            pD = pC + 1;
                        } else {
                            pD = 0;
                        }
                        answerList.add(pB);
                        answerList.add(pC);
                        answerList.add(pD);
                        Collections.shuffle(answerList);
                        if(studyLanguage == 0) {
                            String questionNotSubmit = "Đáp án nào có nghĩa là: " + dataList.get(m).getWordMean();
                            QuizzModel quizzModel = new QuizzModel(questionNotSubmit,"A. " + dataList.get(answerList.get(0)).getWord()
                                    ,"B. " + dataList.get(answerList.get(1)).getWord()
                                    ,"C. " + dataList.get(answerList.get(2)).getWord()
                                    ,"D. " + dataList.get(answerList.get(3)).getWord()
                                    , userNotSubmit
                                    , dataList.get(m).getWord(),dataList.get(m).getWordId());
                            userAnswerList.add(quizzModel);
                            wrongNumber = wrongNumber + 1;
                        }
                        else {
                            String questionNotSubmit = "Đáp án nào là nghĩa của từ: " + dataList.get(m).getWord();
                            QuizzModel quizzModel = new QuizzModel(questionNotSubmit,"A. " + dataList.get(answerList.get(0)).getWordMean()
                                    ,"B. " + dataList.get(answerList.get(1)).getWordMean()
                                    ,"C. " + dataList.get(answerList.get(2)).getWordMean()
                                    ,"D. " + dataList.get(answerList.get(3)).getWordMean()
                                    , userNotSubmit
                                    , dataList.get(m).getWordMean(),dataList.get(m).getWordId());
                            userAnswerList.add(quizzModel);
                            wrongNumber = wrongNumber + 1;
                        }
                    }
//                    Toast.makeText(getApplicationContext(), "size : " + userAnswerList.size(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QuizzActivity.this, FeedBackQuizzActivity.class);
                    intent.putExtra("topicId",topicId);
                    intent.putExtra("feedBack", (ArrayList<QuizzModel>) userAnswerList);
                    intent.putExtra("correctNum", correctNumber);
                    intent.putExtra("wrongNum", wrongNumber);
                    intent.putExtra("favoriteOrNot",favoriteOrNot);
                    intent.putExtra("timeFinish", timeFinish);
                    startActivity(intent);
                    finish();
                }
            }
        }.start(); // Bắt đầu đếm ngược
    }

    private long stopCountdown() {
            countDownTimer.cancel();
            isCounting = false;
            long elapsedSeconds = getElapsedSeconds();
            return elapsedSeconds;
    }

    private long getElapsedSeconds() {
        // Trả về số giây đã trôi qua từ thời gian gốc trừ đi thời gian còn lại
        return (originalTimeMillis - timeRemainingMillis) / 1000;
    }

    private void showCustomDialog(String userSubmit,int answerState) {
        // Inflate layout for the dialog
        LayoutInflater inflater = (LayoutInflater) QuizzActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            if (answerState == 1) {
                titleView = "Chính xác !";
                tvCorrectorNot.setText(titleView);
            } else {
                titleView = "Sai mất rồi !";
                tvCorrectorNot.setText(titleView);
                myImageView.setImageResource(R.drawable.pandawrong);
            }
        }
        else{
            if (answerState == 1) {
                titleView = "Chính xác !";
                tvCorrectorNot.setText(titleView);
            } else {
                titleView = "Sai mất rồi !";
                tvCorrectorNot.setText(titleView);
                myImageView.setImageResource(R.drawable.pandawrong);
            }
        }


        if(studyLanguage == 0) {
            tvCorrectAnswer.setText("Đáp án : "  + dataList.get(position).getWord());
            tvYourAnswer.setText("Bạn chọn : " + userSubmit);
        }
        else {
            tvCorrectAnswer.setText("Đáp án : " + dataList.get(position).getWordMean());
            tvYourAnswer.setText("Bạn chọn : " + userSubmit);
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
                                question = "Đáp án nào có nghĩa là: " + dataList.get(position).getWordMean();
                                tvQuestion.setText(question);
                            } else {
                                question = "Đáp án nào là nghĩa của từ: " + dataList.get(position).getWord();
                                tvQuestion.setText(question);
                            }
                            tvNumOfQuestion.setText(String.valueOf(position + 1) + "/" + dataList.size());
                            setQuizz(position, dataList);
                        } else if (position + 1 == dataList.size()) {
                            long timeFinish = stopCountdown();
                            Intent intent = new Intent(QuizzActivity.this, FeedBackQuizzActivity.class);
                            intent.putExtra("topicId", topicId);
                            intent.putExtra("feedBack", (ArrayList<QuizzModel>) userAnswerList);
                            intent.putExtra("correctNum", correctNumber);
                            intent.putExtra("wrongNum", wrongNumber);
                            intent.putExtra("timeFinish", timeFinish);
                            intent.putExtra("favoriteOrNot",favoriteOrNot);
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