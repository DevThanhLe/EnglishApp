package com.example.quizlearning.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnRemoveTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetAllWordInFolderListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.adapter.MyAdapterTopic;
import com.example.quizlearning.adapter.MyAdapterWord;
import com.example.quizlearning.controller.UpdateTopic;
import com.example.quizlearning.controller.UploadTopic;
import com.example.quizlearning.controller.UploadWord;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailTopicActivity extends AppCompatActivity {
    TextView detailDescTopic, detailTitleTopic, detailShare;
    ImageView detailImage;
    RecyclerView showWord;
//    Button bAddWords;
    List<WordModel> dataList;
    ValueEventListener eventListener;

    MyAdapterTopic myAdapterTopic;
    FloatingActionButton optionTopic;
    ModifierData modifierData;
    FirebaseFirestore mStore;
    CSVWriter writer;
    CollectionReference wordRef;
    String topicId, folderId, topicAuth;
    String topicTitle, topicDescription;
    ImageView icPublic, icPrivate;
    Boolean topicShare;
    Date timeCreate;
    private static final int UPDATE_TOPIC_REQUEST_CODE = 1;
    TopicModel currentTopic;
    Boolean edit;
    String userId;
    ProgressDialog progressDialog;
    String auth = "";
    Getdata getData;
    int isCommunity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_topic);

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username","Anonymous");
        userId = sharedPreferences.getString("userId","Anonymous");
        getData = new Getdata();
        detailDescTopic = findViewById(R.id.detailDescTopic);
        detailTitleTopic = findViewById(R.id.detailTitleTopic);
        icPrivate = findViewById(R.id.icPrivate);
        icPublic = findViewById(R.id.icPublic);
//        bDeleteTopic = findViewById(R.id.bDeleteTopic);
//        bAddWords = findViewById(R.id.bAddWords);
//        bUpdateTopic = findViewById(R.id.bUpdateTopic);
        optionTopic = findViewById(R.id.optionTopic);

        modifierData = new ModifierData();
        mStore = FirebaseFirestore.getInstance();


        //Lấy từ MyAdapterTopic :
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            topicDescription = bundle.getString("topicDescription");
            topicShare = bundle.getBoolean("topicShare");
            topicTitle = bundle.getString("topicTitle");
            topicId = bundle.getString("topicId") ;
            auth = bundle.getString("username") ;
            edit = bundle.getBoolean("edit") ;
            folderId = bundle.getString("folderId");
            topicAuth = bundle.getString("topicAuth");
            timeCreate = new Date(bundle.getLong("timeCreate"));
            detailTitleTopic.setText(topicTitle);
            detailDescTopic.setText(topicDescription);
            isCommunity = bundle.getInt("isCommunity",0);
        }

        optionTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        createCurrentTopic();
        //CURRENT TOPIC:
        if(currentTopic.getTopicShare()){
            icPrivate.setVisibility(View.GONE);
            icPublic.setVisibility(View.VISIBLE);
        }else{
            icPrivate.setVisibility(View.VISIBLE);
            icPublic.setVisibility(View.GONE);
        }

    //Show list word
        showWord = findViewById(R.id.showWords);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        showWord.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dataList = new ArrayList<>();
        MyAdapterWord myAdapter;
        if(isCommunity == 1 || folderId != null){
            myAdapter  = new MyAdapterWord(this, dataList, 1);

        }
        else{
            myAdapter = new MyAdapterWord(this, dataList);
        }
        showWord.setAdapter(myAdapter);
        wordRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Xử lý lỗi nếu có
