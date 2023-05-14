package com.flysolo.dmmsugradelevelapp.services.leaderboard;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardServiceImpl implements LeaderBoardService {
    FirebaseFirestore firestore;

    public LeaderBoardServiceImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void submitAnswer(Respond respond, UiState<String> result) {
        result.Loading();
        respond.setId(firestore.collection(Constants.RESOPONSES_TABLE).document().getId());
        firestore.collection(Constants.RESOPONSES_TABLE)
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
    public void getAllResponse(UiState<List<Respond>> result) {
            result.Loading();
            firestore.collection(Constants.RESOPONSES_TABLE)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            result.Successful(task.getResult().toObjects(Respond.class));
                        } else  {
                            result.Failed("error getting responses");
                        }
                    }).addOnFailureListener(e -> result.Failed(e.getMessage()));

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
    public void getActivity( String quizID, UiState<Quiz> result) {
        result.Loading();
        firestore.collection(Constants.ACTIVITIES_TABLE)
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
    public void getRespondents(String activityID, UiState<List<Respond>> result) {
        result.Loading();
        firestore.collection(Constants.RESOPONSES_TABLE)
                .whereEqualTo("activityID",activityID)
                .orderBy("dateAnswered", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful(task.getResult().toObjects(Respond.class));
                    } else  {
                        result.Failed("Failed getting responses");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
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
