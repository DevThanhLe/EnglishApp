package com.example.quizlearning.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.R;
import com.example.quizlearning.activity.FlashCardActivity;
import com.example.quizlearning.activity.QuizzActivity;
import com.example.quizlearning.activity.WordFillActivity;
import com.example.quizlearning.auth.UserModel;
import com.example.quizlearning.model.Rank;
import com.example.quizlearning.model.TopicModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyHolder>{

    private List<Rank> cat_List;
    private Context context;

    private List<String> userNameList = new ArrayList<String>();
    private List<String> userIdList = new ArrayList<String>();

    private List<String> showName = new ArrayList<String>();
    FirebaseFirestore mStore;
    CollectionReference userRef;

    public RankAdapter() {}

    @NonNull
    @Override
    public RankAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_item_layout, parent, false);
        return new RankAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.MyHolder holder, int position) {

        String correctText = String.valueOf(cat_List.get(position).getCorrectNum());
        String timeText = String.valueOf((int) cat_List.get(position).getTimeFinish());

        mStore.collection("Users")
                .whereEqualTo("userID", cat_List.get(position).getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uname = document.getString("username");
                                holder.userName.setText(uname);
                            }
                        }
                    }
                });

        if(position == 0){
            holder.ivTrophy.setImageResource(R.drawable.gold);
        } else if(position == 1){
            holder.ivTrophy.setImageResource(R.drawable.silver);
        } else{
            holder.ivTrophy.setImageResource(R.drawable.copper);
        }
        holder.tvCorrectNum.setText(correctText);
        holder.tvTime.setText(timeText);

    }

    @Override
    public int getItemCount() {
        return cat_List.size();
    }

    public RankAdapter(List<Rank> cat_List, Context context) {
        this.cat_List = cat_List;
        this.context = context;
        mStore = FirebaseFirestore.getInstance();
        userRef = mStore.collection("Users");

    }


    public class MyHolder extends RecyclerView.ViewHolder{
        TextView userName;

        ImageView ivTrophy;
        TextView tvCorrectNum;
        TextView tvTime;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            ivTrophy = itemView.findViewById(R.id.ivTrophy);
            tvCorrectNum = itemView.findViewById(R.id.tvCorrectNum);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
