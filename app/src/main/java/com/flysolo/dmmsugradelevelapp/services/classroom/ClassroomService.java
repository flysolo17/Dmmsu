package com.flysolo.dmmsugradelevelapp.services.classroom;

import android.net.Uri;

import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import java.util.List;

public interface ClassroomService {
    void getAllClassrooms(String uid , UiState<List<Classroom>> result);
    void uploadBackground(String uid, Uri uri,UiState<String> result);
    void createClassroom(Classroom classroom,UiState<String> result);
    void createActivity(String classroomID,Quiz quiz,UiState<String> result);
    void deleteActivity(String classroomID,String activtyID, UiState<String> result);
    void updateActivity(String activityID,Quiz quiz,UiState<String> result);
}
