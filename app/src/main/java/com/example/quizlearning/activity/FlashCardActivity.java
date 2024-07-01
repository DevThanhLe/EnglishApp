package com.example.quizlearning.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizlearning.R;
import com.example.quizlearning.adapter.FeedBackWordFillAdapter;
import com.example.quizlearning.adapter.FlashCardAdapter;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wajahatkarim3.easyflipview.EasyFlipView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FlashCardActivity extends AppCompatActivity {

    TextView tvWord;
    TextView tvDefinition;
    CollectionReference wordRef;
    FirebaseFirestore mStore;
    List<WordModel> dataList;
    Button btPrevious;
    Button btNext;
    int numOfWords = 0;
    TextView tvNumOfWords;
    ImageButton btSoundBack;
    ImageButton btSoundFront;
    TextToSpeech frontTextToSpeech;;
    TextToSpeech backTextToSpeech;

    ImageButton back;
    boolean isFlipped = false;
    EasyFlipView easyFlipViewVertical;

    String userId;

    private String topicId;
    private int studyLanguage;

    RecyclerView rvFlashcard;
    private int shuffleOrNot = 0;
    private int favoriteOrNot = 0;
    Switch swSlide;
    private static final int AUTO_SLIDE_INTERVAL = 10000;
    private Handler autoSlideHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topicId = bundle.getString("topic_id");
            studyLanguage = bundle.getInt("studyLanguage");
            shuffleOrNot = bundle.getInt("shuffleOrNot");
            favoriteOrNot = bundle.getInt("favoriteOrNot",0);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId","null");

        autoSlideHandler = new Handler();

        dataList = new ArrayList<>();

        btPrevious = findViewById(R.id.btPrevious);
        btNext = findViewById(R.id.btNext);
        tvNumOfWords = findViewById(R.id.tvNumOfWords);
        swSlide = findViewById(R.id.swSlide);
        rvFlashcard = findViewById(R.id.rvFlashcard);
        back = findViewById(R.id.back);

        loadWords(dataList);
        // Cấm người dùng vuốt



        swSlide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Log.d hoặc Toast ở đây để kiểm tra xem sự kiện được kích hoạt hay không
                if(swSlide.isChecked()) {
                    btNext.setEnabled(false);
                    btPrevious.setEnabled(false);
                    autoSlideHandler.postDelayed(autoSlideRunnable, AUTO_SLIDE_INTERVAL);
//                    adapter.Slider(numOfWords);
                }
                else{
                    btNext.setEnabled(true);
                    btPrevious.setEnabled(true);
                    autoSlideHandler.removeCallbacks(autoSlideRunnable);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSlideHandler.removeCallbacks(autoSlideRunnable);
                finish();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numOfWords = numOfWords + 1;
                if (numOfWords == dataList.size()) {
                    numOfWords = numOfWords - 1;
                } else {
//                            setDataFC(numOfWords);
                    tvNumOfWords.setText(String.valueOf(numOfWords + 1) + "/" + dataList.size());
                    scrollToPosition(numOfWords);
                }
            }
        });
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numOfWords = numOfWords - 1;
                if (numOfWords < 0) {
                    numOfWords = numOfWords + 1;
                } else {
//                            setDataFC(numOfWords);
                    tvNumOfWords.setText(String.valueOf(numOfWords + 1) + "/" + dataList.size());
                    scrollToPosition(numOfWords);
                }
            }
        });

    }
    private void loadWords(List<WordModel> dataList) {
        rvFlashcard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        FlashCardAdapter adapter = new FlashCardAdapter(dataList,this,studyLanguage);
        rvFlashcard.setAdapter(adapter);
        rvFlashcard.setLayoutManager(layoutManager);
        mStore = FirebaseFirestore.getInstance();
        if(favoriteOrNot == 0) {
            wordRef = mStore.collection("Topics")
                    .document(topicId)
                    .collection("Words");
        }
        else{
            wordRef = mStore.collection("Favorites")
                    .document(userId)
                    .collection("Words");
        }
        wordRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                dataList.clear();
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    WordModel wordModel = documentSnapshot.toObject(WordModel.class);
                                    dataList.add(wordModel);
                                }

                                // Cập nhật Adapter và Dismiss Dialog
                                if (shuffleOrNot == 1) {
                                    Collections.shuffle(dataList);
                                }
                                adapter.notifyDataSetChanged();
                                tvNumOfWords.setText("1/" + dataList.size());
                            } else {
                                // Dữ liệu trống, xử lý theo cách bạn muốn
                            }
                        } else {
                            // Xử lý lỗi nếu có
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Log.e("Firestore", "Error getting topics", exception);
                            }
                        }
                    }
                });
    }
    private Runnable autoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            if (swSlide.isChecked()) {
                // Làm điều gì đó để cuộn đến vị trí tiếp theo
                numOfWords = numOfWords + 1;
                if (numOfWords >= dataList.size()) {
                    numOfWords = 0;
                }
                rvFlashcard.smoothScrollToPosition(numOfWords);
                tvNumOfWords.setText(String.valueOf(numOfWords + 1) + "/" + dataList.size());
                // Gọi lại chính nó để tạo hiệu ứng auto slide lặp đi lặp lại
                autoSlideHandler.postDelayed(this, AUTO_SLIDE_INTERVAL);
            }
        }
    };

    public void scrollToPosition(int position) {
        // Kiểm tra xem vị trí có hợp lệ hay không
        if (position >= 0 && position < dataList.size()) {
            // Cuộn đến vị trí cụ thể
            rvFlashcard.smoothScrollToPosition(position);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }
}