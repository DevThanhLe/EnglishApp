package com.example.quizlearning.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.quizlearning.R;
import com.example.quizlearning.adapter.FeedBackWordFillAdapter;
import com.example.quizlearning.model.WordFillModel;

import java.util.ArrayList;

public class FeedBackWordFillActivity extends AppCompatActivity {

//    private static final String TAG = "CHECK";
    TextView tvCorrect;
    TextView tvIncorrect;
    ImageButton back;
    RecyclerView fbRecyclerView;
    int correctNum = 0;
    int wrongNum = 0;

    ArrayList<WordFillModel> userAnswerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_word_fill);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvIncorrect = findViewById(R.id.tvIncorrect);
        back = findViewById(R.id.back);
        userAnswerList = (ArrayList<WordFillModel>) getIntent().getSerializableExtra("feedBack");
        correctNum = getIntent().getIntExtra("correctNum",0);
        wrongNum = getIntent().getIntExtra("wrongNum",0);
        tvCorrect.setText(String.valueOf(correctNum));
        tvIncorrect.setText(String.valueOf(wrongNum));
//        Log.e(TAG, "CHECK : " + userAnswerList.get(0).getUserSubmit());
        fbRecyclerView = findViewById(R.id.fbRecyclerView);
        FeedBackWordFillAdapter adapter = new FeedBackWordFillAdapter(userAnswerList,this);
        fbRecyclerView.setAdapter(adapter);
        fbRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}