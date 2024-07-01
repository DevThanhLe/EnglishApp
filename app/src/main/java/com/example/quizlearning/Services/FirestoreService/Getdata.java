package com.example.quizlearning.Services.FirestoreService;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllFolderByCurrentUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnGetAllTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.Community.OnCheckUserCloneTopicCommunity;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetAllTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnGetTopicByCurrentUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnCheckWordInFavoriteListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetAllWordInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetAllWordListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnGetWordByTopicListener;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.example.quizlearning.model.WordModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Getdata {
    private static final String TAG = "Getdata";
    FirebaseFirestore mStore;

    public Getdata() {
        mStore = FirebaseFirestore.getInstance();
    }

    public void getAllTopics(OnGetAllTopicListener listener) {
        mStore
                .collection("Topics")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    listener.onGetAllTopicSuccess(topics);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllTopicFailure(e);
                });
    }

    public void getAllTopicsInFolder(String folderID, OnGetAllTopicInFolderListener listener) {
        mStore
                .collection("Folders")
                .document(folderID)
                .collection("Topics")
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    listener.onGetAllTopicInFolderSuccess(topics);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllTopicInFolderFailure(e);
                });
    }

    public void getAllFolders(OnGetAllFolderListener listener) {
        mStore
                .collection("Folders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FolderModel> folders = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        folders.add(documentSnapshot.toObject(FolderModel.class));
                    }
                    listener.onGetAllFolderSuccess(folders);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllFolderFailure(e);
                });
    }
    private void getAllTopicPublicInFolder(String folderID, OnGetAllTopicListener listener) {
        mStore
                .collection("Folders")
                .document(folderID)
                .collection("Topics")
                .whereEqualTo("topicShare", true)
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<TopicModel> topicsInFolder = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        Log.e(TAG, "getAllTopicPublicInFolder: "+documentSnapshot.toObject(TopicModel.class).getTopicTitle() );
                        topicsInFolder.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    listener.onGetAllTopicSuccess(topicsInFolder);
                })
                .addOnFailureListener(command -> {
                    listener.onGetAllTopicFailure(command);
                });
    }
    public void getAllTopicsInFolderPublic(OnGetAllTopicListener topicListener) {
        getAllFolders(new OnGetAllFolderListener() {
            @Override
            public void onGetAllFolderSuccess(ArrayList<FolderModel> folders) {
                ArrayList<TopicModel> topics = new ArrayList<>();
                int folderCount = folders.size();
                AtomicInteger foldersProcessed = new AtomicInteger(0);

                for (FolderModel folder : folders) {
                    getAllTopicPublicInFolder(folder.getFolderId(), new OnGetAllTopicListener() {
                        @Override
                        public void onGetAllTopicSuccess(ArrayList<TopicModel> topicPublic) {
                            topics.addAll(topicPublic);
                            int processedFolders = foldersProcessed.incrementAndGet();
                            if (processedFolders == folderCount) {
                                topicListener.onGetAllTopicSuccess(topics);
                            }
                        }

                        @Override
                        public void onGetAllTopicFailure(Exception e) {
                            int processedFolders = foldersProcessed.incrementAndGet();
                            if (processedFolders == folderCount) {
                                topicListener.onGetAllTopicFailure(e);
                            }
                        }
                    });
                }
//                listener.onGetAllTopicSuccess(topics);
            }

            @Override
            public void onGetAllFolderFailure(Exception e) {
                topicListener.onGetAllTopicFailure(e);
            }
        });
    }
    public void getAllTopicsPublic(OnGetAllTopicListener resListener) {
        mStore
                .collection("Topics")
                .whereEqualTo("topicShare", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Log.e(TAG, "getAllTopicsPublic: "+documentSnapshot.toObject(TopicModel.class).getTopicTitle() );
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    getAllTopicsInFolderPublic(new OnGetAllTopicListener() {
                        @Override
                        public void onGetAllTopicSuccess(ArrayList<TopicModel> topicsRes) {
                            topics.addAll(topicsRes);
                            resListener.onGetAllTopicSuccess(topics);
                        }

                        @Override
                        public void onGetAllTopicFailure(Exception e) {

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    resListener.onGetAllTopicFailure(e);
                });
    }

    public void getAllFolderByUser(String userID, OnGetAllFolderByCurrentUserListener listener) {
        mStore
                .collection("Folders")
                .whereEqualTo("author", userID)
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<FolderModel> folders = new ArrayList<FolderModel>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        folders.add(documentSnapshot.toObject(FolderModel.class));
                        Log.e(TAG, "getAllFolderByUser: "+documentSnapshot.toObject(FolderModel.class).getFolderName() );
                    }
                    listener.onGetAllFolderByCurrentUserSuccess(folders);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllFolderByCurrentUserFailure(e);
                });
    }

    public void getAllTopicsInFolderByUser(String userID, ArrayList<TopicModel> topics, OnGetTopicByCurrentUserListener topicListener) {
        getAllFolderByUser(userID, new OnGetAllFolderByCurrentUserListener() {
            @Override
            public void onGetAllFolderByCurrentUserSuccess(ArrayList<FolderModel> folders) {
                ArrayList<TopicModel> topics = new ArrayList<>();
                int folderCount = folders.size();
                AtomicInteger foldersProcessed = new AtomicInteger(0);
                for (FolderModel folder : folders) {
                    getAllTopicsInFolder(folder.getFolderId(), new OnGetAllTopicInFolderListener() {
                        @Override
                        public void onGetAllTopicInFolderSuccess(ArrayList<TopicModel> topicPublic) {
                            topics.addAll(topicPublic);
                            int processedFolders = foldersProcessed.incrementAndGet();
                            if (processedFolders == folderCount) {
                                topicListener.onGetTopicByCurrentUserSuccess(topics);
                            }
                        }

                        @Override
                        public void onGetAllTopicInFolderFailure(Exception e) {
                            int processedFolders = foldersProcessed.incrementAndGet();
                            if (processedFolders == folderCount) {
                                topicListener.onGetTopicByCurrentUserFailure(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onGetAllFolderByCurrentUserFailure(Exception e) {
                topicListener.onGetTopicByCurrentUserFailure(e);
            }
        });
    }


    public void getAllTopicsOfUser(String userID, OnGetTopicByCurrentUserListener listener) {
        mStore
                .collection("Topics")
                .whereEqualTo("topicAuth", userID)
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    getAllTopicsInFolderByUser(userID, topics, new OnGetTopicByCurrentUserListener() {
                        @Override
                        public void onGetTopicByCurrentUserSuccess(ArrayList<TopicModel> topicID) {
                            topics.addAll(topicID);
                            getCommunityTopicsOfUser(userID, new OnGetTopicByCurrentUserListener() {
                                @Override
                                public void onGetTopicByCurrentUserSuccess(ArrayList<TopicModel> topic) {
                                    topics.addAll(topic);
                                    listener.onGetTopicByCurrentUserSuccess(topics);
                                }
                                @Override
                                public void onGetTopicByCurrentUserFailure(Exception e) {

                                }
                            });
                            listener.onGetTopicByCurrentUserSuccess(topics);
                        }

                        @Override
                        public void onGetTopicByCurrentUserFailure(Exception e) {

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    listener.onGetTopicByCurrentUserFailure(e);
                });
    }
    public void getWordsOfTopic(String topicID, OnGetAllWordListener listener) {
        mStore
                .collection("Topics")
                .document(topicID)
                .collection("Words")
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<WordModel> words = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        words.add(documentSnapshot.toObject(WordModel.class));
                    }
                    listener.onGetAllWordSuccess(words);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllWordFailure(e);
                });
    }
    public void checkIfUserCloneTopicCommunity(TopicModel topic, String userId, OnCheckUserCloneTopicCommunity listener){
        mStore.collection("Users")
                .document(userId)
                .collection("Community")
                .document(topic.getTopicId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot topicDocument = task.getResult();
                            if (topicDocument.exists()) {
                                listener.onCloneTopicAlready(topic);
                            }
                        }
                    }

                })
                .addOnFailureListener(error ->{
                    listener.onHaveNotCloneTopic();
                });
    }
    public void checkIfWordInFavorites(WordModel word, String userId, OnCheckWordInFavoriteListener listener){
        mStore.
                collection("Favorites")
                .document(userId)
                .collection("Words")
                .document(word.getWordId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot topicDocument = task.getResult();
                            if (topicDocument.exists()) {
                                listener.onHasWordInFavorite(word);
                            }
                        }
                    }
                })
                .addOnFailureListener(error ->{
                    listener.onNoWordInFavorite(error);
                });

    }
