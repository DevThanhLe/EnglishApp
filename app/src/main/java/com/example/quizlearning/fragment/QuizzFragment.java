package com.example.quizlearning.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.R;
import com.example.quizlearning.TopicToStudy;
import com.example.quizlearning.activity.FlashCardActivity;
import com.example.quizlearning.activity.QuizzActivity;
import com.example.quizlearning.activity.WordFillActivity;
import com.example.quizlearning.model.WordModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizzFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizzFragment extends Fragment {

    Context mContext;
    private CardView cvFlashCard;
    private CardView cvQuizz;
    private CardView cvWordFill;
    CollectionReference wordRef;
    FirebaseFirestore mStore;
    private Switch swLanguage;

    private int favoriteOrNot = 0;
    String userId;
    int favoriteListSize = 0;

    private int feedBackPerQuestion = 0;
    private int shuffleOrNot = 0;
//    private GridView cat_Grid;
//    public List<TopicModel> catList = new ArrayList<>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuizzFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizzFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizzFragment newInstance(String param1, String param2) {
        QuizzFragment fragment = new QuizzFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quizz, container, false);
        cvFlashCard = view.findViewById(R.id.cvFlashCard);
        cvQuizz = view.findViewById(R.id.cvQuizz);
        cvWordFill = view.findViewById(R.id.cvWordFill);
        swLanguage = view.findViewById(R.id.swLanguage);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "null");
        cvFlashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(view,0);
            }
        });
        cvQuizz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(view,1);
            }
        });
        cvWordFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(view,2);
            }
        });
        return view;
    }

    public int studyLanguage(){
        if(swLanguage.isChecked())
            return 1;
        else
            return 0;
    }
    private void showCustomDialog(View view,int studyId) {
        // Inflate layout for the dialog
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.quiz_wordfill_feature, null);
        Switch switch1 = dialogView.findViewById(R.id.switch1);
        Switch switch2 = dialogView.findViewById(R.id.switch2);
        Switch switch3 = dialogView.findViewById(R.id.switch3);

        if(studyId == 0){
            switch1.setVisibility(View.GONE);
            switch1.setEnabled(false);
        }
        checkFavorite();
        // Initialize switches

        // Build the custom dialog

        TextView titleView = new TextView(view.getContext());
        if(studyId == 1){
            titleView.setText("Chọn chức năng cho Quiz");
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        else if(studyId == 2){
            titleView.setText("Chọn chức năng cho Word Fill");
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        else{
            titleView.setText("Chọn chức năng cho Flashcard");
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCustomTitle(titleView)
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve switch values and handle them
                        if(switch1.isChecked())
                            feedBackPerQuestion = 1;
                        else
                            feedBackPerQuestion = 0;
                        if(switch2.isChecked())
                            shuffleOrNot = 1;
                        else
                            shuffleOrNot = 0;
                        if(switch3.isChecked())
                            favoriteOrNot = 1;
                        else
                            favoriteOrNot = 0;

                        // Do something with the switch values
                        if(studyId == 1){
                            if(favoriteOrNot == 0) {
                                Intent intent = new Intent(view.getContext(), TopicToStudy.class);
                                intent.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                intent.putExtra("shuffleOrNot", shuffleOrNot);
                                intent.putExtra("typeStudy", 2);
                                intent.putExtra("studyLanguage", studyLanguage());
                                view.getContext().startActivity(intent);
                            }
                            else {
                                if(favoriteListSize > 3) {
                                    Intent intent = new Intent(view.getContext(), QuizzActivity.class);
                                    intent.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                    intent.putExtra("shuffleOrNot", shuffleOrNot);
                                    intent.putExtra("studyLanguage", studyLanguage());
                                    intent.putExtra("favoriteOrNot", favoriteOrNot);
                                    view.getContext().startActivity(intent);
                                }
                                else{
                                    Toast.makeText(view.getContext(), "Bạn chưa có đủ từ trong Favorite !",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else if(studyId == 2){
                            if(favoriteOrNot == 0) {
                                Intent intent = new Intent(view.getContext(), TopicToStudy.class);
                                intent.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                intent.putExtra("shuffleOrNot", shuffleOrNot);
                                intent.putExtra("typeStudy", 3);
                                intent.putExtra("studyLanguage", studyLanguage());
                                view.getContext().startActivity(intent);
                            }
                            else {
                                if(favoriteListSize != 0) {
                                    Intent intent = new Intent(view.getContext(), WordFillActivity.class);
                                    intent.putExtra("feedBackPerQuestion", feedBackPerQuestion);
                                    intent.putExtra("shuffleOrNot", shuffleOrNot);
                                    intent.putExtra("studyLanguage", studyLanguage());
                                    intent.putExtra("favoriteOrNot", favoriteOrNot);
                                    view.getContext().startActivity(intent);
                                }
                                else{
                                    Toast.makeText(view.getContext(), "Bạn chưa lưu từ trong Favorite !",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else{
                            if(favoriteOrNot == 0) {
                                Intent intent = new Intent(mContext, TopicToStudy.class);
                                intent.putExtra("typeStudy", 1);
                                intent.putExtra("studyLanguage", studyLanguage());
                                intent.putExtra("shuffleOrNot", shuffleOrNot);
                                intent.putExtra("favoriteOrNot", favoriteOrNot);
                                mContext.startActivity(intent);
                            }
                            else{
                                if(favoriteListSize != 0) {
                                    Intent intent = new Intent(mContext, FlashCardActivity.class);
                                    intent.putExtra("studyLanguage", studyLanguage());
                                    intent.putExtra("shuffleOrNot", shuffleOrNot);
                                    intent.putExtra("favoriteOrNot", favoriteOrNot);
                                    mContext.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(view.getContext(), "Bạn chưa lưu từ trong Favorite !",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        feedBackPerQuestion = 0;
                        shuffleOrNot = 0;
                        favoriteOrNot = 0;
                    }
                });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void checkFavorite(){
        mStore = FirebaseFirestore.getInstance();
        wordRef = mStore.collection("Favorites")
                .document(userId)
                .collection("Words");
        wordRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Xử lý lỗi nếu có
//                    Log.e("Firestore", "Error getting topics", e);
                    return;
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    favoriteListSize = queryDocumentSnapshots.size();
                }
                else{
                    favoriteListSize = 0;
                }
            }
        });
    }
}