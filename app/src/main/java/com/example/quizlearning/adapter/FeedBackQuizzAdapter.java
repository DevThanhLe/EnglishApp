package com.example.quizlearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.model.QuizzModel;
import com.example.quizlearning.R;

import java.util.List;

public class FeedBackQuizzAdapter extends RecyclerView.Adapter<FeedBackQuizzAdapter.MyHolder>{
    private List<QuizzModel> feedBackList;

    private Context context;
    public FeedBackQuizzAdapter() {}

    @NonNull
    @Override
    public FeedBackQuizzAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_quizz_item_layout, parent, false);
        return new FeedBackQuizzAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackQuizzAdapter.MyHolder holder, int position) {

        QuizzModel currentQuestion = feedBackList.get(position);

        holder.tvQuestion.setText(String.valueOf(position + 1) + ". " + currentQuestion.getQuestion());
        holder.tvAanswer.setText(currentQuestion.getOption1());
        holder.tvBanswer.setText(currentQuestion.getOption2());
        holder.tvCanswer.setText(currentQuestion.getOption3());
        holder.tvDanswer.setText(currentQuestion.getOption4());

        holder.tvAanswer.setBackgroundResource(R.drawable.border_radius);
        holder.tvBanswer.setBackgroundResource(R.drawable.border_radius);
        holder.tvCanswer.setBackgroundResource(R.drawable.border_radius);
        holder.tvDanswer.setBackgroundResource(R.drawable.border_radius);

        String answer = currentQuestion.getCorrectAnswer();
        String userSubmit = currentQuestion.getUserSubmit();

        if (!userSubmit.isEmpty()) {
            String[] parts = userSubmit.split("\\.");
            String userAnswerType = parts[0].trim();
            String userAnswerWord = parts[1].trim();

            if (userAnswerWord.equals(answer)) {
                setAnswerBackground(holder, userAnswerType, R.drawable.selected_right_answer);
            } else {
                setAnswerBackground(holder, userAnswerType, R.drawable.selected_wrong_answer);
                setAnswerBackground(holder, getCorrectAnswerText(currentQuestion), R.drawable.selected_right_answer);
            }
        } else {
            setAnswerBackground(holder, getCorrectAnswerText(currentQuestion), R.drawable.right_answer_unsubmit);
        }
    }

    private void setAnswerBackground(FeedBackQuizzAdapter.MyHolder holder, String answerText, int backgroundResource) {
        switch (answerText) {
            case "A":
                holder.tvAanswer.setBackgroundResource(backgroundResource);
                break;
            case "B":
                holder.tvBanswer.setBackgroundResource(backgroundResource);
                break;
            case "C":
                holder.tvCanswer.setBackgroundResource(backgroundResource);
                break;
            case "D":
                holder.tvDanswer.setBackgroundResource(backgroundResource);
                break;
        }
    }

    private String getCorrectAnswerText(QuizzModel question) {
        return ("A. " + question.getCorrectAnswer()).equals(question.getOption1()) ? "A" :
                ("B. " + question.getCorrectAnswer()).equals(question.getOption2()) ? "B" :
                        ("C. " + question.getCorrectAnswer()).equals(question.getOption3()) ? "C" :
                                "D";
    }

    @Override
    public int getItemCount() {
        return feedBackList.size();
    }

    public FeedBackQuizzAdapter (List<QuizzModel> feedBackList, Context context) {
        this.feedBackList = feedBackList;
        this.context = context;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tvQuestion;
        TextView tvAanswer;
        TextView tvBanswer;
        TextView tvCanswer;
        TextView tvDanswer;

        LinearLayout bgFeedback;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAanswer = itemView.findViewById(R.id.tvAanswer);
            tvBanswer = itemView.findViewById(R.id.tvBanswer);
            tvCanswer = itemView.findViewById(R.id.tvCanswer);
            tvDanswer = itemView.findViewById(R.id.tvDanswer);
        }
    }
}
