package com.flysolo.dmmsugradelevelapp.services.classroom;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        classroomArrayList.clear();
                        for (DocumentSnapshot snapshot: value.getDocuments()) {
                            Classroom classroom = snapshot.toObject(Classroom.class);
                            classroomArrayList.add(classroom);

                        }
                        result.Successful(classroomArrayList);
                    }
                    if (error != null) {
                        result.Failed(error.getMessage());
                    }
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
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
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
    public void startClass(String classID,UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classID)
                .update("status",true,"code",generateClassCode())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Class is now open!");
                    } else {
                        result.Failed("Failed to start class");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void endClass(String classID,UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classID)
                .update("status",false,"code","","activeStudents",new ArrayList<String>())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        result.Successful("Class is now closed!");
                    } else {
                        result.Failed("Failed to end class");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
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

    @Override
    public void getAllMyClass(String studentID, UiState<List<Classroom>> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .whereArrayContains("students",studentID)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        result.Successful(value.toObjects(Classroom.class));
                    }
                    if (error != null) {
                        result.Failed(error.getMessage());
                    }
                });
    }

    @Override
    public void searchClass(String code, UiState<Classroom> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .whereEqualTo("code",code)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        result.Successful(task.getResult().toObjects(Classroom.class).get(0));
                    } else {
                        result.Failed("No class matching the code: " + code);
                    }
                 }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void joinClass(String classID,String studentID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classID)
                .update("students",FieldValue.arrayUnion(studentID))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("You successfully join the class");
                    } else {
                        result.Failed("Failed to join the class");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));

    }

    @Override
    public void addAttendance(String classID, String studentID, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.CLASSROOM_TABLE)
                .document(classID)
                .update("activeStudents",FieldValue.arrayUnion(studentID))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("You successfully join the class");
                    } else {
                        result.Failed("Failed to join the class");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    private String generateClassCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 10;
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }


}