//    public void getAllTopicOfAFolderByUser(String userID, String folderID, OnGetAllTopicInFolderListener listener) {
//        mStore
//                .collection("Folders")
//                .document(folderID)
//                .collection("Topics")
//                .whereEqualTo("topicAuth", userID)
//                .get()
//                .addOnSuccessListener(command -> {
//                    ArrayList<TopicModel> topics = new ArrayList<>();
//                    for (DocumentSnapshot documentSnapshot : command) {
//                        topics.add(documentSnapshot.toObject(TopicModel.class));
//                    }
//                    listener.onGetAllTopicInFolderSuccess(topics);
//                })
//                .addOnFailureListener(e -> {
//                    listener.onGetAllTopicInFolderFailure(e);
//                });
//    }
    public void getAllTopicOfAFolder(String folderID, OnGetAllTopicInFolderListener listener) {
        mStore
                .collection("Folders")
                .document(folderID)
                .collection("TopicList")
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    listener.onGetAllTopicInFolderSuccess(topics);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllTopicInFolderFailure(e);
                });
    }
    public void getCommunityTopicsOfUser(String userID, OnGetTopicByCurrentUserListener listener) {
        mStore
                .collection("Users")
                .document(userID)
                .collection("Community")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<TopicModel> topics = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        topics.add(documentSnapshot.toObject(TopicModel.class));
                    }
                    listener.onGetTopicByCurrentUserSuccess(topics);
                })
                .addOnFailureListener(e -> {
                    listener.onGetTopicByCurrentUserFailure(e);
                });
    }
    public void getWordsOfCommunityTopic(String userID, String topicID, OnGetAllWordInFolderListener listener) {
        mStore
                .collection("Users")
                .document(userID)
                .collection("Community")
                .document(topicID)
                .collection("Words")
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<WordModel> words = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        words.add(documentSnapshot.toObject(WordModel.class));
                    }
                    listener.onGetAllWordInFolderSuccess(words);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllWordInFolderFailure(e);
                });
    }
    public void getWordsInTopic(String topicID, OnGetAllWordInFolderListener listener) {
        mStore
                .collection("Topics")
                .document(topicID)
                .collection("Words")
                .get()
                .addOnSuccessListener(command -> {
                    ArrayList<WordModel> words = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : command) {
                        words.add(documentSnapshot.toObject(WordModel.class));
                    }
                    listener.onGetAllWordInFolderSuccess(words);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllWordInFolderFailure(e);
                });
    }
    public void getWordOfTopicInFolder(String folderID, String topicID, OnGetAllWordListener listener) {
        mStore
                .collection("Folders")
                .document(folderID)
                .collection("Topics")
                .document(topicID)
                .collection("Words")
                .get()
                .addOnSuccessListener(queries -> {
                    ArrayList<WordModel> words = new ArrayList<WordModel>();
                    for (DocumentSnapshot query : queries) {
                        words.add(query.toObject(WordModel.class));
                    }
                    listener.onGetAllWordSuccess(words);
                })
                .addOnFailureListener(e -> {
                    listener.onGetAllWordFailure(e);
                });
    }
}
