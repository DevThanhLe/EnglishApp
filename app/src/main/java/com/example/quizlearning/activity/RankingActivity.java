package com.example.quizlearning.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.R;
import com.example.quizlearning.adapter.RankAdapter;
import com.example.quizlearning.adapter.TopicQuizzAdapter;
import com.example.quizlearning.model.Rank;
import com.example.quizlearning.model.TopicModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RankingActivity extends AppCompatActivity {


    private String topicTitle;
    private String topicId;
    CollectionReference userRef;
    private String userId;

    FirebaseFirestore mStore;
    CollectionReference rankRef;

    public List<Rank> dataList = new ArrayList<>();

    public List<String> userIdRanking = new ArrayList<>();
    RecyclerView rvRank;

    ImageButton back;
    TextView topicName;
    private List<String> userNameList = new ArrayList<String>();
    private List<String> userIdList = new ArrayList<String>();

    private List<String> showName = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            topicTitle = bundle.getString("topicTitle");
            topicId = (bundle.getString("topicId"));
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");

        mStore = FirebaseFirestore.getInstance();
        rankRef = mStore.collection("Rank").document(topicId).collection("Users");

        topicName = findViewById(R.id.topicName);
        rvRank = findViewById(R.id.rvRank);
        topicName.setText(topicTitle);
        back = findViewById(R.id.back);

        RankAdapter adapter = new RankAdapter(dataList, this);
        loadData(adapter);
        rvRank.setAdapter(adapter);
        rvRank.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadData(RankAdapter adapter) {
        dataList.clear();

        rankRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Lấy timestamp
                        Timestamp timestamp = documentSnapshot.getTimestamp("currentTimestampQuiz");
                        Date currentTimestampQuiz = timestamp.toDate();
                        //Lấy correctNum
                        int correctNum = documentSnapshot.getLong("correctNum").intValue();
                        //Lấy timeFinish
                        long timeFinish = documentSnapshot.getLong("timeFinish");
                        // Lấy userId bỏ vào ArrayList userIdRanking
                        String uId = documentSnapshot.getId().toString();
//                        userIdRanking.add(uId);
                        // Tạo object Rank
                        Rank rankModel = new Rank(correctNum,timeFinish,uId,currentTimestampQuiz);
                        // thêm phần tử Rank vào dataList
                        dataList.add(rankModel);
                    }
                    sorting(dataList);
                    if(dataList.size() > 10) {
                        dataList.subList(0, 10);
                    }
//                    Toast.makeText(RankingActivity.this, "dataList size : " + dataList.size(), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(RankingActivity.this, "Chưa ai tham gia học Topic này", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void sorting(List<Rank> dataList){
        for(int i=0;i<dataList.size();i++){
            for(int j=0;j<dataList.size();j++){
                if(dataList.get(i).getCorrectNum() > dataList.get(j).getCorrectNum()){
                    swapItem(i,j,dataList);
                }
                else if (dataList.get(i).getCorrectNum() == dataList.get(j).getCorrectNum()) {
                    if(dataList.get(i).getTimeFinish() < dataList.get(j).getTimeFinish()){
                        swapItem(i,j,dataList);
                    }
                }
            }
        }
    }

    public void swapItem(int i,int j,List<Rank> dataList){
        Rank temp = new Rank(dataList.get(i).getCorrectNum(), dataList.get(i).getTimeFinish(),
                dataList.get(i).getUserId(), dataList.get(i).getCurrentTimestampQuiz());

        dataList.get(i).setCorrectNum( dataList.get(j).getCorrectNum());
        dataList.get(i).setTimeFinish( dataList.get(j).getTimeFinish());
        dataList.get(i).setCurrentTimestampQuiz( dataList.get(j).getCurrentTimestampQuiz());
        dataList.get(i).setUserId(dataList.get(j).getUserId());

        dataList.get(j).setCorrectNum(temp.getCorrectNum());
        dataList.get(j).setTimeFinish(temp.getTimeFinish());
        dataList.get(j).setCurrentTimestampQuiz(temp.getCurrentTimestampQuiz());
        dataList.get(j).setUserId(temp.getUserId());
    }


}