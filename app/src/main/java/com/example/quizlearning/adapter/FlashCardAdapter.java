package com.example.quizlearning.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.R;
import com.example.quizlearning.model.WordFillModel;
import com.example.quizlearning.model.WordModel;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.List;
import java.util.Locale;

public class FlashCardAdapter extends RecyclerView.Adapter<FlashCardAdapter.MyHolder>{
    private List<WordModel> dataList;

    private Context context;

    int studyLanguage;
    boolean isFlipped = false;
    TextToSpeech frontTextToSpeech;;
    TextToSpeech backTextToSpeech;


    public FlashCardAdapter() {}

    @NonNull
    @Override
    public FlashCardAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.flashcard_item_layout, parent, false);
        return new FlashCardAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashCardAdapter.MyHolder holder, int position) {
        WordModel wordModel = dataList.get(position);
        boolean isFrontSide = holder.easyFlipViewVertical.isFrontSide();

        if(studyLanguage == 0) {
            holder.tvDefinition.setText(wordModel.getWordMean());
            holder.tvWord.setText(wordModel.getWord());
        }
        else{
            holder.tvDefinition.setText(wordModel.getWord());
            holder.tvWord.setText(wordModel.getWordMean());
        }

        if(!isFrontSide){
            holder.btSoundFront.setVisibility(View.GONE);
            holder.btSoundFront.setEnabled(false);
            holder.btSoundBack.setVisibility(View.VISIBLE);
            holder.btSoundBack.setEnabled(true);
        }
        else{
            holder.btSoundFront.setVisibility(View.VISIBLE);
            holder.btSoundFront.setEnabled(true);
            holder.btSoundBack.setVisibility(View.GONE);
            holder.btSoundBack.setEnabled(false);
        }

        holder.easyFlipViewVertical.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView easyFlipView, EasyFlipView.FlipState newCurrentSide) {
                if (newCurrentSide == EasyFlipView.FlipState.FRONT_SIDE) {
                            // Handle the back (flipped) state
                    isFlipped = false;
                    holder.btSoundFront.setVisibility(View.VISIBLE);
                    holder.btSoundFront.setEnabled(true);
                    holder.btSoundBack.setVisibility(View.GONE);
                    holder.btSoundBack.setEnabled(false);
                                // Do something when it's flipped to the front
                }
                else{
                                // Handle the front (not flipped) state
                    isFlipped = true;
                    holder.btSoundFront.setVisibility(View.GONE);
                    holder.btSoundFront.setEnabled(false);
                    holder.btSoundBack.setVisibility(View.VISIBLE);
                    holder.btSoundBack.setEnabled(true);
                                // Do something when it's flipped to the back
                }

            }
        });

        holder.btSoundFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frontText = holder.tvWord.getText().toString();
                frontTextToSpeech.speak(frontText, TextToSpeech.QUEUE_FLUSH,null);
//                Toast.makeText(FlashCardActivity.this,"FRONT",Toast.LENGTH_SHORT).show();
            }
        });


        holder.btSoundBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(FlashCardActivity.this,"BACK",Toast.LENGTH_SHORT).show();
                String backText = holder.tvDefinition.getText().toString();
                backTextToSpeech.speak(backText,TextToSpeech.QUEUE_FLUSH,null);
            }
        });

    }


//    public void Slider(int position) {
//        // Kiểm tra xem vị trí có hợp lệ hay không
//        for(int i = position;i<dataList.size();i++) {
//            int finalI = i;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    rvFlashcard.smoothScrollToPosition(finalI);
//                }
//            }, 1000);
//        }
//    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public FlashCardAdapter(List<WordModel> dataList, Context context,int studyLanguage) {
        this.dataList = dataList;
        this.context = context;
        this.studyLanguage = studyLanguage;

        if(studyLanguage == 0) {
            frontTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(Locale.ENGLISH);
                }
            });

            backTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        backTextToSpeech.setLanguage(new Locale("vi"));
                }
            });
        }
        else{
            frontTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        frontTextToSpeech.setLanguage(new Locale("vi"));
                }
            });

            backTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR)
                        backTextToSpeech.setLanguage(Locale.ENGLISH);
                }
            });
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tvDefinition;
        TextView tvWord;
        ImageButton btSoundBack;
        ImageButton btSoundFront;
        EasyFlipView easyFlipViewVertical;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvDefinition = itemView.findViewById(R.id.tvDefinition);
            tvWord = itemView.findViewById(R.id.tvWord);
            btSoundBack = itemView.findViewById(R.id.btSoundBack);
            btSoundFront = itemView.findViewById(R.id.btSoundFront);
            easyFlipViewVertical = itemView.findViewById(R.id.easyFlipViewVertical);
        }
    }
}
