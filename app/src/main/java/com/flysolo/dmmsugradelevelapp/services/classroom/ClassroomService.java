package com.flysolo.dmmsugradelevelapp.services.classroom;

import android.net.Uri;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import java.util.ArrayList;
import java.util.List;

public interface ClassroomService {
    void getAllClassrooms(String uid , UiState<List<Classroom>> result);
    void createClassroom(Classroom classroom,UiState<String> result);
    void deleteClassroom(String classroomID,UiState<String> result);
    void editClassroom(Classroom classroom,UiState<String> result);
    void getClassroomByID(String classroomID,UiState<Classroom> result);
    void createActivity(String classroomID,Quiz quiz,UiState<String> result);
    void deleteActivity(String classroomID,String activtyID, UiState<String> result);
    void updateActivity(String activityID,Quiz quiz,UiState<String> result);
    void getStudents(UiState<ArrayList<Accounts>> result);
    void addStudent(String classroomID,String studentID,UiState<String> result);
    void removeStudent(String classroomID,String studentID,UiState<String> result);
    void getAllActivities(String uid,UiState<List<Quiz>> result);
    void startClass(String classID,UiState<String> result);
    void endClass(String classID,UiState<String> result);
    void getAllActivities2(List<Classroom> classroomList,UiState<List<Quiz>> result);
    //for students
    void getAllClass(UiState<List<Classroom>> result);
    void getAllMyClass(String studentID,UiState<List<Classroom>> result);
    void searchClass(String code,UiState<Classroom> result);
    void joinClass(String classID,String studentID,UiState<String> result);
    void addAttendance(String classID,String studentID,UiState<String> result);
    void getClassroom(String classroomID,UiState<Classroom> result);


}
