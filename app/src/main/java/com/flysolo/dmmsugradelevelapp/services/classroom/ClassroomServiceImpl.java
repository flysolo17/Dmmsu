package com.flysolo.dmmsugradelevelapp.services.classroom;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ClassroomServiceImpl implements ClassroomService {
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    public ClassroomServiceImpl(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firestore = firestore;
        this.storage = storage;
    }

    @Override
    public void getAllClassrooms(String uid, UiState<List<Classroom>> result) {
        ArrayList<Classroom> classroomArrayList = new ArrayList<>();
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .whereEqualTo("teacherID",uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classroomArrayList.clear();
                        for (QueryDocumentSnapshot snapshot:
                             task.getResult()) {
                            Classroom classroom = snapshot.toObject(Classroom.class);
                            classroomArrayList.add(classroom);

                        }
                        result.Successful(classroomArrayList);
                    } else {
                        result.Failed("Failed getting classroom");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));

    }

    @Override
    public void uploadBackground(String uid, Uri uri, UiState<String> result) {
        StorageReference reference = storage.getReference(Constants.CLASSROOM_TABLE)
                .child(uid)
                .child(String.valueOf(System.currentTimeMillis()));
        result.Loading();
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> result.Successful(uri1.toString()))).addOnFailureListener(e -> {
            result.Failed(e.getMessage());
        });
    }

    @Override
    public void createClassroom(Classroom classroom, UiState<String> result) {
        classroom.setId(firestore.collection(Constants.CLASSROOM_TABLE).document().getId());
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroom.getId())
                .set(classroom)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully added!");
                    } else {
                        result.Failed("Failed creating classroom");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.Failed(e.getMessage());
                    }
                });
    }

    @Override
    public void createActivity(String classroomID ,Quiz quiz, UiState<String> result) {
        result.Loading();
        quiz.setId(firestore.collection(Constants.CLASSROOM_TABLE).document(classroomID).collection(Constants.ACTIVITIES).document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES)
                .document(quiz.getId())
                .set(quiz)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            result.Successful("Successfully Created!");
                        } else {
                            result.Failed("Failed creating activity");
                        }
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void deleteActivity(String classroomID,String activtyID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES)
                .document(activtyID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Successfully Deleted!");
                    } else {
                        result.Failed("Failed deleting activity");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void updateActivity(String activityID, Quiz quiz, UiState<String> result) {

    }
}
