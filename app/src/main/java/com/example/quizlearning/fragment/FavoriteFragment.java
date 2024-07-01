package com.example.quizlearning.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.adapter.MyAdapterTopic;
import com.example.quizlearning.adapter.MyAdapterWord;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private View view;
    private String userId;
    ProgressDialog progressDialog;
    FirebaseFirestore mStore;
    RecyclerView  showWord;
    SearchView search;
    Getdata getdata;
    MyAdapterWord myAdapterWord;
    ArrayList<WordModel> dataList = new ArrayList<>();
    CollectionReference favoriteRef;
    public FavoriteFragment() {

    }
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");
        progressDialog = new ProgressDialog(view.getContext());
        mStore = FirebaseFirestore.getInstance();
        favoriteRef =
                mStore
                        .collection("Favorites")
                        .document(userId)
                        .collection("Words");
        getdata = new Getdata();

        showWord = view.findViewById(R.id.showWord);

        showWordContent();
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
        return view;
    }
    public void SearchList(String text){
        ArrayList<WordModel> searchWord = new ArrayList<WordModel>();
        for(WordModel word: dataList){
            if(word.getWord().toLowerCase().contains(text.toLowerCase())
                    || word.getWordMean().toLowerCase().contains(text.toLowerCase())){
                searchWord.add(word);
            }
        }
        myAdapterWord.searchWord(searchWord);
        showWord.invalidate();
    }
    public void showWordContent() {
        GridLayoutManager gridLayoutManagerTopic = new GridLayoutManager(getActivity(), 1);
        showWord.setLayoutManager(gridLayoutManagerTopic);
        dataList = new ArrayList<>();
        myAdapterWord = new MyAdapterWord(view.getContext(), dataList, 1);
        showWord.setAdapter(myAdapterWord);
        progressDialog.show();
        favoriteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    dataList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        WordModel word = documentSnapshot.toObject(WordModel.class);
                        dataList.add(word);
                    }
                    // Cập nhật Adapter và Dismiss Dialog
                    myAdapterWord.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
                else{
                    dataList.clear();
                    myAdapterWord.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        showWordContent();
    }
}