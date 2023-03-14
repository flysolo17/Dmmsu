package com.flysolo.dmmsugradelevelapp.services.lesson;

import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import java.util.ArrayList;

public interface LessonService {
    void createLesson(Lesson lesson, UiState<String> result);
    void deleteLesson(String lessonID ,UiState<String> result);
    void updateLesson(Lesson lesson,UiState<String> result);
    void getAllLesson(UiState<ArrayList<Lesson>> result);
    void createActivity(Quiz quiz,UiState<String> result);
    void getAllActivity(UiState<ArrayList<Quiz>> result);
    void deleteActivity(String quizID,UiState<String> result);
}
