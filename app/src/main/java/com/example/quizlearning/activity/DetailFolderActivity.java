package com.example.quizlearning.activity;

import static android.app.PendingIntent.getActivity;

import static com.google.android.gms.common.util.WorkSourceUtil.size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveFolderListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.adapter.MyAdapterTopic;
import com.example.quizlearning.controller.UploadTopic;
import com.example.quizlearning.model.TopicModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class DetailFolderActivity extends AppCompatActivity {
    private static final String TAG = "DetailFolderActivity";
    RecyclerView showTopic;
    ArrayList<TopicModel> dataList;
    MyAdapterTopic myAdapterTopic;
    FirebaseFirestore mStore;
    CollectionReference folderRef;
    String userId, folderId, author;
    FloatingActionButton fab;
    Button bAddTopic ;
    ImageButton back;
    TextView tvNameFolder;
    ModifierData modifierData;
    ProgressDialog progressDialog;
    Getdata getdata;
    private static final int REQUEST_CODE_ADD_TOPIC = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_folder);

        modifierData = new ModifierData();
        showTopic = findViewById(R.id.showTopicOfFolder);
        tvNameFolder = findViewById(R.id.tvNameFolder);
        fab = findViewById(R.id.optionFolder);
        mStore = FirebaseFirestore.getInstance();
        folderRef = mStore.collection("Folders");
        back= findViewById(R.id.back);
        getdata = new Getdata();
        progressDialog = new ProgressDialog(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //getIntent từ AdapterFolder
        Intent intent = getIntent();
        folderId = intent.getStringExtra("folderId");
        author = intent.getStringExtra("author");

        if(folderId!=null){
            folderRef = folderRef.document(folderId)
                    .collection("Topics");
        }

        //SHOW DETAILS TOPIC
        progressDialog.show();
        fetchDataForTopicList();
        progressDialog.dismiss();

        //ADD NEW TOPIC AT FOLDER
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_TOPIC && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("topicAdded", false)) {
                fetchDataForTopicList();
            }
        }
    }
    private void fetchDataForTopicList() {
        Intent intent = getIntent();
        String nameFolder = intent.getStringExtra("folderName");
        tvNameFolder.setText(nameFolder);
        GridLayoutManager gridLayoutManagerTopic = new GridLayoutManager(this, 1);
        showTopic.setLayoutManager(gridLayoutManagerTopic);
        // Clear existing data to avoid duplicates
        dataList = new ArrayList<>();
        myAdapterTopic = new MyAdapterTopic(this, dataList, folderId);
        showTopic.setAdapter(myAdapterTopic);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");
        Log.e(TAG, "fetchDataForTopicList: "+userId );
        getdata.getAllTopicOfAFolder(folderId, new OnGetAllTopicInFolderListener() {
            @Override
            public void onGetAllTopicInFolderSuccess(ArrayList<TopicModel> topics) {
                dataList.clear();
                dataList.addAll(topics);
                myAdapterTopic.notifyDataSetChanged();
            }

            @Override
            public void onGetAllTopicInFolderFailure(Exception e) {
                Log.e(TAG, "onGetAllTopicInFolderFailure: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        fetchDataForTopicList();
        super.onResume();
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.option_folder);

        // Handle click events for options in the bottom sheet
        View addTopicOption = bottomSheetDialog.findViewById(R.id.addTopic);
        if (addTopicOption != null) {
            addTopicOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UploadTopic.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("folderId", folderId);
                    startActivityForResult(intent, REQUEST_CODE_ADD_TOPIC);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        View removeFolderOption = bottomSheetDialog.findViewById(R.id.removeFolder);
        if (removeFolderOption != null) {
            removeFolderOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                    bottomSheetDialog.dismiss();
                }
            });
        }

        bottomSheetDialog.show();
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                            modifierData.removeFolder(folderId, new OnRemoveFolderListener() {
                                @Override
                                public void onRemoveFolderSuccess() {
                                    Toast.makeText(DetailFolderActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(DetailFolderActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onRemoveFolderFailure(Exception e) {
                                    finish();
                                }
                            });

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
}

