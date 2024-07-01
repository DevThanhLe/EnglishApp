package com.example.quizlearning.Services.FirestoreService;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnAddFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnAddTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnUpdateTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.Community.OnAddTopicCommunityListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnAddTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnMoveTopicToFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnRemoveTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnUpdateTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.User.OnAddUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.User.OnRemoveUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.User.OnUpdateUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordToFavoriteListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnAddWordToTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetAllWordListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetWordByTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnPutWordImageListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordFromFavoriteListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordInTopicFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnRemoveWordListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnUpdateWordInTopicFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnUpdateWordListener;
import com.example.quizlearning.auth.UserModel;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ModifierData {
    Context mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private ProviderIDFirestore providerIDFirestore = new ProviderIDFirestore();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private Getdata getdata = new Getdata();

    public ModifierData() {
    }

    public ModifierData(Context mContext) {
        this.mContext = mContext;
    }

    public ModifierData(Context mContext, FirebaseAuth mAuth, FirebaseFirestore mStore, ProviderIDFirestore providerIDFirestore) {
        this.mContext = mContext;
        this.mAuth = mAuth;
        this.mStore = mStore;
        this.providerIDFirestore = providerIDFirestore;
    }

    public void putImage(Uri imageUri, String ref, OnPutWordImageListener listener) {
        mStorage
                .getReference(ref)
                .child(imageUri.getLastPathSegment())
                .putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri downloadUri = uriTask.getResult();
                    listener.onPutImageSuccess(downloadUri);
                })
                .addOnFailureListener(e -> {
                    listener.onPutImageFailure(e);
                });

    }

    public void addUser(UserModel user, OnAddUserListener listener) {
        mStore
                .collection("Users")
                .document(user.getUserID())
                .set(user)
                .addOnSuccessListener(unused -> {
                    listener.onAddUserSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onAddUserFailure(e);
                });
    }

    public void removeUser(UserModel user, OnRemoveUserListener listener) {
        mStore
                .collection("Users")
                .document(user.getUserID())
                .delete()
                .addOnSuccessListener(unused -> {
                    listener.onRemoveUserSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onRemoveUserFailure(e);
                });
    }

    public void updateUser(UserModel user, OnUpdateUserListener listener) {
        mStore
                .collection("Users")
                .document(user.getUserID())
                .update(user.toMap())
                .addOnSuccessListener(unused -> {
                    listener.onUpdateUserSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onUpdateUserFailure(e);
                });
    }

    public void addTopic(TopicModel topicModel, OnAddTopicListener listener) {
        TopicModel topicRes = new TopicModel();
        Date timeCreate = Calendar.getInstance().getTime();
        topicRes.setTimeCreate(timeCreate);
        topicRes.setUsername(topicModel.getUsername());
        mStore
                .collection("Topics")
                .add(topicModel.toMap())
                .addOnSuccessListener(documentReference -> {
                    topicRes.setTopicWithMap(topicModel.toMap());
                    providerIDFirestore.updateIDForTopic(documentReference.getId(), new ProviderIDFirestore.ProviderListener() {
                        @Override
                        public void onProviderSuccess() {
                            topicRes.setTopicId(documentReference.getId());
                            topicRes.setTimeCreate(timeCreate);
                            listener.onAddTopicSuccess(topicRes);
                        }

                        @Override
                        public void onProviderFailure(Exception e) {
                            listener.onAddTopicFailure(e);
                        }
                    });
                });
    }

    public void updateTopic(TopicModel topic, String folderId, OnUpdateTopicListener listener) {
        mStore
                .collection("Topics")
                .document(topic.getTopicId())
                .update(topic.toMap())
                .addOnSuccessListener(result -> {
                    if(folderId != null){
                        mStore.collection("Folders")
                                .document(folderId)
                                .collection("TopicList")
                                .document(topic.getTopicId())
                                .update(topic.toMap())
                                .addOnSuccessListener(unused->{
                                    listener.onUpdateTopicSuccess(topic);
                                });
                    }
                })
                .addOnFailureListener(e -> listener.onUpdateTopicFailure(e));
    }

    public void removeTopic(TopicModel topicModel, OnRemoveTopicListener listener) {
        mStore.collection("Topics")
                .document(topicModel.getTopicId())
                .collection("Words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = mStore.batch();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference documentRef = mStore.collection("Topics")
                                    .document(topicModel.getTopicId())
                                    .collection("Words")
                                    .document(document.getId());
                            batch.delete(documentRef);
                        }
                        batch.commit().addOnCompleteListener(batchTask -> {
                            if (batchTask.isSuccessful()) {
                                mStore.collection("Topics")
                                        .document(topicModel.getTopicId())
                                        .delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                // Xóa thành công
                                                listener.onRemoveTopicSuccess();
                                            } else {
                                                // Xóa thất bại
                                                Exception e = deleteTask.getException();
                                                if (e != null) {
                                                    listener.onRemoveTopicFailure(e);
                                                }
                                            }
                                        });
                            } else {
                                Exception e = batchTask.getException();
                                if (e != null) {
                                    listener.onRemoveTopicFailure(e);
                                }
                            }
                        });
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            listener.onRemoveTopicFailure(e);
                        }
                    }
                });
    }

    public void removeTopicInFolder(TopicModel topicModel, String folderId, OnRemoveTopicInFolderListener listener) {
       mStore.collection("Folders")
               .document(folderId)
               .collection("TopicList")
               .document(topicModel.getTopicId())
               .delete()
               .addOnSuccessListener(unused->{
                   listener.onRemoveTopicInFolderSuccess();
               })
               .addOnFailureListener(e ->{
                   listener.onRemoveTopicInFolderFailure(e);
               });
    }
    public void removeTopicCommunity(TopicModel topic, String userId, OnRemoveTopicListener listener){
        mStore.collection("Users")
                .document(userId)
                .collection("Community")
                .document(topic.getTopicId())
                .delete()
                .addOnSuccessListener(unused -> {
                    listener.onRemoveTopicSuccess();
                })
                .addOnFailureListener(e ->{
                    listener.onRemoveTopicFailure(e);
                });
    }
    public void removeFolder(String folderId, OnRemoveFolderListener listener){
        mStore.collection("Folders")
                .document(folderId)
                .delete()
                .addOnSuccessListener(unused ->{
                    listener.onRemoveFolderSuccess();
                })
                .addOnFailureListener(e ->{
                    listener.onRemoveFolderFailure(e);
                });

    }

        public void moveTopicToFolder(String folderId, TopicModel topic, OnMoveTopicToFolderListener listener) {
        mStore
                .collection("Folders")
                .document(folderId)
                .collection("TopicList")
                .document(topic.getTopicId())
                .set(topic)
                .addOnSuccessListener(successful ->{
                   listener.onMoveTopicToFolderSuccess();
                })
                .addOnFailureListener(e ->{
                   listener.onMoveTopicToFolderFailure(e);
                });
    }
    public void addWord(WordModel wordModel, String topicID, OnAddWordListener listener) {
        WordModel wordRes = new WordModel();
        mStore
                .collection("Topics")
                .document(topicID)
                .collection("Words")
                .add(wordModel)
                .addOnSuccessListener(documentReference -> {
                    wordRes.setWordWithMap(wordModel.toMap());
                    providerIDFirestore.updateIDForWord(topicID, documentReference.getId(), new ProviderIDFirestore.ProviderListener() {
                        @Override
                        public void onProviderSuccess() {
                            wordRes.setWordId(documentReference.getId());
                            listener.onAddWordSuccess(wordRes);
                        }

                        @Override
                        public void onProviderFailure(Exception e) {
                            listener.onAddWordFailure(e);
                        }
                    });
                });
    }

    public void updateWord(WordModel wordModel, String topicId, String wordId, OnUpdateWordListener listener) {
        mStore
                .collection("Topics")
                .document(topicId)
                .collection("Words")
                .document(wordId)
                .update(wordModel.toMap())
                .addOnSuccessListener(result -> {
                    listener.onUpdateWordSuccess(wordModel);
                })
                .addOnFailureListener(e -> listener.onUpdateWordFailure(e));
    }

    public void removeWord(String topic, WordModel wordModel, OnRemoveWordListener listener) {
        mStore
                .collection("Topics")
                .document(topic)
                .collection("Words")
                .document(wordModel.getWordId())
                .delete()
                .addOnSuccessListener(unused -> {
                    listener.onRemoveWordSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onRemoveWordFailure(e);
                });
    }

    public void addFolder(FolderModel folder, OnAddFolderListener listener) {
        FolderModel folderRes = new FolderModel();
        mStore
                .collection("Folders")
                .add(folder.toMap())
                .addOnSuccessListener(documentReference -> {
                    folderRes.setFolderWithMap(folder.toMap());
                    providerIDFirestore.updateIDForFolder(documentReference.getId(), new ProviderIDFirestore.ProviderListener() {
                        @Override
                        public void onProviderSuccess() {
                            folderRes.setFolderId(documentReference.getId());
                            listener.onAddFolderSuccess(folderRes);
                        }

                        @Override
                        public void onProviderFailure(Exception e) {
                            listener.onAddFolderFailure(e);
                        }
                    });
                });
    }


    public void addWordCommunity(String userID, String topicID, WordModel word, OnAddWordListener listener) {
        mStore
                .collection("Users")
                .document(userID)
                .collection("Community")
                .document(topicID)
                .collection("Words")
                .document(word.getWordId())
                .set(word)
                .addOnSuccessListener(result -> {
                    listener.onAddWordSuccess(word);
                })
                .addOnFailureListener(e -> listener.onAddWordFailure(e));
    }
    public void addWordToFavorite(WordModel word, String userId,OnAddWordToFavoriteListener listener){
        mStore.collection("Favorites")
                .document(userId)
                .collection("Words")
                .document(word.getWordId())
                .set(word)
                .addOnSuccessListener(result ->{
                    listener.onAddWordToFavoriteSuccess(word);
                })
                .addOnFailureListener(e ->{
                    listener.onAddWordToFavoriteFailure(e);
                });
    }
    public void removeWordFromFavorite(WordModel word, String userId, OnRemoveWordFromFavoriteListener listener){
        mStore.collection("Favorites")
                .document(userId)
                .collection("Words")
                .document(word.getWordId())
                .delete()
                .addOnSuccessListener(result ->{
                    listener.onRemoveWordFromFavoriteSuccess(word);
                })
                .addOnFailureListener(e ->{
                    listener.onRemoveWordFromFavoriteFailure(e);
                });
    }
    public void addCommunityTopic(String userID, TopicModel topic, OnAddTopicCommunityListener listener) {
        mStore
                .collection("Users")
                .document(userID)
                .collection("Community")
                .document(topic.getTopicId())
                .set(topic)
                .addOnSuccessListener(result -> {
//                    Done copy topic to community
//                    Continue with Words
                    getdata.getWordsOfTopic(topic.getTopicId(), new OnGetAllWordListener() {
                        @Override
                        public void onGetAllWordSuccess(ArrayList<WordModel> words) {
                            for (WordModel word : words) {
                                addWordCommunity(userID, topic.getTopicId(), word, new OnAddWordListener() {
                                    @Override
                                    public void onAddWordSuccess(WordModel word) {

                                    }

                                    @Override
                                    public void onAddWordFailure(Exception e) {
                                        listener.onAddTopicCommunityFailure(e);
                                    }
                                });
                            }
                            listener.onAddTopicCommunitySuccess();
                        }

                        @Override
                        public void onGetAllWordFailure(Exception e) {
                            listener.onAddTopicCommunityFailure(e);
                        }
                    });
                })
                .addOnFailureListener(e -> listener.onAddTopicCommunityFailure(e));
    }


}
