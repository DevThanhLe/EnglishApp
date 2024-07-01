package com.example.quizlearning.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllFolderByCurrentUserListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.adapter.MyAdapterFolder;
import com.example.quizlearning.controller.UploadFolder;
import com.example.quizlearning.model.FolderModel;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FolderFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    FloatingActionButton fabAddTopic, fabAddFolder;
    Button bAddFolder;
    RecyclerView  showFolder;
    View view;
    String userId = "null";
//    List<TopicModel> dataList, dataTopic;
    List<FolderModel> dataFolder;
    CollectionReference topicsRef, folderRef;
    SearchView search;
    FloatingActionButton fabAdd;
    FirebaseFirestore mStore;
//    MyAdapterTopic myAdapterTopic;
    MyAdapterFolder myAdapterFolder;
    ProgressDialog progressDialog;
    Getdata getdata;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");
        progressDialog = new ProgressDialog(view.getContext());
        mStore = FirebaseFirestore.getInstance();
//        topicsRef = mStore.collection("Topics");
        folderRef = mStore.collection("Folders");
        getdata = new Getdata();
        //GET ID USER:
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        showFolder = view.findViewById(R.id.showFolder);
        showFolderContent();

        //SEARCH
        search = view.findViewById(R.id.search);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchList(newText);
                return true;
            }
        });
        fabAdd = (FloatingActionButton)view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UploadFolder.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        return view;
    }

    public void SearchList(String text){
         ArrayList<FolderModel> searchFolder = new ArrayList<FolderModel>();
         for(FolderModel folderModel: dataFolder){
             if(folderModel.getFolderName().toLowerCase().contains(text.toLowerCase())){
                 searchFolder.add(folderModel);
             }
         }
         myAdapterFolder.searchFolder(searchFolder);
        showFolder.invalidate();
    }

    public void showFolderContent(){
        //SHOW DETAILS FOLDER
        GridLayoutManager gridLayoutManagerFolder = new GridLayoutManager(getActivity(), 1);
        showFolder.setLayoutManager(gridLayoutManagerFolder);
        dataFolder = new ArrayList<>();
        myAdapterFolder = new MyAdapterFolder(view.getContext(), dataFolder);
        showFolder.setAdapter(myAdapterFolder);
        progressDialog.show();
        getdata.getAllFolderByUser(userId, new OnGetAllFolderByCurrentUserListener() {
            @Override
            public void onGetAllFolderByCurrentUserSuccess(ArrayList<FolderModel> folders) {
                dataFolder.clear();
                dataFolder.addAll(folders);
                myAdapterFolder.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onGetAllFolderByCurrentUserFailure(Exception e) {
                progressDialog.dismiss();
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        showFolderContent();
    }
}
