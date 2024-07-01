package com.example.quizlearning.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetTopicByCurrentUserListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.adapter.MyAdapterFolder;
import com.example.quizlearning.adapter.MyAdapterTopic;
import com.example.quizlearning.controller.UploadFolder;
import com.example.quizlearning.controller.UploadTopic;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TopicFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    Button bAddTopic;
    RecyclerView  showTopic;
    View view;
    String userId = "null";
    List<TopicModel> dataList;
    CollectionReference topicsRef;
    SearchView search;
    FirebaseFirestore mStore;
    FloatingActionButton fabAdd;
    MyAdapterTopic myAdapterTopic;
    ProgressDialog progressDialog;
    Getdata getdata;
    CollectionReference topicCommunityRef;
    List<String> topicCommunityId = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_topic, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");
        progressDialog = new ProgressDialog(view.getContext());
        mStore = FirebaseFirestore.getInstance();
        topicsRef = mStore.collection("Topics");
        getdata = new Getdata();
        //GET ID USER:
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        showTopic = view.findViewById(R.id.showTopic);
//        bAddTopic = view.findViewById(R.id.bAddTopic);
        showTopicContent();
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
                Intent intent = new Intent(v.getContext(), UploadTopic.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        return view;
    }
    public void SearchList(String text){
        ArrayList<TopicModel> searchTopic = new ArrayList<TopicModel>();
        for(TopicModel topicModel: dataList){
            if(topicModel.getTopicTitle().toLowerCase().contains(text.toLowerCase())){
                searchTopic.add(topicModel);
            }
        }
        myAdapterTopic.searchTopic(searchTopic);
        showTopic.invalidate();

    }
    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        showTopicContent();
        progressDialog.dismiss();
    }

    public void showTopicContent() {
        GridLayoutManager gridLayoutManagerTopic = new GridLayoutManager(getActivity(), 1);
        showTopic.setLayoutManager(gridLayoutManagerTopic);
        dataList = new ArrayList<>();
        myAdapterTopic = new MyAdapterTopic(view.getContext(), dataList);
        showTopic.setAdapter(myAdapterTopic);
        progressDialog.show();
        //Topic --- TopicID --- Created By
        topicCommunityRef = mStore.collection("Users").document(userId).collection("Community");
        topicCommunityRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String communityTopic = documentSnapshot.getId().toString();
                        topicCommunityId.add(communityTopic);
                    }
                }
            }
        });
        topicsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
//                    progressDialog.dismiss();
                    return;
                }
                if(!queryDocumentSnapshots.isEmpty()){
                    dataList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String topicAuth = documentSnapshot.getString("topicAuth");
                        String topicIdCheck = documentSnapshot.getString("topicId");
                        // Kiểm tra topic nào thuộc id User đang đăng nhập thì xử lý dữ liệu
                        if (topicAuth != null && topicAuth.equals(userId)) {
                            TopicModel topicModel = documentSnapshot.toObject(TopicModel.class);
                                dataList.add(topicModel);
                        }
                        else{
                            if(topicCommunityId != null){
                                for(int i = 0; i<topicCommunityId.size(); i++){
                                    if(topicCommunityId.get(i).equals(topicIdCheck)){
                                        TopicModel topicModel = documentSnapshot.toObject(TopicModel.class);
                                        dataList.add(topicModel);
                                        break;
                                    }
                                }
                            }
                        }
                    }
//                    dataList.add();

                    // Cập nhật Adapter và Dismiss Dialog
                    myAdapterTopic.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
                else{
                    dataList.clear();
                    myAdapterTopic.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        });

        progressDialog.dismiss();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

}