//                    Log.e("Firestore", "Error getting topics", e);
                    return;
                }
                dataList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    WordModel wordModel = documentSnapshot.toObject(WordModel.class);
                    dataList.add(wordModel);

                }
                // Cập nhật Adapter và Dismiss Dialog
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.option_topic);

        // Handle click events for options in the bottom sheet
        View addWordOption = bottomSheetDialog.findViewById(R.id.addWord);
        View editTopicOption = bottomSheetDialog.findViewById(R.id.editTopic);
        View removeTopicOption = bottomSheetDialog.findViewById(R.id.removeTopic);
        View exportTopicOption = bottomSheetDialog.findViewById(R.id.exportTopic);
        View rankingTopicOption = bottomSheetDialog.findViewById(R.id.Ranking);

        if(!edit){
            if(isCommunity ==  0) {
                addWordOption.setVisibility(View.GONE);
                editTopicOption.setVisibility(View.GONE);
                rankingTopicOption.setVisibility(View.GONE);
                exportTopicOption.setVisibility(View.GONE);
            }
            else{
                addWordOption.setVisibility(View.GONE);
                editTopicOption.setVisibility(View.GONE);
                removeTopicOption.setVisibility(View.GONE);
                exportTopicOption.setVisibility(View.GONE);
                
            }
        }
        else{
            rankingTopicOption.setVisibility(View.GONE);
        }
        if(exportTopicOption!= null){
            exportTopicOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    String csvFileName = currentTopic.getTopicTitle() + ".csv";
                    File csvFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), csvFileName);
                            try {
                                writer = new CSVWriter(new FileWriter(csvFile));
                                getData.getWordsInTopic(currentTopic.getTopicId(), new OnGetAllWordInFolderListener() {
                                    @Override
                                    public void onGetAllWordInFolderSuccess(ArrayList<WordModel> words) {
                                        if(dataList.isEmpty()){
                                            Toast.makeText(DetailTopicActivity.this,"Topic " + csvFileName + " không có chữ để export, vui lòng chọn Topic khác",Toast.LENGTH_SHORT).show();
                                            finish();
                                            progressDialog.dismiss();
                                        }
                                        List<String[]> data = new ArrayList<>();
                                        for (WordModel word : dataList) {
                                            String[] rowData = new String[]{
                                                    word.getWord(),
                                                    word.getWordMean(),
                                            };
                                            data.add(rowData);
                                            try {
                                                writer.writeAll(data); // Ghi dữ liệu vào file CSV
                                                writer.close(); // Đóng writer sau khi ghi xong
                                                Toast.makeText(DetailTopicActivity.this,"Đã tải về " + csvFileName,Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                progressDialog.dismiss();

                                            }
                                        }
                                    }
                                    @Override
                                    public void onGetAllWordInFolderFailure(Exception e) {
                                        progressDialog.dismiss();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();

                            }
                        }
                    });
            progressDialog.dismiss();

        }
        if (addWordOption != null) {
            addWordOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailTopicActivity.this, UploadWord.class);
                    intent.putExtra("topicId", topicId );
                    intent.putExtra("folderId", folderId);
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        if (editTopicOption != null) {
            editTopicOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailTopicActivity.this, UpdateTopic.class)
                        .putExtra("topicId", currentTopic.getTopicId())
                        .putExtra("topicTitle", currentTopic.getTopicTitle())
                        .putExtra("topicDescription", currentTopic.getTopicDescription())
                        .putExtra("topicShare", currentTopic.getTopicShare())
    //                        .putExtra("username", auth)
                        .putExtra("edit", edit)
                        .putExtra("timeCreate", currentTopic.getTimeCreate());
                    startActivityForResult(intent, UPDATE_TOPIC_REQUEST_CODE);
                    bottomSheetDialog.dismiss();
                }
            });
        }
        if (removeTopicOption != null) {
            removeTopicOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.show();
        if (rankingTopicOption != null) {
            rankingTopicOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailTopicActivity.this, RankingActivity.class)
                            .putExtra("topicId", currentTopic.getTopicId())
                            .putExtra("topicTitle", currentTopic.getTopicTitle());

                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
        }
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete??")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(folderId != null){
                            modifierData.removeTopicInFolder(currentTopic, folderId, new OnRemoveTopicInFolderListener() {
                                @Override
                                public void onRemoveTopicInFolderSuccess() {
                                    Toast.makeText(DetailTopicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onRemoveTopicInFolderFailure(Exception e) {
                                    Toast.makeText(DetailTopicActivity.this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if(!edit){
                            modifierData.removeTopicCommunity(currentTopic, userId, new OnRemoveTopicListener() {
                                @Override
                                public void onRemoveTopicSuccess() {
                                    Toast.makeText(DetailTopicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DetailTopicActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onRemoveTopicFailure(Exception e) {
                                    Toast.makeText(DetailTopicActivity.this, "Can't delete", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                        else{
                            modifierData.removeTopic(currentTopic, new OnRemoveTopicListener() {
                                @Override
                                public void onRemoveTopicSuccess() {
                                    Toast.makeText(DetailTopicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
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

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_TOPIC_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String newTitle = data.getStringExtra("title");
                String newDesc = data.getStringExtra("desc");
                boolean newShare = data.getBooleanExtra("share", false);
                // Kiểm tra xem có sự thay đổi không
                if (!newTitle.equals(topicTitle) || !newDesc.equals(topicDescription)
                        || newShare != topicShare) {
                    topicTitle = newTitle;
                    topicDescription = newDesc;
                    topicShare = newShare;
                    createCurrentTopic();
                    detailDescTopic.setText(currentTopic.getTopicDescription());
                    detailTitleTopic.setText(currentTopic.getTopicTitle());
                    if(currentTopic.getTopicShare()){
                        icPrivate.setVisibility(View.GONE);
                        icPublic.setVisibility(View.VISIBLE);
                    }else{
                        icPrivate.setVisibility(View.VISIBLE);
                        icPublic.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    private void createCurrentTopic(){
            currentTopic = new TopicModel(topicId, topicTitle, topicDescription, topicShare, topicAuth, timeCreate);
            wordRef = mStore.collection("Topics")
                    .document(topicId)
                    .collection("Words");
//        }
    }
}