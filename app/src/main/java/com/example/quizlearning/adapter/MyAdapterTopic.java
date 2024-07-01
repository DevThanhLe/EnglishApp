package com.example.quizlearning.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnGetUserAuthImageListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllFolderByCurrentUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.Community.OnAddTopicCommunityListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.Community.OnCheckUserCloneTopicCommunity;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnRemoveTopicListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.activity.DetailTopicActivity;
import com.example.quizlearning.activity.MainActivity;
import com.example.quizlearning.controller.UpdateTopic;
import com.example.quizlearning.fragment.HomeFragment;
import com.example.quizlearning.fragment.TopicFragment;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapterTopic extends RecyclerView.Adapter<MyViewHolderTopic> {
    private static final String TAG = MyAdapterTopic.class.getSimpleName();
    private Context context;
    private List<TopicModel> dataList;
    private ModifierData modifierData;
    private String userId, folderId;
    private FirebaseFirestore mStore;
    private Getdata getdata;
    private ProgressDialog progressDialog;
    private CollectionReference ref, userRef;
    SharedPreferences sharedPreferences;
    FirebaseAuth mAuth;

    int isCommunity = 0;

    public MyAdapterTopic(Context context, List<TopicModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    public MyAdapterTopic(Context context, List<TopicModel> dataList, String folderId){
        this.context = context;
        this.dataList = dataList;
        this.folderId = folderId;
    }

    public MyAdapterTopic(Context context, List<TopicModel> dataList,int isCommunity) {
        this.context = context;
        this.dataList = dataList;
        this.isCommunity = isCommunity;
    }
    @NonNull
    @Override
    public MyViewHolderTopic onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_topic,parent,false);
        return new MyViewHolderTopic(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTopic holder, @SuppressLint("RecyclerView") int position) {
//        Glide.with(context).load(dataList.get(position).getTopicImage()).into(holder.rvImageTopic);
        mStore = FirebaseFirestore.getInstance();
//        String folderId = dataList.get(position).getSaveByFolder();
        String topicId = dataList.get(position).getTopicId();
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        modifierData = new ModifierData();
        getdata = new Getdata();
        mStore = FirebaseFirestore.getInstance();

        ref = mStore
                .collection("Topics")
                .document(topicId)
                .collection("Words");

        ref.get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    if(count <= 1){
                        holder.tvTotalWord.setText("("+count + " word)");
                    }else{
                        holder.tvTotalWord.setText("("+count + " words)");
                    }
                }
            }
        });
        holder.rvTitleTopic.setText(dataList.get(position).getTopicTitle());
        holder.rvAuthTopic.setText("Cre: " + dataList.get(position).getUsername());
        holder.rvDescTopic.setText("Description: " + dataList.get(position).getTopicDescription());
        try{

            if(!dataList.get(position).getTopicShare()){
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        getUserAuthImage(dataList.get(position).getTopicAuth(), new OnGetUserAuthImageListener() {
            @Override
            public void onGetUserAuthImage(String authImage) {
                if (holder.getAdapterPosition() == position) {
                     Glide.with(context).load(authImage).into(holder.rvImgAuthTopic);
                }
            }
        });
        holder.rvTopic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, holder);
                return true;
            }
        });
        holder.rvTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailTopicActivity.class);
//                Toast.makeText(context, holder.rvAuthTopic.getText().toString() +" and " + username, Toast.LENGTH_SHORT).show();
                intent.putExtra("folderId", folderId);
                intent.putExtra("edit", dataList.get(holder.getAdapterPosition()).getTopicAuth().equals(userId));
//                intent.putExtra("username", dataList.get(holder.getAdapterPosition()).getUsername());
                intent.putExtra("topicId", dataList.get(holder.getAdapterPosition()).getTopicId());
                intent.putExtra("topicDescription", dataList.get(holder.getAdapterPosition()).getTopicDescription());
                intent.putExtra("topicAuth", dataList.get(holder.getAdapterPosition()).getTopicAuth());
                intent.putExtra("topicTitle", dataList.get(holder.getAdapterPosition()).getTopicTitle());
                intent.putExtra("topicShare", dataList.get(holder.getAdapterPosition()).getTopicShare());
                intent.putExtra("timeCreate", dataList.get(holder.getAdapterPosition()).getTimeCreate());
                intent.putExtra("isCommunity",isCommunity);
