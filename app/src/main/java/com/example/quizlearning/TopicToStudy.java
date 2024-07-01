package com.example.quizlearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.adapter.TopicQuizzAdapter;
import com.example.quizlearning.model.TopicModel;
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

public class TopicToStudy extends AppCompatActivity {
    private RecyclerView cat_Grid;

    private int studyWayID;
    private int studyLanguage;
    private int feedBackPerQuestion;
    private int shuffleOrNot;

    public List<TopicModel> catList = new ArrayList<>();
    List<String> topicCommunityId = new ArrayList<>();

    FirebaseFirestore mStore;

    CollectionReference topicsRef;
    CollectionReference topicCommunityRef;
    String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_to_study);

        // GET ACITON BAR
        ActionBar actionBar = TopicToStudy.this.getSupportActionBar();
        actionBar.show();
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
        actionBar.setDisplayHomeAsUpEnabled(true);



        cat_Grid = findViewById(R.id.cat_Grid);

        studyWayID = getIntent().getIntExtra("typeStudy",1);
        studyLanguage = getIntent().getIntExtra("studyLanguage",1);
        feedBackPerQuestion = getIntent().getIntExtra("feedBackPerQuestion",0);
        shuffleOrNot = getIntent().getIntExtra("shuffleOrNot",0);

        // FireStore
        mStore = FirebaseFirestore.getInstance();
        topicsRef = mStore.collection("Topics");


//        Toast.makeText(this,"Study Lang : " + String.valueOf(studyLanguage),Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"feedBackPerQuestion : " + String.valueOf(feedBackPerQuestion),Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"shuffleOrNot : " + String.valueOf(shuffleOrNot),Toast.LENGTH_SHORT).show();

        // Topic data

        loadTopics();




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTopics() {
        catList.clear();
        TopicQuizzAdapter adapter = new TopicQuizzAdapter(catList,this, studyWayID,studyLanguage,feedBackPerQuestion,shuffleOrNot);
        cat_Grid.setAdapter(adapter);
        cat_Grid.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId","null");

        topicCommunityRef = mStore.collection("Users").document(userId).collection("Community");
        topicCommunityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String communityTopic = documentSnapshot.getId().toString();
                        topicCommunityId.add(communityTopic);
                    }

                }
            }
        });
        topicsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String topicAuth = documentSnapshot.getString("topicAuth");
                        String topicIdCheck = documentSnapshot.getString("topicId");
                        // Kiểm tra topic nào thuộc id User đang đăng nhập thì xử lý dữ liệu
                        if (topicAuth != null && topicAuth.equals(userId)) {
                            TopicModel topicModel = documentSnapshot.toObject(TopicModel.class);
                            catList.add(topicModel);
                        }
                        else{
                            if(topicCommunityId != null){
                                for(int i = 0; i<topicCommunityId.size(); i++){
                                    if(topicCommunityId.get(i).equals(topicIdCheck)){
                                        TopicModel topicModel = documentSnapshot.toObject(TopicModel.class);
                                        catList.add(topicModel);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // Cập nhật Adapter và Dismiss Dialog
                    adapter.notifyDataSetChanged();
//                    for (int j = 0;j < catList.size();j++) {
//                        System.out.println("id Topic : " + catList.get(j).getTopicId());
//                        System.out.println("name Topic : " + catList.get(j).getTopicTitle());
//                        System.out.println("auth Topic : " + catList.get(j).getUsername());
//                    }
                }
            }
        });
    }

}