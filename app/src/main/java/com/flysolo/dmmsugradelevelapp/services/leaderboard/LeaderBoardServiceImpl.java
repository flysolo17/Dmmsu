package com.flysolo.dmmsugradelevelapp.services.leaderboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardServiceImpl implements LeaderBoardService {
    FirebaseFirestore firestore;

    public LeaderBoardServiceImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void submitAnswer(String classroomID,Respond respond, UiState<String> result) {
        result.Loading();
        respond.setId(firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.RESOPONSES_TABLE)
                .document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.RESOPONSES_TABLE)
                .document(respond.getId())
                .set(respond)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Successfully Submitted!");
                    } else  {
                        result.Failed("Failed to submit");
                    }
                })
                .addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getAllResponse(List<Classroom> classroomList,String uid, UiState<List<Respond>> result) {
            List<Respond> respondArrayList = new ArrayList<>();
            result.Loading();
            for (Classroom classroom: classroomList) {
                firestore.collection(Constants.CLASSROOM_TABLE)
                        .document(classroom.getId())
                        .collection(Constants.RESOPONSES_TABLE)
                        .whereEqualTo("studentID",uid)
                        .orderBy("dateAnswered", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snap: task.getResult()) {
                                    Respond respond = snap.toObject(Respond.class);
                                    respondArrayList.add(respond);
                                }
                                result.Successful(respondArrayList);
                            } else {
                                result.Failed("Failed getting activities");
                            }
                        }).addOnFailureListener(e -> result.Failed(e.getMessage()));
        }
    }

    @Override
    public void getTeacherResponses(List<Classroom> classroomList, UiState<List<Respond>> result) {
        List<Respond> respondArrayList = new ArrayList<>();
        result.Loading();
        for (Classroom classroom: classroomList) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroom.getId())
                    .collection(Constants.RESOPONSES_TABLE)
                    .orderBy("dateAnswered", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snap: task.getResult()) {
                                Respond respond = snap.toObject(Respond.class);
                                respondArrayList.add(respond);
                            }
                            result.Successful(respondArrayList);
                        } else {
                            result.Failed("Failed getting activities");
                        }
                    }).addOnFailureListener(e -> result.Failed(e.getMessage()));
        }
    }

    @Override
    public void getActivity(String classroomID, String quizID, UiState<Quiz> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
                .document(quizID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        result.Successful(documentSnapshot.toObject(Quiz.class));
                    } else {
                        result.Failed("Error : getting quiz");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void getQuestions(String classroomID, String quizID, UiState<List<Question>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.QUESTIONS_TABLE)
                .whereEqualTo("activityID",quizID)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful(task.getResult().toObjects(Question.class));
                    } else {
                        result.Failed("Failed getting activities");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getStudentInfo(String studentID, UiState<Accounts> result) {
        result.Loading();
        firestore.collection(Constants.ACCOUNTS_TABLE)
                .document(studentID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        result.Successful(documentSnapshot.toObject(Accounts.class));
                    } else {
                        result.Failed("Failed getting student account");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getRespondents(String classroomID, String activityID, UiState<List<Respond>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.RESOPONSES_TABLE)
                .whereEqualTo("activityID",activityID)
                .orderBy("dateAnswered", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        result.Failed(error.getMessage());
                    }
                    if (value != null) {
                        result.Successful(value.toObjects(Respond.class));
                    }
                });
    }

    @Override
    public void getAllActivities(List<Classroom> classroomList, UiState<List<Quiz>> result) {
        List<Quiz> arrayList = new ArrayList<>();
        result.Loading();
        for (Classroom classroom: classroomList) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroom.getId())
                    .collection(Constants.ACTIVITIES_TABLE)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snap: task.getResult()) {
                                Quiz respond = snap.toObject(Quiz.class);
                                arrayList.add(respond);
                            }
                            result.Successful(arrayList);
                        } else {
                            result.Failed("Failed getting activities");
                        }
                    }).addOnFailureListener(e -> result.Failed(e.getMessage()));
        }
    }

}
