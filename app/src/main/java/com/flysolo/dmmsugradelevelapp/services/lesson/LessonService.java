package com.flysolo.dmmsugradelevelapp.services.lesson;

import android.net.Uri;

import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

import java.util.ArrayList;
import java.util.List;

public interface LessonService {
    void createLesson(Lesson lesson, UiState<String> result);
    void deleteLesson(String lessonID ,UiState<String> result);
    void updateLesson(Lesson lesson,UiState<String> result);
    void getAllLesson(UiState<List<Lesson>> result);
    void createActivity(Quiz quiz,UiState<String> result);
    void getAllActivity(String lessonID,UiState<List<Quiz>> result);
    void deleteActivity(String quizID,UiState<String> result);
    void addContent(Content content,UiState<String> result);
    void deleteContent(String contentID,UiState<String> result);
    void updateContent(Content content,UiState<String> result);
    void getAllContent(String lessonID,UiState<List<Content>> result);
    void uploadAttachment(String type,String filename,Uri uri, UiState<String> result);
    void getQuestionsByID(String activityID,UiState<List<Question>> result);
    void addQuestion(Question question, UiState<String> result);
    void deleteQuestion(String questionID,UiState<String> result);
    void updateQuestion(String questionID,Question question,UiState<String> result);
}
