package com.flysolo.dmmsugradelevelapp.services.activity;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
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

    @Override
    public void uploadMultipleImages(Activity activity,String quizID, Uri[] uriList, UiState<List<String>> result) {
        StorageReference reference = storage.getReference(Constants.QUESTIONS_TABLE).child(quizID);
//        result.Loading();
//        Toast.makeText(activity, uriList.length +"TEst", Toast.LENGTH_SHORT).show();
        List<String> urls = new ArrayList<>();
//
//            result.Loading();
//            StorageReference imageRef = reference.child(UUID.randomUUID().toString() + "." + Constants.getFileExtension(activity,uriList.get(0))); // Define the path and filename for each image
//            imageRef.putFile(uriList.get(0)).addOnSuccessListener(taskSnapshot -> {
//                // Image upload successfully completed
//                // You can retrieve the download URL of the uploaded image here if needed
//                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    // The download URL of the uploaded image
//                    String downloadUrl = uri.toString();
//                    urls.add(downloadUrl);
//                    if (urls.size() != uriList.size()) {
//                        result.Successful(urls);
//                    }
//                });
//
//
//            }).addOnFailureListener(e -> result.Failed(e.getMessage()));



        result.Loading();
        // Create a list to hold the upload tasks
        List<Task<Uri>> uploadTasks = new ArrayList<>();

// Iterate through the list of images and create upload tasks
        for (Uri imageUri : uriList) {
            StorageReference imageRef = reference.child( UUID.randomUUID().toString() + Constants.getFileExtension(activity,imageUri));
            UploadTask uploadTask = imageRef.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    result.Failed("Error uploading image");
                }
                // Return the download URL of the uploaded image
                return imageRef.getDownloadUrl();
            });
            uploadTasks.add(urlTask);
        }

// Execute the batch upload using Tasks.whenAllComplete()
        Tasks.whenAllComplete(uploadTasks)
                .addOnSuccessListener(tasks -> {
                    // Batch upload completed successfully
                    // You can retrieve the download URLs of the uploaded images here if needed
                    for (Task<?> task : tasks) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = (Uri) task.getResult();
                            urls.add(downloadUrl.toString());
                        } else {
                            // Handle the case where an upload task failed
                            Exception exception = task.getException();
                            // Handle the exception
                            result.Failed(exception.getMessage());
                        }
                    }
                    result.Successful(urls);
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the batch upload
                    result.Failed(e.getMessage());
                });

    }

    @Override
    public void updateQuestion(String quizID,List<String> images, String answer, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .update("choices",images,"answer",answer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Successfully updated");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }
}
