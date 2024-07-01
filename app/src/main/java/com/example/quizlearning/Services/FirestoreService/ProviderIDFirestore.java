package com.example.quizlearning.Services.FirestoreService;

import com.example.quizlearning.model.TopicModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProviderIDFirestore {
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public ProviderIDFirestore() {
    }

    public ProviderIDFirestore(FirebaseFirestore mStore) {
        this.mStore = mStore;
    }

    public void updateIDForTopic(String id, ProviderListener listener) {
        mStore
                .collection("Topics")
                .document(id)
                .update("topicId", id)
                .addOnSuccessListener(command -> listener.onProviderSuccess())
                .addOnFailureListener(e -> listener.onProviderFailure(e));
    }
    public void updateIDForTopicInFolder(String idFolder,String id, ProviderListener listener) {
        mStore
                .collection("Folders")
                .document(idFolder)
                .collection("Topics")
                .document(id)
                .update("topicId", id)
                .addOnSuccessListener(command -> listener.onProviderSuccess())
                .addOnFailureListener(e -> listener.onProviderFailure(e));
    }
    public void updateIDForFolder(String id, ProviderListener listener) {
        mStore
                .collection("Folders")
                .document(id)
                .update("folderId", id)
                .addOnSuccessListener(command -> listener.onProviderSuccess())
                .addOnFailureListener(e -> listener.onProviderFailure(e));
    }
    public void updateIDForWord(String topicId, String wordId, ProviderListener listener) {
        mStore
                .collection("Topics")
                .document(topicId)
                .collection("Words")
                .document(wordId)
                .update("wordId", wordId)
                .addOnSuccessListener(command -> listener.onProviderSuccess())
                .addOnFailureListener(e -> listener.onProviderFailure(e));
    }

    public void updateIDForWordInTopicFolder(String folderId, String topicId,String wordId, ProviderListener listener) {
        mStore
                .collection("Folders")
                .document(folderId)
                .collection("Topics")
                .document(topicId)
                .collection("Words")
                .document(wordId)
                .update("wordId", wordId)
                .addOnSuccessListener(command -> listener.onProviderSuccess())
                .addOnFailureListener(e -> listener.onProviderFailure(e));
    }
    public interface ProviderListener {
        void onProviderSuccess();
        void onProviderFailure(Exception e);
    }
}
