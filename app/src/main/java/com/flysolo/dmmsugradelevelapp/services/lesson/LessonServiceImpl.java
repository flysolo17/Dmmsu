package com.flysolo.dmmsugradelevelapp.services.lesson;

import androidx.annotation.NonNull;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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
    public void getAllLesson(UiState<ArrayList<Lesson>> result) {
        result.Loading();
        ArrayList<Lesson> lessons = new ArrayList<>();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.LESSONS_TABLE)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    lessons.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot: task.getResult()) {
                            Lesson lesson = snapshot.toObject(Lesson.class);
                            lessons.add(lesson);
                        }
                        result.Successful(lessons);
                    } else {
                        result.Failed("Failed deleting lesson");
                    }
                })
                .addOnFailureListener(e -> result.Failed(e.getMessage()));
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
    public void getAllActivity(UiState<ArrayList<Quiz>> result) {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot: task.getResult()) {
                            Quiz quiz = snapshot.toObject(Quiz.class);
                            quizzes.add(quiz);
                        }
                        result.Successful(quizzes);
                    } else  {
                        result.Failed("Failed getting activities");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
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

}
