package com.flysolo.dmmsugradelevelapp.utils;


import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Days;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Respond;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public interface Constants {
    String ACCOUNTS_TABLE = "Accounts";
    String CLASSROOM_TABLE = "Classroom";
    String ACTIVITIES_TABLE = "Activities";
    String QUESTIONS_TABLE = "Questions";
    String LESSONS_TABLE = "Lessons";
    String CONTENTS_TABLE = "Contents";
    String RESOPONSES_TABLE = "Responses";
    static String getFileExtension(Activity activity, Uri uri) {
        ContentResolver cR = activity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    static String getFileType(String url) {
        return url.substring(url.lastIndexOf(".") + 1 ,url.lastIndexOf("?"));
    }
    static int identifyFile(String file) {
        int id = 0;
        if (file.contains("document")) {
            id = R.drawable.ic_insert_drive_file;
        } else if (file.contains("image")) {
            id = R.drawable.ic_insert_photo;
        } else if (file.contains("video")) {
            id = R.drawable.ic_play_circle_outline;
        } else if (file.contains("audio")) {
            id = R.drawable.ic_audiotrack;
        } else {
            id = R.drawable.ic_insert_drive_file;
        }
        return id;
    }
    static String getFilename(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
    static String formatDate(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
        return dateFormat.format(date);
    }
    static int getMaxScore(List<Question> questions){
        int count = 0;
        for (Question question : questions) {
            count += question.getPoints();
        }
        return count;
    }
    static int getMyScore(List<Question> questions, Respond respond) {
        int score = 0;
        for (Question question: questions) {
            for (Answer answer: respond.getAnswers()) {
                if (question.getId().equals(answer.getQuestionID()) && question.getAnswer().equals(answer.getAnswer())) {
                    score+= question.getPoints();
                }
            }
        }
        return score;
    }
}
