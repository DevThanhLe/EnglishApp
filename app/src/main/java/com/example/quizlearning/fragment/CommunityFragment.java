package com.example.quizlearning.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetAllTopicListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.adapter.MyAdapterFolder;
import com.example.quizlearning.adapter.MyAdapterTopic;
import com.example.quizlearning.R;
import com.example.quizlearning.controller.UploadFolder;
import com.example.quizlearning.controller.UploadTopic;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    ProgressDialog progressDialog;
    FloatingActionButton fabAddTopic, fabAddFolder;
    FloatingActionMenu fabMenu;
    RecyclerView  showTopic;
    View view;
    List<TopicModel> dataList, dataTopic;
    CollectionReference topicsRef, folderRef;
    SearchView search;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    String userId;
    MyAdapterTopic myAdapterTopic;
    Getdata getdata;
    public CommunityFragment() {
    }


    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_community, container, false);
        progressDialog = new ProgressDialog(view.getContext());
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        getdata = new Getdata();

        //GET ID USER:
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        showTopic = view.findViewById(R.id.showCommunityTopic);
        progressDialog = new ProgressDialog(view.getContext());
        showTopicContent();

        //SEARCH
        search = view.findViewById(R.id.searchCommunityTopic);
        search.clearFocus();
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
        return view;
    }

    public void SearchList(String text){
        ArrayList<TopicModel> searchList = new ArrayList<TopicModel>();
        for(TopicModel topicModel: dataList){
            if(topicModel.getTopicTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(topicModel);
            }
        }
        myAdapterTopic.searchTopic(searchList);
    }
    public void showTopicContent() {
        GridLayoutManager gridLayoutManagerTopic = new GridLayoutManager(getActivity(), 1);
        showTopic.setLayoutManager(gridLayoutManagerTopic);
        dataList = new ArrayList<>();
        myAdapterTopic = new MyAdapterTopic(view.getContext(), dataList,1);
        showTopic.setAdapter(myAdapterTopic);
        progressDialog.show();
        //Topic --- TopicID --- Created By
        getdata.getAllTopicsPublic(new OnGetAllTopicListener() {
            @Override
            public void onGetAllTopicSuccess(ArrayList<TopicModel> topics) {
                ArrayList<TopicModel> topicsOfAnotherUser = new ArrayList<>();
                for (TopicModel topicModel : topics) {
                    if(!topicModel.getTopicAuth().equals(userId)){
                        topicsOfAnotherUser.add(topicModel);
                    }
                    Log.e(TAG, "onGetAllTopicSuccess: "+topicModel.getTopicTitle());
                }
                dataList.clear();
                dataList.addAll(topicsOfAnotherUser);
                myAdapterTopic.notifyDataSetChanged();
            }

            @Override
            public void onGetAllTopicFailure(Exception e) {
                Log.e(TAG, "onGetAllTopicFailure: "+e.getMessage() );
            }
        });
        progressDialog.dismiss();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.option_menu_topic, menu);

            // Tìm MenuItem bằng ID
            MenuItem itemEdit = menu.findItem(R.id.action_edit);
            MenuItem itemDelete = menu.findItem(R.id.action_delete);
            MenuItem moveToFolder = menu.findItem(R.id.action_move_to_folder);
            MenuItem getTopic = menu.findItem(R.id.action_get_topic);

            itemEdit.setVisible(false);
            itemDelete.setVisible(false);
            moveToFolder.setVisible(false);
            getTopic.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

}