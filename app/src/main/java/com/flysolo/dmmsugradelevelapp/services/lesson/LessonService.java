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
    void getLesson(String lessonID,UiState<Lesson> result);
    void getAllLesson(UiState<List<Lesson>> result);
    void addContent(String lessonID,Content content,UiState<String> result);
    void deleteContent(String lessonID,Content content,UiState<String> result);
    void updateContent(String lessonID,List<Content> contents,UiState<String> result);
    void uploadAttachment(String type,String filename,Uri uri, UiState<String> result);
}
