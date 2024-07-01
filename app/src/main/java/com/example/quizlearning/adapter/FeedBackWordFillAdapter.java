package com.example.quizlearning.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.R;
import com.example.quizlearning.model.WordFillModel;

import java.util.List;

public class FeedBackWordFillAdapter extends RecyclerView.Adapter<FeedBackWordFillAdapter.MyHolder>{
    private List<WordFillModel> feedBackList;

    private Context context;
    public FeedBackWordFillAdapter() {}

    @NonNull
    @Override
    public FeedBackWordFillAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_wordfill_item_layout, parent, false);
        return new FeedBackWordFillAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackWordFillAdapter.MyHolder holder, int position) {
        holder.tvQuestion.setText(String.valueOf(position + 1) + ". " + feedBackList.get(position).getQuestion());
        holder.tvAnswer.setText(feedBackList.get(position).getWord());
        holder.tvUserSubmit.setText(feedBackList.get(position).getUserSubmit());
        String userSubmit = feedBackList.get(position).getUserSubmit().trim().toLowerCase();
        String answer = feedBackList.get(position).getWord().trim().toLowerCase();
        holder.bgFeedback.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(userSubmit.equals(answer)){
            holder.bgFeedback.setBackgroundColor(Color.parseColor("#008000"));
        }
        else{
            holder.bgFeedback.setBackgroundColor(Color.parseColor("#FF3131"));
        }

    }

    @Override
    public int getItemCount() {
        return feedBackList.size();
    }

    public FeedBackWordFillAdapter(List<WordFillModel> feedBackList, Context context) {
        this.feedBackList = feedBackList;
        this.context = context;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tvQuestion;
        TextView tvAnswer;
        TextView tvUserSubmit;

        LinearLayout bgFeedback;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvUserSubmit = itemView.findViewById(R.id.tvUserSubmit);
            bgFeedback = itemView.findViewById(R.id.bgFeedback);
        }
    }
}
