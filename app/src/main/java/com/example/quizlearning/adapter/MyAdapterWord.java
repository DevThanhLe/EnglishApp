package com.example.quizlearning.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordToFavoriteListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnCheckWordInFavoriteListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordFromFavoriteListener;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.activity.DetailWordActivity;
import com.example.quizlearning.R;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAdapterWord extends RecyclerView.Adapter<MyViewHolderWord> {
    private Context context;
    private List<WordModel> dataList;
    private ModifierData modifierData;
    String userId;
    View view;
    private int anotherFragment = 0;
    Getdata getData;

    TextToSpeech frontTextToSpeech;

    public MyAdapterWord(Context context, List<WordModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        frontTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    frontTextToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }
    public MyAdapterWord(Context context, List<WordModel> dataList, int anotherFragment) {
        this.context = context;
        this.dataList = dataList;
        this.anotherFragment = anotherFragment;
        frontTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    frontTextToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }
    @NonNull
    @Override
    public MyViewHolderWord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_word,parent,false);
        return new MyViewHolderWord(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolderWord holder, @SuppressLint("RecyclerView") int position) {
        modifierData = new ModifierData();
        getData = new Getdata();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
//        Glide.with(context).load(dataList.get(position).getImageWord()).into(holder.rvImageWord);
        holder.rvWord.setText(dataList.get(position).getWord());
//        holder.rvDescWord.setText(dataList.get(position).getWordDesc());
        holder.rvMeanWord.setText(dataList.get(position).getWordMean());

        getData.checkIfWordInFavorites(dataList.get(position), userId, new OnCheckWordInFavoriteListener() {
            @Override
            public void onHasWordInFavorite(WordModel word) {
                holder.bUnStar.setVisibility(View.GONE);
                holder.bStar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNoWordInFavorite(Exception e) {
                holder.bStar.setVisibility(View.GONE);
                holder.bUnStar.setVisibility(View.VISIBLE);
            }
        });

        String text = dataList.get(position).getWord();
        holder.btSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"Da bam vao : " + text, Toast.LENGTH_SHORT).show();
                frontTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);
            }
        });
        if(anotherFragment == 0){
            holder.rvListWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailWordActivity.class);
                    intent.putExtra("wordImage", dataList.get(holder.getAdapterPosition()).getWordImage());
//                intent.putExtra("wordDescription", dataList.get(holder.getAdapterPosition()).getWordDesc());
                    intent.putExtra("wordMean", dataList.get(holder.getAdapterPosition()).getWordMean());
                    intent.putExtra("word", dataList.get(holder.getAdapterPosition()).getWord());
                    intent.putExtra("topicId", dataList.get(holder.getAdapterPosition()).getTopicId());
                    intent.putExtra("wordId", dataList.get(holder.getAdapterPosition()).getWordId());
                    intent.putExtra("state", dataList.get(holder.getAdapterPosition()).getState());
                    context.startActivities(new Intent[]{intent});
                }
            });
        }
        else{
            holder.rvListWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frontTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);
                }
            });
        }
        holder.bUnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bStar.setVisibility(View.VISIBLE);
                holder.bUnStar.setVisibility(View.GONE);
                modifierData.addWordToFavorite(dataList.get(position), userId, new OnAddWordToFavoriteListener() {
                    @Override
                    public void onAddWordToFavoriteSuccess(WordModel word) {
                        Toast.makeText(context, "Added word to favorite list", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAddWordToFavoriteFailure(Exception e) {
                        Toast.makeText(context, "Failed to add word to favorite list", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.bStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bUnStar.setVisibility(View.VISIBLE);
                holder.bStar.setVisibility(View.GONE);
                modifierData.removeWordFromFavorite(dataList.get(position), userId, new OnRemoveWordFromFavoriteListener() {
                    @Override
                    public void onRemoveWordFromFavoriteSuccess(WordModel word) {
                        Toast.makeText(context, "Removed word from favorite list", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onRemoveWordFromFavoriteFailure(Exception e) {
                        Toast.makeText(context, "Failed to remove word from favorite list", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.rvWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }
    public void searchWord(ArrayList<WordModel> searchList){
        notifyDataSetChanged();
        dataList = searchList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
class MyViewHolderWord extends RecyclerView.ViewHolder{
//    ImageView rvImageWord;
    TextView rvMeanWord, rvDescWord, rvWord;
    CardView rvListWord;
    ImageButton btSound, bStar, bUnStar;
    public MyViewHolderWord(@NonNull View itemView) {
        super(itemView);

//        rvImageWord = itemView.findViewById(R.id.rvImageWord);
        rvWord = itemView.findViewById(R.id.rvWord);
//        rvDescWord = itemView.findViewById(R.id.rvDescWord);
        rvMeanWord = itemView.findViewById(R.id.rvMeanWord);
        rvListWord = itemView.findViewById(R.id.rvListWord);
        bStar = itemView.findViewById(R.id.bStar);
        bUnStar = itemView.findViewById(R.id.bUnStar);
        btSound = itemView.findViewById(R.id.btSound);
    }

}
