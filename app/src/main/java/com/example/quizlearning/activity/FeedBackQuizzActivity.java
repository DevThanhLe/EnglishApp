package com.example.quizlearning.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.R;
import com.example.quizlearning.adapter.FeedBackQuizzAdapter;
import com.example.quizlearning.controller.UploadTopic;
import com.example.quizlearning.model.QuizzModel;
import com.example.quizlearning.model.Rank;
import com.example.quizlearning.model.TopicModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class FeedBackQuizzActivity extends AppCompatActivity {

    int correctNum = 0;
    int wrongNum = 0;

    long timeFinish = 0;
    TextView tvCorrectAnswer;
    TextView tvIncorrectAnswer;
    RecyclerView fbQuizzRecyclerView;
    ArrayList<QuizzModel> userAnswerList = new ArrayList<>();

    ArrayList<String> wordIdCorrect = new ArrayList<>();
    ImageButton back;
    String idTopic = "";
    String userId;
    FirebaseFirestore mStore;
    DocumentReference rankDocRef;
    int favoriteOrNot = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_quizz);
        userAnswerList = (ArrayList<QuizzModel>) getIntent().getSerializableExtra("feedBack");

        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer);
        tvIncorrectAnswer = findViewById(R.id.tvIncorrectAnswer);
        back = findViewById(R.id.back);

        //id của User đã làm bài
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");

        timeFinish = getIntent().getLongExtra("timeFinish",0);
        correctNum = getIntent().getIntExtra("correctNum",0);
        wrongNum = getIntent().getIntExtra("wrongNum",0);
        favoriteOrNot = getIntent().getIntExtra("favoriteOrNot",0);

        // id của Topic
        idTopic = getIntent().getStringExtra("topicId");

        tvIncorrectAnswer.setText(String.valueOf(wrongNum));
        tvCorrectAnswer.setText(String.valueOf(correctNum));

        mStore = FirebaseFirestore.getInstance();

        if(favoriteOrNot == 0) {
            uploadData();
        }

        // Lấy các Id của Word đã được User trả lời đúng
        for(int i = 0; i < userAnswerList.size(); i++) {
//            System.out.println("question : " + userAnswerList.get(i).getQuestion());
//            System.out.println("A : " + userAnswerList.get(i).getOption1());
//            System.out.println("B : " + userAnswerList.get(i).getOption2());
//            System.out.println("C : " + userAnswerList.get(i).getOption3());
//            System.out.println("D : " + userAnswerList.get(i).getOption4());
//            System.out.println("CorrectAnswer : " + userAnswerList.get(i).getCorrectAnswer());
//            System.out.println("UserSubmit : " + userAnswerList.get(i).getUserSubmit());
//            System.out.println("WordId : " + userAnswerList.get(i).getWordId());
//            System.out.println("------------------------------------");
            if(!userAnswerList.get(i).getUserSubmit().equals("")) {
                String[] parts = userAnswerList.get(i).getUserSubmit().split("\\.");
                String AnswerType = parts[0];
                String AnswerWord = parts[1];
                if (AnswerWord.trim().toLowerCase().equals(userAnswerList.get(i).getCorrectAnswer().toLowerCase())) {
                    wordIdCorrect.add(userAnswerList.get(i).getWordId());
                }
            }
        }
//        for(int j  = 0; j < wordIdCorrect.size(); j++) {
//            System.out.println("WordId : " + wordIdCorrect.get(j));
//        }

        fbQuizzRecyclerView = findViewById(R.id.fbQuizzRecyclerView);
        FeedBackQuizzAdapter adapter = new FeedBackQuizzAdapter(userAnswerList,this);
        fbQuizzRecyclerView.setAdapter(adapter);
        fbQuizzRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void uploadData() {
         rankDocRef = mStore.collection("Rank")
                .document(idTopic)
                .collection("Users")
                .document(userId);

        Rank uRanking = new Rank(correctNum, timeFinish, userId, Calendar.getInstance().getTime());

        rankDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int existingCorrectNum = document.getLong("correctNum").intValue();
                        long existingTimeFinish = document.getLong("timeFinish");

                        if (correctNum > existingCorrectNum) {
                            // Update if new correctNum is greater
                            rankDocRef.set(uRanking)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(FeedBackQuizzActivity.this, "Bạn đã làm đúng nhiều hơn lần trước đó", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(FeedBackQuizzActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else if (correctNum == existingCorrectNum) {
                            if (timeFinish < existingTimeFinish) {
                                // Update if new timeFinish is smaller
                                rankDocRef.set(uRanking)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(FeedBackQuizzActivity.this, "Số câu đúng bằng lần trước nhưng nhanh hơn", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(FeedBackQuizzActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Existing time is smaller, handle accordingly
                                Toast.makeText(FeedBackQuizzActivity.this, "Số câu đúng bằng với lần trước nhưng thời gian chậm hơn", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Existing correctNum is greater, handle accordingly
                            Toast.makeText(FeedBackQuizzActivity.this, "Số câu đúng trước đó cao hơn hiện tại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Document does not exist, add a new record
                        rankDocRef.set(uRanking)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(FeedBackQuizzActivity.this, "Số câu đúng và thời gian bạn làm đã được lưu lại", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FeedBackQuizzActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    // Handle errors
                    Toast.makeText(FeedBackQuizzActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}