package com.flysolo.dmmsugradelevelapp.services.activity;

import android.app.Activity;
import android.net.Uri;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.List;

public interface ActivityService {
    void addActvity(Quiz quiz, UiState<String> result);
    FirestoreRecyclerOptions<Quiz> getAllActivity(String lessonID);
    void deleteActivity(String actvityID,UiState<String> result);
    void updateActivity(Quiz quiz, UiState<String> result);
    void uploadAttachment(String type, String filename, Uri uri, UiState<String> result);
    void addQuestion(String quizID, Question question, UiState<String> result);
    void deleteQuestion(String quizID,Question question,UiState<String> result);
    void getQuestion(String quizID,UiState<Quiz> result);
    void updateQuestion(String quizID, List<Question> questions,UiState<String> result);
    void uploadMultipleImages(Activity activity,String quizID, Uri[] uriList, UiState<List<String>> result);

    void updateQuestion(String quizID,List<String> images,String answer,UiState<String> result);

}
