package com.flysolo.dmmsugradelevelapp.services.leaderboard;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import java.util.List;

public interface LeaderBoardService {
    void submitAnswer(Respond respond, UiState<String> result);
    void getAllResponse(UiState<List<Respond>> result);
    void getTeacherResponses(List<Classroom> classroomList, UiState<List<Respond>> result);
    void getActivity(String quizID, UiState<Quiz> result);
    void getQuestions(String classroomID, String quizID, UiState<List<Question>> result);
    void getStudentInfo(String studentID, UiState<Accounts> result);
    void getRespondents(String activityID,UiState<List<Respond>> result);
    void getAllActivities(List<Classroom> classroomList,UiState<List<Quiz>> result);
}
