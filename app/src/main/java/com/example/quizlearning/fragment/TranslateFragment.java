package com.example.quizlearning.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI.OnRecognizeTextListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI.OnTranslateListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetAllTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetTopicByCurrentUserListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.AIFeature.RecognizeText;
import com.example.quizlearning.Services.AIFeature.Translate;
import com.example.quizlearning.Services.FirestoreService.Getdata;
import com.example.quizlearning.adapter.SelectTopicAdapter;
import com.example.quizlearning.model.TopicModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.ArrayList;
import java.util.Locale;

import kotlin.Unit;

public class TranslateFragment extends Fragment {
    private static final String TAG = TranslateFragment.class.getSimpleName();

    private Spinner spinnerFrom, spinnerTo;
    private TextInputEditText sourceText;
    private ImageView micBtn, getImageSource, storeToTopic;
    private MaterialButton translateBtn;
    private TextView translatedText;
    private RecognizeText recognizeText;
    private Translate translateService;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    Getdata getdata;
    Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Xử lý kết quả ở đây
                    // ...
                    imageUri = result.getData().getData();
                    recognizeText.recognizeText(imageUri, new OnRecognizeTextListener() {
                        @Override
                        public void onRecognizeTextSuccess(String text) {
                            translatedText.setText(text);
                        }

                        @Override
                        public void onRecognizeTextFailure(Exception e) {
                            Log.e(TAG, "onRecognizeTextFailure: " + e.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(TranslateFragment.this.getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    String[] fromLang = {"From", "English", "Vietnamese"};
    String[] toLang = {"To", "English", "Vietnamese"};
    private static int REQUEST_PERMISSION_CODE = 1;
    String fromLangCode, toLangCode;


    ActivityResultLauncher activityResultLauncherLanguage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        ArrayList<String> resultRes = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        sourceText.setText(resultRes.get(0));
                    }
                }
            }
    );

    public TranslateFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreate: " + view.findViewById(R.id.spinner_from).toString());
        spinnerFrom = (Spinner) view.findViewById(R.id.spinner_from);
        spinnerTo = (Spinner) view.findViewById(R.id.spinner_to);
        sourceText = (TextInputEditText) view.findViewById(R.id.et_sourceText);
        translateBtn = (MaterialButton) view.findViewById(R.id.btn_translate);
        translatedText = (TextView) view.findViewById(R.id.tv_translatedText);
//        micBtn = (ImageView) view.findViewById(R.id.btn_mic);
//        getImageSource = (ImageView) view.findViewById(R.id.getImageSource);
//        storeToTopic = (ImageView) view.findViewById(R.id.storeToTopic);
//        storeToTopic = (ImageView) view.findViewById(R.id.storeToTopic);
        mAuth = FirebaseAuth.getInstance();
        recognizeText = new RecognizeText(view.getContext());
        translateService = new Translate();
        getdata = new Getdata();
        progressDialog = new ProgressDialog(view.getContext());

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLangCode = getLangCode(fromLang[position]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, fromLang);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(fromAdapter);

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLangCode = getLangCode(toLang[position]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, toLang);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(toAdapter);
//        micBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //requestPermission();
//                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//                try {
//                    activityResultLauncherLanguage.launch(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromLangCode.isEmpty() || toLangCode.isEmpty()) {
                    Toast.makeText(TranslateFragment.this.getContext(), "Please select language", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sourceText.getText().toString().isEmpty()) {
                    sourceText.setError("Please enter text to translate");
                    sourceText.requestFocus();
                } else if (fromLangCode.isEmpty()) {
                    ((TextView) spinnerFrom.getSelectedView()).setError("Please select language");
                    spinnerFrom.requestFocus();
                    return;
                } else if (toLangCode.isEmpty()) {
                    ((TextView) spinnerTo.getSelectedView()).setError("Please select language");
                    spinnerTo.requestFocus();
                } else {
                    translatedText.setVisibility(View.VISIBLE);
                    translatedText.setText("Translating...");
                    translateService.translateText(
                            fromLangCode,
                            toLangCode,
                            sourceText.getText().toString(),
                            new OnTranslateListener() {
                                @Override
                                public void onTranslateSuccess(String result) {
                                    translatedText.setText(result);
                                }

                                @Override
                                public void onTranslateFailure(Exception e) {
                                    Log.e(TAG, "onTranslateFailure: "+e.getMessage() );
                                }
                            });
                }
            }
        });

//        getImageSource.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePicker.Companion.with(TranslateFragment.this)
//                        .crop()
//                        .compress(1024)
//                        .maxResultSize(1080, 1080)
//                        .createIntent(intent -> {
//                            imagePickerLauncher.launch(intent);
//                            return Unit.INSTANCE;
//                        });
//            }
//        });
//        storeToTopic.setOnClickListener(v -> {
//            progressDialog.show();
//            getdata.getAllTopicsOfUser(mAuth.getCurrentUser().getUid(), new OnGetTopicByCurrentUserListener() {
//                @Override
//                public void onGetTopicByCurrentUserSuccess(ArrayList<TopicModel> topics) {
//                    View recyclerSelectTopic = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_select_recyclerview_item, null);
//                    RecyclerView rvSelectTopic = recyclerSelectTopic.findViewById(R.id.rvTopic);
//                    rvSelectTopic.setLayoutManager(new LinearLayoutManager(v.getContext()));
//                    rvSelectTopic.setAdapter(new SelectTopicAdapter(v.getContext(), topics, topicPosition -> {
//                        Log.e(TAG, "onGetAllTopicSuccess: Item is Selected: " + topics.get(topicPosition).getTopicTitle());
//                    }));
//                    progressDialog.dismiss();
//                    new AlertDialog.Builder(v.getContext())
//                            .setTitle("Choose a topic")
//                            .setView(recyclerSelectTopic)
//                            .setNegativeButton("Cancel", null)
//                            .show();
//                }
//
//                @Override
//                public void onGetTopicByCurrentUserFailure(Exception e) {
//                    progressDialog.dismiss();
//                    Log.e(TAG, "onGetTopicByCurrentUserFailure: "+e.getMessage() );
//                }
//            });
//        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    private void translateText(String fromLangCode, String toLangCode, String sourceText) {
//        TranslatorOptions options = new TranslatorOptions.Builder()
//                .setSourceLanguage(fromLangCode)
//                .setTargetLanguage(toLangCode)
//                .build();
//        final Translator translator = Translation.getClient(options);
//        DownloadConditions conditions = new DownloadConditions.Builder()
//                .requireWifi()
//                .build();
//        translator.downloadModelIfNeeded(conditions)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        translatedText.setText("Translating...");
//                        translator.translate(sourceText)
//                                .addOnSuccessListener(new OnSuccessListener<String>() {
//                                    @Override
//                                    public void onSuccess(String s) {
//                                        translatedText.setText(s);
//                                    }
//                                });
//                    }
//                }).addOnFailureListener(e -> {
//                    Log.e(TAG, "translateText: "+ e.getMessage() );
//                    translatedText.setText("Download model is failure!");
//                    Toast.makeText(TranslateFragment.this.getContext(), "Download model is failure!", Toast.LENGTH_SHORT).show();
//                });
//
//    }

    private String getLangCode(String lang) {
        String langCode = "";
        switch (lang) {
            case "English":
                langCode = TranslateLanguage.ENGLISH;
                break;
            case "Vietnamese":
                langCode = TranslateLanguage.VIETNAMESE;
                break;
            default:
                langCode = "";
        }
        return langCode;
    }
}