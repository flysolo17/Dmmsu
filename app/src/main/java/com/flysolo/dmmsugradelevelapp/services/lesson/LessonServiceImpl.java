package com.flysolo.dmmsugradelevelapp.services.lesson;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class LessonServiceImpl implements LessonService {
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    String classroomID;

    public LessonServiceImpl(FirebaseFirestore firestore, FirebaseStorage storage, String classroomID) {
        this.firestore = firestore;
        this.storage = storage;
        this.classroomID = classroomID;
    }

    @Override
    public void createLesson(Lesson lesson, UiState<String> result) {
        result.Loading();
        lesson.setId(firestore.collection(Constants.CLASSROOM_TABLE).document(classroomID).collection(Constants.LESSONS_TABLE).document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.LESSONS_TABLE)
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
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.LESSONS_TABLE)
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
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.LESSONS_TABLE)
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
    public void getAllLesson(UiState<List<Lesson>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.LESSONS_TABLE)
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
    public void createActivity(Quiz quiz, UiState<String> result) {
        result.Loading();
        quiz.setId(firestore.collection(Constants.CLASSROOM_TABLE).document(classroomID).collection(Constants.ACTIVITIES_TABLE).document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
                .document(quiz.getId())
                .set(quiz)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Created!");
                    } else {
                        result.Failed("Failed creating activity");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getAllActivity(String lessonID,UiState<List<Quiz>> result) {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
                .whereEqualTo("lessonID",lessonID)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error!= null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObjects(Quiz.class));
                    }
                });
    }

    @Override
    public void deleteActivity(String quizID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Deleted");
                    } else  {
                        result.Failed("Failed deleting activity");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));

    }

    @Override
    public void addContent(Content content, UiState<String> result) {
        result.Loading();
        content.setId( firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.CONTENTS_TABLE)
                .document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.CONTENTS_TABLE)
                .document(content.getId())
                .set(content)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Added!");
                    } else {
                        result.Failed("Failed to add content!");
                    }
                 }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void deleteContent(String contentID, UiState<String> result) {
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.CONTENTS_TABLE)
                .document(contentID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Deleted!");
                    } else {
                        result.Failed("Failed to delete content!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void updateContent(Content content, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.CONTENTS_TABLE)
                .document(content.getId())
                .set(content)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully Updated!");
                    } else {
                        result.Failed("Failed to update content!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getAllContent(String lessonID, UiState<List<Content>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.CONTENTS_TABLE)
                .whereEqualTo("lessonID",lessonID)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error!= null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObjects(Content.class));
                    }
                });
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

    @Override
    public void getQuestionsByID(String activityID, UiState<List<Question>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE)
                .whereEqualTo("activityID",activityID)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error!= null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObjects(Question.class));
                    }
                });
    }
    @Override
    public void addQuestion(Question question, UiState<String> result) {
        result.Loading();
        question.setId(firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE).document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE)
                .document(question.getId())
                .set(question)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully added");
                    } else  {
                        result.Failed("Failed to add question");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void deleteQuestion(String questionID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE)
                .document(questionID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully deleted");
                    } else  {
                        result.Failed("Failed to delete question");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void updateQuestion(String questionID, Question question, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE)
                .document(question.getId())
                .set(question)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully updated");
                    } else  {
                        result.Failed("Failed to update question");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }



}
