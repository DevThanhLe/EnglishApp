package com.example.quizlearning.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.activity.FlashCardActivity;
import com.example.quizlearning.activity.QuizzActivity;
import com.example.quizlearning.R;
import com.example.quizlearning.activity.WordFillActivity;
import com.example.quizlearning.model.TopicModel;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TopicQuizzAdapter extends RecyclerView.Adapter<TopicQuizzAdapter.MyHolder> {

    private List<TopicModel> cat_List;
    DatabaseReference databaseReference;
    private Context context;
    int typeStudy;
    int shuffleOrNot;
    int feedBackPerQuestion;
    int studyLanguage;

    FirebaseFirestore mStore;
    CollectionReference wordRef;
    public TopicQuizzAdapter() {}

    @NonNull
    @Override
    public TopicQuizzAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item_layout, parent, false);
        return new TopicQuizzAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicQuizzAdapter.MyHolder holder, int position) {
        holder.topic_name.setText(cat_List.get(position).getTopicTitle());
        holder.tvCre.setText("cre : " + cat_List.get(position).getUsername().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic_id = cat_List.get(holder.getAdapterPosition()).getTopicId();
//                databaseReference = FirebaseDatabase.getInstance().getReference("Topic").child(topic_id).child("listWords");
                mStore = FirebaseFirestore.getInstance();
                wordRef = mStore.collection("Topics")
                        .document(topic_id)
                        .collection("Words");
                wordRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            int wordCount = queryDocumentSnapshots.size();
                            if (wordCount > 0) {
                                switch (typeStudy) {
                                    case 1:
                                        Intent intentFC = new Intent(context, FlashCardActivity.class);
                                        intentFC.putExtra("topic_id", topic_id);
                                        intentFC.putExtra("studyLanguage", studyLanguage);
                                        intentFC.putExtra("shuffleOrNot", shuffleOrNot);
                                        context.startActivity(intentFC);
                                        ((Activity) context).finish();
                                        break;
                                    case 2:
                                        if (wordCount > 3) {
                                            Intent intentQuizz = new Intent(context, QuizzActivity.class);
                                            intentQuizz.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                            intentQuizz.putExtra("shuffleOrNot", shuffleOrNot);
                                            intentQuizz.putExtra("topic_id", topic_id);
                                            intentQuizz.putExtra("studyLanguage", studyLanguage);
                                            context.startActivity(intentQuizz);
                                            ((Activity) context).finish();
                                        } else {
                                            Toast.makeText(context, "Topic bạn chọn chưa có đủ từ cho chức năng học này", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 3:
                                        Intent intentWF = new Intent(context, WordFillActivity.class);
                                        intentWF.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                        intentWF.putExtra("shuffleOrNot", shuffleOrNot);
                                        intentWF.putExtra("topic_id", topic_id);
                                        intentWF.putExtra("studyLanguage", studyLanguage);
                                        context.startActivity(intentWF);
                                        ((Activity) context).finish();
                                        break;
                                }
                            } else {
                                Toast.makeText(context, "Topic bạn chọn chưa có từ", Toast.LENGTH_SHORT).show();
                            }
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
        });
    }


    @Override
    public int getItemCount() {
        return cat_List.size();
    }

    public TopicQuizzAdapter(List<TopicModel> cat_List, Context context, int typeStudy, int studyLanguage, int feedBackPerQuestion, int shuffleOrNot) {
        this.cat_List = cat_List;
        this.context = context;
        this.typeStudy = typeStudy;
        this.studyLanguage = studyLanguage;
        this.feedBackPerQuestion = feedBackPerQuestion;
        this.shuffleOrNot = shuffleOrNot;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView topic_name;
        TextView tvCre;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            topic_name = itemView.findViewById(R.id.tpName);
            tvCre = itemView.findViewById(R.id.tvCre);
        }
    }
}
