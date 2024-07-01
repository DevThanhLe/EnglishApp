package com.example.quizlearning.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.R;
import com.example.quizlearning.model.TopicModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class SelectTopicAdapter extends RecyclerView.Adapter<SelectTopicAdapter.ViewHolder>{
    private static final String TAG = "SelectTopicAdapter";
    private Context mContext;
    private ArrayList<TopicModel> topics;
    private OnTopicSelectedListener listener;

    public SelectTopicAdapter() {
    }

    public SelectTopicAdapter(Context mContext, ArrayList<TopicModel> topics, OnTopicSelectedListener listener) {
        this.mContext = mContext;
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_select_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rvTitleTopic.setText(topics.get(position).getTopicTitle());
        holder.rvAuthTopic.setText("Cre: "+topics.get(position).getUsername());
        holder.rvDescTopic.setText("Description:"+topics.get(position).getTopicDescription());

        try{

            if(!topics.get(position).getTopicShare()){
                holder.rvPublic.setVisibility(View.INVISIBLE);
                holder.rvPrivate.setVisibility(View.VISIBLE);
            }
            else{
                holder.rvPublic.setVisibility(View.VISIBLE);
                holder.rvPrivate.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            listener.onTopicSelected(position);
            Log.e(TAG, "onBindViewHolder: Item Clicked");
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rvPublic, rvPrivate, options_menu, getTopic;
        TextView rvTitleTopic, rvDescTopic, rvAuthTopic;
        CardView rvTopic;
        public ViewHolder(View itemView) {
            super(itemView);
            rvDescTopic = itemView.findViewById(R.id.rvDescTopic);
            rvTitleTopic = itemView.findViewById(R.id.rvTitleTopic);
            rvAuthTopic = itemView.findViewById(R.id.rvAuthTopic);
            rvTopic = itemView.findViewById(R.id.rvTopic);
            rvPublic = itemView.findViewById(R.id.rvPublic);
            rvPrivate = itemView.findViewById(R.id.rvPrivate);
            options_menu = itemView.findViewById(R.id.options_menu);
            getTopic = itemView.findViewById(R.id.getTopic);
        }
    }
}
