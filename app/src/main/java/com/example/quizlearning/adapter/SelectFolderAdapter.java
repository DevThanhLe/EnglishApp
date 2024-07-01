package com.example.quizlearning.adapter;

import static androidx.core.content.ContextCompat.startActivity;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnMoveTopicToFolderListener;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.activity.MainActivity;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;

import java.util.ArrayList;
public class SelectFolderAdapter extends RecyclerView.Adapter<com.example.quizlearning.adapter.SelectFolderAdapter.ViewHolder>{
    private static final String TAG = "SelectTopicAdapter";
    private Context mContext;
    private ArrayList<FolderModel> folders;
    private OnFolderSelectedListener listener;
    private TopicModel topic;
    private ModifierData modifierData;

    public SelectFolderAdapter() {
    }
    public SelectFolderAdapter(Context mContext, ArrayList<FolderModel> folders, TopicModel topic,OnFolderSelectedListener listener) {
        this.mContext = mContext;
        this.folders = folders;
        this.listener = listener;
        this.topic = topic;
    }

    @NonNull
    @Override
    public com.example.quizlearning.adapter.SelectFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new com.example.quizlearning.adapter.SelectFolderAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_select_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.quizlearning.adapter.SelectFolderAdapter.ViewHolder holder, int position) {
        holder.rvFolderName.setText(folders.get(position).getFolderName());
        modifierData = new ModifierData();

        holder.itemView.setOnClickListener(v -> {
            listener.onFolderSelected(position);
            Log.e(TAG, "onBindViewHolder: Item Clicked");
//            public void moveTopicToFolder(String folderId, TopicModel topic, OnMoveTopicToFolderListener
//            listener) {
            modifierData.moveTopicToFolder(folders.get(position).getFolderId(), topic, new OnMoveTopicToFolderListener() {
                @Override
                public void onMoveTopicToFolderSuccess() {
                    Toast.makeText(mContext, "Move successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }

                @Override
                public void onMoveTopicToFolderFailure(Exception e) {
                    Toast.makeText(mContext, "Move Failure: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView rvFolderName;
        public ViewHolder(View itemView) {
            super(itemView);
            rvFolderName = itemView.findViewById(R.id.rvFolderName);

        }
    }
}

