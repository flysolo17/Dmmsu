package com.flysolo.dmmsugradelevelapp.views.lesson;

import android.net.Uri;

import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LessonServiceImpl implements LessonService {
    FirebaseFirestore firestore;
    FirebaseStorage storage;


    public LessonServiceImpl(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.firestore = firestore;
        this.storage = storage;

    }

    @Override
    public void createLesson(Lesson lesson, UiState<String> result) {
        result.Loading();
        lesson.setId(firestore.collection(Constants.CLASSROOM_TABLE).document(lesson.getClassroomID()).collection(Constants.LESSONS_TABLE).document().getId());
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lesson.getId())
                .set(lesson)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully created!");
                    } else {
                        result.Failed("Failed creating lesson");
                    }
                })
                .addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void deleteLesson(String lessonID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lessonID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully deleted!");
                    } else {
                        result.Failed("Failed deleting lesson");
                    }
                })
                .addOnFailureListener(e -> result.Failed(e.getMessage()));

    }

    @Override
    public void updateLesson(Lesson lesson, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lesson.getId())
                .update("title",lesson.getTitle(),"description",lesson.getDescription())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully updated!");
                    } else {
                        result.Failed("Failed updating lesson");
                    }
                })
                .addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getLesson(String lessonID, UiState<Lesson> result) {
        result.Loading();
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lessonID)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        result.Successful(value.toObject(Lesson.class));
                    }
                    if (error != null){
                        result.Failed(error.getMessage());
                    }
                });
    }

    @Override
    public void getAllLesson(String classroomID,UiState<List<Lesson>> result) {
        result.Loading();
        firestore
                .collection(Constants.LESSONS_TABLE)
                .whereEqualTo("classroomID",classroomID)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObjects(Lesson.class));
                    }
                });
    }
    @Override
    public void addContent(String lessonID,Content content, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lessonID)
                .update("contents",FieldValue.arrayUnion(content))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Added!");
                    } else {
                        result.Failed("Failed to add content!");
                    }
                 }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void deleteContent(String lessonID,Content content, UiState<String> result) {
        result.Loading();
        firestore
                .collection(Constants.LESSONS_TABLE)
                .document(lessonID)
                .update("contents",FieldValue.arrayRemove(content))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Deleted!");
                    } else {
                        result.Failed("Failed to delete content!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void updateContent(String lessonID,List<Content> contents, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.LESSONS_TABLE)
                .document(lessonID)
                .update("contents",contents)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Updated!");
                    } else {
                        result.Failed("Failed to update content!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }


    @Override
    public void uploadAttachment(String type,String filename,Uri uri, UiState<String> result) {
            StorageReference reference = storage.getReference(Constants.CLASSROOM_TABLE)
                    .child("Attachments")
                    .child(type+"?"+filename);
            result.Loading();
            reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> result.Successful(uri1.toString()))).addOnFailureListener(e -> {
                result.Failed(e.getMessage());
            });

    }



}