//                intent.putExtra("topicKey", dataList.get(holder.getAdapterPosition()).getTopicKey());
                context.startActivity(intent);
            }
        });
        if(!userId.equals(dataList.get(holder.getAdapterPosition()).getTopicAuth())){
            holder.options_menu.setVisibility(View.VISIBLE);
        }
        holder.options_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, holder);
                }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    private void getUserAuthImage(String authId, OnGetUserAuthImageListener listener) {
        mStore.collection("Users")
                .document(authId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String authImage = document.getString("userImageURL");
                            listener.onGetUserAuthImage(authImage);
                        }
                    }
                });
    }
    public void searchTopic(ArrayList<TopicModel> searchList){
        notifyDataSetChanged();
        dataList = searchList;
    }

    public void showPopupMenu(View view, MyViewHolderTopic holder) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.option_menu_topic, popupMenu.getMenu());

        MenuItem editTopic = popupMenu.getMenu().findItem(R.id.action_edit);
        MenuItem deleteTopic = popupMenu.getMenu().findItem(R.id.action_delete);
        MenuItem moveToFolder = popupMenu.getMenu().findItem(R.id.action_move_to_folder);
        MenuItem getTopic = popupMenu.getMenu().findItem(R.id.action_get_topic);
        if(!userId.equals(dataList.get(holder.getAdapterPosition()).getTopicAuth())){
            if(isCommunity == 1) {
                editTopic.setVisible(false);
                moveToFolder.setVisible(false);
                getTopic.setVisible(true);
                deleteTopic.setVisible(false);
            }
            else if(folderId != null){
                editTopic.setVisible(false);
                moveToFolder.setVisible(false);
                getTopic.setVisible(false);
                deleteTopic.setVisible(true);
            }
            else{
                editTopic.setVisible(false);
                moveToFolder.setVisible(true);
                getTopic.setVisible(false);
                deleteTopic.setVisible(true);
            }
        }
        progressDialog = new ProgressDialog(context);

        String topicId = dataList.get(holder.getAdapterPosition()).getTopicId();
        String userName = dataList.get(holder.getAdapterPosition()).getUsername();
        String topicDesc = dataList.get(holder.getAdapterPosition()).getTopicDescription();
        String topicAuth = dataList.get(holder.getAdapterPosition()).getTopicAuth();
        String topicTitle = dataList.get(holder.getAdapterPosition()).getTopicTitle();
        boolean topicShare = dataList.get(holder.getAdapterPosition()).getTopicShare();
        Date timeCreate = dataList.get(holder.getAdapterPosition()).getTimeCreate();

        TopicModel topic = new TopicModel(topicTitle, topicDesc, topicShare , topicAuth, userName, timeCreate);
        topic.setTopicId(topicId);

        // Xử lý sự kiện khi một mục được chọn
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.action_get_topic){
                    modifierData.addCommunityTopic(mAuth.getCurrentUser().getUid(),
                            dataList.get(holder.getAdapterPosition()),
                            new OnAddTopicCommunityListener() {
                                @Override
                                public void onAddTopicCommunitySuccess() {
                                    Toast.makeText(context, "Đã thêm vào danh sách của bạn", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    notifyDataSetChanged();
                                    context.startActivity(intent);
                                }

                                @Override
                                public void onAddTopicCommunityFailure(Exception e) {
                                    Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
                if(item.getItemId() == R.id.action_delete) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Are you sure you want to delete??")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                        if(folderId == null) {

                                    if(folderId != null){
                                        modifierData.removeTopicInFolder(topic, folderId, new OnRemoveTopicInFolderListener() {
                                            @Override
                                            public void onRemoveTopicInFolderSuccess() {
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context, MainActivity.class);
                                                notifyDataSetChanged();
                                                context.startActivity(intent);
                                            }

                                            @Override
                                            public void onRemoveTopicInFolderFailure(Exception e) {
                                                Toast.makeText(context, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else if(!userId.equals(dataList.get(holder.getAdapterPosition()).getTopicAuth())){
                                        modifierData.removeTopicCommunity(topic, userId, new OnRemoveTopicListener() {
                                            @Override
                                            public void onRemoveTopicSuccess() {
                                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context, MainActivity.class);
                                                view.getContext().startActivity(intent);
                                            }

                                            @Override
                                            public void onRemoveTopicFailure(Exception e) {
                                                Toast.makeText(context, "Can't delete", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else{
                                        modifierData.removeTopic(topic, new OnRemoveTopicListener() {
                                            @Override
                                            public void onRemoveTopicSuccess() {
                                                notifyDataSetChanged();
                                            }
                                            @Override
                                            public void onRemoveTopicFailure(Exception e) {
                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Tạo AlertDialog và hiển thị nó
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                    // Xử lý khi chọn "Xóa"
                }
                if(item.getItemId() == R.id.action_edit) {
                    Intent intent = new Intent(context, UpdateTopic.class);
//                        intent.putExtra("folderId", dataList.get(holder.getAdapterPosition()).getSaveByFolder());
                    intent.putExtra("topicId", dataList.get(holder.getAdapterPosition()).getTopicId());
                    intent.putExtra("topicDescription", dataList.get(holder.getAdapterPosition()).getTopicDescription());
                    intent.putExtra("topicAuth", dataList.get(holder.getAdapterPosition()).getTopicAuth());
                    intent.putExtra("topicTitle", dataList.get(holder.getAdapterPosition()).getTopicTitle());
                    intent.putExtra("topicShare", dataList.get(holder.getAdapterPosition()).getTopicShare());
                    intent.putExtra("timeCreate", dataList.get(holder.getAdapterPosition()).getTimeCreate());
                    context.startActivities(new Intent[]{intent});
                    return true;
                }
                    // Xử lý khi chọn "Move to folder"
                if(item.getItemId() == R.id.action_move_to_folder){
                        progressDialog.show();
                        getdata.getAllFolderByUser(mAuth.getCurrentUser().getUid(), new OnGetAllFolderByCurrentUserListener() {

                            @Override
                            public void onGetAllFolderByCurrentUserSuccess(ArrayList<FolderModel> folders) {
                                View recyclerSelectFolder = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_select_folder, null);
                                RecyclerView rvFolder = recyclerSelectFolder.findViewById(R.id.rvFolder);
                                rvFolder.setLayoutManager(new LinearLayoutManager(view.getContext()));
                                rvFolder.setAdapter(new SelectFolderAdapter(view.getContext(), folders, topic, topicPosition -> {
                                    Log.e(TAG, "onGetAllFolderSuccess: Item is Selected: " + folders.get(topicPosition).getFolderName());
                                }));
                                progressDialog.dismiss();
                                new android.app.AlertDialog.Builder(view.getContext())
                                        .setTitle("Choose a folder")
                                        .setView(recyclerSelectFolder)
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                            @Override
                            public void onGetAllFolderByCurrentUserFailure(Exception e) {
                                progressDialog.dismiss();
                                Log.e(TAG, "onGetFolderByCurrentUserFailure: "+e.getMessage() );
                            }
                        });
                }
                return true;

            }
        });

        popupMenu.show();
    }
}

class MyViewHolderTopic extends RecyclerView.ViewHolder{
    ImageView  rvPublic, rvPrivate,rvImgAuthTopic, bMoveToFolder, options_menu, getTopic;
    TextView rvTitleTopic, rvDescTopic, rvAuthTopic, tvTotalWord;
    CardView rvTopic;
    public MyViewHolderTopic(@NonNull View itemView) {
        super(itemView);

//        rvImgAuthTopic = itemView.findViewById(R.id.rvImgAuthTopic);
        rvDescTopic = itemView.findViewById(R.id.rvDescTopic);
        rvTitleTopic = itemView.findViewById(R.id.rvTitleTopic);
        rvAuthTopic = itemView.findViewById(R.id.rvAuthTopic);
        rvImgAuthTopic = itemView.findViewById(R.id.rvImgAuthTopic);
        rvTopic = itemView.findViewById(R.id.rvTopic);
        rvPublic = itemView.findViewById(R.id.rvPublic);
        rvPrivate = itemView.findViewById(R.id.rvPrivate);
        options_menu = itemView.findViewById(R.id.options_menu);
        getTopic = itemView.findViewById(R.id.getTopic);
        tvTotalWord = itemView.findViewById(R.id.tvTotalWord);
    }


}
