package com.example.quizlearning.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.R;
import com.example.quizlearning.activity.DetailFolderActivity;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterFolder extends RecyclerView.Adapter<MyViewHolderFolder>{
    private Context context;
    private List<FolderModel> dataList;

    public MyAdapterFolder(Context context, List<FolderModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolderFolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_folder,parent,false);
        return new MyViewHolderFolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFolder holder, int position) {
        holder.rvFolderName.setText(dataList.get(position).getFolderName());
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        holder.rvFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailFolderActivity.class);
                intent.putExtra("author", dataList.get(holder.getAdapterPosition()).getAuthor());
                intent.putExtra("folderId", dataList.get(holder.getAdapterPosition()).getFolderId());
                intent.putExtra("folderName", dataList.get(holder.getAdapterPosition()).getFolderName());
//                intent.putExtra("count", dataList.get(holder.getAdapterPosition()).getTotalTopic());
                context.startActivity(intent);
            }
        });
        holder.rvFolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchFolder(ArrayList<FolderModel> searchList){
        notifyDataSetChanged();
        dataList = searchList;
    }
}
class MyViewHolderFolder extends RecyclerView.ViewHolder{
    TextView rvFolderName;
    CardView rvFolder;
    public MyViewHolderFolder(@NonNull View itemView) {
        super(itemView);

//        rvImgAuthTopic = itemView.findViewById(R.id.rvImgAuthTopic);
        rvFolderName = itemView.findViewById(R.id.rvFolderName);
        rvFolder = itemView.findViewById(R.id.rvFolder);


    }
}
