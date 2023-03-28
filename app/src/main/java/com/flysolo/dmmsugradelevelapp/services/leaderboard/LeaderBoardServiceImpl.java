package com.flysolo.dmmsugradelevelapp.services.leaderboard;

import androidx.annotation.NonNull;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
