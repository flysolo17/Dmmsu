package com.flysolo.dmmsugradelevelapp.services.activity;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.UUID;

public class ActivityServiceImpl implements ActivityService {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public ActivityServiceImpl(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firestore = firestore;
        this.storage = storage;
    }

    @Override
    public void addActvity(Quiz quiz, UiState<String> result) {
        quiz.setId(firestore.collection(Constants.ACTIVITIES_TABLE).document().getId());
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quiz.getId())
                .set(quiz)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully added!");
                    } else  {
                        result.Failed("Failed adding activity");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });

    }

    @Override
    public FirestoreRecyclerOptions<Quiz> getAllActivity(String lessonID) {
        Query query = firestore.collection(Constants.ACTIVITIES_TABLE)
                .whereEqualTo("lessonID",lessonID)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        return new FirestoreRecyclerOptions.Builder<Quiz>()
                .setQuery(query,Quiz.class)
                .build();
    }

    @Override
    public void deleteActivity(String actvityID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(actvityID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully deleted!");
                    } else  {
                        result.Failed("Failed deleting activity");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void updateActivity(Quiz quiz, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quiz.getId())
                .set(quiz)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully updated!");
                    } else  {
                        result.Failed("Failed updating activity");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void uploadAttachment(String type, String filename, Uri uri, UiState<String> result) {
        StorageReference reference = storage.getReference(Constants.CLASSROOM_TABLE)
                .child("Quiz")
                .child(type+"?"+filename);
        result.Loading();
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> result.Successful(uri1.toString()))).addOnFailureListener(e -> {
            result.Failed(e.getMessage());
        });
    }

    @Override
    public void addQuestion(String quizID, Question question, UiState<String> result) {
        question.setId(UUID.randomUUID().toString());
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .update("questions" , FieldValue.arrayUnion(question))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully added!");
                    } else  {
                        result.Failed("Failed adding question");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });

    }

    @Override
    public void deleteQuestion(String quizID, Question question, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .update("questions" , FieldValue.arrayRemove(question))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully deleted!");
                    } else  {
                        result.Failed("Failed deleting question");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void getQuestion(String quizID, UiState<Quiz> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObject(Quiz.class));
                    }
                });
    }

    @Override
    public void updateQuestion(String quizID, List<Question> questions, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .update("questions",questions)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully updated!");
                    } else  {
                        result.Failed("Failed updating question");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }
}
