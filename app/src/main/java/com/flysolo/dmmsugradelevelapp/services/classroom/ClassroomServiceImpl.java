package com.flysolo.dmmsugradelevelapp.services.classroom;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.UserType;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        quiz.setId(firestore.collection(Constants.CLASSROOM_TABLE).document(classroomID).collection(Constants.ACTIVITIES_TABLE).document().getId());
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .collection(Constants.ACTIVITIES_TABLE)
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
                .collection(Constants.ACTIVITIES_TABLE)
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

    @Override
    public void getStudents(UiState<ArrayList<Accounts>> result) {
        result.Loading();
        ArrayList<Accounts> accountsArrayList = new ArrayList<>();
        firestore.collection(Constants.ACCOUNTS_TABLE)
                .whereEqualTo("type", UserType.STUDENT)
                .addSnapshotListener((value, error) -> {
                    accountsArrayList.clear();
                    if (error != null){
                        result.Failed(error.getCode() + ": " + error.getMessage());
                    }
                    if (value != null) {
                        for (QueryDocumentSnapshot snapshot: value) {
                            Accounts accounts = snapshot.toObject(Accounts.class);
                            accountsArrayList.add(accounts);
                        }
                        result.Successful(accountsArrayList);
                    }
                });
    }

    @Override
    public void addStudent(String classroomID, String studentID, UiState<String> result) {
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .update("students", FieldValue.arrayUnion(studentID))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Successfully added!");
                    } else {
                        result.Failed("Failed adding student ");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void removeStudent(String classroomID, String studentID, UiState<String> result) {
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classroomID)
                .update("students", FieldValue.arrayRemove(studentID))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Successfully remove!");
                    } else {
                        result.Failed("Failed removing student");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getAllActivities(String uid, UiState<List<Quiz>> result) {
        result.Loading();
        ArrayList<Quiz> quizArrayList = new ArrayList<>();
        Task<QuerySnapshot> classroom = firestore.collection(Constants.CLASSROOM_TABLE)
                .whereEqualTo("teacherID",uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
        classroom.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot: task.getResult()) {
                    firestore.collection(Constants.CLASSROOM_TABLE)
                            .document(snapshot.getId())
                            .collection(Constants.ACTIVITIES_TABLE)
                            .get()
                            .addOnCompleteListener(doc -> {
                                if (doc.isSuccessful()) {
                                    quizArrayList.clear();
                                    for (QueryDocumentSnapshot snap: doc.getResult()) {
                                        Quiz quiz = snap.toObject(Quiz.class);
                                        quizArrayList.add(quiz);
                                    }
                                    result.Successful(quizArrayList);
                                } else {
                                    result.Failed("Failed getting activities");
                                }
                            }).addOnFailureListener(e -> result.Failed(e.getMessage()));
                }
            }
        });
    }

    @Override
    public void getAllActivities2(List<Classroom> classroomList, UiState<List<Quiz>> result) {
        result.Loading();
        ArrayList<Quiz> quizArrayList = new ArrayList<>();
        for (Classroom classroom : classroomList) {
            firestore.collection(Constants.CLASSROOM_TABLE)
                    .document(classroom.getId())
                    .collection(Constants.ACTIVITIES_TABLE)
                    .get()
                    .addOnCompleteListener(doc -> {
                        if (doc.isSuccessful()) {
                            for (QueryDocumentSnapshot snap: doc.getResult()) {
                                Quiz quiz = snap.toObject(Quiz.class);
                                quizArrayList.add(quiz);
                            }
                            result.Successful(quizArrayList);
                        } else {
                            result.Failed("Failed getting activities");
                        }
                    }).addOnFailureListener(e -> result.Failed(e.getMessage()));
        }
    }

    @Override
    public void getAllClass(UiState<List<Classroom>> result) {
        ArrayList<Classroom> classroomArrayList = new ArrayList<>();
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        result.Failed(error.getCode() + ":  " + error.getMessage());
                    }
                    if (value != null) {
                        classroomArrayList.clear();
                        for (DocumentSnapshot snapshot: value.getDocuments()) {
                            Classroom classroom = snapshot.toObject(Classroom.class);
                            classroomArrayList.add(classroom);
                        }
                        result.Successful(classroomArrayList);
                    }
                });
    }




}
